package fr.unice.polytech.sophiatecheats.cucumber.config;

import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;

/**
 * Test configuration utility for Cucumber scenarios.
 *
 * Provides centralized access to test configuration and shared utilities
 * that can be used across different step definition classes.
 */
public class TestConfig {

    private static ApplicationConfig applicationConfig;
    private static TestDataManager testDataManager;

    /**
     * Get or create the application configuration for tests.
     * This ensures we have a consistent configuration across all test scenarios.
     */
    public static ApplicationConfig getApplicationConfig() {
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig();
        }
        return applicationConfig;
    }

    /**
     * Reset the application configuration.
     * Useful for ensuring clean state between test runs.
     */
    public static void resetConfiguration() {
        applicationConfig = null;
        testDataManager = null;
    }

    /**
     * Get the test data manager for creating consistent test data.
     */
    public static TestDataManager getTestDataManager() {
        if (testDataManager == null) {
            testDataManager = new TestDataManager();
        }
        return testDataManager;
    }
}
