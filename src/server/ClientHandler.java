package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Set;

import common.Consts;
import common.Frame;
import common.TaggedConnection;
import common.Consts.MessageType;

import static common.Consts.UserType;
import server.users.UsersManager;
import server.users.User;


class ClientHandler implements Runnable {

    private final TaggedConnection tc;
    private final UsersManager registeredUsers;
    private final FlightsCatalog flightsCatalog;
    private final ReservationCatalog reservationCatalog;
    private String sessionId;


    ClientHandler(Socket s, UsersManager users, FlightsCatalog flights, ReservationCatalog r) throws IOException {
        this.tc = new TaggedConnection(s);
        this.registeredUsers = users;
        this.sessionId = null;
        this.flightsCatalog = flights;
        this.reservationCatalog = r;
    }

    @Override
    public void run(){

        try{


            boolean loggedIn = false;

            while(!loggedIn){

                Frame f = this.tc.receive();

                if(f.tag == MessageType.LOGIN_REQUEST){
                    String[] credentials = f.getDataAsString().split("\0");
                    User u = this.registeredUsers.login(credentials[0], credentials[1]);

                    loggedIn = u != null;

                    if(!loggedIn)
                        this.tc.send(MessageType.LOGIN_FAIL, "Incorrect credentials");
                    else{
                        this.sessionId = System.currentTimeMillis() + "";
                        this.tc.send(MessageType.LOGIN_SUCESS, this.sessionId);
                        this.tc.send(MessageType.USER_TYPE, u.getType().toString());

                    }
                }
            }

            while(loggedIn){

                Frame f = this.tc.receive();

                switch(f.tag){

                    case
                }
            }




        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
