package it.ivanodonadi;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestArgs {

	@Test 
	public void testValidConstructor() {
		Args args = new Args("l",new String[] {"-l"});
		assertEquals(true,args.isValid());
		assertEquals(1,args.cardinality());
	}
	
	@Test 
	public void testInvalidConstructor() {
		Args args = new Args("l",new String[] {"-l","-t"});
		assertEquals(false,args.isValid());
		assertEquals(1,args.cardinality());
	}
	
	@Test 
	public void testCardinality() {
		Args args = new Args("l,c,d",new String[] {"-l","-c","-d"});
		assertEquals(true, args.isValid());
		assertEquals(3,args.cardinality());
	}
	
	@Test 
	public void testNumberValueIgnored() {
		Args args = new Args("l,9",new String[] {"-l","-9"});
		assertEquals(1,args.cardinality());
	}
	
	@Test 
	public void testUsageShouldNotBeEmpty() {
		Args args = new Args("l,c,d",new String[] {"-l","-c","-d"});
		assertEquals("-[l,c,d]", args.usage());
	}
	
	@Test 
	public void testUsageShouldBeEmpty() {
		Args args = new Args("",new String[] {});
		assertEquals("", args.usage());
	}
	
	@Test
	public void testErrorMessageShouldBeEmpty() {
		Args args = new Args("l",new String[]{"l"});
		assertEquals("",args.errorMessage());
	}
	
	@Test
	public void testErrorMessageShouldNotBeEmpty() {
		Args args = new Args("",new String[]{"-l","-c"});
		assertEquals("Argument(s) -cl unexpected",args.errorMessage());
	}
	
	@Test 
	public void testGetBoolean() {
		Args args = new Args("l,c,d",new String[] {"-ld"});
		assertEquals(true,args.getBoolean('l'));
		assertEquals(false,args.getBoolean('c'));
		assertEquals(true,args.getBoolean('d'));
	}
	
}
