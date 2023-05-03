package ru.mpei.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comtrade {
    private String stationName;
    private String recDevId;
    private Long revYear;
    private Integer totalChannelCount;
    private Integer analogChannelCount;
    private Integer discreteChannelCount;
    private List<AnalogChannel> analogChannels;
    private List<DiscreteChannel> discreteChannels;
    private Double freq;
    private Integer nRates;
    private Double samp, endSamp;
    private Date dateTimeStart;
    private Date dateTimeStop;
    private String fileType;
    private Double timeMultiplier;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalogChannel{
        Integer number;
        String id;
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
        List<Double> readings;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiscreteChannel{
        Integer number;
        String id;
        String phase;
        String componentName;
        Short normalState;
        List<Integer> readings;
    }
}