package me.mckuhei.nbs;

import java.io.IOException;

public class NBS {
	public Header header;
	public Note[] notes;
	public Layer[] layers;
	public Instrument[] instruments;
	public NBS(MyInputStream in) throws IOException {
		this.header=new Header(in);
		this.notes=Note.from(in, this.header.version);
		this.layers=new Layer[this.header.songLayers];
		for (int i=0;i<this.header.songLayers;i++) {
			this.layers[i]=new Layer(in.readNBSString(),this.header.version >= 4 ? in.readBoolean() : false,in.readUnsignedByte()/255,(short)(this.header.version >= 2 ? in.readUnsignedByte()-100 : 0));
		}
		int instrumentCount=in.readUnsignedShort1();
		this.instruments=new Instrument[instrumentCount];
		for (int i=0;i<instrumentCount;i++) {
			String name=in.readNBSString();
			String soundFile=in.readNBSString();
			this.instruments[i]=new Instrument(name,soundFile,(short) in.readUnsignedByte(),in.readBoolean());
		}
	}
}
