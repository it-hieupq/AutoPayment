package com.demo.autopayment.business.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Run implements CommandLineRunner {

    final ImportDataService importDataService;
    static final String filePath = "C:\\Users\\PC\\Desktop\\loan_applications_1M.csv";

    @Override
    public void run(String... args) throws Exception {
        importDataService.importCsv(filePath);
    }
}
