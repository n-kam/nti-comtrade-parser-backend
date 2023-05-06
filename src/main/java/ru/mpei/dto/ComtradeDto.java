package ru.mpei.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComtradeDto {
    private String caseName;
    private String stationName;
    private String recDevId;
    private Long revYear;
    private Integer totalChannelCount;
    private Integer analogChannelCount;
    private Integer discreteChannelCount;
    private List<Channel> channels;
    private Double freq;
    private Integer nRates;
    private Double samp, endSamp;
    private LocalDateTime dateTimeStart;
    private LocalDateTime dateTimeStop;
    private String fileType;
    private Double timeMultiplier;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Channel {
        String type;
        Integer number;
        String chId;
        String phase;
        String componentName;
        String unit;
        Double a;
        Double b;
        Double skew;
        Double minValue;
        Double maxValue;
        Double primary;
        Double secondary;
        String scaleId;
        Short normalState;
        List<Double> readings;
    }
}