package it.ivanodonadi.args.test;

import java.util.ArrayList;
import java.util.List;

import it.ivanodonadi.args.Args;
import it.ivanodonadi.args.exceptions.ArgsException;
import junit.framework.TestCase;

public class TestArgs extends TestCase{
	
	public void testValidConstructor() throws Exception {
		Args args = new Args("l",new String[] {"-l"});
		assertEquals(1,args.cardinality());
	}
	
	public void testUnexpectedArgument() throws Exception {
		try {
			new Args("l",new String[] {"-l","-t"});
			fail();
		} catch (ArgsException e) {
			assertEquals(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT,e.getErrorCode());
		}
	}
	
	public void testCardinality() throws Exception {
		Args args = new Args("l,c,d",new String[] {"-l","-c","-d"});
		assertEquals(3,args.cardinality());
	}
	
	public void testNumberValueShouldThrowParseError() throws Exception {
		try {
			new Args("l,9",new String[] {});
			fail();
		} catch (ArgsException e) { 
			assertEquals("'9' is not a valid argument name.",e.errorMessage());
		}
	}
	
	public void testInvalidSchemaFormatShouldThrowException() throws Exception {
		try {
			new Args("l°",new String[] {});
			fail();
		}catch(ArgsException e) {
			assertEquals("Argument: l has invalid format: °.",e.errorMessage());
		}
	}

	
	public void testErrorMessageShouldNotBeEmpty() throws Exception {
		try {
			new Args("",new String[]{"-l"});
			fail();
		} catch (ArgsException e) {
			assertEquals("Argument -l unexpected.",e.errorMessage());
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
	
	public void testGetBoolean() throws Exception {
		Args args = new Args("l,c,d",new String[] {"-ld"});
		assertEquals(true,args.getBoolean('l'));
		assertEquals(false,args.getBoolean('c'));
		assertEquals(true,args.getBoolean('d'));
		assertEquals(false,args.getBoolean('e'));
	}
	
	public void testGetString() throws Exception {
		Args args = new Args("l*",new String[] {"-l","hello"});
		assertEquals("hello",args.getString('l'));
		assertEquals("",args.getString('c'));
	}
	
	public void testGetInteger() throws Exception {
		Args args = new Args("l#",new String[] {"-l","3"});
		assertEquals(3,args.getInteger('l'));
		assertEquals(0,args.getInteger('c'));
	}
	
	public void testGetDouble() throws Exception {
		Args args = new Args("l##",new String[] {"-l","42.3"});
		assertEquals(1,args.cardinality());
		assertTrue(args.has('l'));
		assertEquals(42.3,args.getDouble('l'),0.001);
		assertEquals(0.00,args.getDouble('c'),0.001);
	}
	
	public void testGetStringArray() throws Exception {
		Args args = new Args("l[*]",new String[] {"-l","hello","bye"});
		assertEquals(1,args.cardinality());
		assertTrue(args.has('l'));
		String[] values = args.getStringArray('l');
		assertEquals("hello",values[0]);
		assertEquals("bye",values[1]);
		assertEquals(0,args.getStringArray('c').length);
	}
	
	public void testGetStringArrayAndInteger() throws Exception {
		Args args = new Args("l[*],c#",new String[] {"-l","hello","bye","-c","3"});
		assertEquals(2,args.cardinality());
		assertTrue(args.has('l'));
		assertTrue(args.has('c'));
		String[] values = args.getStringArray('l');
		assertEquals("hello",values[0]);
		assertEquals("bye",values[1]);
		assertEquals(3,args.getInteger('c'));
	}
	
	public void testGetBadClass() throws Exception {
		Args args = new Args("l,c*,d#,e##,f[*]",new String[] {"-l","-c","hello","-d","3","-e","42.3","-f","hello"});
		assertEquals(false,args.getBoolean('c'));
		assertEquals("",args.getString('d'));
		assertEquals(0,args.getInteger('e'));
		assertEquals(0.00, args.getDouble('f'),0.001);
		assertEquals(0,args.getStringArray('l').length);
	}
	
	public void testMissingString() throws Exception {
		try {
			new Args("l*",new String[] {"-l"});
			fail();
		} catch (ArgsException e){
			assertEquals(
				"Could not find string parameter for -l.",
				e.errorMessage());
		}
	}
	
	public void testMissingInteger() throws Exception {
		try {
			new Args("l#",new String[] {"-l"});
			fail();
		}catch(ArgsException e) {
			assertEquals(
				"Could not find integer parameter for -l.",
				e.errorMessage());
		}
		
	}
	
	public void testMissingDouble() throws Exception {
		try {
			new Args("l##",new String[] {"-l"});
			fail();
		} catch (ArgsException e){
			assertEquals(
				"Could not find double parameter for -l.",
				e.errorMessage());
		}
	}
	
	public void testMissingStringArray() throws Exception {
		try {
			new Args("l[*]",new String[] {"-l"});
			fail();
		} catch (ArgsException e) {
			assertEquals(
					"Could not find string array parameter for -l.",
					e.errorMessage());
		}
		
	}
	public void testMissingStringArrayWithOtherArg() throws Exception {
		try {
			new Args("l[*],c",new String[] {"-l","-c"});
			fail();
		} catch (ArgsException e) {
			assertEquals(
					"Could not find string array parameter for -l.",
					e.errorMessage());
		}
		
	}
	
	public void testInvalidInteger() throws Exception {
		try {
			new Args("l#",new String[] {"-l","h"});
			fail();
		} catch (ArgsException e) {
			assertEquals(
				"Argument -l expects an integer but was 'h'.",
				e.errorMessage());
		}
		
	}
	
	public void testInvalidDouble() throws Exception {
		try {
			new Args("l##",new String[] {"-l","h"});
			fail();
		} catch (ArgsException e) {
			assertEquals(
				"Argument -l expects a double but was 'h'.",
				e.errorMessage());
		}
	}
	
	public void testHasFunction() throws Exception {
		Args args = new Args("l,c*,d#",new String[] {"-l","-c","hello","-d","3"});
		assertEquals(true,args.has('l'));
		assertEquals(true,args.has('c'));
		assertEquals(true,args.has('d'));
		assertEquals(false,args.has('e'));
	}
	
	public void testMissingIdentifier() throws Exception{
		Args args = new Args("l",new String[] {"l"}); 
		assertEquals(false,args.has('l'));
	}
	
		
}
