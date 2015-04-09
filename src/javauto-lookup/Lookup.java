import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;

public class Lookup {
	/* define constants for different terminal colors */
	private static String RED = "\033[91m";
	private static String GREEN = "\033[92m";
	private static String YELLOW = "\033[93m";
	private static String BLUE = "\033[96m";
	private static String BOLD = "\033[1m";
	private static String NORMAL = "\033[0m";

	private static boolean nameSearch = false;
	private static boolean termSearch = false;
	private static boolean termSearchShort = false;
	private static boolean docDisplay = false;
	private static String javautoFile = "Javauto.java";

	private static String msg = "" + 
		"Usage: javauto-lookup [option] <search term>\n" +
		"Options:\n" +
		"-d\t--doc\t\tdisplay documentation for a function/variable\n" +
		"-t\t--term\t\tsearch by related term\n" +
		"-ts\t--term-short\tsame as the -t flag except output is truncated\n" +
		"-n\t--name\t\tsearch for a function/variable by all or part of a name"; 

	public static void main(String[] args) {

		/* check to see if colors are in use */
		boolean useColors = true;
		try {
			BufferedReader br = new BufferedReader(new FileReader("colors.conf"));
			StringBuilder data = new StringBuilder();
			String line = br.readLine();
			while (line!=null) {
				data.append(line);
				data.append('\n');
				line = br.readLine();
			}
			String fileData = data.toString().trim();
			br.close();
			int c = Integer.parseInt(fileData);
			if (c == 0)
				useColors = false;
		} catch(Exception e) {
		}
		if (!useColors) {
			RED = "";
			GREEN = "";
			YELLOW = "";
			BLUE = "";
			BOLD = "";
			NORMAL = "";
		}

		if (args.length == 0) {
			System.out.println(msg);
		}

		for (String arg : args) {
			if (arg.equals("-n") || arg.equals("--name")) {
				nameSearch = true;
				continue;
			}
			else if (arg.equals("-t") || arg.equals("--term")) {
				termSearch = true;
				continue;
			}
			else if (arg.equals("-ts") || arg.equals("--term-short")) {
				termSearchShort = true;
				continue;
			}
			else if (arg.equals("-d") || arg.equals("--doc")) {
				docDisplay = true;
				continue;
			}

			else if (docDisplay) {
				String jaString = resourceRead(javautoFile);
				/* each index is like [name, return type, javadoc, signatures] */
				ArrayList<String[]> funcData = getFuncData(jaString);
				/* each index is like [name, javadoc] */
				ArrayList<String[]> varData = getVarData(jaString);

				boolean found = false;
				for (String[] f : funcData) {
					if (f[0].toLowerCase().equals(arg.toLowerCase())) {
						displayFunction(f);
						found = true;
					}
				}
				for (String[] v : varData) {
					if (v[0].toLowerCase().equals(arg.toLowerCase())) {
						System.out.println(BOLD + v[0] + NORMAL);
						System.out.println(BLUE + "\t" + v[1].replaceAll("\n", "\n\t") + NORMAL);
						found = true;
					}
				}
				if (!found)
					System.out.println("No results found for: " + GREEN + arg + NORMAL);
				docDisplay = false;
			}
			else if (termSearch) {
				ArrayList<String[]> pResults = new ArrayList<String[]>();
				ArrayList<String[]> sResults = new ArrayList<String[]>();

				/* each index is like [name, return type, javadoc, signatures] */
				ArrayList<String[]> funcData = getFuncData(resourceRead(javautoFile));
				for (String[] f : funcData) {
					if (f[0].toLowerCase().startsWith(arg.toLowerCase()))
						pResults.add(f);
					else if (f[0].toLowerCase().contains(arg.toLowerCase()))
						sResults.add(f);
					else if (f[2].toLowerCase().contains(arg.toLowerCase()) || f[3].toLowerCase().contains(arg.toLowerCase()))
						sResults.add(f);
				}

				/* each index is like [name, javadoc] */
				boolean varDisplayed = false;
				funcData = getVarData(resourceRead(javautoFile));
				for (String[] f : funcData) {
					if (f[0].toLowerCase().contains(arg.toLowerCase())) {
						varDisplayed = true;
						System.out.println(BOLD + f[0] + NORMAL);
						System.out.println(BLUE + "\t" + f[1].replaceAll("\n", "\n\t") + NORMAL);
					}
					else if (f[1].toLowerCase().contains(arg.toLowerCase())) {
						varDisplayed = true;
						System.out.println(BOLD + f[0] + NORMAL);
						System.out.println(BLUE + "\t" + f[1].replaceAll("\n", "\n\t") + NORMAL);
					}
				}

				for (String[] func : pResults)
					displayFunction(func);
				for (String[] func : sResults)
					displayFunction(func);

				if (pResults.size() == 0 && sResults.size() == 0 && varDisplayed == false)
					System.out.println("No results found for: " + GREEN + arg + NORMAL);
				termSearch = false;
			}
			else if (termSearchShort) {
				ArrayList<String[]> pResults = new ArrayList<String[]>();
				ArrayList<String[]> sResults = new ArrayList<String[]>();

				/* each index is like [name, return type, javadoc, signatures] */
				ArrayList<String[]> funcData = getFuncData(resourceRead(javautoFile));
				for (String[] f : funcData) {
					if (f[0].toLowerCase().startsWith(arg.toLowerCase()))
						pResults.add(f);
					else if (f[0].toLowerCase().contains(arg.toLowerCase()))
						sResults.add(f);
					else if (f[2].toLowerCase().contains(arg.toLowerCase()) || f[3].toLowerCase().contains(arg.toLowerCase()))
						sResults.add(f);
				}

				/* each index is like [name, javadoc] */
				boolean varDisplayed = false;
				funcData = getVarData(resourceRead(javautoFile));
				for (String[] f : funcData) {
					if (f[0].toLowerCase().contains(arg.toLowerCase())) {
						varDisplayed = true;
						pResults.add(f);
					}
					else if (f[1].toLowerCase().contains(arg.toLowerCase())) {
						varDisplayed = true;
						sResults.add(f);
					}
				}

				for (String[] func : pResults)
					System.out.println(func[0]);
				for (String[] func : sResults)
					System.out.println(func[0]);

				if (pResults.size() == 0 && sResults.size() == 0 && varDisplayed == false)
					System.out.println("No results found for: " + GREEN + arg + NORMAL);
				termSearchShort = false;
			}

			/* if the user is performing a search just do that and then exit */
			else if (nameSearch) {
				String term = arg;

				/* list to hold our search results */
				ArrayList<String> pResults = new ArrayList<String>();
				ArrayList<String> sResults = new ArrayList<String>();
				ArrayList<String> results = new ArrayList<String>();

				/* get all the function and variable names from javauto file */
				ArrayList<String[]> funcNames  = getFunctions();
				String[] varNames = generateVariables().split("\n");

				/* search function names for ones that equal or begin with our term */
				for (String[] f : funcNames) {
					/* get the function name with only lower case letters/numbers */
					String func = f[0].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

					/* if it starts with/equals our search term add it to results */
					if (func.startsWith(term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()))
						pResults.add(f[0]);

					/* if it contains our search term add it to secondary results */
					else if (func.trim().contains(term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()))
						sResults.add(f[0]);
				}

				/* search class variables for ones that equal or begin with our term */
				for (String v : varNames) {
					/* get the variable name with only lower case letters/numbers */
					v = v.split("=")[0].trim();
					v = v.split(" ")[v.split(" ").length-1];
					String var = v.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

					/* if it starts with/equals our search term add it to primary results */
					if (var.trim().startsWith(term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()))
						pResults.add(v);

					/* if it contains our search term add it to secondary results */
					else if (var.trim().contains(term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()))
						sResults.add(v);
				}

				/* order with primary results first and secondary second (duh) */
				results.addAll(pResults);
				results.addAll(sResults);

				for (String r : results) {
					System.out.println(highlight(term, r, YELLOW, NORMAL));
				}

				if (results.size() == 0)
					System.out.println("No results found for: " + GREEN + arg + NORMAL);
			}
			else {
				System.out.println(msg);
				System.exit(1);
			}
		}

	}

