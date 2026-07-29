package edu.utsa.cs3443.mydosemate.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the application's medication list and associated dose history.
 * <p>
 * This class is responsible for loading and saving medications from CSV,
 * adding, updating, removing medications, and recording take/skip actions.
 */
public class MedicationTracker {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path MEDICATION_FILE =  DATA_DIR.resolve("medications.csv");
    private static final String CSV_HEADER = "med_id,name,dosage,unit,frequency,times_per_day," + "scheduled_times,start_date,current_amount,notes";
    private static final int EXPECTED_COLUMN_COUNT = 10;
    private final ArrayList<Medication> medications;
    private final History history;

    /**
     * Creates a new medication tracker and attempts to load stored medications
     * and dose history from disk.
     */
    public MedicationTracker() {

        medications = new ArrayList<Medication>();
        history = new History();
        try {
            loadMedications();
            this.history.loadDoseLogs();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Loads medications from the default medication CSV file.
     *
     * @throws IOException if the medication file cannot be read or parsed
     */
    public void loadMedications() throws IOException {
        loadMedications(MEDICATION_FILE);
    }

    /**
     * Loads medications from the specified CSV file.
     *
     * @param medications_csv the medication CSV file to load
     * @throws IOException if the file is missing, empty, malformed, or contains invalid data
     */
    public void loadMedications(Path medications_csv) throws IOException{
        if (medications_csv == null) {
            throw new IllegalArgumentException("Medication file path cannot be null");
        }

        if (!Files.exists(medications_csv)) {
            throw new FileNotFoundException("medications.csv was not found at " + medications_csv);
        }

        ArrayList<Medication> loadedMedications = new ArrayList<Medication>();

        try (BufferedReader reader = Files.newBufferedReader(medications_csv, StandardCharsets.UTF_8)) {

            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Medication file is empty: " + medications_csv);
            }

            if (!CSV_HEADER.equals(header.trim())) {
                throw new IOException("Unexpected medications.csv header: " + header);
            }

            String row;
            int lineNumber = 1;

            while ((row = reader.readLine()) != null) {
                lineNumber++;

                if (row.trim().isEmpty()) {
                    continue;
                }

                Medication medication = parseMedicationRow(row, lineNumber);

                if (containsMedicationId(loadedMedications, medication.getMedicationId())) {
                    throw new IOException("Duplicate medication ID " + medication.getMedicationId() + " on line " + lineNumber);
                }

                loadedMedications.add(medication);
            }
        }

        medications.clear();
        medications.addAll(loadedMedications);
    }

    /**
     * Saves medications to the default medication CSV file.
     *
     * @throws IOException if the file cannot be written
     */
    public void saveMedications() throws IOException {
        saveMedications(MEDICATION_FILE);
    }

    /**
     * Saves medications to the specified CSV file.
     *
     * @param medications_csv the destination CSV file
     * @throws IOException if the file cannot be written
     */
    public void saveMedications(Path medications_csv) throws IOException {
        if (medications_csv == null) {
            throw new IllegalArgumentException("Medication file path cannot be null");
        }

        Path parentDirectory = medications_csv.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        for (Medication medication : medications) {
            validateMedication(medication);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                medications_csv, StandardCharsets.UTF_8)) {

            writer.write(CSV_HEADER);
            writer.newLine();

            for (Medication medication : medications) {
                writer.write(medication.toCsvRow());
                writer.newLine();
            }
        }
    }

    /**
     * Adds a new medication to the tracker and persists the updated list.
     *
     * @param medication the medication to add
     * @throws IOException if saving the updated medication list fails
     */
    public void addMedication(Medication medication) throws IOException {
        validateMedication(medication);

        if (getMedicationById(medication.getMedicationId()) != null) {
            throw new IllegalArgumentException("Medication ID " + medication.getMedicationId() + " already exists");
        }

        medications.add(medication);

        try {
            saveMedications();
        } catch (IOException exception) {
            medications.remove(medication);
            throw exception;
        }
    }

