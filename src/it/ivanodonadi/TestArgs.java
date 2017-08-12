package it.ivanodonadi;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestArgs {
	@Rule
	  public final ExpectedException exception = ExpectedException.none();

	@Test 
	public void testValidConstructor() throws Exception{
		Args args = new Args("l",new String[] {"-l"});
		assertEquals(true,args.isValid());
		assertEquals(1,args.cardinality());
	}
	
	@Test 
	public void testInvalidConstructor() throws Exception{
		Args args = new Args("l",new String[] {"-l","-t"});
		assertEquals(false,args.isValid());
		assertEquals(1,args.cardinality());
	}
	
	@Test 
	public void testCardinality() throws Exception{
		Args args = new Args("l,c,d",new String[] {"-l","-c","-d"});
		assertEquals(true, args.isValid());
		assertEquals(3,args.cardinality());
	}
	
	@Test 
	public void testNumberValueIgnored() throws Exception{
		exception.expect(ParseException.class);
		exception.expectMessage("Bad character: 9 in Args format: l,9");
		new Args("l,9",new String[] {"-l","-9"});
	}
	
	@Test 
	public void testUsageShouldNotBeEmpty() throws Exception{
		Args args = new Args("l,c,d",new String[] {"-l","-c","-d"});
		assertEquals("-[l,c,d]", args.usage());
	}
	
	@Test 
	public void testUsageShouldBeEmpty() throws Exception{
		Args args = new Args("",new String[] {});
		assertEquals("", args.usage());
	}
	
	@Test
	public void testErrorMessageShouldThrowException() throws Exception {
			Args args = new Args("l",new String[]{"l"});
			exception.expect(Exception.class);
			exception.expectMessage("TILT: Should not get here.");
			args.errorMessage();
	}
	
	@Test
	public void testErrorMessageShouldNotBeEmpty() throws Exception{
		Args args = new Args("",new String[]{"-l","-c"});
		assertEquals("Argument(s) -cl unexpected",args.errorMessage());
	}
	
	@Test 
	public void testGetBoolean() throws Exception{
		Args args = new Args("l,c,d",new String[] {"-ld"});
		assertEquals(true,args.getBoolean('l'));
		assertEquals(false,args.getBoolean('c'));
		assertEquals(true,args.getBoolean('d'));
	}
	
	@Test
	public void testGetBooleanAndString() throws Exception{
		Args args = new Args("l,c*", new String[]{"-l","-c","ciao"});
		assertEquals(true,args.getBoolean('l'));
		assertEquals("ciao",args.getString('c'));
		assertEquals(false,args.getBoolean('d'));
		assertEquals(true,args.has('c'));
		assertEquals(false,args.has('d'));
	}
	
	@Test
	public void testFindMissingString() throws Exception{
		Args args = new Args("c*",new String[]{"-c"});
		assertEquals("",args.getString('c'));
		assertEquals("",args.getString('d'));
		assertEquals("Could not find string parameter for -c.",args.errorMessage());
	}
	
}
