package it.ivanodonadi.args.marshalers;

import java.util.ListIterator;

import it.ivanodonadi.args.exceptions.ArgsException;

public class BooleanArgumentMarshaler implements ArgumentMarshaler{
	private boolean booleanValue = false;

	@Override
	public void set(ListIterator<String> currentArgument) throws ArgsException {
		booleanValue = true;	
	}
	
	public static boolean getValue(ArgumentMarshaler am) {
		if(am != null && am instanceof BooleanArgumentMarshaler) {
			return ((BooleanArgumentMarshaler) am).booleanValue; 
		}
		return false;
	}
	
}