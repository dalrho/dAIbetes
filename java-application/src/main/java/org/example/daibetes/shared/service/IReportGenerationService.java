package org.example.daibetes.shared.service;

import java.io.File;

public interface IReportGenerationService {
    int generateAndPersistReport(
            int patientId,
            int doctorId,
            File reportImageFile,
            String criticality,
            String doctorReasoning,
            String microaneurysms,
            String hemorrhages,
            String exudates,
            String cottonWoolSpots,
            String macularEdemaFinding,
            String venousBeading,
            String irma,
            String neovascularization,
            String vitreousHemorrhage,
            String retinalDetachment,
            String drGrade,
            String dmeGrade,
            boolean isAnnual,
            boolean isSixMonth,
            boolean isRefer,
            boolean isUrgent,
            boolean isLaser,
            boolean isVEGF,
            String notes
    ) throws Exception;
}
