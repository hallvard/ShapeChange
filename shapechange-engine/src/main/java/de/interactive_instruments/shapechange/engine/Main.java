/**
 * ShapeChange - processing application schemas for geographic information
 *
 * This file is part of ShapeChange. ShapeChange takes a ISO 19109
 * Application Schema from a UML model and translates it into a
 * GML Application Schema or other implementation representations.
 *
 * Additional information about the software can be found at
 * http://shapechange.net/
 *
 * (c) 2002-2012 interactive instruments GmbH, Bonn, Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact:
 * interactive instruments GmbH
 * Bundeskanzlerplatz 2d
 * 53113 Bonn
 * Germany
 */

package de.interactive_instruments.shapechange.engine;

import java.lang.Runtime.Version;

import de.interactive_instruments.shapechange.core.Converter;
import de.interactive_instruments.shapechange.core.Options;
import de.interactive_instruments.shapechange.core.ShapeChangeAbortException;
import de.interactive_instruments.shapechange.core.ShapeChangeResult;

/**
 * Command line entry point for the ShapeChange engine. Unlike
 * {@code de.interactive_instruments.shapechange.app.Main}, this class has no
 * GUI: a configuration file must be given on the command line, and there is
 * no dialog fallback.
 */
public class Main {

    /**
     * The main method to run the ShapeChange engine.
     *
     * @param argv the command line arguments
     */
    public static void main(String argv[]) {

	try {

	    Options options = new Options();
	    ShapeChangeResult result = new ShapeChangeResult(options);

	    Version javaVersion = Runtime.version();
	    if (javaVersion.feature() < 21) {
		result.addProcessFlowError(null, 18, javaVersion.toString());
		System.exit(1);
	    }

	    Converter converter = new Converter(options, result);

	    // process arguments
	    String arg = null;
	    for (int i = 0; i < argv.length; i++) {
		arg = argv[i];
		if (arg.startsWith("-")) {
		    String option = arg.substring(1);
		    if (option.equals("h")) {
			printUsage();
			System.exit(1);
		    }
		    if (option.equals("c")) {
			if (++i == argv.length) {
			    result.addProcessFlowError(null, 111, "-c");
			} else
			    options.configFile = argv[i];
			continue;
		    }
		    if (option.equals("x")) {
			String x1 = null, x2 = null;
			if (++i == argv.length)
			    result.addProcessFlowError(null, 111, "-x");
			else {
			    x1 = argv[i];
			    if (++i == argv.length)
				result.addProcessFlowError(null, 111, "-x");
			    else
				x2 = argv[i];
			}
			if (x2 != null)
			    options.setReplaceValue(x1, x2);
			continue;
		    }
		}
	    }

	    if (options.configFile == null) {
		printUsage();
		System.exit(1);
	    }

	    options.loadConfiguration();
	    converter.convert();

	} catch (ShapeChangeAbortException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
    } // main(String[])

    /**
     * Prints the usage information for the application.
     */
    protected static void printUsage() {

	System.err.println("ShapeChange engine command line interface");
	System.err.println();
	System.err.println("ShapeChange takes a ISO 19109 application schema");
	System.err.println("from a UML model and translates it into a GML application");
	System.err.println("schema or other implementation representations");
	System.err.println();
	System.err.println("usage: java -jar shapechange-engine.jar (options) modelfile");
	System.err.println();
	System.err.println("options:");
	System.err.println(" -c cfgfile The location of the main configuration");
	System.err.println("            file. XInclude is supported and can be used");
	System.err.println("            to modularise the confguration. The default is");
	System.err.println("            http://shapechange.net/resources/config/minimal.xml.");
	System.err.println(" -x val rep If a configuration file contains a parameter");
	System.err.println("            with a value of 'val' then the value will be");
	System.err.println("            replaced by 'rep'. This option may occur multiple");
	System.err.println("            times.");
	System.err.println("            Example: -x '$dir$' './result/xsd' would replace.");
	System.err.println("            any parameter values '$dir$' in the configuration.");
	System.err.println("            file with './result/xsd'.");
	System.err.println(" -h         This help screen.");
	System.err.println();

    } // printUsage()

}
