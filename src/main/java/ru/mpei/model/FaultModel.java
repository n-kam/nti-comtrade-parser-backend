package ru.mpei.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "faults")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaultModel {

    @Id
    @Column(name = "case_number")
    private String caseName;

    @Column(name = "date_time_start")
    private LocalDateTime dateTimeStart;

    @Column(name = "ia_rms")
    private Double iaRms;

    @Column(name = "ib_rms")
    private Double ibRms;

    @Column(name = "ic_rms")
    private Double icRms;

    @Column(name = "phases_in_fault")
    private String phasesInFault;
}
