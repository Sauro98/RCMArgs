package it.ivanodonadi.args.marshalers;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import it.ivanodonadi.args.exceptions.ArgsException;


public class DoubleArgumentMarshaler implements ArgumentMarshaler {

	private double doubleValue;
	
	@Override
	public void set(ListIterator<String> currentArgument) throws ArgsException {
		String parameter = null;
		try {
			parameter = currentArgument.next();
			doubleValue = Double.parseDouble(parameter);
		} catch (NoSuchElementException e) {
			throw new ArgsException(ArgsException.ErrorCode.MISSING_DOUBLE);
		} catch (NumberFormatException e) {
			throw new ArgsException( ArgsException.ErrorCode.INVALID_DOUBLE, parameter);
		}
	}
	
	public static double getValue(ArgumentMarshaler am) {
		if(am != null && am instanceof DoubleArgumentMarshaler) {
			return ((DoubleArgumentMarshaler) am).doubleValue;
		}
		return 0;
	}
	
}