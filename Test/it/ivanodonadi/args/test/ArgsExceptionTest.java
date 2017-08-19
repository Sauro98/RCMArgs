package it.ivanodonadi.args.test;

import org.junit.Test;

import it.ivanodonadi.args.exceptions.ArgsException;
import junit.framework.TestCase;

public class ArgsExceptionTest extends TestCase{
	
	public void testGetErrorArgumentId() {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.OK,'a',"");
		assertEquals('a',e.getErrorArgumentId());
		e.setErrorArgumentId('b');
		assertEquals('b',e.getErrorArgumentId());
	}
	
	public void testGetErrorParameter() {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.OK,"hello");
		assertEquals("hello",e.getErrorParameter());
		e.setErrorParameter("bye");
		assertEquals("bye",e.getErrorParameter());
	}
	
	public void testGetErrorCode() {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.OK);
		assertEquals(ArgsException.ErrorCode.OK,e.getErrorCode());
		e.setErrorCode(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT);
		assertEquals(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT,e.getErrorCode());
	}
	
	public void testNoError() throws Exception {
		try {
			ArgsException ae = new ArgsException();
			ae.errorMessage();
			fail();
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		
	}
	
	public void testUnexpectedArgument() throws Exception {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT,'l',null);
		assertEquals("Argument -l unexpected.",e.errorMessage());
	}
	
	@Test 
	public void testMissingInteger() throws Exception {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.MISSING_INTEGER,'l',null);
		assertEquals("Could not find integer parameter for -l.",e.errorMessage());
	}
	
	public void testMissingDouble() throws Exception {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.MISSING_DOUBLE,'l',null);
		assertEquals("Could not find double parameter for -l.",e.errorMessage());
	}
	
	public void testMissingString() throws Exception {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.MISSING_STRING,'l',null);
		assertEquals("Could not find string parameter for -l.",e.errorMessage());
	}
	
	public void testInvalidInteger() throws Exception {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER,'l',"hello");
		assertEquals("Argument -l expects an integer but was 'hello'.",e.errorMessage());
	}
	
	public void testInvalidDouble() throws Exception {
		ArgsException e = new ArgsException(ArgsException.ErrorCode.INVALID_DOUBLE,'l',"hello");
		assertEquals("Argument -l expects a double but was 'hello'.",e.errorMessage());
	}
	
}
