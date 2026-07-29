package edu.utsa.cs3443.mydosemate.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages dose history entries for medications.
 * <p>
 * This class loads, saves, appends, and filters {@link DoseLog} records using
 * an in-memory list backed by a CSV file.
 */
public class History {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DOSE_LOG_FILE = DATA_DIR.resolve("dose_log.csv");
    private static final String CSV_HEADER = "log_id,med_id,scheduled_time,taken_time,dose_amount,status";
    private static final int EXPECTED_COLUMN_COUNT = 6;
    private final ArrayList<DoseLog> doseLogs;

    /**
     * Creates an empty history store.
     */
    public History() {
        doseLogs = new ArrayList<DoseLog>();
    }

    /**
     * Loads dose logs from the default dose log CSV file.
     *
     * @throws IOException if the file cannot be read or parsed
     */
    public void loadDoseLogs() throws IOException {
        loadDoseLogs(resolveDoseLogFile());
    }

    /**
     * Loads dose logs from the specified CSV file.
     *
     * @param dose_log_csv the dose log CSV file to load
     * @throws IOException if the file is missing, empty, malformed, or contains invalid data
     */
    public void loadDoseLogs(Path dose_log_csv) throws IOException {
        if (dose_log_csv == null) {
            throw new IllegalArgumentException("Dose log file path cannot be null");
        }

        if (!Files.exists(dose_log_csv)) {
            saveDoseLogs(); // create the file if it doesn't exist
        }

        ArrayList<DoseLog> loadedDoseLogs = new ArrayList<DoseLog>();

        try (BufferedReader reader = Files.newBufferedReader(dose_log_csv, StandardCharsets.UTF_8)) {

            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Dose log file is empty: " + dose_log_csv);
            }

            if (!CSV_HEADER.equals(header.trim())) {
                throw new IOException("Unexpected dose_log.csv header: " + header);
            }

            String row;
            int lineNumber = 1;

            while ((row = reader.readLine()) != null) {
                lineNumber++;

                if (row.trim().isEmpty()) {
                    continue;
                }

                DoseLog doseLog = parseDoseLogRow(row, lineNumber);
                loadedDoseLogs.add(doseLog);
            }
        }

