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
	private Map<Character,ArgumentMarshaler> marshalers = new HashMap<Character,ArgumentMarshaler>();
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
			marshalers.put(elementId,new BooleanArgumentMarshaler());
		} else if (isStringSchemaElement(elementTail)){
			marshalers.put(elementId,new StringArgumentMarshaler());
		} else if (isIntegerSchemaElement(elementTail)){
			marshalers.put(elementId,new IntegerArgumentMarshaler());
		} else {
			throw new ParseException(String.format("Argument: %c has invalid format: %s.",elementId,elementTail),0);
		}
	}
	
	private void validateSchemaElementId(char elementId) throws ParseException{
		if(!Character.isLetter(elementId)){
			throw new ParseException("Bad character: " + elementId + " in Args format: " + schema,0);
		}
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
		ArgumentMarshaler am = marshalers.get(argChar); 
		try{
			if(am instanceof BooleanArgumentMarshaler)
				setBooleanArg(am);
			else if (am instanceof StringArgumentMarshaler)
				setStringArg(am);
			else if (am instanceof IntegerArgumentMarshaler)
				setIntArg(am);
			else 
				return false;
		}catch (ArgsException e){
			valid = false;
			errorArgumentId = argChar;
			throw e;
		}
		return true;
	}
	
	private void setStringArg(ArgumentMarshaler am) throws ArgsException{
		currentArgument++;
		try{
			String parameter = args[currentArgument];
			am.set(parameter);
		} catch (ArrayIndexOutOfBoundsException e){
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();
		}
	}
	
	private void setIntArg(ArgumentMarshaler am) throws ArgsException{
		currentArgument++;
		String parameter = null;
		try{
			parameter = args[currentArgument];
			am.set(parameter);
		}catch(ArrayIndexOutOfBoundsException e){
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArgsException();
		} catch (ArgsException e){
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw e;
		}
	}
	
	private void setBooleanArg(ArgumentMarshaler am) {
		try{
			am.set("true");
		} catch (ArgsException e){
			
		}
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
		Args.ArgumentMarshaler am = marshalers.get(arg);
		try{
			return am != null && (Boolean)am.get();
		} catch (ClassCastException e){
			return false;
		}
	}
	
	public String getString(char arg){
		Args.ArgumentMarshaler am = marshalers.get(arg);
		try{
			return am == null ? "" : (String)am.get();
		} catch (ClassCastException e) {
			return "";
		}
	}
	
	public int getInteger(char arg){
		ArgumentMarshaler am = marshalers.get(arg);
		try{
			return am == null ? 0 : (Integer)am.get();
		} catch (Exception e){
			return 0;
		}
	}
	
	public boolean has(char arg){
		return argsFound.contains(arg);
	}
	
	public boolean isValid() {
		return valid;
	}
	
	private class ArgsException extends Exception{
		
	}
	
	private abstract class ArgumentMarshaler{
		public abstract void set(String s) throws ArgsException;
		public abstract Object get();
	}
	
	private class BooleanArgumentMarshaler extends ArgumentMarshaler{
		private boolean booleanValue = false;
		public void set(String s){
			booleanValue = true;
		}
		public Object get(){
			return booleanValue;
		}
	}
	
	private class StringArgumentMarshaler extends ArgumentMarshaler{
		private String stringValue = "";
		@Override
		public void set(String s) {
			stringValue = s;
		}

		@Override
		public Object get() {
			return stringValue;
		}
		
	}
	
	private class IntegerArgumentMarshaler extends ArgumentMarshaler{
		private int integerValue = 0;
		@Override
		public void set(String s) throws ArgsException {
			try{
				integerValue = Integer.parseInt(s);
			} catch (NumberFormatException e){
				throw new ArgsException();
			}
		}

		@Override
		public Object get() {
			return integerValue;
		}
		
	}
	
}
