package com.hphoto.util;

import com.hphoto.util.KeyUtil;

import junit.framework.TestCase;

public class TextKeyUtil extends TestCase{
	
	public TextKeyUtil(){
		super();
	}
	
	public void testKey(){
		System.out.println(KeyUtil.getKey("???" 
				,7));
		System.out.println(KeyUtil.getKey("joah ma",6));
		System.out.println(KeyUtil.getKey("http://www.zcom.com/g", 5));
		System.out.println(KeyUtil.getKey("???" 
				,8));
	}
}
