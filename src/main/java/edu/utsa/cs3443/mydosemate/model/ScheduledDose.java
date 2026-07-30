package edu.utsa.cs3443.mydosemate.model;

import java.time.LocalDateTime;

/**
 * Represents one scheduled medication dose on the daily dashboard.
 */
public class ScheduledDose {

    /** The current state of a scheduled dose. */
    public enum Status {
        TAKEN,
        MISSED,
        UPCOMING
    }

    private final Medication medication;
    private final LocalDateTime scheduledTime;
    private final Status status;
    private final LocalDateTime takenTime;

    /**
     * Creates a dashboard entry for one medication schedule slot.
     *
     * @param medication the medication scheduled for this slot
     * @param scheduledTime the date and time the dose is scheduled
     * @param status the current dose status
     * @param takenTime the time the dose was taken or {@code null}
     */
    public ScheduledDose(
            Medication medication,
            LocalDateTime scheduledTime,
            Status status,
            LocalDateTime takenTime
    ) {
        this.medication = medication;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.takenTime = takenTime;
    }

    public Medication getMedication() {
        return medication;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getTakenTime() {
        return takenTime;
    }
}
