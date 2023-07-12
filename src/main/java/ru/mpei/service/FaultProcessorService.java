package ru.mpei.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mpei.dto.ComtradeDto;
import ru.mpei.dto.FaultDto;
import ru.mpei.model.FaultModel;
import ru.mpei.model.WaveformModel;
import ru.mpei.repository.FaultCurrentRepo;
import ru.mpei.repository.WaveformInMemoryRepo;
import ru.mpei.utils.FourierImpl;
import ru.mpei.utils.VectorF;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FaultProcessorService {

    @Value("${setPoint}")
    private double setPoint;

    @Value("${comtradePath}")
    private String casesDir;

    private final FaultCurrentRepo faultCurrentRepo;
    private final WaveformInMemoryRepo waveformRepo;
    //    private final WaveformSimpleInMemoryRepo waveformRepo;
    private final ComtradeFilesService comtradeFilesService;
    private final ComtradeParserService comtradeParserService;

    public FaultProcessorService(FaultCurrentRepo faultCurrentRepo, WaveformInMemoryRepo waveformRepo, ComtradeFilesService comtradeFilesService, ComtradeParserService comtradeParserService) {
        this.faultCurrentRepo = faultCurrentRepo;
        this.waveformRepo = waveformRepo;
        this.comtradeFilesService = comtradeFilesService;
        this.comtradeParserService = comtradeParserService;
    }

    public void process(ComtradeDto cDto) {

        double freq = cDto.getFreq();
        double samp = cDto.getSamp();
        double readingsPerPeriod = samp / freq;
        String phasesInFault = "";

        double faultCurrentA = 0;
        double faultCurrentB = 0;
        double faultCurrentC = 0;

        String aChannelName = null;
        String bChannelName = null;
        String cChannelName = null;

        List<Double> iAWaveform = null;
        List<Double> iBWaveform = null;
        List<Double> iCWaveform = null;

        LocalDateTime dateTimeStart = null;
        FourierImpl fourier = new FourierImpl(1, (int) readingsPerPeriod);


        // Filter analog channels containing currents data
        List<ComtradeDto.Channel> filteredChannels = cDto.getChannels().stream()
                .filter(ch -> ch.getChId().startsWith("I") && ch.getType().equals("analog"))
                .limit(3)
                .toList();

//        log.info("date time start: {}", cDto.getDateTimeStart());

        // Loop through filtered channels
        for (ComtradeDto.Channel ch : filteredChannels) {

            double normalCurrent = 1_000_000;
            VectorF vectorF = new VectorF();

            // Determine channel phase
            String phase;
            if ((ch.getPhase() == null && ch.getChId().replaceAll("[0-9]", "").endsWith("A"))
                    || ch.getPhase() != null && ch.getPhase().equals("A")) {
                phase = "A";
                aChannelName = ch.getChId();
                iAWaveform = ch.getReadings();

            } else if ((ch.getPhase() == null && ch.getChId().replaceAll("[0-9]", "").endsWith("B"))
                    || ch.getPhase() != null && ch.getPhase().equals("B")) {
                phase = "B";
                bChannelName = ch.getChId();
                iBWaveform = ch.getReadings();

            } else if ((ch.getPhase() == null && ch.getChId().replaceAll("[0-9]", "").endsWith("C"))
                    || ch.getPhase() != null && ch.getPhase().equals("C")) {
                phase = "C";
                cChannelName = ch.getChId();
                iCWaveform = ch.getReadings();

            } else throw new RuntimeException("Error in phase parsing");

//            log.info("channel name (id): {}, phase: {}", ch.getChId(), phase);

            int index = 0;

            for (Double reading : ch.getReadings()) {
                index++;

                // Save RMS value for first period as normal current value
                if (index == readingsPerPeriod) {
                    normalCurrent = vectorF.getMag();
                }

                // If current is above set point process fault data
                if (index > readingsPerPeriod && (vectorF.getMag() > normalCurrent * setPoint)) {

                    // Save fault start date-time
                    if (dateTimeStart == null) {
                        long usFromRecordingStart = (long) (index / samp * 1_000_000);
                        dateTimeStart = cDto.getDateTimeStart().plus(usFromRecordingStart, ChronoUnit.MICROS);
                    }

                    // Save max fault current for phase
                    switch (phase) {
                        case "A" -> faultCurrentA = Math.max(faultCurrentA, vectorF.getMag());
                        case "B" -> faultCurrentB = Math.max(faultCurrentB, vectorF.getMag());
                        case "C" -> faultCurrentC = Math.max(faultCurrentC, vectorF.getMag());
                    }
                }
                fourier.process(reading, vectorF);
            }
            log.info("case: {}, channel: {}, Ia: {}, Ib: {}, Ic: {}, ph: {}, datetime: {}", cDto.getCaseName(), ch.getChId(), faultCurrentA, faultCurrentB, faultCurrentC, phasesInFault, dateTimeStart);

            fourier.reset();
        }

        // Store fault data
        FaultModel faultModel = new FaultModel();
        faultModel.setCaseName(cDto.getCaseName());
        faultModel.setDateTimeStart(dateTimeStart);
        faultModel.setIaRms(faultCurrentA);
        faultModel.setIbRms(faultCurrentB);
        faultModel.setIcRms(faultCurrentC);
        if (faultCurrentA != 0) phasesInFault += "A";
        if (faultCurrentB != 0) phasesInFault += "B";
        if (faultCurrentC != 0) phasesInFault += "C";
        faultModel.setPhasesInFault(phasesInFault);


        log.info("fault model: {}", faultModel);


        faultCurrentRepo.save(faultModel);

        // Store waveforms
        WaveformModel waveformModel = new WaveformModel();
        waveformModel.setCaseName(cDto.getCaseName());
        waveformModel.setAChannelName(aChannelName);
        waveformModel.setBChannelName(bChannelName);
        waveformModel.setCChannelName(cChannelName);

        // TEMPORARY SIMPLIFICATION!!! <<<<<-----
        DecimalFormat df = new DecimalFormat("#.##");
        int n = 2;
//        List<LocalDateTime> times = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        double timeMillis = 1 / samp * 1000;
//        System.out.println("timeMillis=" + timeMillis);

        for (int i = 0; i < iAWaveform.size(); i += n) {
//            System.out.println("i=" + i);
            times.add(Double.valueOf(df.format(timeMillis * i)));
        }

        List<Double> list = new ArrayList<>();

        for (int i = 0; i < iAWaveform.size(); i += n) {
            list.add(iAWaveform.get(i));
        }
        iAWaveform = list;

        list = new ArrayList<>();
        for (int i = 0; i < iBWaveform.size(); i += n) {
            list.add(iBWaveform.get(i));
        }
        iBWaveform = list;

        list = new ArrayList<>();
        for (int i = 0; i < iCWaveform.size(); i += n) {
            list.add(iCWaveform.get(i));
        }
        iCWaveform = list;
//        ------>>>>>>

        waveformModel.setIa(iAWaveform);
        waveformModel.setIb(iBWaveform);
        waveformModel.setIc(iCWaveform);
        waveformModel.setTimes(times);


        log.info("waveform model: {}", waveformModel);


        waveformRepo.save(waveformModel);
    }

    public boolean selectCase(String caseName) throws IOException {
        if (!comtradeFilesService.getCaseNames(casesDir).contains(caseName)) return false;

        List<String> casePaths = comtradeFilesService.getCasePaths(casesDir);
        Map<String, Map<String, String>> cfgAndDatMap = comtradeFilesService.getCfgAndDatMap(casePaths);
        ComtradeDto comtradeDto = comtradeParserService.parse(cfgAndDatMap.get(caseName));
        process(comtradeDto);
        return true;
    }

    public FaultDto getFaultDto(String caseName) {
        FaultModel faultModel = faultCurrentRepo.getReferenceById(caseName);
        return new FaultDto(faultModel.getCaseName(),
                faultModel.getDateTimeStart(),
                faultModel.getIaRms(),
                faultModel.getIbRms(),
                faultModel.getIcRms(),
                faultModel.getPhasesInFault());
    }
}
