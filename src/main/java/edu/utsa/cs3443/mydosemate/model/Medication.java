package edu.utsa.cs3443.mydosemate.model;

/**
 * Represents a medication entry in the application.
 * <p>
 * This class stores the medication's identifying information, dosage details,
 * schedule, remaining amount, and optional notes.
 */
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

    /**
     * Creates a new medication with the given values.
     *
     * @param medication_id the medication ID
     * @param name the medication name
     * @param dosage the dosage amount
     * @param unit the dosage unit
     * @param frequency the frequency description
     * @param times_per_day the number of times the medication is taken per day
     * @param scheduled_times the scheduled times for the medication
     * @param start_date the medication start date
     * @param current_amount the current remaining amount
     * @param notes optional notes for the medication
     */
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

    /**
     * Returns the medication ID.
     *
     * @return the medication ID
     */
    public int getMedicationId() {return medication_id;}

    /**
     * Returns the medication name.
     *
     * @return the medication name
     */
    public String getName() {return name;}

    /**
     * Returns the dosage amount.
     *
     * @return the dosage
     */
    public double getDosage() {return dosage;}

    /**
     * Returns the dosage unit.
     *
     * @return the dosage unit
     */
    public String getUnit() {return unit;}

    /**
     * Returns the frequency description.
     *
     * @return the frequency
     */
    public String getFrequency() {return frequency;}

    /**
     * Returns the number of times this medication is taken per day.
     *
     * @return times per day
     */
    public int getTimesPerDay() {return times_per_day;}

    /**
     * Returns the scheduled times for this medication.
     *
     * @return the scheduled times
     */
    public String getScheduledTimes() {return scheduled_times;}

    /**
     * Returns the start date of this medication.
     *
     * @return the start date
     */
    public String getStartDate() {return start_date;}

    /**
     * Returns the current remaining amount.
     *
     * @return the current amount
     */
    public int getCurrentAmount() {return current_amount;}

    /**
     * Returns any notes associated with this medication.
     *
     * @return the notes
     */
    public String getNotes() {return notes;}

    // setters

    /**
     * Sets the medication ID.
     *
     * @param medication_id the medication ID
     */
    public void setMedicationId(int medication_id) {this.medication_id = medication_id;}

    /**
     * Sets the medication name.
     *
     * @param name the medication name
     */
    public void setName(String name) {this.name = name;}

    /**
     * Sets the dosage amount.
     *
     * @param dosage the dosage
     */
    public void setDosage(double dosage) {this.dosage = dosage;}

    /**
     * Sets the dosage unit.
     *
     * @param unit the dosage unit
     */
    public void setUnit(String unit) {this.unit = unit;}

    /**
     * Sets the frequency description.
     *
     * @param frequency the frequency
     */
    public void setFrequency(String frequency) {this.frequency = frequency;}

    /**
     * Sets the number of times per day.
     *
     * @param times_a_day the times per day
     */
    public void setTimesPerDay(int times_a_day) {this.times_per_day = times_a_day;}

    /**
     * Sets the scheduled times.
     *
     * @param scheduled_times the scheduled times
     */
    public void setScheduledTimes(String scheduled_times) {this.scheduled_times = scheduled_times;}

    /**
     * Sets the start date.
     *
     * @param start_date the start date
     */
    public void setStartDate(String start_date) {this.start_date = start_date;}

    /**
     * Sets the current remaining amount.
     *
     * @param current_amount the current amount
     */
    public void setCurrentAmount(int current_amount) {this.current_amount = current_amount;}

    /**
     * Sets the notes.
     *
     * @param notes the notes
     */
    public void setNotes(String notes) {this.notes = notes;}

    /**
     * Returns a user-friendly string representation of this medication.
     *
     * @return the formatted medication summary
     */
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

    /**
     * Converts this medication into a CSV row.
     *
     * @return the medication formatted as a CSV record
     */
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

    /**
     * Formats a value for safe inclusion in a CSV row.
     * <p>
     * Null values are converted to empty strings. Values containing commas,
     * quotes, or line breaks are wrapped in quotes, and embedded quotes are
     * escaped by doubling them.
     *
     * @param value the value to format
     * @return the CSV-safe value
     */
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

    /**
     * Formats the dosage so whole numbers do not display a trailing {@code .0}.
     *
     * @return the formatted dosage string
     */
    private String formatDosage() {
        if (dosage == Math.rint(dosage)) {
            return String.valueOf((long) dosage);
        }

        return String.valueOf(dosage);
    }

    /**
     * Uses one dose from the current amount if available.
     *
     * @return {@code true} if one dose was used; {@code false} if none remained
     */
    public boolean useOneDose() {
        if (current_amount <= 0) {
            return false;
        }

        current_amount--;
        return true;
    }
}
