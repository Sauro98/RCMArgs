package it.ivanodonadi.args.marshalers;

import java.util.ListIterator;

import it.ivanodonadi.args.exceptions.ArgsException;

public abstract interface ArgumentMarshaler {
	public abstract void set(ListIterator<String> currentArgument) throws ArgsException;
}
