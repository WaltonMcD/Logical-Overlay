package cs455.overlay;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReciever implements Runnable {
    private Socket socket;
    private DataInputStream din;

    public TCPReciever(Socket socket) throws IOException {
        this.socket = socket;
        din = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        Integer dataLength;
        try {
            dataLength = din.readInt();
            System.out.println("Recieved a message of length: " + dataLength);

            byte[] data = new byte[dataLength];
            din.readFully(data, 0, dataLength);

            String msg_str = new String(data);
            System.out.println("Recieved message: " + msg_str);

        } catch(IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public void close() throws IOException {
        din.close();
    }
    
}