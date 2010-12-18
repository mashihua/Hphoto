package com.hphoto.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.hphoto.util.*;
import junit.framework.TestCase;

public class TestConvertion extends TestCase{
	
	public TestConvertion(){
		super();
	}
	
	public void testDecode() throws Exception{
		String s = "beijing.josh";
		int i = 56;
		boolean b = true;
		long l = 200L;
		double d = 1d;
		float f = 1F;
		char c = 'x';
		byte by = 0x00;
		//short sh = 0x7fff;
		Date date = new Date();
		
		TestClass tc = new TestClass();
		
		tc.setI(3);
		assertEquals("Test string",s,
				Convertion.encode(String.class,Convertion.decode(String.class, s)));
		assertEquals("Test int:",i,
				Convertion.encode(Integer.TYPE,Convertion.decode(Integer.TYPE, i)));
		assertEquals("Test boolean:",b,
				Convertion.encode(Boolean.TYPE,Convertion.decode(Boolean.TYPE, b)));
		assertEquals("Test long:",l,
				Convertion.encode(Long.TYPE,Convertion.decode(Long.TYPE, l)));
		assertEquals("Test double:",d,
				Convertion.encode(Double.TYPE,Convertion.decode(Double.TYPE, d)));
		assertEquals("Test floar:",f,
				Convertion.encode(Float.TYPE,Convertion.decode(Float.TYPE, f)));
		
		assertEquals("Test character:",c,
				Convertion.encode(Character.TYPE,Convertion.decode(Character.TYPE, c)));
		assertEquals("Test byte",by,
				Convertion.encode(Byte.TYPE,Convertion.decode(Byte.TYPE, by)));
		
		//System.out.println(Convertion.encode(Byte.TYPE,Convertion.decode(Byte.TYPE, by)));
		
		assertEquals("Test date:",date,
				Convertion.encode(Date.class,Convertion.decode(Date.class, date)));
		assertEquals("Test class:",tc.getI(),
				((TestClass)Convertion.encode(Class.class,
						Convertion.decode(Class.class, tc))).getI());
	}
	
}
