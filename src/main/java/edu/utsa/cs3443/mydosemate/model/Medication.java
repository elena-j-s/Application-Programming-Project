package edu.utsa.cs3443.mydosemate.model;

public class Medication {
    private int medication_id;
    private String name;
    private double dosage;
    private String unit;
    private String frequency;
    private int times_per_day;
    private String scheduled_times;
    private String start_date;
    private int current_amount;
    private String notes;

    public Medication(int medication_id, String name, double dosage, String unit, String frequency, int times_per_day, String scheduled_times, String start_date, int current_amount, String notes) {
        this.medication_id = medication_id;
        this.name = name;
        this.dosage = dosage;
        this.unit = unit;
        this.frequency = frequency;
        this.times_per_day = times_per_day;
        this.scheduled_times = scheduled_times;
        this.start_date = start_date;
        this.current_amount = current_amount;
        this.notes = notes;
    }

    // getters
    public int getMedicationId() {return medication_id;}
    public String getName() {return name;}
    public double getDosage() {return dosage;}
    public String getUnit() {return unit;}
    public String getFrequency() {return frequency;}
    public int getTimesPerDay() {return times_per_day;}
    public String getScheduledTimes() {return scheduled_times;}
    public String getStartDate() {return start_date;}
    public int getCurrentAmount() {return current_amount;}
    public String getNotes() {return notes;}
    // setters
    public void setMedicationId(int medication_id) {this.medication_id = medication_id;}
    public void setName(String name) {this.name = name;}
    public void setDosage(double dosage) {this.dosage = dosage;}
    public void setUnit(String unit) {this.unit = unit;}
    public void setFrequency(String frequency) {this.frequency = frequency;}
    public void setTimesPerDay(int times_a_day) {this.times_per_day = times_a_day;}
    public void setScheduledTimes(String scheduled_times) {this.scheduled_times = scheduled_times;}
    public void setStartDate(String start_date) {this.start_date = start_date;}
    public void setCurrentAmount(int current_amount) {this.current_amount = current_amount;}
    public void setNotes(String notes) {this.notes = notes;}

    @Override
    public String toString() {
        String displayTimes;

        if (scheduled_times == null || scheduled_times.trim().isEmpty()) {
            displayTimes = "No scheduled time";
        } else {
            displayTimes = scheduled_times.replace(";", " and ");
        }

        return name + " — " + formatDosage() + " " + unit + " — " + displayTimes;
    }

    public String toCsvRow() {
        return medication_id + ","
                + formatForCsv(name) + ","
                + formatDosage() + ","
                + formatForCsv(unit) + ","
                + formatForCsv(frequency) + ","
                + times_per_day + ","
                + formatForCsv(scheduled_times) + ","
                + formatForCsv(start_date) + ","
                + current_amount + ","
                + formatForCsv(notes);
    }

    //a helper method
    private String formatForCsv(String value) {
        if (value == null) {
            return "";
        }

        String formattedValue = value.replace("\"", "\"\"");

        if (formattedValue.contains(",") || formattedValue.contains("\"") || formattedValue.contains("\n") || formattedValue.contains("\r")) {

            return "\"" + formattedValue + "\"";
        }

        return formattedValue;
    }

    private String formatDosage() {
        if (dosage == Math.rint(dosage)) {
            return String.valueOf((long) dosage);
        }

        return String.valueOf(dosage);
    }

    public boolean useOneDose() {
        if (current_amount <= 0) {
            return false;
        }

        current_amount--;
        return true;
    }
}
