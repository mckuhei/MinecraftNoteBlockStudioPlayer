package me.mckuhei.nbs;

import java.io.IOException;

public class Header {
	public int songLength;
	public short version;
	public int defaultInstruments;
	public int songLayers;
	public String songName;
	public String songAuthor;
	public String originalAuthor;
	public String description;
	public float tempo;
	public boolean autoSave;
	public short autoSaveDuration;
	public byte timeSignature;
	public long minutesSpent;
	public long leftClicks;
	public long rigthClicks;
	public long blocksAdded;
	public long blocksRemoved;
	public String songOrigin;
	public boolean loop;
	public short maxLoopCount;
	public int loopStart;
	public Header(MyInputStream in) throws IOException  {
		this.songLength=in.readUnsignedShort1();
		if (this.songLength==0) {
			this.version=(short) in.readUnsignedByte();
		}
		this.defaultInstruments=this.version!=0 ? in.readUnsignedByte() : 10;
		if (this.version>=3) {
			this.songLength=in.readUnsignedShort1();
		}
		this.songLayers=in.readUnsignedShort1();
		this.songName=in.readNBSString();
		this.songAuthor=in.readNBSString();
		this.originalAuthor=in.readNBSString();
		this.description=in.readNBSString();
		this.tempo=in.readUnsignedShort1()/100;
		this.autoSave=in.readBoolean();
		this.autoSaveDuration=(short) in.readUnsignedByte();
		this.timeSignature=(byte) in.readUnsignedByte();
		this.minutesSpent=in.readUnsignedInt();
		this.leftClicks=in.readUnsignedInt();
		this.rigthClicks=in.readUnsignedInt();
		this.blocksAdded=in.readUnsignedInt();
		this.blocksRemoved=in.readUnsignedInt();
		this.songOrigin=in.readNBSString();
		this.loop=in.readBoolean();
		this.maxLoopCount=(short) in.readUnsignedByte();
		this.loopStart=in.readUnsignedShort1();
	}
}
