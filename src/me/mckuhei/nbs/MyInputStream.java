package me.mckuhei.nbs;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class MyInputStream extends DataInputStream {
	public MyInputStream(InputStream in) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	public short readShort1() throws IOException {
		return (short)(super.readUnsignedByte()<<8&super.readUnsignedByte());
	}
	public int readUnsignedShort1() throws IOException{
	        byte[] bytes = new byte[2];
			super.read(bytes);

	        int integerVal = 0;
	        for(int i = bytes.length - 1; i >= 0; i--){
	            byte b = bytes[i];
	            integerVal += Byte.toUnsignedInt(b) << (i*8);
	        }
	        return integerVal;
	    }

	public long readUnsignedInt() throws IOException{
	        byte[] bytes = new byte[4];
	        super.read(bytes);

	        long longVal = 0;
	        for(int i = bytes.length - 1; i >= 0; i--){
	            byte b = bytes[i];
	            longVal += Byte.toUnsignedInt(b) << (i*8);
	        }
	        return longVal;
	    }

	public String readNBSString() throws IOException{
	        int length = (int) readUnsignedInt();
	        byte[] bytes = new byte[length];
	        super.read(bytes);
	        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
	        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
	        char[] string = new char[length];
	        charBuffer.get(string);
	        return String.valueOf(string);
	    }

	    public byte flipByteBitOrder(byte flipByte){
	        return (byte) (Integer.reverse(flipByte) >>> (Integer.SIZE - Byte.SIZE));
	    }
}
