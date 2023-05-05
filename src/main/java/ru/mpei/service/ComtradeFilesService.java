package ru.mpei.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
public class ComtradeFilesService {

    public List<String> getCaseNames(String comtradeCasesPath) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(comtradeCasesPath))));

        return reader.lines()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getCasePaths(String comtradeCasesPath) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(comtradeCasesPath))));

        return reader.lines()
                .map(f -> comtradeCasesPath + "/" + f)
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<Integer, Map<String, String>> getCfgAndDatMap(List<String> casePaths) {

        Map<Integer, Map<String, String>> comtrades = new HashMap<>();
        Map<String, String> comtrade;

        for (String casePath : casePaths) {
            String[] split = casePath.split("/");
            String caseNumber = split[split.length - 1];

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(casePath))))) {

                comtrade = reader.lines()
                        .collect(
                                Collectors.toMap(f -> {
                                    if (f.endsWith(".cfg")) return "cfg";
                                    if (f.endsWith(".dat")) return "dat";
                                    return null;
                                }, p -> casePath + "/" + p));

                comtrades.put(Integer.parseInt(caseNumber), comtrade);

            } catch (IllegalStateException e) {
                log.error("Broken files in {}", casePath);
                throw new RuntimeException(e);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return comtrades;
    }
}
