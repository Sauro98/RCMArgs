package it.ivanodonadi.args.marshalers;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import it.ivanodonadi.args.exceptions.ArgsException;


public class IntegerArgumentMarshaler implements ArgumentMarshaler {

	private int integerValue;

	@Override
	public void set(ListIterator<String> currentArgument) throws ArgsException {
		String parameter = null;
		try {
			parameter = currentArgument.next();
			integerValue = Integer.parseInt(parameter);
		} catch (NoSuchElementException e) {
			throw new ArgsException(ArgsException.ErrorCode.MISSING_INTEGER);
		} catch (NumberFormatException e) {
			throw new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER, parameter);
		}
	}
	
	public static int getValue(ArgumentMarshaler am) {
		if(am != null && am instanceof IntegerArgumentMarshaler) {
			return ((IntegerArgumentMarshaler) am).integerValue;
		}
		return 0;
	}
	
}