package com.hphoto.util;

public class NotSupportClassException extends RuntimeException{
	
		public NotSupportClassException(){
			super();
		}
		
		public NotSupportClassException(String msg){
			super(msg);
		}
}
