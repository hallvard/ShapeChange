package de.interactive_instruments.shapechange.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end check that a project depending only on shapechange-engine (not
 * shapechange-core) can implement its own
 * {@link de.interactive_instruments.shapechange.core.target.Target} and run
 * it against an SCXML model by invoking {@link Main#main(String[])}, the
 * same entry point used from the command line.
 * <p>
 * The custom target is {@code customtarget.ClassNameListTarget}; the input
 * model is the {@code scxml_valid.xml} fixture already used by
 * {@code XSDUtilTest}.
 */
class MainCustomTargetTest {

    @Test
    void customTargetRunsViaMain(@TempDir Path tempDir) throws IOException {

	Path logFile = tempDir.resolve("log.xml");

	Main.main(new String[] { //
		"-c", "src/test/resources/customtargettest/config.xml", //
		"-x", "$outputDir$", tempDir.toString(), //
		"-x", "$logFile$", logFile.toString() });

	// Converter appends the model provider id (default "INPUT") to outputDirectory
	Path outputFile = tempDir.resolve("INPUT").resolve("classNames.txt");
	assertTrue(Files.exists(outputFile), "custom target should have written its output file");

	List<String> classNames = Files.readAllLines(outputFile);
	assertEquals(List.of("CodeList", "DataType", "DataType2", "Enumeration", "FeatureType1", "FeatureType2",
		"NilUnion", "Union"), classNames);
    }
}
