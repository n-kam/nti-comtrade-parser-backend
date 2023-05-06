package ru.mpei.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultDto {
    private String caseName;
    private LocalDateTime dateTimeStart;
    private Double iaRms;
    private Double ibRms;
    private Double icRms;
    private String phasesInFault;
}
