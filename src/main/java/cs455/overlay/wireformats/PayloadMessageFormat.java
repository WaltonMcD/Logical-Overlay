package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PayloadMessageFormat {
    public final int type = 5;
    public int hops;
    public int toPort, fromPort;
    public int numberOfMessage;
    public String toHostname, fromHostname;
    public long payload;

    public PayloadMessageFormat(int hops, int numberOfMessage, long payload, int fromPort, String fromHost, int toPort, String toHost){
        this.fromHostname = fromHost; 
        this.fromPort = fromPort;
        this.toHostname = toHost; 
        this.toPort = toPort;
        this.hops = hops; 
        this.numberOfMessage = numberOfMessage; 
        this.payload = payload;
    }

    public PayloadMessageFormat(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        this.hops = din.readInt();
        this.numberOfMessage = din.readInt();
        this.payload = din.readLong();
        int hostNameLength = din.readInt();
        byte[] hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);
        this.fromHostname = new String(hostNameBytes);
        this.fromPort = din.readInt();

        hostNameLength = din.readInt();
        hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);
        this.toHostname = new String(hostNameBytes);
        this.toPort = din.readInt();

        byteArrayInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream((byteArrayOutputStream)));

        dout.writeInt(this.hops);
        dout.writeInt(this.numberOfMessage);
        dout.writeLong(this.payload);

        byte[] hostnameBytes = this.fromHostname.getBytes();
        int hostnameLength = hostnameBytes.length;
        dout.writeInt(hostnameLength);
        dout.write(hostnameBytes);
        dout.writeInt(this.fromPort);

        hostnameBytes = this.toHostname.getBytes();
        hostnameLength = hostnameBytes.length;
        dout.writeInt(hostnameLength);
        dout.write(hostnameBytes);
        dout.writeInt(this.toPort);
        
        dout.flush();

        byte[] marshalledBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public void printContents(){
        System.out.println( this.fromHostname + " message: " + this.numberOfMessage + " payload: " + this.payload);
    }
}
