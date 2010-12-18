
package com.hphoto.util;

import java.io.*;

import org.apache.hadoop.io.Writable;

import com.hphoto.ClassWriteale;

public class TestClass implements ClassWriteale {
	
	private int i = 0;
	
	public TestClass(){
		
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(i);
	}

	public void readFields(DataInput in) throws IOException {
		in.readInt();
	}
	
}
