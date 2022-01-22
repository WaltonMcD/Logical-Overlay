package cs455.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Registry {
    private int portNum;
    private int messageType;
    private String ipAddress;

    public Registry(int portNum, int messageType, String ipAddress) {
        this.portNum = portNum;
        this.messageType = messageType;
        this.ipAddress = ipAddress;
    }

    public void addPeers() throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNum);

        while(true) {
            Socket socket = serverSocket.accept();
        }
    }
    
}
