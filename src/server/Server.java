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
        FlightsCatalog fc = FlightsCatalog.getInstance();
        ReservationCatalog rc = ReservationCatalog.getInstance();

        System.out.println("Listening on port " + Consts.DEFAULT_PORT);


        try{
            while(true){
                Socket s = ss.accept();
                new Thread(new ClientHandler(s, um, fc, rc)).start();
            }
        }
        finally{
            ss.close();
        }
    }
}
