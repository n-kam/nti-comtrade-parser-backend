package ru.mpei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.dto.FaultDto;
import ru.mpei.model.WaveformModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/test/files/getComtradeCaseNames")
    public List<String> getComtradeCaseNames() {
        List<String> caseNames = List.of("01", "02", "10", "20", "100");
        log.info("getComtradeCaseNames: {}", caseNames);
        return caseNames;
    }

    @GetMapping("/test/fault/getFaultData")
    public FaultDto getFaultData(@RequestParam String caseName) {
        LocalDateTime ldt = LocalDateTime.now();
        FaultDto faultDto = new FaultDto("caseName", ldt, 10.0, 20.0, 30.0, "ABC");
        log.info("getFaultData. requestParam: {}; response (faultDto): {}", caseName, faultDto);
        return faultDto;
    }

//    @GetMapping("/test/fault/getFaultReadings")
//    public Optional<WaveformModel> getFaultReadings(@RequestParam String caseName) {
//        List<Double> ia = List.of(10.0, 20.0, 30.0, 100.0);
//        List<Double> ib = List.of(20.0, 20.0, 30.0, 100.0);
//        List<Double> ic = List.of(30.0, 20.0, 30.0, 100.0);
//        Optional<WaveformModel> waveform = Optional.of(new WaveformModel("caseName", "aCh", "bCh", "cCh", ia, ib, ic));
//        log.info("getFaultReadings. requestParam: {}; response (waveform): {}", caseName, waveform);
//        return waveform;
//    }

    @PostMapping("/test/fault/selectCase")
    public boolean selectCase(@RequestParam String caseName) {
        log.info("selected case: {}", caseName);
        return true;
    }
}