    /**
     * Removes a medication by its ID and persists the updated list.
     *
     * @param medication_id the ID of the medication to remove
     * @return {@code true} if the medication was removed; {@code false} if no medication matched the ID
     * @throws IOException if saving the updated medication list fails
     */
    public boolean removeMedication(int medication_id) throws IOException {
        int medicationIndex = findMedicationIndexById(medication_id);

        if (medicationIndex == -1) {
            return false;
        }

        Medication removedMedication = medications.remove(medicationIndex);

        try {
            saveMedications();
        } catch (IOException exception) {
            medications.add(medicationIndex, removedMedication);
            throw exception;
        }

        return true;
    }

    /**
     * Replaces an existing medication with updated values and persists the changes.
     *
     * @param updated_medication the updated medication
     * @return {@code true} if the medication was found and updated; {@code false} otherwise
     * @throws IOException if saving the updated medication list fails
     */
    public boolean updateMedication(Medication updated_medication) throws IOException {
        validateMedication(updated_medication);

        int medicationIndex = findMedicationIndexById(
                updated_medication.getMedicationId());

        if (medicationIndex == -1) {
            return false;
        }

        Medication oldMedication = medications.set(medicationIndex, updated_medication);

        try {
            saveMedications();
        } catch (IOException exception) {
            medications.set(medicationIndex, oldMedication);
            throw exception;
        }

        return true;
    }

    /**
     * Retrieves a medication by its ID.
     *
     * @param medication_id the medication ID to search for
     * @return the matching medication, or {@code null} if no match exists
     */
    public Medication getMedicationById(int medication_id) {
        int medicationIndex = findMedicationIndexById(medication_id);

        if (medicationIndex == -1) {
            return null;
        }

        return medications.get(medicationIndex);
    }

    /**
     * Generates the next available medication ID.
     *
     * @return an ID value one greater than the current highest medication ID
     */
    public int generateNextMedicationId() {
        int largestId = 0;

        for (Medication medication : medications) {
            if (medication.getMedicationId() > largestId) {
                largestId = medication.getMedicationId();
            }
        }

        return largestId + 1;
    }

    /**
     * Returns a copy of the current medication list.
     *
     * @return a new list containing all tracked medications
     */
    public List<Medication> getMedications() {
        return new ArrayList<Medication>(medications);
    }

    /**
     * Marks a medication as taken, updates the stored amount, saves the change,
     * and records the dose in history.
     *
     * @param medication_id the ID of the medication to take
     * @return {@code true} if the medication was taken; {@code false} if the medication does not exist or has no remaining amount
     * @throws IOException if saving the medication update or writing the dose log fails
     */
    public boolean takeMedication(int medication_id) throws IOException {
        Medication medication = getMedicationById(medication_id);

        if (medication == null || medication.getCurrentAmount() <= 0) {
            return false;
        }

        int previousAmount = medication.getCurrentAmount();
        medication.setCurrentAmount(previousAmount - 1);

        try {
            saveMedications();
        } catch (IOException exception) {
            medication.setCurrentAmount(previousAmount);
            throw exception;
        }

        DoseLog doseLog = buildDoseLog(medication, "taken", LocalDateTime.now());

        try {
            history.appendDoseLog(doseLog);
        } catch (IOException exception) {
            medication.setCurrentAmount(previousAmount);
            saveMedications();
            throw exception;
        }

        return true;
    }

    /**
     * Marks a medication as skipped and records the event in history.
     *
     * @param medication_id the ID of the medication to skip
     * @return {@code true} if the medication exists; {@code false} otherwise
     * @throws IOException if writing the dose log fails
     */
    public boolean skipMedication(int medication_id) throws IOException {
        Medication medication = getMedicationById(medication_id);

        if (medication == null) {
            return false;
        }

        DoseLog doseLog = buildDoseLog(medication, "skipped", null);
        history.appendDoseLog(doseLog);

        return true;
    }

