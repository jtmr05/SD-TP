package server;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import common.Frame;
import common.TaggedConnection;
import common.Consts.MessageType;

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
        this.sessionId = "";
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

                Frame frame = this.tc.receive();
                String s = frame.getDataAsString();

                String[] args = s.split("\0");
                if(!this.sessionId.equals(args[0]))
                    continue;

                switch(frame.tag){

                    case MAKE -> {

                        String[] cities = args[1].split(" ");
                        List<Flight> flights = new ArrayList<>();

                        for(int i = 0; i < cities.length - 1; i++){

                            var f = this.flightsCatalog.getFlight(cities[i], cities[i+1]);

                            if(f == null){
                                flights = null;
                                break;
                            }
                            else
                                flights.add(f);
                        }


                        if(flights != null){

                            LocalDate date = LocalDate.now();
                            int offset = 0;
                            final int size = flights.size();

                            System.out.println("before loop");
                            for(int i = 0; i < size; i++){

                                var f = flights.get(i);
                                date.plusDays(offset);

                                if(!f.bookSeat(date)){
                                    for(int j = 0; j < i; j++)
                                        flights.get(j).cancelSeat(date);

                                    offset++;
                                    i = -1;
                                }
                            }
                            System.out.println("after loop");


                            Reservation r = new Reservation(this.sessionId, IdGen.get(), date);
                            r.addFlights(flights.toArray());
                            long id = this.reservationCatalog.addReservation(r);

                            this.tc.send(MessageType.NOTIF,
                                "Reservation "+ id +" on day "+date.toString());
                        }
                    }

                    case GET -> {
                        var iter = this.flightsCatalog.allFlightsAvailable().iterator();

                        StringBuilder sb = new StringBuilder();

                        while(iter.hasNext()){
                            sb.append(iter.next());
                            if(iter.hasNext())
                                sb.append("\0");
                        }

                        this.tc.send(MessageType.FLIGHTS_LIST, sb.toString());
                    }

                    case NEW -> {
                        Flight f = Flight.fromString(args[1]);
                        this.flightsCatalog.addFlight(f);
                        this.tc.send(MessageType.NOTIF, "Flight created");
                    }

                    case CANCEL -> {
                        this.reservationCatalog.removeReservation(Long.parseLong(args[1]));
                        this.tc.send(MessageType.NOTIF, "Reservation "+ args[1] +" cancelled");
                    }

                    case CLOSE -> {
                        this.reservationCatalog.cancelDay(LocalDate.parse(args[1], DateTimeFormatter.ISO_LOCAL_DATE));
                    }

                    case QUIT -> {
                        loggedIn = false;
                    }

                    default -> {}
                }
            }

            this.tc.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
