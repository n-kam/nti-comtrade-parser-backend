package ru.mpei.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultDto {
    private String caseName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime dateTimeStart;
    private Double iaRms;
    private Double ibRms;
    private Double icRms;
    private String phasesInFault;
}
