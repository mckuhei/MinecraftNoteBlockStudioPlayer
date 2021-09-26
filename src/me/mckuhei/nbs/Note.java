package me.mckuhei.nbs;

import java.io.IOException;
import java.util.ArrayList;


public class Note {
	public int tick;
	public int layer;
	public short instrument;
	public short key;
	public float velocity;
	public short panning;
	public short pitch;
	//private static final float[] pitchs=new float[] {0.5f,0.5297315471796477f,0.5612310241546865f,0.5946035575013605f,0.6299605249474366f,0.6674199270850172f,0.7071067811865476f,0.7491535384383408f,0.7937005259840998f,0.8408964152537145f,0.8908987181403393f,0.9438743126816935f,1.0f,1.0594630943592953f,1.122462048309373f,1.189207115002721f,1.2599210498948732f,1.3348398541700344f,1.4142135623730951f,1.4983070768766815f,1.5874010519681994f,1.681792830507429f,1.7817974362806785f,1.887748625363387f,2.0f};
	public Note(int tick,int layer,short instrument,short key,short velocity,short panning,short pitch) {
		this.tick=tick;
		this.layer=layer;
		this.instrument=instrument;
		this.key=key;
		this.velocity=velocity/100F;
		this.panning=panning;
		this.pitch=pitch;
	}
	public static Note[] from(MyInputStream in,int version) throws IOException {
		ArrayList<Note> notes=new ArrayList<Note>();
		Jumper i=new Jumper(in);
		while (i.next()) {
			Jumper j=new Jumper(in);
			while (j.next()) {
				notes.add(new Note(i.value,j.value,(short)in.readUnsignedByte(),(short)in.readUnsignedByte(),(short)(version>=4 ? in.readUnsignedByte() : 100),(short)(version>=4 ? in.readUnsignedByte()-100 : 0),version>=4 ? in.readShort1() : 0));
			}
		}
		return notes.toArray(new Note[0]);
	}
	public int hashCode() {
		return this.tick&this.layer&this.instrument&this.key&(int)this.velocity*100&this.panning&this.pitch;
	}
	public boolean equals(Object obj) {
		if (!(obj instanceof Note)) return false;
		Note n=(Note) obj;
		return this.tick==n.tick&&this.layer==n.layer&&this.instrument==n.instrument&&this.key==n.key&&this.velocity==n.velocity
				&&this.panning==n.panning&&this.pitch==n.pitch;
	}
	public float getPitch() {
		return (float)Math.pow(2, ((this.key+this.pitch/100F)-45)/12F);
	}
}
