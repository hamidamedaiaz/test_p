package fr.unice.polytech.sophiatecheats.cucumber.hooks;

import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Global hooks for Cucumber test scenarios.
 *
 * This class manages the lifecycle of test scenarios, providing setup and teardown
 * functionality that runs before and after each scenario.
 */
public class TestHooks {

    private static ApplicationConfig applicationConfig;

    @Before
    public void setUp(Scenario scenario) {
        try {
            // Initialize application configuration for each scenario
            applicationConfig = new ApplicationConfig();

            // Log scenario start for better debugging
            System.out.println("Starting scenario: " + scenario.getName());
        } catch (Exception e) {
            System.err.println("Error initializing ApplicationConfig: " + e.getMessage());
            throw new RuntimeException("Failed to initialize test configuration", e);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            // Clean up resources after each scenario
            applicationConfig = null;

            // Log scenario completion status
            if (scenario.isFailed()) {
                System.out.println("Scenario failed: " + scenario.getName());
            } else {
                System.out.println("Scenario completed successfully: " + scenario.getName());
            }
        } catch (Exception e) {
            System.err.println("Error during tearDown: " + e.getMessage());
        }
    }

    /**
     * Get the current ApplicationConfig instance.
     * This allows step definitions to access a shared configuration.
     *
     * @return the shared ApplicationConfig instance
     * @throws IllegalStateException if called before scenario setup
     */
    public static ApplicationConfig getApplicationConfig() {
        if (applicationConfig == null) {
            throw new IllegalStateException("ApplicationConfig not initialized. This should not happen during scenario execution.");
        }
        return applicationConfig;
    }
}
