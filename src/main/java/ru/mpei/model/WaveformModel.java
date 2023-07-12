package ru.mpei.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaveformModel {
    String caseName;
    String aChannelName;
    String bChannelName;
    String cChannelName;
    List<Double> times;
    List<Double> ia;
    List<Double> ib;
    List<Double> ic;

    @Override
    public String toString() {
        return "WaveformModel{" +
                "caseName='" + caseName + '\'' +
                ", aChannelName='" + aChannelName + '\'' +
                ", bChannelName='" + bChannelName + '\'' +
                ", cChannelName='" + cChannelName + '\'' +
//                ", times=" + times +
                ", times size=" + times.size() +
                ", ia size=" + ia.size() +
                ", ib size=" + ib.size() +
                ", ic size=" + ic.size() +
                '}';
    }
}
