package me.mckuhei.nbs;

public class Timer {
	public Timer() {
		time=System.currentTimeMillis();
	}
	
	private long time = -1L;

	public boolean hasTimePassed(long MS) {
		return (System.currentTimeMillis() >= this.time + MS);
	}

	public long hasTimeLeft(long MS) {
		return MS + this.time - System.currentTimeMillis();
	}

	public void reset() {
		this.time = System.currentTimeMillis();
	}
}
