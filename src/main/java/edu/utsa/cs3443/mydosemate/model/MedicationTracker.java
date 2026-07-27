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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationTracker {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path MEDICATION_FILE =  DATA_DIR.resolve("medications.csv");
    private static final String CSV_HEADER = "med_id,name,dosage,unit,frequency,times_per_day," + "scheduled_times,start_date,current_amount,notes";
    private static final int EXPECTED_COLUMN_COUNT = 10;
    private final ArrayList<Medication> medications;
    private final History history;

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

    public void loadMedications() throws IOException {
        loadMedications(MEDICATION_FILE);
    }

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

    public void saveMedications() throws IOException {
        saveMedications(MEDICATION_FILE);
    }

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

    public Medication getMedicationById(int medication_id) {
        int medicationIndex = findMedicationIndexById(medication_id);

        if (medicationIndex == -1) {
            return null;
        }

        return medications.get(medicationIndex);
    }

    public int generateNextMedicationId() {
        int largestId = 0;

        for (Medication medication : medications) {
            if (medication.getMedicationId() > largestId) {
                largestId = medication.getMedicationId();
            }
        }

        return largestId + 1;
    }

    public List<Medication> getMedications() {
        return new ArrayList<Medication>(medications);
    }

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

        return true;
    }

    public boolean skipMedication(int medication_id) {
        return getMedicationById(medication_id) != null;
    }

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

    private int findMedicationIndexById(int medicationId) {
        for (int index = 0; index < medications.size(); index++) {
            if (medications.get(index).getMedicationId() == medicationId) {
                return index;
            }
        }

        return -1;
    }

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

    private boolean containsMedicationId(List<Medication> medicationList, int medicationId) {
        for (Medication medication : medicationList) {
            if (medication.getMedicationId() == medicationId) {
                return true;
            }
        }

        return false;
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public History getHistory() {
        return history;
    }
}
