package com.example.codecompanion.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used to get explanations of error messages
 */
public class CompilerMessageCatalogue {

	/**
	 * Tries to find and return our custom (short) explanation of a message by its original description
	 *
	 * @param description the description of the error message, as seen in the IntelliJ compiler
	 * @return our custom explanation for the message, in short form
	 * TODO extract all the hardcoded strings and explanations into strings.xml and maybe provide German and English versions
	 */
	public static String getShortExplanationByDescription(String description) {
		// error Missing return statement
		if (description.contains("Missing return statement")) {
			return "You are not returning any value in a non-void method";
		}

		/*
		 *	error Cannot resolve symbol x
		 *  see here for detailed comments on how to add new entries
		 */
		// first, check if the description matches the error you want to add
		if (description.matches("Cannot resolve symbol .*")) {
			// try to get the custom user input using regex
			Pattern p = Pattern.compile("Cannot resolve symbol (.*)");
			Matcher m = p.matcher(description);
			if (m.find()) {
				// groups are the things within round brackets () in our regex pattern
				String symbol = m.group(1); // in this case, we are only interested in the first match
				return "The symbol " + symbol + " cannot be interpreted. Check if you are within the " +
						" scope of a valid class or function";
			}

			// fallback if we somehow do not match anything
			return "The symbol cannot be interpreted. Check if you are within the " +
					" scope of a valid class or function";
		}

		// warning Class x is never used
		if (description.matches("(Method|Class|Constructor) (.*) is never used")) {
			Pattern p = Pattern.compile("(Method|Class|Constructor) (.*) is never used");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String param = m.group(1);
				String userInput = m.group(2);
				return "Your " + param + " " + userInput + " is not used anywhere. Check where you wanted to use it!";
			}
			return "Your class is not used anywhere. Check where you wanted to use it!";
		}

		// default
		return "placeholder-short";
	}

	/**
	 * Tries to find and return our custom (long) explanation of a message by its original description
	 *
	 * @param description the description of the error message, as seen in the IntelliJ compiler
	 * @return our custom explanation for the message, in long form
	 */
	public static String getLongExplanationByDescription(String description) {
		if (description.contains("Missing return statement")) {
			return "Very long description of why you should return something here";
		}
		return "placeholder-long";
	}
}
