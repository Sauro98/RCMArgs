package it.ivanodonadi.args.marshalers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import it.ivanodonadi.args.exceptions.ArgsException;

public class StringArrayArgumentMarshaler implements ArgumentMarshaler {

	private String[] stringArrayValue = new String[] {};
	
	@Override
	public void set(ListIterator<String> currentArgument) throws ArgsException {
		String currentString = null;
		List<String> stringList = new ArrayList<String>();
		while(currentArgument.hasNext()) {
			currentString = currentArgument.next();
			if(currentString.startsWith("-")) {
				currentArgument.previous();
				if(stringList.size() == 0)
					throw new ArgsException(ArgsException.ErrorCode.MISSING_STRING_ARRAY);
				break;
			}
			stringList.add(currentString);
		}
		if(stringList.size() == 0) 
			throw new ArgsException(ArgsException.ErrorCode.MISSING_STRING_ARRAY);
		copyListInArray(stringList);
	}
	
	private void copyListInArray(List<String> strings) {
		stringArrayValue = new String[strings.size()];
		for(int a = 0; a < strings.size(); a++) {
			stringArrayValue[a] = strings.get(a);
		}
	}
	
	public static String[] getValue(ArgumentMarshaler am){
		if(am != null && am instanceof StringArrayArgumentMarshaler) {
			return ((StringArrayArgumentMarshaler) am).stringArrayValue;
		}
		return new String[] {};
	}

}