	private static void displayFunction(String[] data) {
		/* each index is like [name, return type, javadoc, signatures] */
		System.out.println(BOLD + data[0] + NORMAL);
		System.out.println(BLUE + data[2].replaceAll("\n", "\n\t").split("@param")[0].split("@return")[0].trim() + NORMAL);
		System.out.println("\t" + GREEN + data[3].replaceAll("\n", "\n\t") + NORMAL);
		System.out.println("\tReturn type:  " + YELLOW + data[1].trim() + NORMAL);
		if (!data[1].equals("void"))
			System.out.println("\tReturn value: " + YELLOW + data[2].split("@return")[1].trim() + NORMAL);
		System.out.println("");
	}

	private static ArrayList<String[]> getVarData(String funcFileContents) {
		/* ArrayList to hold variable data, each item having format [var name, description] */
		ArrayList<String[]> varData = new ArrayList<String[]>();
		String[] lines = funcFileContents.split("\n");
		for (int i = lines.length-1; i >= 0; i--) {
			/* if it looks like a function declaration and the previous line has an ending javadoc comment */
			if (lines[i].matches("^\\s*public\\s+[\\S+\\s+]*\\s+(\\w+)\\s*=.*;$") && lines[i-1].matches(".*\\*/\\s*$")) {

				/* while we haven't found the start of the javadoc comment */
				int j = i-1;
				while (!lines[j].matches("^\\s*/\\*\\*.*$"))
					j--;

				/* get the javadoc comment */
				String jdComment = "";
				for (int f = j; f < i; f++)
					jdComment = jdComment + "\n" + lines[f];
				jdComment = jdComment.replaceAll("\n\\s*\\*/\\s*", "").replaceAll("\n\\s*\\*\\s*", "\n").replaceAll("^\\s*/\\*\\*\\s*", "");

				/* search for function parts pattern in the string */
				String regexp = "^\\s*public\\s+[\\S+\\s+]*\\s+(\\w+)\\s*=.*;$";
				Pattern pattern = Pattern.compile(regexp);
				Matcher matcher = pattern.matcher(lines[i]);

				matcher.find();
				varData.add(new String[] {matcher.group(1), jdComment});
			}
		}

		return varData;
	}

