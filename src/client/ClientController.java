package client;

import common.TaggedConnection;
import utils.IO;

import static common.Consts.MessageType;
import static common.Consts.UserType;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientController{

    private final IO io;
    private final List<String> flights;
    private String clientId;

    private final Thread receiver;
    private final WaitingRoom wr;

    private final Lock ioLock;
    private final Lock flightsLock;
    private final Lock loginLock;
    private final Condition loginCond;


    class ClientReceiver implements Runnable {

        private final TaggedConnection tc;

        ClientReceiver(TaggedConnection tc){
            this.tc = tc;
        }

        @Override
        public void run(){
            try{
                while(!Thread.interrupted()){
                    var frame = this.tc.receive();

                    switch(frame.tag){

                        case USER_TYPE -> {
                            ioLock.lock();
                            io.setMainMenu(UserType.valueOf(frame.getDataAsString()), ClientController.this);
                            ioLock.unlock();
                        }

                        case FLIGHTS_LIST -> {
                            flightsLock.lock();

                            String[] flights__ = frame.getDataAsString().split("\0");

                            flights.clear();
                            for(String s : flights__)
                                flights.add(s);

                            flightsLock.unlock();
                        }

                        case NOTIF -> {
                            ioLock.lock();
                            io.addNotif(frame.getDataAsString());
                            ioLock.unlock();
                        }

                        case LOGIN_FAIL -> {
                            loginLock.lock();
                            io.err(frame.getDataAsString());
                            loginCond.signal();
                            loginLock.unlock();
                        }

                        case LOGIN_SUCESS -> {
                            loginLock.lock();
                            clientId = frame.getDataAsString();
                            loginCond.signal();
                            loginLock.unlock();
                        }

                        default -> {}
                    }
                }
            }
            catch(IOException e){}
        }
    }

    ClientController(WaitingRoom wr, IO io, TaggedConnection tc){
        this(wr, io, tc, null);
    }

    ClientController(WaitingRoom wr, IO io, TaggedConnection tc, String clientId){
        this.wr = wr;
        this.io = io;
        this.flights = new ArrayList<>();
        this.clientId = clientId;
        this.ioLock = new ReentrantLock();
        this.flightsLock = new ReentrantLock();
        this.loginLock = new ReentrantLock();
        this.loginCond = this.loginLock.newCondition();
        (this.receiver = new Thread(new ClientReceiver(tc))).start();
    }

    void start(){

        this.login();

        boolean flag = true;

        while(flag){
            this.ioLock.lock();

            int op = this.io.runMainMenu();

            if(op == 0){
                flag = false;
                this.wr.addToSendQueue(MessageType.QUIT, this.clientId);
                this.receiver.interrupt();
            }

            this.ioLock.unlock();
        }
    }

    private void login(){

        try{
            this.loginLock.lock();
            while(this.clientId == null){

                var credentials = this.io.login();
                StringBuilder sb = new StringBuilder(credentials.getA()).
                                       append("\0").
                                       append(credentials.getB());
                String s = sb.toString();

                this.wr.addToSendQueue(MessageType.LOGIN_REQUEST, s);

                while(this.clientId == null)
                    this.loginCond.await();
            }
            this.loginLock.unlock();
        }
        catch(InterruptedException e){
            this.io.err(e.getMessage());
            this.loginLock.unlock();
        }
    }

    public void makeReservation(){
        this.flightsLock.lock();

        String flight = this.io.showAvailableCities(this.flights.
                                                        stream().
                                                        map(f -> f.split(" ->")[0]).
                                                        toList());

        this.flightsLock.unlock();

        if(flight != null)
            this.wr.addToSendQueue(MessageType.MAKE, this.clientId + "\0" + flight);
    }

    public void cancelReservation(){
        String id = this.io.cancel();

        if(id != null)
            this.wr.addToSendQueue(MessageType.CANCEL, this.clientId + "\0" + id);
    }

    public void getFlightsList(){
        this.flightsLock.lock();

        this.io.showFlights(this.flights);

        this.flightsLock.unlock();
    }

    public void showNotifs(){
        this.ioLock.lock();

        this.io.showNotifs();

        this.ioLock.unlock();
    }

    public void newFlight(){
        String s = this.io.newFlight();

        this.wr.addToSendQueue(MessageType.NEW, this.clientId + "\0" + s);
    }

    public void closeDay(){
        LocalDate ld = this.io.readDate();

        this.wr.addToSendQueue(MessageType.CLOSE, this.clientId + "\0" + ld.toString());
    }
}

/**
 * LOGIN_REQUEST + username + '\0' + password            //cliente
 * LOGIN_FAIL + err_msg                                  //server
 * LOGIN_SUCESS + sessionId                              //server
 *
 * ===//===
 *
 * USER_TYPE + usertype                                  //server
 * FLIGHTS_LIST + flight1 + '\0' + flight2 + ...         //server
 * NOTIF + message                                       //server
 *
 * MAKE, CANCEL, CLOSE, NEW + '\0' + string
 *
 * QUIT + client_id                                      //client
 */