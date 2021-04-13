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
			return "You are not returning any value in a non-void method. Make sure you include return statements on every possible code branch, or change your method return type to 'void'";
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
		if (description.matches("(Method|Class|Constructor|Private field) (.*) is never used")) {
			Pattern p = Pattern.compile("(Method|Class|Constructor|Private field) (.*) is never used");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String param = m.group(1);
				String userInput = m.group(2);
				return "Your " + param + " " + userInput + " is not used anywhere. Check where you wanted to use it!";
			}
			return "Your class is not used anywhere. Check where you wanted to use it!";
		}

		if(description.matches("Parameter (.*) is never used")){

			Pattern p = Pattern.compile("Parameter (.*) is never used");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String symbol = m.group(1);
				return "Your " + symbol + " is never used anywhere. Check whether you need it.";
			}
			return "Your parameter is never used anywhere. Check whether you need it.";
		}

		if(description.matches("Cannot return a value from a method with void result type")){
			return "You are trying to return something from a void method. Change the method type to a class matching the return value if you need to return something.";
		}

		if(description.matches("Condition is always true")){
			return "The condition is always true. There is no need for checking something if it's always true.";
		}

		if(description.matches("(.*) statement has empty body")){
			Pattern p = Pattern.compile("(.*) statement has empty body");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String symbol = m.group(1);
				return "Your " + symbol + " statement is not doing anything. Check if you need your " + symbol + " statement and add contents within its curly braces.";
			}
			return "Your statement is not doing anything. Check if you need your statement.";
		}

		if(description.matches("Variable (.*) might not have been initialized")){
			Pattern p = Pattern.compile("Variable (.*) might not have been initialized");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String symbol = m.group(1);
				return "Your variable" + symbol + " is never initialized. You should check " + symbol + ". The variable is missing a set value.";
			}
			return "Your variable is never initialized.The variable is missing a set value.";

		}

		if(description.matches("Variable (.*) is never assigned")){
			Pattern p = Pattern.compile("Variable (.*) is never assigned");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String symbol = m.group(1);
				return "Your variable" + symbol + " is missing a set value. You need to initialize the variable before using it.";
			}
			return "Your variable is missing a set value. You need to initialize the variable before using it.";

		}

		if(description.matches("(.*) expected")){
			Pattern p = Pattern.compile("(.*) expected");
			Matcher m = p.matcher(description);
			if (m.find()) {
				String symbol = m.group(1);
				return "You are missing a " + symbol;
			}
			return "You are missing a ';' or '('";
		}

		//unused import statement
		if(description.matches("Unused import statement")){
			return "The code you imported is never used. Please double check if and where you want to use it.";
		}
		
		// default
		return "Seems like there is no detailed description for your error. Maybe you can check the web.";
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
