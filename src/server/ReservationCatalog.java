package server;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReservationCatalog {
    private Map<Long, Reservation> reservationCatalog; // Key: reservationID ; Object: Reservation

    public ReservationCatalog() {
        this.reservationCatalog = new HashMap<>();
    }

    public long addReservation(Reservation r){
        if (this.reservationCatalog.size() == 0)
            this.reservationCatalog = new HashMap<>();
        this.reservationCatalog.put(r.getReservationID(),r);

        return r.getReservationID();
    }
    public void removeReservation(long reservationID){
        this.reservationCatalog.remove(reservationID);
    }

    public void cancelDay(LocalDate date){
        for(Reservation r : this.reservationCatalog.values())
            if(r.getReservationDate().equals(date)) this.reservationCatalog.remove(r.getReservationID());
    }
}
