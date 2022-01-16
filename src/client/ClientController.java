package client;

import common.TaggedConnection;
import utils.IO;

import static common.Consts.MessageType;
import static common.Consts.UserType;


import java.io.IOException;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

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
    private final Condition flightsCond;
    private final Condition ioCond;
    private boolean loginSleepFlag;


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
                            ioCond.signalAll();
                            ioLock.unlock();
                        }

                        case FLIGHTS_LIST -> {
                            flightsLock.lock();

                            String[] flights__ = frame.getDataAsString().split("\0");

                            flights.clear();
                            for(String s : flights__)
                                flights.add(s);

                            flightsCond.signal();

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
                            loginSleepFlag = false;
                            loginCond.signalAll();
                            loginLock.unlock();
                        }

                        case LOGIN_SUCESS -> {
                            loginLock.lock();
                            clientId = frame.getDataAsString();
                            loginSleepFlag = false;
                            loginCond.signalAll();
                            loginLock.unlock();
                        }

                        default -> {}
                    }
                }
            }
            catch(IOException e){
                if(!(e instanceof SocketException))
                    e.printStackTrace();
            }
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
        this.flightsCond = this.flightsLock.newCondition();
        this.ioCond = this.ioLock.newCondition();
        this.loginSleepFlag = true;
        (this.receiver = new Thread(new ClientReceiver(tc))).start();
    }

    void start(){

        this.login();

        boolean flag = true;

        this.ioLock.lock();

        while(!this.io.isMenuAvailable())
            try{
                this.ioCond.await();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        this.ioLock.unlock();

        while(flag){
            int op = this.io.runMainMenu();

            if(op == 0){
                flag = false;
                this.wr.addToSendQueue(MessageType.QUIT, this.clientId);
                this.io.leave();
                this.receiver.interrupt();
            }
        }
    }

    private void login(){

        try{
            this.loginLock.lock();


            while(this.clientId == null){
                this.loginSleepFlag = true;

                var credentials = this.io.login();
                StringBuilder sb = new StringBuilder(credentials.getA()).
                                       append("\0").
                                       append(credentials.getB());
                String s = sb.toString();

                this.wr.addToSendQueue(MessageType.LOGIN_REQUEST, s);

                while(this.loginSleepFlag)
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

        var set = new HashSet<String>();
        Consumer<String[]> arrayConsumer = pair -> {
            set.add(pair[0]);
            set.add(pair[1]);
        };

        this.flights.stream().
                     map(f -> f.split(" -> ")).
                     forEach(arrayConsumer);
        String flight = this.io.showAvailableCities(set);

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

        this.flights.clear();
        this.wr.addToSendQueue(MessageType.GET, this.clientId);

        while(this.flights.size() == 0)
            try {
                this.flightsCond.await();
            }
            catch(InterruptedException e){
                this.io.err(e.getMessage());
            }

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

    public void clearNotifs(){
        this.ioLock.lock();

        this.io.clearNotifs();

        this.ioLock.unlock();
    }
}