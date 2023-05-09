package ru.mpei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.dto.FaultDto;
import ru.mpei.model.WaveformModel;
import ru.mpei.repository.FaultCurrentRepo;
import ru.mpei.repository.WaveformInMemoryRepo;
import ru.mpei.service.FaultProcessorService;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
public class ComtradeFaultDataController {

    @Value("${comtradePath}")
    String path;

    private final FaultProcessorService faultProcessorService;
    private final FaultCurrentRepo faultCurrentRepo;
    private final WaveformInMemoryRepo waveformRepo;

    public ComtradeFaultDataController(FaultProcessorService faultProcessorService, FaultCurrentRepo faultCurrentRepo, WaveformInMemoryRepo waveformRepo) {
        this.faultProcessorService = faultProcessorService;
        this.faultCurrentRepo = faultCurrentRepo;
        this.waveformRepo = waveformRepo;
    }

    @GetMapping("/fault/getFaultData")
    public FaultDto getFaultData(@RequestParam String caseName) {
//        return faultCurrentRepo.getById(caseName);
//        return faultCurrentRepo.getReferenceById(caseName);
        return faultProcessorService.getFaultDto(caseName);
    }

//    @GetMapping("/fault/getFaultReadings")
//    public WaveformModel getFaultReadings(@RequestParam String caseName) {
//        return waveformRepo.findById(caseName).orElse(null);
//    }

//    @GetMapping("/fault/getFaultReadings")
//    public WaveformModel getFaultReadings(@RequestParam String caseName) {
//        return waveformRepo.findById(caseName);
//    }

    @GetMapping("/fault/getFaultReadings")
    public Optional<WaveformModel> getFaultReadings(@RequestParam String caseName) {
        return waveformRepo.findById(caseName);
    }

    @PostMapping("/fault/selectCase")
    public boolean selectCase(@RequestParam String caseName) throws IOException {
        return faultProcessorService.selectCase(caseName);
    }

}
