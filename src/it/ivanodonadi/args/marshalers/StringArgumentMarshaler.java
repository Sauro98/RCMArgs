package it.ivanodonadi.args.marshalers;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import it.ivanodonadi.args.exceptions.ArgsException;

public class StringArgumentMarshaler implements ArgumentMarshaler {

	private String stringValue = "";
	
	@Override
	public void set(ListIterator<String> currentArgument) throws ArgsException {
		try {
			stringValue = currentArgument.next();
		}catch (NoSuchElementException e) {
			throw new ArgsException(ArgsException.ErrorCode.MISSING_STRING);
		}
	}
	
	public static String getValue(ArgumentMarshaler am) {
		if(am != null && am instanceof StringArgumentMarshaler) {
			return ((StringArgumentMarshaler) am).stringValue;
		}
		return "";
	}
	
}