	/**
	 * Returns data about each function defined in an ArrayList<String[]>
	 * Where each index is like [name, return type, javadoc, signatures]
	 */
	private static ArrayList<String[]> getFuncData(String funcFileContents) {
		/* ArrayList to hold function data in the form [name, return type, javadoc, signatures] */
		ArrayList<String[]> funcData = new ArrayList<String[]>();
		String[] lines = funcFileContents.split("\n");

		for (int i = lines.length-1; i >= 0; i--) {
			/* if it looks like a function declaration and the previous line has an ending javadoc comment */
			if (lines[i].matches("^\\s*(public).*\\s+(\\S+)\\s+((\\w+)\\s*\\(.*\\)).*$") && lines[i-1].matches(".*\\*/\\s*$")) {

				/* while we haven't found the start of the javadoc comment */
				int j = i-1;
				while (!lines[j].matches("^\\s*/\\*\\*.*$"))
					j--;

				/* get the javadoc comment */
				String jdComment = "";
				for (int f = j; f < i; f++)
					jdComment = jdComment + "\n" + lines[f];
				jdComment = jdComment.replaceAll("\n\\s*\\*/\\s*", "").replaceAll("\n\\s*\\*\\s*", "\n").replaceAll("^\\s*/\\*\\*\\s*", "");

				/* search for function parts pattern in the string */
				String regexp = "^\\s*(public|private).*\\s+(\\S+)\\s+((\\w+)\\s*\\(.*\\)).*$";
				Pattern pattern = Pattern.compile(regexp);
				Matcher matcher = pattern.matcher(lines[i]);

				/* get function name, return type, and signature */
				matcher.find();
				String name = matcher.group(4);
				String returnType = matcher.group(2);
				String signature = matcher.group(3);

				/* figure out if our function is already in the data and we're adding a new signature/doc comment */
				boolean contains = false;
				for (int k = 0; k < funcData.size(); k++) {
					/* get the data for this index */
					String[] f = funcData.get(k);

					/* if we found our current function in there */
					if (f[0].equals(name)) {
						String[] newF = new String[4];
						newF = f;

						/* add the signature */
						newF[3] = newF[3] + "\n" + signature;

						/* if the found javadoc is more expansive */
						if (jdComment.length() > newF[2].length()) {
							newF[2] = jdComment;
						}

						/* update the data */
						funcData.set(k, newF);

						/* we found it so we're done with this one */
						contains = true;
					}
				}

				/* if it's not in there just create a new entry */
				if (!contains)
					funcData.add(new String[] {name, returnType, jdComment, signature});
			}
		}
		return funcData;
	}

	/**
	 * Search the javautoFile for all class variables and return them
	 * @return class variables that have been generated
	 */
	private static String generateVariables() {
		/* get raw file contents of the file containing our variables */
		String[] variablesContents = resourceRead(javautoFile).split("\n");

		/* variable to store our generated class vars */
		String classVars = "";

		/* add each line that has a class varaible to our classVars */
		for (String line: variablesContents) {
			if ( (line.trim().startsWith("public ") || line.trim().startsWith("private ")) && line.trim().endsWith(";"))
				classVars = classVars + line + "\n";
		}

		/* return our class vars */
		return classVars;
	}

