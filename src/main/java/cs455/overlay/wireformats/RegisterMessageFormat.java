package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterMessageFormat {
    public final int type = 0;
	public String hostName;
	public int portNumber;

    public RegisterMessageFormat(String hostName, int portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public RegisterMessageFormat(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        int hostNameLength = din.readInt();
        byte[] hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);

        this.hostName = new String(hostNameBytes);
        this.portNumber = din.readInt();

        baInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] hostnameBytes = this.hostName.getBytes();
		int hostnameLength = hostnameBytes.length;
		dout.writeInt(hostnameLength);
		dout.write(hostnameBytes);
		dout.writeInt(this.portNumber);
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		return marshalledBytes;
    }

    public void printContents(){
        System.out.println("Connection Created with " + this.hostName + " on port " + this.portNumber);
    }
}
