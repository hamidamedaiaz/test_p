package fr.unice.polytech.sophiatecheats.cucumber;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Main Cucumber test runner - Unified configuration with comprehensive reporting.
 * Runs all Cucumber scenarios with proper step definitions, hooks and generates multiple report formats.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value =
    "pretty," +
    "html:target/cucumber-reports/html-report.html," +
    "json:target/cucumber-reports/cucumber-report.json," +
    "junit:target/cucumber-reports/cucumber-report.xml")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value =
    "fr.unice.polytech.sophiatecheats.cucumber.stepdefs," +
    "fr.unice.polytech.sophiatecheats.cucumber.hooks")
public class CucumberTestsRunner {
}