    /**
     * Builds a dose log entry for the given medication, matching it to whichever
     * of the medication's scheduled times-of-day is closest to right now.
     *
     * @param medication the medication the dose belongs to
     * @param status the dose status (e.g. {@code "taken"}, {@code "skipped"})
     * @param takenAt the moment the dose was actually taken, or {@code null} if
     *                it was not taken (e.g. skipped or missed)
     * @return a new, not-yet-persisted {@link DoseLog}
     */
    private DoseLog buildDoseLog(Medication medication, String status, LocalDateTime takenAt) {
        LocalDateTime scheduledDateTime = LocalDateTime.of(
                LocalDate.now(), closestScheduledTime(medication));

        String scheduledTime = scheduledDateTime.toString();
        String takenTime = takenAt == null ? "" : takenAt.toString();
        String doseAmount = formatDosageAmount(medication.getDosage()) + " " + medication.getUnit();

        return new DoseLog(
                history.getDoseLogId(),
                medication.getMedicationId(),
                scheduledTime,
                takenTime,
                doseAmount,
                status);
    }

    /**
     * Finds the medication's scheduled time-of-day closest to the current time,
     * so a dose taken or skipped "now" gets matched to the right slot even if
     * it's a little early or late.
     *
     * @param medication the medication whose scheduled times to search
     * @return the closest scheduled {@link LocalTime}
     */
    private LocalTime closestScheduledTime(Medication medication) {
        LocalTime now = LocalTime.now();
        String[] scheduledTimes = medication.getScheduledTimes().split(";", -1);

        LocalTime closestTime = null;
        long smallestDifferenceMinutes = Long.MAX_VALUE;

        for (String scheduledTimeText : scheduledTimes) {
            LocalTime scheduledTime = LocalTime.parse(scheduledTimeText.trim());
            long differenceMinutes = Math.abs(Duration.between(now, scheduledTime).toMinutes());

            if (differenceMinutes < smallestDifferenceMinutes) {
                smallestDifferenceMinutes = differenceMinutes;
                closestTime = scheduledTime;
            }
        }

        return closestTime;
    }

    /**
     * Formats a dosage without a trailing {@code .0} for whole numbers, matching
     * how {@link Medication} formats its own dosage for display and CSV rows.
     *
     * @param dosage the dosage amount
     * @return the formatted dosage
     */
    private String formatDosageAmount(double dosage) {
        if (dosage == Math.rint(dosage)) {
            return String.valueOf((long) dosage);
        }

        return String.valueOf(dosage);
    }

    /**
     * Parses a single medication CSV row into a {@link Medication} instance.
     *
     * @param row the CSV row text
     * @param lineNumber the source line number, used for error reporting
     * @return the parsed medication
     * @throws IOException if the row cannot be parsed or contains invalid data
     */
    private Medication parseMedicationRow(String row, int lineNumber)
            throws IOException {
        try {
            String[] fields = parseCsvFields(row);

            if (fields.length != EXPECTED_COLUMN_COUNT) {
                throw new IllegalArgumentException("expected " + EXPECTED_COLUMN_COUNT + " columns but found " + fields.length);
            }

            Medication medication = new Medication(
                    Integer.parseInt(fields[0].trim()),
                    fields[1].trim(),
                    Double.parseDouble(fields[2].trim()),
                    fields[3].trim(),
                    fields[4].trim(),
                    Integer.parseInt(fields[5].trim()),
                    fields[6].trim(),
                    fields[7].trim(),
                    Integer.parseInt(fields[8].trim()),
                    fields[9].trim());

            validateMedication(medication);
            return medication;

        } catch (NumberFormatException exception) {
            throw new IOException("Invalid number on medication CSV line " + lineNumber + ": " + row, exception);
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid medication CSV line " + lineNumber + ": " + exception.getMessage(), exception);
        }
    }

