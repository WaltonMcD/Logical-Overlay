package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoneMessageFormat {
    public final int type = 1;
    public String hostname;
    public int port;

    public DoneMessageFormat(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    public DoneMessageFormat(byte[] marshalledMsg) throws IOException{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(marshalledMsg);
        DataInputStream din = new DataInputStream(new BufferedInputStream(byteArrayInputStream));

        int hostNameLength = din.readInt();
        byte[] hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);
        String inHostname = new String(hostNameBytes);
        int inPort = din.readInt();
        byteArrayInputStream.close();
        din.close();
        this.hostname = inHostname; this.port = inPort;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));

        byte[] hostnameBytes = this.hostname.getBytes();
        int hostnameLength = hostnameBytes.length;
        dout.writeInt(hostnameLength);
        dout.write(hostnameBytes);
        dout.writeInt(this.port);
        dout.flush();

        byte[] marshalledBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    public void printContents() {
        System.out.println("Node: "+ this.hostname +" on port " + this.port + " Deregistration Request");
    }
}
