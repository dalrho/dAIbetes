package org.example.daibetes.core.database;

import org.example.daibetes.shared.models.Appointment;
import java.util.List;

public interface ICalendarDAO extends GenericDAO<Appointment, Integer> {

    // Custom domain operations that are NOT in GenericDAO:
    List<Appointment> getAppointmentsByDoctor(int doctorId);
    List<Appointment> getAppointmentsByPatient(int patientId);
    boolean updateAppointmentStatus(int requestId, boolean accepted);
    List<String[]> getAllDoctors();
    boolean deleteAppointmentRequest(int requestId);
    int insertAppointmentRequest(int patientId, int doctorId, String requestedOn);
}
