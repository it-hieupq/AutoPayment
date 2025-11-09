package com.demo.autopayment.business.service;

import com.demo.autopayment.model.entity.LoanApplication;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportDataService {

    private final UtilService utilService;

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Value("${spring.jpa.batch.insert.size:3000}")
    int batchSize;

    int count = 0;

    public void importCsv(String path) throws IOException {

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<?>> futures = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line;

            // skipped header
            reader.readLine();
            List<String> lineBatch = new ArrayList<>();
            while ((line = reader.readLine()) != null) {

                lineBatch.add(line);

                if (++count % batchSize == 0) {
                    List<String> batchToSubmit = new ArrayList<>(lineBatch);
                    futures.add(executor.submit(() -> utilService.saveBatch(batchToSubmit)));
                    lineBatch.clear();
                    log.info("{} lines processed...", count);
                }

            }

            if (!lineBatch.isEmpty()) {
                List<String> batchToSubmit = new ArrayList<>(lineBatch);
                futures.add(executor.submit(() -> utilService.saveBatch(batchToSubmit)));
                log.info("{} lines processed...", count);
            }

            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.MINUTES);

            LocalDateTime end = LocalDateTime.now();

            long runTime = ChronoUnit.SECONDS.between(now, end);

            System.out.printf("Import completed: %d records with %d seconds", count, runTime);
            System.out.println();

        } catch (InterruptedException e) {
            log.error("Error while importing CSV files", e);
            throw new RuntimeException(e);
        }
    }

    public static LoanApplication toApplication(String applicationString) {
        //loanId,loanProduct,loanPackage,loanAmount,loanStatus,phoneNumber,customerName,identityNumber,dueDate,createdAt,lastModifiedAt
        String[] fields = applicationString.split(",");

        if(validateFieldsBlank(fields[0],  fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8])) {
            return null;
        }

//        int loanAmount = Integer.parseInt(fields[3]);
//        if ( loanAmount < 15000000 ) {
//            log.info("Loan amount {} is less than 1500000",  loanAmount);
//            return null;
//        }

        return LoanApplication.builder()
                .loanId(fields[0].trim())
                .loanProduct(fields[1].trim())
                .loanPackage(fields[2].trim())
                .loanAmount(Integer.parseInt(fields[3].trim()))
                .loanStatus(fields[4].trim())
                .phoneNumber(fields[5].trim())
                .customerName(fields[6].trim())
                .identityNumber(fields[7].trim())
                .dueDate(LocalDate.parse(fields[8].trim(), DATE_FORMATTER))
                .build();
    }

    public static boolean validateFieldsBlank(String... fields) {
        return Arrays.stream(fields)
                .anyMatch(Strings::isBlank);
    }

}
