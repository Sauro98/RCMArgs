package it.ivanodonadi;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Args {
	
	private String schema;
	private String[] args;
	private boolean valid = true;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Set<Character> argsFound = new HashSet<Character>();
	private Map<Character,Boolean> booleanArgs = new HashMap<Character,Boolean>();
	private Map<Character,String> stringArgs = new HashMap<Character,String>();
	private Map<Character,Integer> integerArgs = new HashMap<Character,Integer>();
	private int currentArgument;
	private char errorArgumentId = '\0';
	private String errorParameter = "TILT";
	
	enum ErrorCode {OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT};
	
	private ErrorCode errorCode = ErrorCode.OK;
	
	public Args(String schema, String[] args) throws ParseException{
		this.schema = schema;
		this.args = args;
		valid = parse();
	}
	
	private boolean parse() throws ParseException{
		if(schema.length() == 0 && args.length == 0 )
			return true;
		parseSchema();
		try{
			parseArguments();
		} catch (ArgsException e){
			
		}
		return valid;
	}
	
	private boolean parseSchema() throws ParseException{
		for(String element : schema.split(",")) {
			if(element.length() > 0){
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
		}
		return true;
	}
	
	private void parseSchemaElement(String element) throws ParseException{
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if(isBooleanSchemaElement(elementTail)) {
			parseBooleanSchemaElement(elementId);
		} else if (isStringSchemaElement(elementTail)){
			parseStringSchemaElement(elementId);
		} else if (isIntegerSchemaElement(elementTail)){
			parseIntegerSchemaElement(elementId);
		} else {
			throw new ParseException(String.format("Argument: %c has invalid format: %s.",elementId,elementTail),0);
		}
	}
	
	private void validateSchemaElementId(char elementId) throws ParseException{
		if(!Character.isLetter(elementId)){
			throw new ParseException("Bad character: " + elementId + " in Args format: " + schema,0);
		}
	}
	
	private void parseIntegerSchemaElement(char elementId){
		integerArgs.put(elementId, 0);
	}
	
	private void parseStringSchemaElement(char elementId) {
		stringArgs.put(elementId, "");
	}
	
	private void parseBooleanSchemaElement(char elementId) {
		booleanArgs.put(elementId,false);
	}
	
	private boolean isIntegerSchemaElement(String elementTail){
		return elementTail.equals("#");
	}
	
	private boolean isStringSchemaElement(String elementTail){
		return elementTail.equals("*");
	}
	
	private boolean isBooleanSchemaElement(String elementTail){
		return elementTail.length() == 0;
	}
	
	private boolean parseArguments() throws ArgsException{
		for(currentArgument = 0;  currentArgument < args.length; currentArgument++){
			String arg = args[currentArgument];
			parseArgument(arg);
		}
		return true;
	}
	
	private void parseArgument(String arg) throws ArgsException {
		if(arg.startsWith("-"))
			parseElements(arg);
	}
	
	private void parseElements(String arg) throws ArgsException {
		for(int a = 1; a < arg.length(); a++)
			parseElement(arg.charAt(a));
	}
	
	private void parseElement(char argChar) throws ArgsException {
		if(setArgument(argChar))
			argsFound.add(argChar);
		else{
			errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
			unexpectedArguments.add(argChar);
			valid = false;
		}
	}
	
	private boolean setArgument(char argChar) throws ArgsException{
		if(isBooleanArg(argChar)){
			setBooleanArg(argChar,true);
		} else if (isStringArg(argChar)){
			setStringArg(argChar,"");
		} else if (isIntArg(argChar)){
			setIntArg(argChar,0);
		} else 
			return false;
		return true;
	}
	
	private void setStringArg(char argChar, String s) throws ArgsException{
		currentArgument++;
		try{
			stringArgs.put(argChar,  args[currentArgument]);
		} catch (ArrayIndexOutOfBoundsException e){
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();
		}
	}
	
	private void setIntArg(char argChar, int i) throws ArgsException{
		currentArgument++;
		String parameter = null;
		try{
			parameter = args[currentArgument];
			integerArgs.put(argChar,new Integer(parameter));
		}catch(ArrayIndexOutOfBoundsException e){
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArgsException();
		} catch (NumberFormatException e){
			valid = false;
			errorArgumentId = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw new ArgsException();
		}
	}
	
	private void setBooleanArg(char argChar, boolean value) {
		booleanArgs.put(argChar,value);
	}
	
	private boolean isIntArg(char argChar) {
		return integerArgs.containsKey(argChar);
	}
	
	private boolean isBooleanArg(char argChar) {
		return booleanArgs.containsKey(argChar);
	}
	
	private boolean isStringArg(char argChar){
		return stringArgs.containsKey(argChar);
	}
	
	public int cardinality() {
		return argsFound.size();
	}
	
	public String usage() {
		if(schema.length() > 0) 
			return "-["+schema+"]";
		else
			return "";
	}
	
	public String errorMessage() throws Exception{
		switch(errorCode){
		case MISSING_STRING:
			return String.format("Could not find string parameter for -%c.", errorArgumentId);
		case MISSING_INTEGER:
			return String.format("Could not find integer parameter for -%c.", errorArgumentId);
		case INVALID_INTEGER:
			return String.format("Argument -%c expects an integer but was '%s'.", errorArgumentId,errorParameter);
		case UNEXPECTED_ARGUMENT:
			return unexpectedArgumentsMessage();
		case OK: 
			throw new Exception("TILT: Should not get here.");
		} 
		return "";
	}
	
	private String unexpectedArgumentsMessage() {
		StringBuffer message = new StringBuffer("Argument(s) -");
		for(char c : unexpectedArguments) {
			message.append(c);
		}
		message.append(" unexpected");
		return message.toString();
	}
	
	public boolean getBoolean(char arg) {
		return falseIfNull(booleanArgs.get(arg));
	}
	
	private boolean falseIfNull(Boolean b){
		return b == null ? false: b;
	}
	
	public String getString(char arg){
		return blankIfNull(stringArgs.get(arg));
	}
	
	private String blankIfNull(String s){
		return s == null ? "" : s;
	}
	
	public int getInteger(char arg){
		return zeroIfNull(integerArgs.get(arg));
	}
	
	private int zeroIfNull(Integer i){
		return i == null ? 0 : i;
	}
	
	public boolean has(char arg){
		return argsFound.contains(arg);
	}
	
	public boolean isValid() {
		return valid;
	}
	
	private class ArgsException extends Exception{
		
	}
}
