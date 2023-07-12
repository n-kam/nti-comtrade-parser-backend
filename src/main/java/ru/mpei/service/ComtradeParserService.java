package ru.mpei.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mpei.dto.ComtradeDto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ComtradeParserService {

    ComtradeDto comtradeDto = new ComtradeDto();
    List<ComtradeDto.Channel> analogChannels = new ArrayList<>();
    List<ComtradeDto.Channel> discreteChannels = new ArrayList<>();

    public void reset() {
        this.comtradeDto = new ComtradeDto();
        this.analogChannels = new ArrayList<>();
        this.discreteChannels = new ArrayList<>();
    }

    public ComtradeDto parse(Map<String, String> comtradeFile) throws IOException {
        reset();

        String baseName = "src/main/resources/";
        String cfgFileName = baseName + comtradeFile.get("cfg");
        String datFileName = baseName + comtradeFile.get("dat");

//        ComtradeDto comtradeDto = new ComtradeDto();

        String[] split = cfgFileName.split("/");
        String caseName = split[split.length - 2];
        this.comtradeDto.setCaseName(caseName);

        parseCfg(cfgFileName);

        if (this.comtradeDto.getFileType().equalsIgnoreCase("ascii")) {
            parseDatAscii(datFileName);
        } else if (this.comtradeDto.getFileType().equalsIgnoreCase("binary")) {
            parseDatBinary(datFileName);
        }

        return this.comtradeDto;
    }

    public void parseCfg(String cfgFileName) throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get(cfgFileName));
        Charset charset = validateCharset(bytes);
        BufferedReader reader = new BufferedReader(new FileReader(cfgFileName, Objects.requireNonNull(charset)));

        // First line
        String[] values = reader.readLine().split(" *, *");
        this.comtradeDto.setStationName(values[0]);
        this.comtradeDto.setRecDevId(values[1]);
        this.comtradeDto.setRevYear(Long.parseLong(values[2]));

        // Second line
        values = reader.readLine().split(" *, *");
        this.comtradeDto.setTotalChannelCount(Integer.parseInt(values[0]));
        int analogChannelCount = Integer.parseInt(values[1].replaceAll("[a-zA-Z]", ""));
        this.comtradeDto.setAnalogChannelCount(analogChannelCount);
        int discreteChannelCount = Integer.parseInt(values[2].replaceAll("[a-zA-Z]", ""));
        this.comtradeDto.setDiscreteChannelCount(discreteChannelCount);


        // Analog channels
        for (int i = 0; i < analogChannelCount; i++) {

            values = reader.readLine().split(" *, *");
            this.analogChannels.add(new ComtradeDto.Channel(
                    "analog",
                    Integer.parseInt(values[0]),
                    emptyToNull(values[1]),
                    emptyToNull(values[2]),
                    emptyToNull(values[3]),
                    emptyToNull(values[4]),
                    Double.parseDouble(values[5]),
                    Double.parseDouble(values[6]),
                    Double.parseDouble(values[7]),
                    Double.parseDouble(values[8]),
                    Double.parseDouble(values[9]),
                    Double.parseDouble(values[10]),
                    Double.parseDouble(values[11]),
                    emptyToNull(values[12]),
                    null,
                    new ArrayList<>()));
        }


        // Discrete channels
        for (int i = 0; i < discreteChannelCount; i++) {
            values = reader.readLine().split(" *, *");

            this.discreteChannels.add(new ComtradeDto.Channel(
                    "discrete",
                    Integer.parseInt(values[0]),
                    emptyToNull(values[1]),
                    emptyToNull(values[2]),
                    emptyToNull(values[3]),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Short.parseShort(values[4]),
                    new ArrayList<>()));
        }

        this.comtradeDto.setChannels(new ArrayList<>());
        this.comtradeDto.getChannels().addAll(this.analogChannels);
        this.comtradeDto.getChannels().addAll(this.discreteChannels);

        this.comtradeDto.setFreq(Double.parseDouble(reader.readLine()));
        this.comtradeDto.setNRates(Integer.parseInt(reader.readLine()));

        values = reader.readLine().split(" *, *");
        this.comtradeDto.setSamp(Double.parseDouble(values[0]));
        this.comtradeDto.setEndSamp(Integer.parseInt(values[1]));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy,HH:mm:ss.SSSSSS");
        this.comtradeDto.setDateTimeStart(LocalDateTime.parse(reader.readLine(), dtf));
        this.comtradeDto.setDateTimeStop(LocalDateTime.parse(reader.readLine(), dtf));

        this.comtradeDto.setFileType(reader.readLine());
        this.comtradeDto.setTimeMultiplier(Double.parseDouble(reader.readLine()));

        reader.close();
    }


    public void parseDatAscii(String datFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(datFileName));

        String readLine;
        while ((readLine = reader.readLine()) != null) {
            String[] line = readLine.split(" *, *");

            int i = 1;

            for (ComtradeDto.Channel analogChannel : this.analogChannels) {
                Double reading = Double.parseDouble(line[i]) * analogChannel.getA() + analogChannel.getB();
                analogChannel.getReadings().add(reading);
                i++;
            }

            for (ComtradeDto.Channel discreteChannel : this.discreteChannels) {
                discreteChannel.getReadings().add(Double.parseDouble(line[i]));
                i++;
            }
        }
    }

    public void parseDatBinary(String datFileName) throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get(datFileName));

        int analogChCount = this.analogChannels.size();
        int discreteChCount = this.discreteChannels.size();
        Integer endSamp = this.comtradeDto.getEndSamp();


        //
        analogChCount = 33;
        discreteChCount = 128;
        endSamp = 6000;
        this.comtradeDto.setTimeMultiplier(1.0);
        //


        int discreteChByteCount;
        if (discreteChCount % 16 == 0) {
//            lineLength = (4 + 4 + analogChCount * 2 + (discreteChCount / 16) * 2);
            discreteChByteCount = (discreteChCount / 16) * 2;
        } else {
//            lineLength = (4 + 4 + analogChCount * 2 + (discreteChCount / 16 + 1) * 2);
            discreteChByteCount = (discreteChCount / 16 + 1) * 2;
        }

        int lineLength = 4 + 4 + analogChCount * 2 + discreteChByteCount;

        System.out.println("ll: " + lineLength);

