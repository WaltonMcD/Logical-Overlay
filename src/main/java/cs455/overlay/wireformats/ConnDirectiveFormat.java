package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConnDirectiveFormat {
    public final int type = 3;
	public String hostName;
	public int portNumber;
    public String toHost;
    public int toPort;

    public ConnDirectiveFormat(String hostName, int portNumber, String toHost, int toPort){
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.toHost = toHost;
        this.toPort = toPort;
    }

    public ConnDirectiveFormat(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        int hostNameLength = din.readInt();
        byte[] hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);

        this.hostName = new String(hostNameBytes);
        this.portNumber = din.readInt();

        int toHostNameLength = din.readInt();
        byte[] toHostNameBytes = new byte[toHostNameLength];
        din.readFully(toHostNameBytes);

        this.toHost = new String(toHostNameBytes);
        this.toPort = din.readInt();

        baInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] hostnameBytes = this.hostName.getBytes();
		int hostnameLength = hostnameBytes.length;

        byte[] toHostNameBytes = this.toHost.getBytes();
        int toHostNameLength = toHostNameBytes.length;

		dout.writeInt(hostnameLength);
		dout.write(hostnameBytes);
		dout.writeInt(this.portNumber);
        dout.writeInt(toHostNameLength);
		dout.write(toHostNameBytes);
		dout.writeInt(this.toPort);
		dout.flush();

		marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		return marshalledBytes;
    }

    public void printContents(){
        System.out.println("Connection Directive Node To Connect: " + this.hostName + " on port " + this.portNumber);
    }
}
