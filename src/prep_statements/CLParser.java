package prep_statements;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * parses command line options using Apache Commons CLI 1.31
 * 
 * @author Daniel May, Martin Weber
 * @version 20160224.1
 *
 */
public class CLParser {

	private CommandLine cl;
	private Options opt;
	private HelpFormatter formatter;
	private String password;
	private Properties prop;

	/**
	 * constructor
	 */
	public CLParser(String[] args) {
		loadProperties();
		setUpOptions();
		formatter = new HelpFormatter();
		parseOptions(args);
	}

	/**
	 * setting up the command line options
	 */
	private void setUpOptions() {
		opt = new Options();
		opt.addOption(Option.builder("d").argName("database-name").desc("database name to connect to").hasArg()
				.longOpt("database").numberOfArgs(1).build());
		opt.addOption(Option.builder("H").argName("hostname").desc("database server host").hasArg().longOpt("host")
				.numberOfArgs(1).build());
		opt.addOption(Option.builder("p").argName("port").desc("database server port").hasArg().longOpt("port")
				.numberOfArgs(1).build());
		opt.addOption(Option.builder("u").argName("username").desc("database user name").hasArg().longOpt("user")
				.numberOfArgs(1).build());

		OptionGroup pwGroup = new OptionGroup();
		pwGroup.addOption(Option.builder("w").argName("password").desc("pass password as command line argument")
				.hasArg().longOpt("password").numberOfArgs(1).build());
		pwGroup.addOption(Option.builder("W").desc("force password prompt").longOpt("password-prompt").build());
		opt.addOptionGroup(pwGroup);
	}

	private void loadProperties() {
		try (FileReader reader = new FileReader("src/statements.properties")) {
			prop = new Properties();
			prop.load(reader);
		} catch (FileNotFoundException e) {
			System.out.println("Properties File doesn't exist");
			e.printStackTrace();
		} catch (IOException e1) {
			System.out.println("Can't read Property File");
			e1.printStackTrace();
		}

	}

	/**
	 * Parse command line arguments. If a ParseException occurs, the error
	 * message and the usage will be printed. The application will be terminated
	 * in that case.
	 * 
	 * @param args
	 *            command line anrguments
	 */
	private void parseOptions(String[] args) {
		DefaultParser parser = new DefaultParser();
		try {
			cl = parser.parse(opt, args);
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			printUsage();
			System.exit(-1);
		}
		interrogate();
	}

	/**
	 * Simple wrapper method in order to print the help message including the
	 * usage.
	 */
	private void printUsage() {
		System.out.println(
				"Every option which is not defined with the CLI arguments will be filled with default values from the Property File");
		formatter.printHelp("preps", opt, true);
	}

	/**
	 * Detects if a password prompt is forced and executes the prompt.
	 */
	private void interrogate() {
		if (cl.hasOption('W')) {
			Console console = System.console();
			/*
			 * stackoverflow helped me
			 * http://stackoverflow.com/questions/8138411/masking-password-input
			 * -from-the-console-java
			 */
			if (console == null) {
				System.out.println("Couldn't get console instance!");
				System.exit(-1);
			}
			/*
			 * %n is a line separator; %s is for unicode characters
			 */
			password = new String(console.readPassword("Please enter your password: "));
		}
	}

	/**
	 * Gets the database specified by the command line.
	 * 
	 * @return the specified database
	 */
	public String getDatabase() {
		if (cl.hasOption("d"))
			return cl.getOptionValue('d');
		else {
			if (!prop.containsKey("database")) {
				System.err.println("Missing option database");
				System.exit(-1);
				return "";
			} else {
				return prop.getProperty("database");
			}
		}

	}

	/**
	 * Gets the hostname specified by the command line.
	 * 
	 * @return the specified hostname
	 */
	public String getHost() {
		if (cl.hasOption("H"))
			return cl.getOptionValue('H');
		else {
			if (!prop.containsKey("host")) {
				System.err.println("Missing option host");
				System.exit(-1);
				return "";
			} else {
				return prop.getProperty("host");
			}
		}
	}

	/**
	 * Gets the username specified by the command line.
	 * 
	 * @return the specified username
	 */
	public String getUser() {
		if (cl.hasOption("u"))
			return cl.getOptionValue('u');
		else {
			if (!prop.containsKey("user")) {
				System.err.println("Missing option user");
				System.exit(-1);
				return "";
			} else {
				return prop.getProperty("user");
			}
		}
	}

	/**
	 * Detects if the password was specified as argument or entered via password
	 * prompt and returns it.
	 * 
	 * @return the specified password
	 */
	public String getPassword() {
		if (cl.hasOption('w'))
			return cl.getOptionValue('w');
		else if (cl.hasOption("W"))
			return password;
		else {
			if (!prop.containsKey("password")) {
				System.err.println("Missing option password");
				System.exit(-1);
				return "";
			} else {
				return prop.getProperty("password");
			}
		}
	}

	/**
	 * Gets the port if specified by the command line, otherwise the standard
	 * port for PSQL 5432 will be used. If the specified port number is not an
	 * integer the usage will be printed and the application will be terminated.
	 * 
	 * @return the specified port
	 */
	public int getPort() {
		if (cl.hasOption("p")) {
			try {
				return Integer.parseInt(cl.getOptionValue('p'));
			} catch (NumberFormatException nfe) {
				printUsage();
				System.exit(-1);
				return 0;
			}
		} else {
			if (!prop.containsKey("port")) {
				return 5432;
			} else {
				try {
					return Integer.parseInt(prop.getProperty("port"));
				} catch (NumberFormatException nfe) {
					System.exit(-1);
					return 0;
				}
			}
		}

	}

	/**
	 * Sets the password field to null.
	 */
	public void clearPassword() {
		password = null;
	}
}