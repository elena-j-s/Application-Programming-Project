package edu.utsa.cs3443.mydosemate.model;

import java.time.LocalDateTime;

/**
 * Represents one scheduled medication dose on the daily dashboard.
 */
public class ScheduledDose {

    /** The current state of a scheduled dose. */
    public enum Status {
        /** The dose has been recorded as taken. */
        TAKEN,
        /** The scheduled time passed without a recorded dose. */
        MISSED,
        /** The dose is scheduled but not yet due. */
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

    /**
     * Returns the medication assigned to this schedule slot.
     *
     * @return the scheduled medication
     */
    public Medication getMedication() {
        return medication;
    }

    /**
     * Returns when this dose is scheduled.
     *
     * @return the scheduled date and time
     */
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Returns the current state of this dose.
     *
     * @return the dose status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns when this dose was taken, when applicable.
     *
     * @return the taken time, or {@code null} if no taken time was recorded
     */
    public LocalDateTime getTakenTime() {
        return takenTime;
    }
}
