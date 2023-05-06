package ru.mpei.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaveformModel {
    String caseName;
    String aChannelName;
    String bChannelName;
    String cChannelName;
    List<Double> ia;
    List<Double> ib;
    List<Double> ic;
}
