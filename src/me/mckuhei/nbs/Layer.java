package me.mckuhei.nbs;

public class Layer {
	public String name;
	public boolean lock;
	public float volume;
	public short panning;
	public Layer(String name,boolean lock,float volume,short panning) {
		this.name=name;
		this.lock=lock;
		this.volume=volume;
		this.panning=panning;
	}
}
