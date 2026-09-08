package de.interactive_instruments.shapechange.engine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@code SbvrRuleLoader} (which reads SBVR constraint rules from an Excel
 * file) lives in shapechange-core, not shapechange-engine, and is loaded
 * reflectively by {@code ModelImpl.loadInformationFromExternalSources()}.
 * This verifies that on shapechange-engine's own classpath - where
 * shapechange-core, and hence {@code SbvrRuleLoader}, is not present - a
 * config setting the {@code constraintExcelFile} input parameter degrades
 * gracefully (a logged error, run completes) rather than crashing the run.
 */
class SbvrRuleLoaderAbsentTest {

    @Test
    void constraintExcelFileWithoutCoreOnClasspathDegradesGracefully(@TempDir Path tempDir) throws IOException {

	Path logFile = tempDir.resolve("log.xml");

	Main.main(new String[] { //
		"-c", "src/test/resources/customtargettest/config_with_constraint_excel_file.xml", //
		"-x", "$outputDir$", tempDir.toString(), //
		"-x", "$logFile$", logFile.toString() });

	// the run must still complete and produce the target's output
	Path outputFile = tempDir.resolve("INPUT").resolve("classNames.txt");
	assertTrue(Files.exists(outputFile), "target should still run even though SBVR loading was skipped");

	String log = Files.readString(logFile);
	assertTrue(log.contains("SBVR rule loader"), "log should report that the SBVR rule loader was unavailable");
    }
}