    /**
     * Validates that a medication contains all required values and that each
     * value is in the expected format or range.
     *
     * @param medication the medication to validate
     * @throws IllegalArgumentException if any field is missing, malformed, or out of range
     */
    private void validateMedication(Medication medication) {
        if (medication == null) {
            throw new IllegalArgumentException("Medication cannot be null");
        }

        if (medication.getMedicationId() <= 0) {
            throw new IllegalArgumentException("Medication ID must be greater than zero");
        }

        if (isNullOrBlank(medication.getName())) {
            throw new IllegalArgumentException("Medication name cannot be blank");
        }

        if (medication.getDosage() <= 0) {
            throw new IllegalArgumentException("Dosage must be greater than zero");
        }

        if (isNullOrBlank(medication.getUnit())) {
            throw new IllegalArgumentException("Medication unit cannot be blank");
        }

        if (isNullOrBlank(medication.getFrequency())) {
            throw new IllegalArgumentException("Frequency cannot be blank");
        }

        if (medication.getTimesPerDay() <= 0) {
            throw new IllegalArgumentException("Times per day must be greater than zero");
        }

        validateScheduledTimes(medication.getScheduledTimes(), medication.getTimesPerDay());

        if (isNullOrBlank(medication.getStartDate())) {
            throw new IllegalArgumentException("Start date cannot be blank");
        }

        try {
            LocalDate.parse(medication.getStartDate().trim());
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException("Start date must use YYYY-MM-DD format");
        }

        if (medication.getCurrentAmount() < 0) {
            throw new IllegalArgumentException("Current amount cannot be negative");
        }
    }

    /**
     * Validates the scheduled time list for a medication.
     *
     * @param scheduledTimes the semicolon-separated scheduled times
     * @param timesPerDay the expected number of scheduled times
     * @throws IllegalArgumentException if the scheduled times are blank, the count does not match, or a time is malformed
     */
    private void validateScheduledTimes(String scheduledTimes, int timesPerDay) {
        if (isNullOrBlank(scheduledTimes)) {
            throw new IllegalArgumentException("Scheduled times cannot be blank");
        }

        String[] times = scheduledTimes.split(";", -1);

        if (times.length != timesPerDay) {
            throw new IllegalArgumentException("Scheduled time count must match times per day");
        }

        for (String time : times) {
            try {
                LocalTime.parse(time.trim());
            } catch (DateTimeException exception) {
                throw new IllegalArgumentException("Scheduled times must use HH:mm format");
            }
        }
    }

