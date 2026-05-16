package org.example.daibetes.shared.models;

import java.time.LocalDate;

/**
 * Domain model for a consultation request viewed as a calendar appointment.
 * Backed by tblConsultationRequest joined with tblTests and tblDoctor/tblUser.
 *
 * Status logic:
 *   responded_on = NULL                        → PENDING  (gray)
 *   responded_on NOT NULL + is_accepted = true  → ACCEPTED (green)
 *   responded_on NOT NULL + is_accepted = false → REJECTED (red)
 */
public class Appointment {


    public enum Status { PENDING, ACCEPTED, REJECTED }

    private int       requestId;
    private int       patientId;
    private int       doctorId;
    private String    patientName;
    private String    doctorName;
    private LocalDate date;
    private String    time;
    private Status    status;

    public Appointment(int requestId, int patientId, int doctorId,
                       String patientName, String doctorName,
                       LocalDate date, String time, Status status) {
        this.requestId   = requestId;
        this.patientId   = patientId;
        this.doctorId    = doctorId;
        this.patientName = patientName;
        this.doctorName  = doctorName;
        this.date        = date;
        this.time        = time;
        this.status      = status;
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int       getRequestId()   { return requestId; }
    public int       getPatientId()   { return patientId; }
    public int       getDoctorId()    { return doctorId; }
    public String    getPatientName() { return patientName; }
    public String    getDoctorName()  { return doctorName; }
    public LocalDate getDate()        { return date; }
    public String    getTime()        { return time; }
    public Status    getStatus()      { return status; }

    // Convenience helpers used by the controller
    public boolean   isAccepted()     { return status == Status.ACCEPTED; }
    public boolean   isPending()      { return status == Status.PENDING; }
    public boolean   isRejected()     { return status == Status.REJECTED; }

    public String    getStatusLabel() {
        return switch (status) {
            case ACCEPTED -> "Accepted";
            case REJECTED -> "Rejected";
            default       -> "Pending";
        };
    }

    public String    getStatusColor() {
        return switch (status) {
            case ACCEPTED -> "#27AE60";
            case REJECTED -> "#E74C3C";
            default       -> "#888888";
        };
    }
}