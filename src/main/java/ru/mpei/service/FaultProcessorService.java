package ru.mpei.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mpei.dto.ComtradeDto;
import ru.mpei.model.FaultModel;
import ru.mpei.repository.FaultCurrentRepo;
import ru.mpei.utils.FourierImpl;
import ru.mpei.utils.VectorF;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class FaultProcessorService {

    @Value("${setPoint}")
    private double setPoint;

    private final FaultCurrentRepo faultCurrentRepo;

    public FaultProcessorService(FaultCurrentRepo faultCurrentRepo) {
        this.faultCurrentRepo = faultCurrentRepo;
    }

    public void process(ComtradeDto cDto) {

        double freq = cDto.getFreq();
        double samp = cDto.getSamp();
        double readingsPerPeriod = samp / freq;
        String phasesInFault = "";

        double faultCurrentA = 0;
        double faultCurrentB = 0;
        double faultCurrentC = 0;

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

            } else if ((ch.getPhase() == null && ch.getChId().replaceAll("[0-9]", "").endsWith("B"))
                    || ch.getPhase() != null && ch.getPhase().equals("B")) {
                phase = "B";

            } else if ((ch.getPhase() == null && ch.getChId().replaceAll("[0-9]", "").endsWith("C"))
                    || ch.getPhase() != null && ch.getPhase().equals("C")) {
                phase = "C";

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
//        log.info("fault model: {}", faultModel);
        faultCurrentRepo.save(faultModel);
    }
}