        doseLogs.clear();
        doseLogs.addAll(loadedDoseLogs);
    }

    /**
     * Adds a dose log entry to the in-memory history.
     *
     * @param doseLog the dose log to add
     */
    public void addDoseLog(DoseLog doseLog) {
        doseLogs.add(doseLog);
    }

    /**
     * Returns a copy of all dose logs currently stored in memory.
     *
     * @return a list containing all dose logs
     */
    public List<DoseLog> getDoseLogs() {
        return new ArrayList<DoseLog>(doseLogs);
    }

    /**
     * Rewrites {@code data/dose_log.csv} from the in-memory dose log list,
     * replacing any existing contents.
     *
     * @throws IOException if the file cannot be written
     */
    public void saveDoseLogs() throws IOException {
        saveDoseLogs(DOSE_LOG_FILE);
    }

    /**
     * Rewrites the given dose log CSV file from the in-memory dose log list.
     *
     * @param dose_log_csv the file to overwrite
     * @throws IOException if the file cannot be written
     */
    public void saveDoseLogs(Path dose_log_csv) throws IOException {
        if (dose_log_csv == null) {
            throw new IllegalArgumentException("Dose log file path cannot be null");
        }

        Path parentDirectory = dose_log_csv.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(dose_log_csv, StandardCharsets.UTF_8)) {
            writer.write(CSV_HEADER);
            writer.newLine();

            for (DoseLog doseLog : doseLogs) {
                writer.write(doseLog.toCsvRow());
                writer.newLine();
            }
        }
    }

    /**
     * Appends a single dose log entry to {@code data/dose_log.csv} without
     * rewriting the rest of the file, then records it in memory.
     * <p>
     * If the file does not exist yet, it is created with the CSV header first.
     *
     * @param doseLog the dose log entry to append
     * @throws IOException if the file cannot be written
     */
    public void appendDoseLog(DoseLog doseLog) throws IOException {
        appendDoseLog(doseLog, DOSE_LOG_FILE);
    }

    /**
     * Appends a single dose log entry to the given dose log CSV file without
     * rewriting the rest of the file, then records it in memory.
     *
     * @param doseLog the dose log entry to append
     * @param doseLogCsv the file to append to
     * @throws IOException if the file cannot be written
     */
    public void appendDoseLog(
            DoseLog doseLog,
            Path doseLogCsv
    ) throws IOException {
        if (doseLog == null) {
            throw new IllegalArgumentException("Dose log cannot be null");
        }

        if (doseLogCsv == null) {
            throw new IllegalArgumentException(
                    "Dose log file path cannot be null"
            );
        }

        Path parentDirectory = doseLogCsv.getParent();

        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        boolean needsHeader =
                !Files.exists(doseLogCsv)
                        || Files.size(doseLogCsv) == 0;

        try (BufferedWriter writer = Files.newBufferedWriter(
                doseLogCsv,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            if (needsHeader) {
                writer.write(CSV_HEADER);
                writer.newLine();
            }

            writer.write(doseLog.toCsvRow());
            writer.newLine();
        }

        doseLogs.add(doseLog);
    }
    /**
     * Returns all dose logs recorded for a given medication, in load/insertion order.
     *
     * @param medication_id the medication ID to filter by
     * @return the matching dose logs
     */
    public List<DoseLog> getDoseLogsForMedication(int medication_id) {
        ArrayList<DoseLog> matchingLogs = new ArrayList<DoseLog>();

        for (DoseLog doseLog : doseLogs) {
            if (doseLog.getMedId() == medication_id) {
                matchingLogs.add(doseLog);
            }
        }

        return matchingLogs;
    }

    /**
     * Returns all dose logs recorded for a given medication on a given date.
     *
     * @param medication_id the medication ID to filter by
     * @param date the calendar date to filter by
     * @return the matching dose logs
     */
    public List<DoseLog> getDoseLogsForMedicationOnDate(int medication_id, LocalDate date) {
        ArrayList<DoseLog> matchingLogs = new ArrayList<DoseLog>();

        if (date == null) {
            return matchingLogs;
        }

        for (DoseLog doseLog : doseLogs) {
            if (doseLog.getMedId() == medication_id && date.equals(doseLog.getScheduledDate())) {
                matchingLogs.add(doseLog);
            }
        }

        return matchingLogs;
    }

    /**
     * Returns all dose logs scheduled for a given calendar date, based on each
     * entry's {@code scheduled_time}.
     *
     * @param date the date to filter by
     * @return the matching dose logs
     */
    public List<DoseLog> getDoseLogsForDate(LocalDate date) {
        ArrayList<DoseLog> matchingLogs = new ArrayList<DoseLog>();

        if (date == null) {
            return matchingLogs;
        }

        for (DoseLog doseLog : doseLogs) {
            if (date.equals(doseLog.getScheduledDate())) {
                matchingLogs.add(doseLog);
            }
        }

        return matchingLogs;
    }

    /**
     * Returns the number of dose logs scheduled on a given date whose status indicates the dose was taken.
     *
     * @param date the date to inspect
     * @return the number of taken doses for that date
     */
    public int getTakenCountForDate(LocalDate date) {
        int takenCount = 0;

        for (DoseLog doseLog : getDoseLogsForDate(date)) {
            if ("taken".equalsIgnoreCase(doseLog.getStatus())) {
                takenCount++;
            }
        }

        return takenCount;
    }

    /**
     * Returns the number of dose logs scheduled on a given date whose status indicates the dose was missed or skipped.
     *
     * @param date the date to inspect
     * @return the number of missed doses for that date
     */
    public int getMissedCountForDate(LocalDate date) {
        int missedCount = 0;

        for (DoseLog doseLog : getDoseLogsForDate(date)) {
            String status = doseLog.getStatus();

            if ("missed".equalsIgnoreCase(status) || "skipped".equalsIgnoreCase(status)) {
                missedCount++;
            }
        }

        return missedCount;
    }

    /**
     * Parses a single dose log CSV row into a {@link DoseLog} instance.
     *
     * @param row the CSV row text
     * @param lineNumber the source line number, used for error reporting
     * @return the parsed dose log
     * @throws IOException if the row cannot be parsed or contains invalid data
     */
    private DoseLog parseDoseLogRow(String row, int lineNumber) throws IOException {
        try {
            String[] fields = parseCsvFields(row);

            if (fields.length != EXPECTED_COLUMN_COUNT) {
                throw new IllegalArgumentException("expected " + EXPECTED_COLUMN_COUNT + " columns but found " + fields.length);
            }

            return new DoseLog(
                    Integer.parseInt(fields[0].trim()),
                    Integer.parseInt(fields[1].trim()),
                    fields[2].trim(),
                    fields[3].trim(),
                    fields[4].trim(),
                    fields[5].trim());

        } catch (NumberFormatException exception) {
            throw new IOException("Invalid number on dose log CSV line " + lineNumber + ": " + row, exception);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid dose log CSV line " + lineNumber + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Splits a CSV row into fields while respecting quoted values and escaped quotes.
     *
     * @param row the raw CSV row
     * @return the parsed fields
     * @throws IllegalArgumentException if the row contains an unclosed quotation mark
     */
    private String[] parseCsvFields(String row) {
        ArrayList<String> fields = new ArrayList<String>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int index = 0; index < row.length(); index++) {
            char currentCharacter = row.charAt(index);

            if (currentCharacter == '"') {
                if (insideQuotes && index + 1 < row.length() && row.charAt(index + 1) == '"') {
                    currentField.append('"');
                    index++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (currentCharacter == ',' && !insideQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(currentCharacter);
            }
        }

        if (insideQuotes) {
            throw new IllegalArgumentException("CSV row contains an unclosed quotation mark");
        }

        fields.add(currentField.toString());
        return fields.toArray(new String[fields.size()]);
    }

    /**
     * Generates a new dose log ID based on the current number of stored logs.
     *
     * @return the next dose log ID value
     */
    public int getDoseLogId() {
        return doseLogs.size() + 100;
    }

    /**
     * Resolves the dose log file from the current working directory or one of
     * its parent directories.
     *
     * <p>
     * This keeps the app working when the JVM launches from an IDE, a test
     * runner, or the project root.
     *
     * @return the first existing dose log path found, or the default path if
     *         no existing file is discovered
     */
    private Path resolveDoseLogFile() {
        Path currentDirectory = Paths.get("").toAbsolutePath();

        while (currentDirectory != null) {
            Path candidate = currentDirectory.resolve(DOSE_LOG_FILE).normalize();
            if (Files.exists(candidate)) {
                return candidate;
            }

            currentDirectory = currentDirectory.getParent();
        }

        return DOSE_LOG_FILE;
    }
}
