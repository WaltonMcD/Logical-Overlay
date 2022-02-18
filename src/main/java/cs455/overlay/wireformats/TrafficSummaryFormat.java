package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummaryFormat {
    public final int type = 8;
    public String ip;
    public int port;
    public int numMessagesSent;
    public int numMessagesReceived;
    public long sumOfSentMessages;
    public long sumOfReceivedMessages;

    public TrafficSummaryFormat(String ip, int port, int numMessagesSent, int numMessagesReceived, long sumOfSentMessages, long sumOfReceivedMessages){
        this.ip = ip;
        this.port = port;
        this.numMessagesReceived = numMessagesReceived;
        this.numMessagesSent = numMessagesSent;
        this.sumOfReceivedMessages = sumOfReceivedMessages;
        this.sumOfSentMessages = sumOfSentMessages;
    }

    public TrafficSummaryFormat(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);

        this.ip = new String(ipBytes);
        this.port = din.readInt();
        this.numMessagesReceived = din.readInt();
        this.numMessagesSent = din.readInt();
        this.sumOfReceivedMessages = din.readLong();
        this.sumOfSentMessages = din.readLong();

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
        dout.writeInt(this.numMessagesReceived);
        dout.writeInt(this.numMessagesSent);
        dout.writeLong(this.sumOfReceivedMessages);
        dout.writeLong(this.sumOfSentMessages);
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
		baOutputStream.close();
		dout.close();

		return marshalledBytes;
    }

    public void printContents(){
        System.out.println("\nTraffic Summary Received from " + this.ip + " on port " + this.port);
    }
}
