package de.interactive_instruments.shapechange.engine.customtarget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.SortedSet;
import java.util.TreeSet;

import de.interactive_instruments.shapechange.core.Options;
import de.interactive_instruments.shapechange.core.RuleRegistry;
import de.interactive_instruments.shapechange.core.ShapeChangeAbortException;
import de.interactive_instruments.shapechange.core.ShapeChangeResult;
import de.interactive_instruments.shapechange.core.model.ClassInfo;
import de.interactive_instruments.shapechange.core.model.Model;
import de.interactive_instruments.shapechange.core.model.PackageInfo;
import de.interactive_instruments.shapechange.core.target.Target;

/**
 * Minimal {@link Target} implementation used by
 * {@code MainCustomTargetTest} to verify that a project depending only on
 * shapechange-engine can implement its own target and run it via
 * {@link de.interactive_instruments.shapechange.engine.Main#main(String[])},
 * without shapechange-core on the classpath.
 * <p>
 * Writes the sorted names of the processed classes to a text file, one name
 * per line.
 */
public class ClassNameListTarget implements Target {

    private final SortedSet<String> classNames = new TreeSet<>();
    private String outputDirectory;
    private String outputFilename;
    private ShapeChangeResult result;

    @Override
    public void registerRulesAndRequirements(RuleRegistry r) {
	// no custom rules or requirements
    }

    @Override
    public void initialise(PackageInfo pi, Model m, Options o, ShapeChangeResult r, boolean diagOnly)
	    throws ShapeChangeAbortException {
	result = r;
	outputDirectory = o.parameter(this.getClass().getName(), "outputDirectory");
	outputFilename = o.parameter(this.getClass().getName(), "outputFilename");
	if (outputFilename == null) {
	    outputFilename = "classNames";
	}
    }

    @Override
    public void process(ClassInfo ci) {
	classNames.add(ci.name());
    }

    @Override
    public void write() {
	try {
	    Path dir = Path.of(outputDirectory);
	    Files.createDirectories(dir);
	    Files.write(dir.resolve(outputFilename + ".txt"), classNames);
	} catch (IOException e) {
	    result.addError("ClassNameListTarget could not write its output file: " + e.getMessage());
	}
    }

    @Override
    public String getTargetName() {
	return "Class Name List Target (test)";
    }

    @Override
    public String getTargetIdentifier() {
	return "cnlt";
    }

    @Override
    public String getDefaultEncodingRule() {
	return "*";
    }
}
