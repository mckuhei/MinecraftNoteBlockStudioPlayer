package me.mckuhei.nbs;

import java.io.IOException;

public class Jumper {
	public int value=-1;
	private MyInputStream input;
	public Jumper(MyInputStream in) {
		this.input=in;
	}
	public boolean next() throws IOException {
		int jump=this.input.readUnsignedShort1();
		if (jump==0) return false;
		this.value+=jump;
		return true;
	}
}
