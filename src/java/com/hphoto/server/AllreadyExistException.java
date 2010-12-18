package com.hphoto.server;

import java.io.IOException;


public class AllreadyExistException extends IOException{
	
	public AllreadyExistException(){
		super();
	}
	public AllreadyExistException(String msg){
		super(msg);
	}
}
