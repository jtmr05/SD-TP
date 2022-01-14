package server;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private long clientID;
    private long reservationID;
    private LocalDate reservationDate;
    private List<Flight> flights;

    public Reservation(long clientID, long reservationID, LocalDate reservationDate) {
        this.clientID = clientID;
        this.reservationID = reservationID;
        this.reservationDate = reservationDate;
        this.flights = new ArrayList<>();
    }

    public Reservation(long clientID, long reservationID, LocalDate reservationDate, Flight f) {
        this.clientID = clientID;
        this.reservationID = reservationID;
        this.flights = new ArrayList<>();
        this.reservationDate = reservationDate;
        this.flights.add(f);
    }

    public void addFlight(Flight f){
        if (this.flights.size() == 0)
            this.flights = new ArrayList<>();
        this.flights.add(f);
    }

    public long getClientID() {
        return clientID;
    }

    public long getReservationID() {
        return reservationID;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

}