	/**
	 * Gets a list of functions and their source code from the javautoFile
	 * @return ArrayList<String[]> with each element containing {function name, function code}
	 */
	private static ArrayList<String[]> getFunctions() {
		/* define the list where we'll store all the data */
		ArrayList<String[]> functionDataList = new ArrayList<String[]>(); 

		/* get raw file contents of the file containing our functions */
		String functionContents = resourceRead(javautoFile);

		/* split into lines for evaluation */
		String[] functionContentsLines = functionContents.split("\n");

		/* check each line and extract function names */
		for (String line: functionContentsLines) {
			/* if the line is like "public *{" or "private *{" but isn't like "public class" */
			if ((line.trim().startsWith("public ") || line.trim().startsWith("private ")) && (line.trim().endsWith("{")) && (!line.trim().startsWith("public class"))) {
				/* if it meets the above criteria it's a function & we add it to the list */

				/* get function name & code */
				String functionName = line.trim().split(" ")[2].split("[(]")[0];
				String functionCode = getFunctionCode(line, functionContents);

				/* modify the function code so that the declaration becomes static */
				String[] lines = functionCode.split("\n");
				lines[0] = lines[0].trim();
				if (lines[0].startsWith("private ")) {
					lines[0] = "\tprivate static " + lines[0].substring(8);
				} else if (lines[0].startsWith("public ")) {
					lines[0] = "\tpublic static " + lines[0].substring(7);
				}
				functionCode = "";
				for (String l : lines)
					functionCode = functionCode + l + "\n";

				/* add it to the list unless it's the "run" function used in a thread*/
				if (!functionName.toLowerCase().equals("run")) {
					String[] function = {functionName.trim(), functionCode};
					functionDataList.add(function);
				}
			}
		}

		/* return our list without duplicates */
		return combineFunctionDuplicates(functionDataList);
	}

	/**
	 * Get the code for a single function from a java file of functions based off its signature
	 * @param signature All or part of the function signature, eg. "public void function(int i)"
	 * If there are two declarations with the same name and you only provide "public void functionName" 
	 * it will return the first one. To get a specific one include the full signature like "public void functionName(int i, int j)"
	 * @param fullFunctionsText The contents of a java file that contains the function we're trying to extract
	 * @return Full text of the single function we're trying to find
	 */
	private static String getFunctionCode(String signature, String wholeFile) {
		/* find the index of the function */ 
		int funcIndex = wholeFile.indexOf(signature);

		/* trim the file so that it starts at this index */
		wholeFile = wholeFile.substring(funcIndex);

		/* file as string -> file as char array */
		char[] wholeFileChars = wholeFile.toCharArray();

		/* variables to hold position and brace counts */
		int index      = 0;
		int openBrace  = 0; 
		int closeBrace = 0;

		/* find the opening bracket of function */
		while (openBrace == 0) {

			/* if we find the brace we're done */
			if (wholeFileChars[index] == '{') {
				openBrace++;
				index++;
			}

			/* check for different kinds of comments */
			else if (wholeFileChars[index] == '/') {
				index++;

				/* if it's // comment until end of line */
				if (wholeFileChars[index] == '/') {
					while (wholeFileChars[index] != '\n') {
						index++;
					}
					index++;
				}

				/* if it's a /* comment until * / */
				else if (wholeFileChars[index] == '*') {
					index++;
					boolean done = false;
					while (done == false) {
						if (wholeFileChars[index] == '*') {
							index++;
							if (wholeFileChars[index] == '/') {
								index++;
								done = true;
							}
						} else {
							index++;
						}
					}
				}
			}

			/* if it's some other character just keep going */
			else {
				index++;
			}
		}

		while (openBrace > closeBrace) {
			/* if we find a brace then increment */
			if (wholeFileChars[index] == '{') {
				openBrace++;
				index++;
			} else if (wholeFileChars[index] == '}') {
				closeBrace++;
				index++;
			}

			/* check for string literals */
			else if (wholeFileChars[index] == '"') {
				index++;
				while (wholeFileChars[index] != '"') {
					if (wholeFileChars[index] == '\\') {
						index++;
					}
					index++;
				}
				index++;
			}

			/* check for character literals */
			else if (wholeFileChars[index] == '\'') {
				index++;
				while (wholeFileChars[index] != '\'') {
					if (wholeFileChars[index] == '\\') {
						index++;
					}
					index++;
				}
				index++;
			}

			/* check for different kinds of comments */
			else if (wholeFileChars[index] == '/') {
				index++;

				/* if it's // comment until end of line */
				if (wholeFileChars[index] == '/') {
					while (wholeFileChars[index] != '\n') {
						index++;
					}
					index++;
				}

				/* if it's a /* comment until * / */
				else if (wholeFileChars[index] == '*') {
					index++;
					boolean done = false;
					while (done == false) {
						if (wholeFileChars[index] == '*') {
							index++;
							if (wholeFileChars[index] == '/') {
								index++;
								done = true;
							}
						} else {
							index++;
						}
					}
				}
			}

			/* if it's some other character just keep going */
			else {
				index++;
			}

		}

		return wholeFile.substring(0, index+1);
	}