//        StringBuilder header = new StringBuilder();
//        header.append("number      time      ");
//        header.append("number         date           ");
//        for (int i = 1; i <= analogChCount + discreteChCount; i++) {
//            header.append(String.format("%6d", i)).append(",");
//        }
//        System.out.println(header);


        for (int i = 0; i < endSamp; i++) {
//            StringBuilder result = new StringBuilder();

            byte[] line = Arrays.copyOfRange(bytes, i * lineLength, i * lineLength + lineLength);

            int number = byteArrayToIntTraversed(line, 0);
            double time = byteArrayToIntTraversed(line, 4) * this.comtradeDto.getTimeMultiplier();


            int j = 0;
            for (ComtradeDto.Channel analogChannel : this.analogChannels) {
//                short val = byteArrayToShortTraversed(new byte[]{line[j], line[j + i]}, 0);
                Double reading = byteArrayToShortTraversed(line, j) * analogChannel.getA() + analogChannel.getB();
                analogChannel.getReadings().add(reading);
                j += 2;
            }

            int[] ints = new int[discreteChCount];

            for (int k = 0; k < discreteChByteCount; k++) {

                System.arraycopy(byteToBinaryInts(line[k]), 0, ints, 7 * k, 8);

//                if (7 * (k + 1) - 7 * k >= 0){
//                    System.arraycopy(byteToBinaryInts(line[k]), 7 * k, ints, 7 * k, 7 * (k + 1) - 7 * k);
//                }
            }

            j = 0;
            for (ComtradeDto.Channel discreteChannel : this.discreteChannels) {
//                int[] ints = byteToBinaryInts(bytes[i * lineLength + j]);
                discreteChannel.getReadings().add((double) ints[j]);
//                j += 2;
                j++;
            }


//            byte[] num = new byte[4];
//
//            for (int j = 0; j < 4; j++) {
//                num[j] = bytes[i * lineLength + j];
//            }
//            result.append(String.format("%06d", byteArrayToIntTraversed(num, 0)));
//            result.append("   ");
//
//            for (int j = 4; j < 8; j++) {
//                num[j - 4] = bytes[i * lineLength + j];
//            }
//            result.append(String.format("%7d ms", byteArrayToIntTraversed(num, 0)));
//            result.append("   ");
//
//            int analogLength = 33 * 2;
//
//
//            for (int j = 8; j < 8 + analogLength; j += 2) {
//                short s = byteArrayToShortTraversed(new byte[]{bytes[i * lineLength + j], bytes[i * lineLength + j + 1]}, 0);
//                result.append(String.format("%6d,", s));
//            }
//            result.append(" |   ");

//            for (int j = 8 + analogLength; j < lineLength; j++) {
//                int[] ints = byteToBinaryInts(bytes[i * lineLength + j]);
//                result.append(Arrays.toString(ints)
//                        .replaceAll("\\[", "")
//                        .replaceAll("\\]", ", ")
//                        .replaceAll(", ", ",     "));
//            }
//
//            System.out.println(result);
        }
    }


    private String emptyToNull(String s) {
        if (s.isEmpty() || s.isBlank()) return null;
        return s;
    }

    private Charset validateCharset(byte[] bytes) {

        Charset utf8 = StandardCharsets.UTF_8;
        Charset win1251 = Charset.forName("windows-1251");

        List<Charset> expectedCharsets = new ArrayList<>();
        expectedCharsets.add(utf8);
        expectedCharsets.add(win1251);

        for (Charset charset : expectedCharsets) {

            CharsetDecoder decoder = charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT);

            try {
                decoder.decode(ByteBuffer.wrap(bytes));
                return charset;
            } catch (CharacterCodingException ignored) {
            }
        }

        log.warn("Unknown charset");

        return null;
    }

    private int byteArrayToIntTraversed(byte[] b, int offset) {
        return b[offset] & 0xFF | (b[1 + offset] & 0xFF) << 8 | (b[2 + offset] & 0xFF) << 16 | (b[3 + offset] & 0xFF) << 24;
    }

    private short byteArrayToShortTraversed(byte[] b, int offset) {
        return (short) (b[offset] & 0xFF | (b[1 + offset] & 0xFF) << 8);
    }

    private int[] byteToBinaryInts(byte b) {
        int[] bits = new int[8];
        byte bb = (byte) 0b1;
        for (int i = 0; i < 8; i++) {
            int k = b & (byte) 0b1;
            b >>= 1;
            bits[bits.length - i - 1] = k;
        }
        return bits;
    }
}
