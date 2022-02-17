package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegResponseFormat {
    public final int type = 2;
    public int statusCode;
    public int identifier;
    public String additionalInfo;

    public RegResponseFormat(int code, int identifier, String info){
        this.statusCode = code;
        this.identifier = identifier;
        this.additionalInfo = info;
    }

    public RegResponseFormat(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.statusCode = din.readInt();
        this.identifier = din.readInt();

        int infoLength = din.readInt();
        byte[] infoBytes = new byte[infoLength];
        din.readFully(infoBytes);

        this.additionalInfo = new String(infoBytes);

        baInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        byte[] infoBytes = this.additionalInfo.getBytes();
        int infoLength = infoBytes.length;
        dout.writeInt(this.statusCode);
        dout.writeInt(this.identifier);
        dout.writeInt(infoLength);
        dout.write(infoBytes);
        dout.flush();
        
        marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		return marshalledBytes;
    }

    public void printContents(){
        System.out.println("Registration Response Received From Node: " + this.identifier + " Status Code: " + 
            				   this.statusCode + "\nAdditional Info: " + 
            				   this.additionalInfo);
    }
}
