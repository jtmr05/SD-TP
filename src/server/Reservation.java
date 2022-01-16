package server;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Reservation {

    private final String clientID;
    private final long reservationID;
    private final LocalDate reservationDate;
    private List<Flight> flights;
    private final Lock lock;

    Reservation(String clientID, long reservationID, LocalDate reservationDate) {
        this.clientID = clientID;
        this.reservationID = reservationID;
        this.reservationDate = reservationDate;
        this.flights = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    Reservation(String clientID, long reservationID, LocalDate reservationDate, Flight f) {
        this(clientID, reservationID, reservationDate);
        this.flights.add(f);
    }

    void addFlight(Flight f){
        this.lock.lock();
        if (this.flights.size() == 0)
            this.flights = new ArrayList<>();
        this.flights.add(f);
        this.lock.unlock();
    }

    void addFlights(Object... fs){
        this.lock.lock();
        if (this.flights.size() == 0)
            this.flights = new ArrayList<>();

        for(Object f : fs)
            if(f instanceof Flight)
                this.flights.add((Flight) f);

        this.lock.unlock();
    }

    void cancel(LocalDate date){
        this.lock.lock();
        for(Flight f : this.flights)
            f.cancelSeat(date);
        this.lock.unlock();
    }

    String getClientID() {
        return this.clientID;
    }

    long getReservationID() {
        return this.reservationID;
    }

    LocalDate getReservationDate() {
        return this.reservationDate;
    }
}
