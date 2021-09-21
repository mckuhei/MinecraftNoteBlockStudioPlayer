package me.mckuhei.nbs;

import java.io.IOException;
import java.util.ArrayList;

public class NBS {
	public Header header;
	public Note[] notes;
	public Layer[] layers;
	public Instrument[] instruments;
	public NBS(MyInputStream in) throws IOException {
		this.header=new Header(in);
		this.notes=Note.from(in, this.header.version);
		ArrayList<Layer> layers = new ArrayList<>();
		for (int i=0;i<this.header.songLayers;i++) {
			layers.add(new Layer(in.readNBSString(),this.header.version >= 4 ? in.readBoolean() : false,in.readUnsignedByte()/100F,(short)(this.header.version >= 2 ? in.readUnsignedByte()-100 : 0)));
		}
		for(Note note:this.notes) {
			if(note.layer>=layers.size()) {
				layers.add(new Layer("", false, 1F, (short) 0));
			}
		}
		this.layers = layers.toArray(new Layer[0]);
		int instrumentCount=in.readUnsignedShort1();
		this.instruments=new Instrument[instrumentCount];
		for (int i=0;i<instrumentCount;i++) {
			String name=in.readNBSString();
			String soundFile=in.readNBSString();
			this.instruments[i]=new Instrument(name,soundFile,(short) in.readUnsignedByte(),in.readBoolean());
		}
	}
}
