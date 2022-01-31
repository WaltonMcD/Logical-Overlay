package cs455.overlay;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    public Server(Integer port) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Created Server Socket... ");
            int count = 0;
            while(true) {
                Socket incomingConnectionSocket = serverSocket.accept();
                incomingConnectionSocket.setReuseAddress(true);
                count++;
                System.out.println("Received a connection. Client: " + incomingConnectionSocket.getLocalPort() + " " + incomingConnectionSocket.getInetAddress());
                NodeHandler clientSock = new NodeHandler(incomingConnectionSocket, count);
                new Thread(clientSock).start();
            }
        }
        catch (IOException ioe) {
            System.out.print(ioe.getMessage());
        }
        finally {
            if (serverSocket != null) {
                try{
                    serverSocket.close();
                }
                catch(IOException ioe){
                    System.out.print(ioe.getMessage());
                }
                    
            }
        }
    }

    // Thread Handler
    private static class NodeHandler implements Runnable {
        private final Socket nodeSocket;
        private final Integer clientNum;

        public NodeHandler(Socket incomingConnectionSocket, Integer clientNum) {
            this.nodeSocket = incomingConnectionSocket;
            this.clientNum = clientNum;
        }

        public void run(){
            try{
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(nodeSocket.getInputStream()));
                String line = "";

                //constantly excepting input until termination string is provided.
                while(!line.equals("Exit")){
                    try{
                        line = inputStream.readUTF();
                        System.out.println("Node #" + clientNum + " says " + line);
                    }
                    catch(IOException ioe){
                        System.out.println(ioe.getMessage());
                    }
                }

                System.out.println("Closing Connection with Node: #" + clientNum);
                nodeSocket.close();
                inputStream.close();
            }
            catch(IOException ioe){
                System.out.println(ioe.getMessage());
            }
        }

    }
}
