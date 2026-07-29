package edu.utsa.cs3443.mydosemate.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * info in CSV will look like:
 *
 * 103,1,2026-07-29T08:00,2026-07-29T08:04,500 mg,taken
 * 104,1,2026-07-29T20:00,,,missed
 * (log_id,med_id,scheduled_time,taken_time,dose_amount,status)
 */

public class DoseLogManager {

    private static final Path DATA_DIR = Paths.get("data");

    private static final Path DOSE_LOG_FILE =
            DATA_DIR.resolve("dose_log.csv");

    private static final String CSV_HEADER =
            "log_id,med_id,scheduled_time,taken_time,dose_amount,status";

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Records a medication dose as taken.
     *
     * @return true if the dose was recorded, or false if it was
     * already recorded
     */
    public boolean recordTaken(
            Medication medication,
            LocalDateTime scheduledTime) throws IOException {

        if (medication == null || scheduledTime == null) {
            throw new IllegalArgumentException(
                    "Medication and scheduled time are required"
            );
        }

        return appendDoseLog(
                medication,
                scheduledTime,
                LocalDateTime.now()
                        .withSecond(0)
                        .withNano(0),
                formatDoseAmount(medication),
                "taken"
        );
    }

    /**
     * Records a medication dose as missed.
     *
     * A missed dose does not have a taken time or dose amount.
     *
     * @return true if the dose was recorded, or false if it was
     * already recorded
     */
    public boolean recordMissed(
            Medication medication,
            LocalDateTime scheduledTime) throws IOException {

        if (medication == null || scheduledTime == null) {
            throw new IllegalArgumentException(
                    "Medication and scheduled time are required"
            );
        }

        return appendDoseLog(
                medication,
                scheduledTime,
                null,
                "",
                "missed"
        );
    }

    /**
     * Checks whether this scheduled medication dose was already logged.
     */
    public boolean isDoseRecorded(
            int medicationId,
            LocalDateTime scheduledTime) throws IOException {

        ensureDoseLogFileExists();

        String scheduledTimeText =
                DATE_TIME_FORMAT.format(scheduledTime);

        try (BufferedReader reader = Files.newBufferedReader(
                DOSE_LOG_FILE,
                StandardCharsets.UTF_8)) {

            // Skip the header.
            reader.readLine();

            String row;

            while ((row = reader.readLine()) != null) {
                if (row.trim().isEmpty()) {
                    continue;
                }

                String[] fields = row.split(",", -1);

                if (fields.length >= 6
                        && Integer.parseInt(fields[1].trim())
                                == medicationId
                        && fields[2].trim()
                                .equals(scheduledTimeText)) {

                    return true;
                }
            }
        }

        return false;
    }

    private boolean appendDoseLog(
            Medication medication,
            LocalDateTime scheduledTime,
            LocalDateTime takenTime,
            String doseAmount,
            String status) throws IOException {

        ensureDoseLogFileExists();

        if (isDoseRecorded(
                medication.getMedicationId(),
                scheduledTime)) {

            return false;
        }

        int logId = generateNextLogId();

        String takenTimeText = takenTime == null
                ? ""
                : DATE_TIME_FORMAT.format(takenTime);

        String row = logId + ","
                + medication.getMedicationId() + ","
                + DATE_TIME_FORMAT.format(scheduledTime) + ","
                + takenTimeText + ","
                + doseAmount + ","
                + status;

        try (BufferedWriter writer = Files.newBufferedWriter(
                DOSE_LOG_FILE,
                StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {

            writer.write(row);
            writer.newLine();
        }

        return true;
    }

    private int generateNextLogId() throws IOException {
        int largestLogId = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                DOSE_LOG_FILE,
                StandardCharsets.UTF_8)) {

            // Skip the header.
            reader.readLine();

            String row;

            while ((row = reader.readLine()) != null) {
                if (row.trim().isEmpty()) {
                    continue;
                }

                int firstComma = row.indexOf(',');

                if (firstComma > 0) {
                    try {
                        int logId = Integer.parseInt(
                                row.substring(0, firstComma).trim()
                        );

                        if (logId > largestLogId) {
                            largestLogId = logId;
                        }

                    } catch (NumberFormatException ignored) {
                        // Ignore malformed IDs.
                    }
                }
            }
        }

        return largestLogId + 1;
    }

    private void ensureDoseLogFileExists() throws IOException {
        Files.createDirectories(DATA_DIR);

        if (!Files.exists(DOSE_LOG_FILE)
                || Files.size(DOSE_LOG_FILE) == 0) {

            try (BufferedWriter writer = Files.newBufferedWriter(
                    DOSE_LOG_FILE,
                    StandardCharsets.UTF_8)) {

                writer.write(CSV_HEADER);
                writer.newLine();
            }
        }
    }

    private String formatDoseAmount(Medication medication) {
        double dosage = medication.getDosage();
        String dosageText;

        if (dosage == Math.rint(dosage)) {
            dosageText = String.valueOf((long) dosage);
        } else {
            dosageText = String.valueOf(dosage);
        }

        return dosageText + " " + medication.getUnit();
    }
}