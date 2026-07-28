package edu.utsa.cs3443.mydosemate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class DoseLog {
    private int log_id;
    private int med_id;
    private String scheduled_time;
    private String taken_time;
    private String dose_amount;
    private String status;

    public DoseLog(int log_id, int med_id, String scheduled_time, String taken_time, String dose_amount, String status) {
        this.log_id = log_id;
        this.med_id = med_id;
        this.scheduled_time = scheduled_time;
        this.taken_time = taken_time;
        this.dose_amount = dose_amount;
        this.status = status;
    }

    // getters
    public int getLogId() {return log_id;}
    public int getMedId() {return med_id;}
    public String getScheduledTime() {return scheduled_time;}
    public String getTakenTime() {return taken_time;}
    public String getDoseAmount() {return dose_amount;}
    public String getStatus() {return status;}
    // setters
    public void setLogId(int log_id) {this.log_id = log_id;}
    public void setMedId(int med_id) {this.med_id = med_id;}
    public void setScheduledTime(String scheduled_time) {this.scheduled_time = scheduled_time;}
    public void setTakenTime(String taken_time) {this.taken_time = taken_time;}
    public void setDoseAmount(String dose_amount) {this.dose_amount = dose_amount;}
    public void setStatus(String status) {this.status = status;}

    /**
     * Parses {@code scheduled_time} as a full timestamp.
     * <p>
     * Stored as an ISO-8601 string (e.g. {@code 2026-07-28T08:00}) combining the
     * calendar date the dose was due with its scheduled time-of-day, so history
     * can be sorted and filtered by day.
     *
     * @return the scheduled timestamp, or {@code null} if it cannot be parsed
     */
    public LocalDateTime getScheduledDateTime() {
        return parseDateTime(scheduled_time);
    }

    /**
     * Parses {@code taken_time} as a full timestamp.
     *
     * @return the timestamp the dose was actually taken, or {@code null} if the
     *         dose was never taken (or the value cannot be parsed)
     */
    public LocalDateTime getTakenDateTime() {
        return parseDateTime(taken_time);
    }

    /**
     * Returns the calendar date this dose log entry belongs to, based on
     * {@code scheduled_time}.
     *
     * @return the scheduled date, or {@code null} if it cannot be parsed
     */
    public LocalDate getScheduledDate() {
        LocalDateTime scheduledDateTime = getScheduledDateTime();
        return scheduledDateTime == null ? null : scheduledDateTime.toLocalDate();
    }

    /**
     * Returns whether this dose was actually taken (as opposed to skipped or missed).
     *
     * @return {@code true} if {@code taken_time} holds a valid timestamp
     */
    public boolean isTaken() {
        return getTakenDateTime() != null;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value.trim());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        String displayTakenTime;

        if (taken_time == null || taken_time.trim().isEmpty()) {
            displayTakenTime = "not taken";
        } else {
            displayTakenTime = taken_time;
        }

        return "Log " + log_id + " (med " + med_id + ") — scheduled " + scheduled_time + ", taken " + displayTakenTime + " — " + status;
    }

    public String toCsvRow() {
        return log_id + ","
                + med_id + ","
                + formatForCsv(scheduled_time) + ","
                + formatForCsv(taken_time) + ","
                + formatForCsv(dose_amount) + ","
                + formatForCsv(status);
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
}