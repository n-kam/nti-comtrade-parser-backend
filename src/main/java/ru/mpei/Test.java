package ru.mpei;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mpei.dto.ComtradeDto;
import ru.mpei.service.ComtradeFilesService;
import ru.mpei.service.ComtradeParserService;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class Test {
    @SneakyThrows
    public static void main(String[] args) throws SQLException {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
//        Console.main(args);

//        ComtradeFilesService comtradeFileService = context.getBean(ComtradeFilesService.class);
//        ComtradeParserService parser = new ComtradeParserService();
//        String path = "comtrade-cases";
//        List<String> caseNames = comtradeFileService.getCaseNames(path);
//        List<String> casePaths = comtradeFileService.getCasePaths(path);
//        Map<String, Map<String, String>> cfgAndDatMap = comtradeFileService.getCfgAndDatMap(casePaths);
//        ComtradeDto comtradeDto = parser.parse(cfgAndDatMap.get(caseNames.get(0)));
//        FaultProcessorService faultProcessorService = context.getBean(FaultProcessorService.class);
//        System.out.println(" ");
//        faultProcessorService.process(comtradeDto);
//
//        comtradeDto = parser.parse(cfgAndDatMap.get(caseNames.get(1)));
//        faultProcessorService.process(comtradeDto);
//
//        comtradeDto = parser.parse(cfgAndDatMap.get(caseNames.get(2)));
//        faultProcessorService.process(comtradeDto);
//
//        comtradeDto = parser.parse(cfgAndDatMap.get(caseNames.get(0)));
//        faultProcessorService.process(comtradeDto);

        parseBin();
//        parseCfg();

//        ComtradeParserService comtradeParserService = context.getBean(ComtradeParserService.class);
//        ComtradeFilesService comtradeFilesService = context.getBean(ComtradeFilesService.class);

//        String casePaths = "comtrade-cases";
//        String casePaths = "comtrade-cases-with-broken";

//        Map<String, Map<String, String>> cfgAndDatMap = comtradeFilesService.getCfgAndDatMap(comtradeFilesService.getCasePaths(casePaths));

//        ComtradeDto comtradeDto = comtradeParserService.parse(cfgAndDatMap.get("02"));

//        printComtrade(comtradeDto);

//        ComtradeParserService comtradeParserService = new ComtradeParserService();

//        Map<String, String> comtrade = new HashMap<>();

//        comtradeParserService.parseCfg();
//        String datFile = "src/main/resources/comtrade-cases-with-broken/02/Number start = 690 Test = 4.1.2.1.1 Time = 07_19_2022 13_50_13.811 Terminal ПСА.dat";
//        datFile = "src/main/resources/comtrade-cases-with-broken/18/Number start = 695 Test = 4.1.2.2.2 Time = 07_19_2022 13_53_07.861 Terminal ПСБ.dat";
//        comtradeParserService.parseDatBinary(datFile);

//        System.out.println(Integer.toBinaryString((byte) 0x52));
//        System.out.println(Integer.toBinaryString((byte) 0b1));
//        System.out.println(Integer.toBinaryString((byte) 0b11));
//        System.out.println(Integer.toBinaryString((byte) 0b101));
//        hexToInt((byte) 0x52);
//        System.out.println();
//        hexToInt((byte) 0xff);
//        System.out.println();
//        hexToInt((byte) 0x7e);
//        hexToInt((byte) 0xA2);


    }


    static void parseCfg() {
        String fileNameCase1 = "src/main/resources/comtrade-cases-with-broken/01/Number start = 690 Test = 4.1.2.1.1 Time = 07_19_2022 13_50_13.811 RTDS.cfg";
        String fileNameCase2 = "src/main/resources/comtrade-cases-with-broken/02/Number start = 690 Test = 4.1.2.1.1 Time = 07_19_2022 13_50_13.811 Terminal ПСА.cfg";

        byte[] bytes = new byte[]{};

        try {
            bytes = Files.readAllBytes(Paths.get(fileNameCase1));
            System.out.println("filename 1");
            validateCharset(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            bytes = Files.readAllBytes(Paths.get(fileNameCase2));
            System.out.println("filename 2");
            validateCharset(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static Charset validateCharset(byte[] bytes) {

        List<Charset> possibleCharsets = new ArrayList<>();

        Charset utf8 = StandardCharsets.UTF_8;
        Charset win1251 = Charset.forName("windows-1251");
        possibleCharsets.add(utf8);
        possibleCharsets.add(win1251);

        for (Charset charset : possibleCharsets) {

            CharsetDecoder decoder = charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT);

            CharBuffer buf;
            try {
                buf = decoder.decode(ByteBuffer.wrap(bytes));
//                System.out.println("charset: " + charset.name() + " success: " + buf);
                return charset;
            } catch (CharacterCodingException e) {
//                System.out.println("charset: " + charset.name() + " exception");
            }


        }

        return null;

    }

    static void parseBin() {

//        String fileName = "src/main/resources/comtrade-cases-with-broken/02/Number start = 690 Test = 4.1.2.1.1 Time = 07_19_2022 13_50_13.811 Terminal ПСА.dat";
        String fileName = "src/main/resources/test.bin";

        Path path = Paths.get(fileName);
        try {
            byte[] bytes = Files.readAllBytes(path);
//            String asText = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println(asText);

            String result = "";
//            System.out.println("2-0: " + byteArrayToInt2(bytes, 0));
//            System.out.println("4-0: " + byteArrayToInt4(bytes, 0));
//
//            System.out.println("2-2: " + byteArrayToInt2(bytes, 2));
//            System.out.println("4-2: " + byteArrayToInt4(bytes, 2));
//
//            System.out.println("2-4: " + byteArrayToInt2(bytes, 4));
//            System.out.println("4-4: " + byteArrayToInt4(bytes, 4));

//            bytes = null;
//            bytes = new byte[]{
//                    0x05,
//                    0x00,
//                    0x00,
//                    0x00,
//                    (byte) 0x9b,
//                    0x02,
//                    0x00,
//                    0x00,
//                    0x08,
//                    (byte) 0xfd,
//                    (byte) 0xfa,
//                    0x04,
//                    0x48,
//                    0x00,
//                    0x3d,
//                    0x00,
//                    0x74,
//                    (byte) 0xff,
//                    0x0a,
//                    (byte) 0xfe,
//                    0x30,
//                    0x00};

//            System.out.println("bb: " + byteArrayToInt4(bb, 0));
//            bb = Byte.parseByte("ffff");

            for (byte b : Arrays.copyOfRange(bytes, 0, 22)) {
                result += String.format("%02x", b).toUpperCase() + ",";
            }

//            for (byte b : bytes) {
//                result += String.format("%02x", b).toUpperCase() + ",";
//            }
            System.out.println("res: " + result);

            result = "";

            result += byteArrayToInt4Trav(bytes, 0) + ",";
            result += byteArrayToInt4Trav(bytes, 4) + ",";
            result += byteArrayToShort2Trav(bytes, 8) + ",";
            result += byteArrayToShort2Trav(bytes, 10) + ",";
            result += byteArrayToShort2Trav(bytes, 12) + ",";
            result += byteArrayToShort2Trav(bytes, 14) + ",";
            result += byteArrayToShort2Trav(bytes, 16) + ",";
            result += byteArrayToShort2Trav(bytes, 18) + ",";

            System.out.println("res: " + result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//            System.out.println(reader.lines().toList().subList(1, 10));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static int byteArrayToInt4(byte[] b, int offset) {
        return b[3 + offset] & 0xFF | (b[2 + offset] & 0xFF) << 8 | (b[1 + offset] & 0xFF) << 16 | (b[offset] & 0xFF) << 24;
    }

    public static int byteArrayToInt2(byte[] b, int offset) {
        return (b[1 + offset] & 0xFF) | (b[offset] & 0xFF) << 8;
    }

    public static int byteArrayToInt4Trav(byte[] b, int offset) {
        return b[0 + offset] & 0xFF | (b[1 + offset] & 0xFF) << 8 | (b[2 + offset] & 0xFF) << 16 | (b[3 + offset] & 0xFF) << 24;
    }

//    public static int byteArrayToInt2Trav(byte[] b, int offset) {
//        return (b[0 + offset] & 0xFF) | (b[1 + offset] & 0xFF) << 8;
//    }

    public static short byteArrayToShort2Trav(byte[] b, int offset) {
        return (short) ((b[0 + offset] & 0xFF) | (b[1 + offset] & 0xFF) << 8);
    }

    public static String byteArrayToString(byte[] b, int offset, int length) {
        return new String(Arrays.copyOfRange(b, offset, offset + length - 1), StandardCharsets.UTF_8);
    }

    static void printComtrade(ComtradeDto comtradeDto) {

        System.out.println("\n###### ComtradeDto: " +
                        "\ncase name: " + comtradeDto.getCaseName() +
                        "\nstat name: " + comtradeDto.getStationName() +
                        "\nrecDevId: " + comtradeDto.getRecDevId() +
                        "\nrevYear: " + comtradeDto.getRevYear() +
                        "\ntotal ch: " + comtradeDto.getTotalChannelCount() +
//                "\ntotal ch read: " + comtradeDto.getChannels().size() +
//                "\nanalog ch: " + comtradeDto.getAnalogChannelCount() +
                        "\ndiscr ch: " + comtradeDto.getDiscreteChannelCount() +
                        "\nfreq: " + comtradeDto.getFreq() +
                        "\nnrates: " + comtradeDto.getNRates() +
                        "\nsamp: " + comtradeDto.getSamp() +
                        "\nendSamp: " + comtradeDto.getEndSamp() +
                        "\ndtStart: " + comtradeDto.getDateTimeStart() +
                        "\nstStop: " + comtradeDto.getDateTimeStop() +
                        "\nfileType: " + comtradeDto.getFileType() +
                        "\ntime mult: " + comtradeDto.getTimeMultiplier()
        );

        for (ComtradeDto.Channel ch : comtradeDto.getChannels()) {
            System.out.println("\n### Channel " + ch.getNumber() + ": "+
                    "\ntype: " + ch.getType() +
//                    "\nnum: " + ch.getNumber() +
                    "\nid: " + ch.getChId() +
                    "\nph: " + ch.getPhase() +
                    "\ncomp name: " + ch.getComponentName() +
                    "\nunit: " + ch.getUnit() +
                    "\na: " + ch.getA() +
                    "\nb: " + ch.getB() +
                    "\nskew: " + ch.getSkew() +
                    "\nmin: " + ch.getMinValue() +
                    "\nmax: " + ch.getMaxValue() +
                    "\npri: " + ch.getPrimary() +
                    "\nsec: " + ch.getSecondary() +
                    "\nscale: " + ch.getScaleId() +
                    "\nn state: " + ch.getNormalState() +
                    "\nreadings size: " + ch.getReadings().size()
            );
        }

    }
}

