package fr.unice.polytech.sophiatecheats.infrastructure.external;

import fr.unice.polytech.sophiatecheats.application.dto.photoai.PhotoAnalysisResult;
import fr.unice.polytech.sophiatecheats.domain.services.photoai.PhotoAnalysisService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MockAIPhotoAnalysisServiceTest {
    @Test
    void testAnalyzeReturnsExpectedResult() {
        PhotoAnalysisService service = new MockAIPhotoAnalysisService();
        byte[] dummyImage = new byte[]{1,2,3};
        PhotoAnalysisResult result = service.analyze(dummyImage);
        assertEquals("Plat appétissant avec légumes frais.", result.getDescription());
        assertEquals("plat", result.getCategory());
        assertTrue(result.getTags().contains("végétarien"));
        assertTrue(result.getTags().contains("sans gluten"));
    }

    @Test
    void testStatsServiceRecordsAndRetrievesStats() {
        PhotoAnalysisStatsService stats = new PhotoAnalysisStatsService();
        stats.recordAnalysis("plat", true);
        stats.recordAnalysis("plat", false);
        assertEquals(2, stats.getAnalysisCount("plat"));
        assertTrue(stats.getPrecision("plat") <= 1.0);
    }
}

