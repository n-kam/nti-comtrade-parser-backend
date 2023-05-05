package ru.mpei.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mpei.dto.ComtradeDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ComtradeParserService {

    @Value("${cfgFileName}")
    private String cfgFileName;
    @Value("${datFileName}")
    private String datFileName;
    @Value("${current}")
    private String current;

    public ComtradeDto parse() {


        cfgFileName = "comtrade/Number start = 698 Test = 4.2.1.1 Time = 07_19_2022 13_54_51.673 RTDS.cfg";
        datFileName = "comtrade/Number start = 698 Test = 4.2.1.1 Time = 07_19_2022 13_54_51.673 RTDS.dat";


        ComtradeDto comtradeDto = new ComtradeDto();
        Iterator<String> linesIterator;

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(cfgFileName))));
        linesIterator = reader.lines().toList().iterator();

        String[] line = linesIterator.next().split(" *, *");
        comtradeDto.setStationName(line[0]);
        comtradeDto.setRecDevId(line[1]);
        comtradeDto.setRevYear(Long.parseLong(line[2]));

        line = linesIterator.next().split(" *, *");
        comtradeDto.setTotalChannelCount(Integer.parseInt(line[0]));
        int analogChannelCount = Integer.parseInt(line[1].replaceAll("[a-zA-Z]", ""));
        comtradeDto.setAnalogChannelCount(analogChannelCount);
        int discreteChannelCount = Integer.parseInt(line[2].replaceAll("[a-zA-Z]", ""));
        comtradeDto.setDiscreteChannelCount(discreteChannelCount);

//        List<ComtradeDto.AnalogChannel> analogChannels = new ArrayList<>();
//        List<ComtradeDto.DiscreteChannel> discreteChannels = new ArrayList<>();
        List<ComtradeDto.Channel> analogChannels = new ArrayList<>();
        List<ComtradeDto.Channel> discreteChannels = new ArrayList<>();

//        for (int i = 0; i < analogChannelCount; i++) {
//
//            line = linesIterator.next().split(" *, *");
//            analogChannels.add(new ComtradeDto.AnalogChannel(
//                    Integer.parseInt(line[0]),
//                    line[1],
//                    line[2],
//                    line[3],
//                    line[4],
//                    Double.parseDouble(line[5]),
//                    Double.parseDouble(line[6]),
//                    Double.parseDouble(line[7]),
//                    Double.parseDouble(line[8]),
//                    Double.parseDouble(line[9]),
//                    Double.parseDouble(line[10]),
//                    Double.parseDouble(line[11]),
//                    line[12],
//                    new ArrayList<>()));
//
//        }

        for (int i = 0; i < analogChannelCount; i++) {

            line = linesIterator.next().split(" *, *");
            analogChannels.add(new ComtradeDto.Channel(
                    "analog",
                    Integer.parseInt(line[0]),
                    emptyToNull(line[1]),
                    emptyToNull(line[2]),
                    emptyToNull(line[3]),
                    emptyToNull(line[4]),
                    Double.parseDouble(line[5]),
                    Double.parseDouble(line[6]),
                    Double.parseDouble(line[7]),
                    Double.parseDouble(line[8]),
                    Double.parseDouble(line[9]),
                    Double.parseDouble(line[10]),
                    Double.parseDouble(line[11]),
                    emptyToNull(line[12]),
                    null,
                    new ArrayList<>()));
        }

//        for (int i = 0; i < discreteChannelCount; i++) {
//            line = linesIterator.next().split(" *, *");
//
//            discreteChannels.add(new ComtradeDto.DiscreteChannel(
//                    Integer.parseInt(line[0]),
//                    line[1],
//                    line[2],
//                    line[3],
//                    Short.parseShort(line[4]),
//                    new ArrayList<>()));
//
//        }

        for (int i = 0; i < discreteChannelCount; i++) {
            line = linesIterator.next().split(" *, *");

            discreteChannels.add(new ComtradeDto.Channel(
                    "discrete",
                    Integer.parseInt(line[0]),
                    emptyToNull(line[1]),
                    emptyToNull(line[2]),
                    emptyToNull(line[3]),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Short.parseShort(line[4]),
                    new ArrayList<>()));

        }

//        comtradeDto.setAnalogChannels(analogChannels);
//        comtradeDto.setDiscreteChannels(discreteChannels);


        comtradeDto.setFreq(Double.parseDouble(linesIterator.next()));
        comtradeDto.setNRates(Integer.parseInt(linesIterator.next()));
        line = linesIterator.next().split(" *, *");
        comtradeDto.setSamp(Double.parseDouble(line[0]));
        comtradeDto.setEndSamp(Double.parseDouble(line[1]));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss.SSS");
        try {
            comtradeDto.setDateTimeStart(dateFormat.parse(linesIterator.next()));
            comtradeDto.setDateTimeStop(dateFormat.parse(linesIterator.next()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        comtradeDto.setFileType(linesIterator.next());
        comtradeDto.setTimeMultiplier(Double.parseDouble(linesIterator.next()));


        reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(datFileName))));
        linesIterator = reader.lines().toList().iterator();

        while (linesIterator.hasNext()) {
            line = linesIterator.next().split(" *, *");

            int i = 1;

//            for (ComtradeDto.AnalogChannel analogChannel : analogChannels) {
//                Double reading = Double.parseDouble(line[i]) * analogChannel.getA() + analogChannel.getB();
//                analogChannel.getReadings().add(reading);
//                i++;
//            }
//
//            for (ComtradeDto.DiscreteChannel discreteChannel : discreteChannels) {
//                discreteChannel.getReadings().add(Integer.parseInt(line[i]));
//                i++;
//            }

            for (ComtradeDto.Channel analogChannel : analogChannels) {
                Double reading = Double.parseDouble(line[i]) * analogChannel.getA() + analogChannel.getB();
                analogChannel.getReadings().add(reading);
                i++;
            }

            for (ComtradeDto.Channel discreteChannel : discreteChannels) {
                discreteChannel.getReadings().add(Double.parseDouble(line[i]));
                i++;
            }


        }

        comtradeDto.setChannels(Stream.concat(
                        analogChannels.stream(),
                        discreteChannels.stream())
                .collect(Collectors.toList())
        );

        return comtradeDto;
    }


    public String emptyToNull(String s) {
        if (s.isEmpty() || s.isBlank()) return null;
        return s;
    }
}
