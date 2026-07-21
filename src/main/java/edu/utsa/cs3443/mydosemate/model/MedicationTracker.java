package edu.utsa.cs3443.mydosemate.model;

import java.nio.file.Path;
import java.util.ArrayList;

public class MedicationTracker {

    private User user;
    private final ArrayList<Medication> medications;

    public MedicationTracker() {
        medications = new ArrayList<>();
    }

    public void loadMedications(Path medications_csv) {

    }

    public void saveMedications(Path medications_csv) {
    }

    public void addMedication(Medication medication) {
    }

    public void removeMedication(Medication medication) {
    }

    public void updateMedication(Medication medication) {
    }

    public void takeMedication(Medication medication) {
    }

    public void skipMedication(Medication medication) {
    }

    public ArrayList<Medication> getMedications() {
        return medications;
    }
}