    /**
     * Finds the index of a medication in the internal list by ID.
     *
     * @param medicationId the medication ID to search for
     * @return the medication index, or {@code -1} if no match is found
     */
    private int findMedicationIndexById(int medicationId) {
        for (int index = 0; index < medications.size(); index++) {
            if (medications.get(index).getMedicationId() == medicationId) {
                return index;
            }
        }

        return -1;
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
     * Checks whether a medication list already contains a medication with the given ID.
     *
     * @param medicationList the medication list to inspect
     * @param medicationId the medication ID to search for
     * @return {@code true} if the ID exists in the list; {@code false} otherwise
     */
    private boolean containsMedicationId(List<Medication> medicationList, int medicationId) {
        for (Medication medication : medicationList) {
            if (medication.getMedicationId() == medicationId) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether a string is {@code null}, empty, or contains only whitespace.
     *
     * @param value the string to check
     * @return {@code true} if the value is null or blank; {@code false} otherwise
     */
    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Returns the history object used to store dose logs.
     *
     * @return the medication dose history
     */
    public History getHistory() {
        return history;
    }

    /**
     * Returns today's progress report as an array of doubles where
     * the first element is the number of doses taken, the second is the number of doses missed,
     * the third is the number of doses upcoming, and the fourth is the progress as a percentage.
     * @return the progress report
     */
    public int[] getProgressReport() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int taken = 0;
        int missed = 0;
        int upcoming = 0;

        Map<String, DoseLog> todaysLogsBySlot = new HashMap<String, DoseLog>();
        for (DoseLog doseLog : history.getDoseLogsForDate(today)) {
            todaysLogsBySlot.put(buildDoseSlotKey(doseLog.getMedId(), doseLog.getScheduledDateTime()), doseLog);
        }

        for (Medication medication : medications) {
            if (!isMedicationDueToday(medication, today)) {
                continue;
            }

            String[] scheduledTimes = medication.getScheduledTimes().split(";", -1);
            for (String scheduledTimeText : scheduledTimes) {
                LocalTime scheduledTime = LocalTime.parse(scheduledTimeText.trim());
                LocalDateTime scheduledDateTime = LocalDateTime.of(today, scheduledTime);
                DoseLog doseLog = todaysLogsBySlot.get(buildDoseSlotKey(medication.getMedicationId(), scheduledDateTime));

                if (doseLog != null) {
                    if ("taken".equalsIgnoreCase(doseLog.getStatus())) {
                        taken++;
                    } else {
                        missed++;
                    }
                } else if (scheduledDateTime.isAfter(now)) {
                    upcoming++;
                } else {
                    missed++;
                }
            }
        }

        int totalDoses = taken + missed + upcoming;
        int progress = totalDoses == 0 ? 0 : (int) ((taken / (double) totalDoses) * 100.0);
        return new int[]{taken, missed, upcoming, progress};
    }

    /**
     * Returns whether the medication should be counted in today's report.
     *
     * @param medication the medication to inspect
     * @param date the date to evaluate
     * @return {@code true} if the medication is active on the given date
     */
    private boolean isMedicationDueToday(Medication medication, LocalDate date) {
        if (medication == null || date == null) {
            return false;
        }

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(medication.getStartDate().trim());
        } catch (Exception exception) {
            return false;
        }

        if (date.isBefore(startDate)) {
            return false;
        }

        String frequency = medication.getFrequency().trim().toLowerCase();

        if (frequency.contains("daily")) {
            return true;
        }

        if (frequency.contains("weekly")) {
            return ChronoUnit.DAYS.between(startDate, date) % 7 == 0;
        }

        if (frequency.contains("monthly")) {
            return startDate.getDayOfMonth() == date.getDayOfMonth();
        }

        if (frequency.contains("year")) {
            return startDate.getMonth() == date.getMonth() && startDate.getDayOfMonth() == date.getDayOfMonth();
        }

        return true;
    }

    /**
     * Builds a stable lookup key for a medication dose slot.
     *
     * @param medicationId the medication ID
     * @param scheduledDateTime the scheduled date and time
     * @return the lookup key
     */
    private String buildDoseSlotKey(int medicationId, LocalDateTime scheduledDateTime) {
        return medicationId + "|" + scheduledDateTime;
    }

    /**
     * Converts a dose log into a user-friendly sentence.
     *
     * @param doseLog the dose log to describe
     * @return a readable description of the dose log
     */
    public String doseLogToSentence(DoseLog doseLog) {
        if (doseLog == null) {
            return "No dose information is available.";
        }

        Medication medication = getMedicationById(doseLog.getMedId());

        String medicationName = medication == null
                ? "Unknown medication"
                : medication.getName();

        String scheduledTime = formatDateTime(doseLog.getScheduledTime());

        String takenTime = doseLog.getTakenTime();
        String statusDescription;

        if (takenTime == null || takenTime.trim().isEmpty()) {
            statusDescription = "It was not recorded as taken";
        } else {
            statusDescription =
                    "It was taken " + formatDateTime(takenTime);
        }

        return medicationName
                + " was scheduled for "
                + scheduledTime
                + ". "
                + statusDescription
                + ".";
    }

    /**
     * Converts an ISO date-time string into a readable date and time.
     *
     * @param dateTimeText an ISO date-time such as 2026-07-20T08:05
     * @return a user-friendly date and time
     */
    private String formatDateTime(String dateTimeText) {
        if (dateTimeText == null || dateTimeText.trim().isEmpty()) {
            return "an unknown time";
        }

        try {
            LocalDateTime dateTime =
                    LocalDateTime.parse(dateTimeText.trim());

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "MMMM d, yyyy 'at' h:mm a"
                    );

            return dateTime.format(formatter);
        } catch (DateTimeParseException exception) {
            return dateTimeText;
        }
    }
}
