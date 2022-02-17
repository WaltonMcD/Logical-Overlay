package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskCompleteFormat {
    public final int type = 6;
    public int identifier;
    public String ip;
    public int port;

    public TaskCompleteFormat(int identifier, String ip, int port){
        this.identifier = identifier;
        this.ip = ip;
        this.port = port;
    }

    public TaskCompleteFormat(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);

        this.ip = new String(ipBytes);
        this.port = din.readInt();
        this.identifier = din.readInt();

        baInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] ipBytes = this.ip.getBytes();
        int ipLength = ipBytes.length;
        dout.writeInt(ipLength);
        dout.write(ipBytes);
        dout.writeInt(this.port);
        dout.writeInt(this.identifier);
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		return marshalledBytes;
    }

    public void printContents(){
        System.out.println("Received Task Complete From Node: " + this.identifier + " @ " + this.ip);
    }
}
