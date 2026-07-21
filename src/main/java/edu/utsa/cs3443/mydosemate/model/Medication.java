package edu.utsa.cs3443.mydosemate.model;

public class Medication {
    private String medication_id;
    private String name;
    private int dosage;
    private String unit;
    private String frequency;
    private int times_a_day;
    private String start_date;
    private String notes;

    public Medication(String medication_id, String name, int dosage, String unit, String frequency, int times_a_day, String start_date, String notes) {
        this.medication_id = medication_id;
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.frequency = frequency;
        this.times_a_day = times_a_day;
        this.start_date = start_date;
        this.notes = notes;
    }

    // getters
    public String getMedicationId() {return medication_id;}
    public String getName() {return name;}
    public int getDosage() {return dosage;}
    public String getUnit() {return unit;}
    public String getFrequency() {return frequency;}
    public int getTimesADay() {return times_a_day;}
    public String getStartDate() {return start_date;}
    public String getNotes() {return notes;}
    // setters
    public void setMedicationId(String medication_id) {this.medication_id = medication_id;}
    public void setName(String name) {this.name = name;}
    public void setDosage(int dosage) {this.dosage = dosage;}
    public void setUnit(String unit) {this.unit = unit;}
    public void setFrequency(String frequency) {this.frequency = frequency;}
    public void setTimesADay(int times_a_day) {this.times_a_day = times_a_day;}
    public void setStartDate(String start_date) {this.start_date = start_date;}
    public void setNotes(String notes) {this.notes = notes;}
}
