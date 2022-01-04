package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private static final int DEFAULT_PORT = 12345;
    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(DEFAULT_PORT);
        Map<String, User> users = new HashMap<>();



        try{
            while(true){
                Socket s = ss.accept();
                new Thread(new ClientHandler(s, users)).start();
            }
        }
        finally{
            ss.close();
        }
    }
}
