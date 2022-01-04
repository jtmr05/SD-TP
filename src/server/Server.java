package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.users.UsersManager;
import common.*;

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(Consts.DEFAULT_PORT);
        UsersManager um = UsersManager.getInstance();


        try{
            while(true){
                Socket s = ss.accept();
                new Thread(new ClientHandler(s, um)).start();
            }
        }
        finally{
            ss.close();
        }
    }
}
