package com.example.referral.util;

import com.example.referral.model.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class CsvUtil {
    public static byte[] generateCsv(List<User> users) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT
                .withHeader("ID", "Email", "ReferralCode", "ReferrerID", "ProfileCompleted"))) {
            for (User user : users) {
                csvPrinter.printRecord(
                        user.getId(),
                        user.getEmail(),
                        user.getReferralCode(),
                        user.getReferrerId() != null ? user.getReferrerId() : "None",
                        user.isProfileCompleted()
                );
            }
            csvPrinter.flush();
        }
        return out.toByteArray();
    }
}