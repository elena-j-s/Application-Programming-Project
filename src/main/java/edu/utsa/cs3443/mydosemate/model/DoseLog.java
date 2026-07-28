package edu.utsa.cs3443.mydosemate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a single dose history entry.
 * <p>
 * A dose log stores the medication ID, scheduled time, taken time, dose amount,
 * and status for a specific recorded medication event.
 */
public class DoseLog {
    private int log_id;
    private int med_id;
    private String scheduled_time;
    private String taken_time;
    private String dose_amount;
    private String status;

    /**
     * Creates a new dose log entry.
     *
     * @param log_id the log ID
     * @param med_id the medication ID
     * @param scheduled_time the scheduled time as an ISO-8601 date-time string
     * @param taken_time the actual taken time as an ISO-8601 date-time string, or blank if not taken
     * @param dose_amount the dose amount description
     * @param status the log status, such as {@code taken} or {@code skipped}
     */
    public DoseLog(int log_id, int med_id, String scheduled_time, String taken_time, String dose_amount, String status) {
        this.log_id = log_id;
        this.med_id = med_id;
        this.scheduled_time = scheduled_time;
        this.taken_time = taken_time;
        this.dose_amount = dose_amount;
        this.status = status;
    }

    // getters

    /**
     * Returns the log ID.
     *
     * @return the log ID
     */
    public int getLogId() {return log_id;}

    /**
     * Returns the medication ID associated with this log.
     *
     * @return the medication ID
     */
    public int getMedId() {return med_id;}

    /**
     * Returns the scheduled time string.
     *
     * @return the scheduled time
     */
    public String getScheduledTime() {return scheduled_time;}

    /**
     * Returns the taken time string.
     *
     * @return the taken time
     */
    public String getTakenTime() {return taken_time;}

    /**
     * Returns the dose amount string.
     *
     * @return the dose amount
     */
    public String getDoseAmount() {return dose_amount;}

    /**
     * Returns the log status.
     *
     * @return the status
     */
    public String getStatus() {return status;}

    // setters

    /**
     * Sets the log ID.
     *
     * @param log_id the log ID
     */
    public void setLogId(int log_id) {this.log_id = log_id;}

    /**
     * Sets the medication ID.
     *
     * @param med_id the medication ID
     */
    public void setMedId(int med_id) {this.med_id = med_id;}

    /**
     * Sets the scheduled time string.
     *
     * @param scheduled_time the scheduled time
     */
    public void setScheduledTime(String scheduled_time) {this.scheduled_time = scheduled_time;}

    /**
     * Sets the taken time string.
     *
     * @param taken_time the taken time
     */
    public void setTakenTime(String taken_time) {this.taken_time = taken_time;}

    /**
     * Sets the dose amount string.
     *
     * @param dose_amount the dose amount
     */
    public void setDoseAmount(String dose_amount) {this.dose_amount = dose_amount;}

    /**
     * Sets the status string.
     *
     * @param status the status
     */
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

    /**
     * Parses a date-time string into a {@link LocalDateTime}.
     *
     * @param value the date-time string to parse
     * @return the parsed date-time, or {@code null} if the value is blank or invalid
     */
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

    /**
     * Returns a readable string representation of this dose log.
     *
     * @return the formatted dose log summary
     */
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

    /**
     * Converts this dose log into a CSV row.
     *
     * @return the dose log formatted as a CSV record
     */
    public String toCsvRow() {
        return log_id + ","
                + med_id + ","
                + formatForCsv(scheduled_time) + ","
                + formatForCsv(taken_time) + ","
                + formatForCsv(dose_amount) + ","
                + formatForCsv(status);
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
}
