package me.mckuhei.nbs;

public class Instrument {
	public String name;
	public String soundFile;
	public short pitch;
	public boolean pressKey;
	public Instrument(String name,String soundFile,short pitch,boolean pressKey) {
		this.name=name;
		this.soundFile=soundFile;
		this.pitch=pitch;
		this.pressKey=pressKey;
	}
}
