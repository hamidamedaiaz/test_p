package fr.unice.polytech.sophiatecheats;

import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de validation de l'architecture Clean mise en place.
 * Vérifie que la structure de base fonctionne correctement.
 */
class ArchitectureValidationTest {

  @Test
  void shouldBootstrapApplicationCorrectly() {
    // Given
    SophiaTechEatsApplication app = new SophiaTechEatsApplication();

    // When
    ApplicationConfig config = app.getConfig();

    // Then
    assertThat(config).isNotNull();
    assertThat(app).isNotNull();
  }

  @Test
  void shouldCreateApplicationConfigSuccessfully() {
    // When
    ApplicationConfig config = new ApplicationConfig();

    // Then
    assertThat(config).isNotNull();
  }

  @Test
  void shouldRunApplicationWithoutErrors() {
    // Given
    SophiaTechEatsApplication app = new SophiaTechEatsApplication();

    // When
    app.run();

    // Then
    // Ensure the application bootstrapped correctly
    assertThat(app.getConfig())
            .as("ApplicationConfig should be initialized after running the app")
            .isNotNull();
  }

}
