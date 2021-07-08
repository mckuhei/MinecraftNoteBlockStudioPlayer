package me.mckuhei.nbs;

import java.io.IOException;
import java.util.ArrayList;


public class Note {
	public int tick;
	public int layer;
	public short instrument;
	public short key;
	public short velocity;
	public short panning;
	public short pitch;
	public Note(int tick,int layer,short instrument,short key,short velocity,short panning,short pitch) {
		this.tick=tick;
		this.layer=layer;
		this.instrument=instrument;
		this.key=key;
		this.velocity=velocity;
		this.panning=panning;
		this.pitch=pitch;
	}
	public static Note[] from(MyInputStream in,int version) throws IOException {
		ArrayList<Note> notes=new ArrayList<Note>();
		Jumper i=new Jumper(in);
		Jumper j=new Jumper(in);
		while (i.next()) {
			while (j.next()) {
				notes.add(new Note(i.value,j.value,(short)in.readUnsignedByte(),(short)in.readUnsignedByte(),(short)(version>=4 ? in.readUnsignedByte() : 100),(short)(version>=4 ? in.readUnsignedByte()-100 : 0),version>=4 ? in.readShort1() : 0));
			}
		}
		return notes.toArray(new Note[0]);
	}
	public int hashCode() {
		return this.tick&this.layer&this.instrument&this.key&this.velocity&this.panning&this.pitch;
	}
	public boolean equals(Object obj) {
		if (!(obj instanceof Note)) return false;
		Note n=(Note) obj;
		return this.tick==n.tick&&this.layer==n.layer&&this.instrument==n.instrument&&this.key==n.key&&this.velocity==n.velocity
				&&this.panning==n.panning&&this.pitch==n.pitch;
	}
}
