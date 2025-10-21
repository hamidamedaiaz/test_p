package fr.unice.polytech.sophiatecheats.domain.services.photoai;

import fr.unice.polytech.sophiatecheats.application.dto.photoai.PhotoAnalysisResult;

public interface PhotoAnalysisService {
    PhotoAnalysisResult analyze(byte[] imageData);
}
