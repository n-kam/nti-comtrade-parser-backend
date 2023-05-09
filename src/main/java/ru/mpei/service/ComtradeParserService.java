package ru.mpei.service;

import org.springframework.stereotype.Service;
import ru.mpei.dto.ComtradeDto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ComtradeParserService {

    public ComtradeDto parse(Map<String, String> comtradeFile) throws IOException {

        String baseName = "src/main/resources/";
        String cfgFileName = baseName + comtradeFile.get("cfg");
        String datFileName = baseName + comtradeFile.get("dat");

        ComtradeDto comtradeDto = new ComtradeDto();

        String[] split = cfgFileName.split("/");
        String caseName = split[split.length - 2];
        comtradeDto.setCaseName(caseName);

        BufferedReader reader = new BufferedReader(new FileReader(cfgFileName));

        String[] line = reader.readLine().split(" *, *");
        comtradeDto.setStationName(line[0]);
        comtradeDto.setRecDevId(line[1]);
        comtradeDto.setRevYear(Long.parseLong(line[2]));

        line = reader.readLine().split(" *, *");
        comtradeDto.setTotalChannelCount(Integer.parseInt(line[0]));
        int analogChannelCount = Integer.parseInt(line[1].replaceAll("[a-zA-Z]", ""));
        comtradeDto.setAnalogChannelCount(analogChannelCount);
        int discreteChannelCount = Integer.parseInt(line[2].replaceAll("[a-zA-Z]", ""));
        comtradeDto.setDiscreteChannelCount(discreteChannelCount);

        List<ComtradeDto.Channel> analogChannels = new ArrayList<>();
        List<ComtradeDto.Channel> discreteChannels = new ArrayList<>();


        for (int i = 0; i < analogChannelCount; i++) {

            line = reader.readLine().split(" *, *");
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


        for (int i = 0; i < discreteChannelCount; i++) {
            line = reader.readLine().split(" *, *");

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

        comtradeDto.setFreq(Double.parseDouble(reader.readLine()));
        comtradeDto.setNRates(Integer.parseInt(reader.readLine()));
        line = reader.readLine().split(" *, *");
        comtradeDto.setSamp(Double.parseDouble(line[0]));
        comtradeDto.setEndSamp(Double.parseDouble(line[1]));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy,HH:mm:ss.SSSSSS");
        comtradeDto.setDateTimeStart(LocalDateTime.parse(reader.readLine(), dtf));
        comtradeDto.setDateTimeStop(LocalDateTime.parse(reader.readLine(), dtf));

        comtradeDto.setFileType(reader.readLine());
        comtradeDto.setTimeMultiplier(Double.parseDouble(reader.readLine()));

        reader = new BufferedReader(new FileReader(datFileName));

        String tempLine;
        while ((tempLine = reader.readLine()) != null) {
            line = tempLine.split(" *, *");

            int i = 1;

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
