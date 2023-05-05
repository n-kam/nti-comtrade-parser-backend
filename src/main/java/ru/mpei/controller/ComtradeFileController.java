package ru.mpei.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.service.ComtradeFilesService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ComtradeFileController {

    @Value("${comtradePath}")
    String path;

    private final ComtradeFilesService comtradeFilesService;

    public ComtradeFileController(ComtradeFilesService comtradeFilesService) {
        this.comtradeFilesService = comtradeFilesService;
    }

    @GetMapping("/files/getComtradeCaseNames")
    public List<String> getComtradeCaseNames() {
        return comtradeFilesService.getCaseNames(path);
    }

    @GetMapping("/files/getComtradeCasePaths")
    public List<String> getComtradeCasePaths() {
        return comtradeFilesService.getCasePaths(path);
    }

    @GetMapping("/files/getCfgAndDatMap")
    public Map<Integer, Map<String, String>> getCfgAndDatMap() {
        return comtradeFilesService.getCfgAndDatMap(comtradeFilesService.getCasePaths(path));
    }
}