	/**
	 * Take an array list with each item having a format {function name, function code}
	 * and combine duplicate function names into one entry that still has the source code of each
	 * so an array list containing two elements like this: {doSomething, code1}, {doSomething, code2}
	 * would become {doSomething, code1\n\ncode2}
	 * combineFunctionDuplicates() is necessary because some of our functions are defined multiple
	 * times to allow for different parameters and this allows us to add all iterations of a function at once.
	 *
	 * @param functionListWithDuplicates the list to run through
	 * @return a version of the list without duplicates but with all the code
	 */
	private static ArrayList<String[]> combineFunctionDuplicates( ArrayList<String[]> functionListWithDuplicates ) {
		/* define our final list that wont have duplicates */
		ArrayList<String[]> functionListNoDuplicates = new ArrayList<String[]>();

		/* list to keep track of function names that have already been combined */
		ArrayList<String> alreadyCombined = new ArrayList<String>();

		for (String[] f : functionListWithDuplicates) {
			/* the name of the function is stored in the first indice */
			String functionName = f[0];

			/* if we haven't already combined the function */
			if (!alreadyCombined.contains(functionName)) {
				/* the combined code of all functions of the same name */
				String combinedCode = "";

				/* check every item in the list */
				for (String[] function : functionListWithDuplicates) {
					/* if the names match add the code in */
					if (functionName.equals(function[0])) {
						combinedCode = combinedCode + function[1] + "\n\n";
					}
				}

				/* add the function to the list */
				String[] combinedFunction = {functionName, combinedCode};
				functionListNoDuplicates.add(combinedFunction);

				/* mark the name as covered */
				alreadyCombined.add(functionName); 
			}
		}

		/* return our final results */
		return functionListNoDuplicates;
	}

	/**
	 * Reads file contents (from a resource inside the JAR) into string
	 * @param resourcePath the path to the resource within the JAR file
	 * @return file contents as string
	 */
	private static String resourceRead(String resourcePath) {
		try {
			InputStream inputStream = Lookup.class.getResourceAsStream(resourcePath);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				stringBuilder.append(line);
				if (line != null) {
					stringBuilder.append("\n");
				}
				line = bufferedReader.readLine();
			}
			return stringBuilder.toString();
		} catch (Exception e) {
			System.out.println("Compiler encountered an error reading JAR resource \"" + resourcePath + "\" -- the JAR file may be corrupt.");
			e.printStackTrace();
			System.exit(1);
			return "";
		}
	}

	/**
	 * Highlight a term inside of a string.
	 * @param term the term to highlight (ingores anything thats not a letter or number)
	 * @param context the string in which the term is found and highlighted
	 * @param startHighlight the chracter to use to "highlight" the beginning
	 * @param endHighlight the character to end the highlighting with
	 * @return the highlighted string
	 */
	private static String highlight(String term, String context, String startHighlight, String endHighlight) {
		/* convert to lower case and replace non word characters with spaces so that indexes remain the same */
		String indexedCon = context.replaceAll("[^a-zA-Z0-9]", " ").toLowerCase();

		/* get our term without non words */
		String stripTerm = term.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

		/* build a regex to find the starting and ending indexes of our term */
		String regexp = "";
		String[] termParts = stripTerm.split("");
		for (int i = 1; i < termParts.length; i++) {
			regexp = regexp + termParts[i] + "\\s*";
		}
		regexp = regexp.substring(0, regexp.length()-3);

		/* search for our pattern in the string */
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(indexedCon);

		/* get start and end indexes of our term inside the string */
		ArrayList<Integer[]> startEnd = new ArrayList<Integer[]>();
		while (matcher.find())
			startEnd.add(new Integer[] {matcher.start(), matcher.end()});

		/* craft our highlighted result */
		int shiftLen = 0;
		String res = context;
		for (Integer[] coords : startEnd) {
			int start = coords[0] + shiftLen;
			int end = coords[1] + shiftLen;
			res = res.substring(0, start) + startHighlight + res.substring(start, end) + endHighlight + res.substring(end, res.length());
			shiftLen = shiftLen + startHighlight.length() + endHighlight.length();
		}

		return res;
	}

}
