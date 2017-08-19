package it.ivanodonadi.args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import it.ivanodonadi.args.exceptions.ArgsException;
import it.ivanodonadi.args.marshalers.*;


public class Args {
	
	private Map<Character,ArgumentMarshaler> marshalers;
	private Set<Character> argsFound;
	private ListIterator<String> currentArgument;
	
	public Args(String schema, String[] args) throws ArgsException{
		marshalers = new HashMap<Character,ArgumentMarshaler>();
		argsFound = new HashSet<Character>();
		
		parseSchema(schema);
		parseArgumentStrings(Arrays.asList(args));
	}
	
	private boolean parseSchema(String schema) throws ArgsException{
		for(String element : schema.split(",")) {
			if(element.length() > 0) {
				parseSchemaElement(element.trim());
			}
		}
		return true;
	}
	
	private void parseSchemaElement(String element) throws ArgsException{
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if(elementTail.length() == 0)
			marshalers.put(elementId, new BooleanArgumentMarshaler());
		else if(elementTail.equals("*"))
			marshalers.put(elementId, new StringArgumentMarshaler());
		else if(elementTail.equals("#"))
			marshalers.put(elementId, new IntegerArgumentMarshaler());
		else if(elementTail.equals("##"))
			marshalers.put(elementId, new DoubleArgumentMarshaler());
		else if(elementTail.equals("[*]"))
			marshalers.put(elementId, new StringArrayArgumentMarshaler());
		else
			throw new ArgsException(ArgsException.ErrorCode.INVALID_FORMAT,elementId,elementTail);
	}
	
	private void validateSchemaElementId(char elementId) throws ArgsException{
		if(!Character.isLetter(elementId)) {
			throw new ArgsException(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME,elementId,null);
		}
	}

	private boolean parseArgumentStrings(List<String> argsList) throws ArgsException{
		for(currentArgument = argsList.listIterator(); currentArgument.hasNext();) {
			String currArg = currentArgument.next();
			if(currArg.startsWith("-")) {
				parseArgumentCharacters(currArg.substring(1));
			} else {
				currentArgument.previous();
				break;
			}
		}
		return true;
	}
	
	
	private void parseArgumentCharacters(String arg) throws ArgsException{
		for(int a = 0; a < arg.length(); a++) {
			parseArgumentCharacter(arg.charAt(a));
		}
	}
	
	private void parseArgumentCharacter(char argChar) throws ArgsException {
		ArgumentMarshaler am = marshalers.get(argChar);
		if(am == null) throw new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT,argChar,null);
		argsFound.add(argChar);
		try {
			am.set(currentArgument);
		} catch (ArgsException e) {
			e.setErrorArgumentId(argChar);
			throw e;
		}
	}
	
	public int cardinality() {
		return argsFound.size();
	}
	
	public boolean getBoolean(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		return BooleanArgumentMarshaler.getValue(am);
	}
	
	public String getString(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		return StringArgumentMarshaler.getValue(am);
	}
	
	public int getInteger(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		return IntegerArgumentMarshaler.getValue(am);
	}
	
	public double getDouble(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		return DoubleArgumentMarshaler.getValue(am);
	}
	
	public String[] getStringArray(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		return StringArrayArgumentMarshaler.getValue(am);
	}
	
	public boolean has(char arg) {
		return argsFound.contains(arg);
	}
	
}