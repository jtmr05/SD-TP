package server;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReservationCatalog {

    private Map<Long, Reservation> reservationCatalog; // Key: reservationID ; Object: Reservation
    private final Lock lock;

    private ReservationCatalog() {
        this.reservationCatalog = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public long addReservation(Reservation r){
        try{
            this.lock.lock();
            if (this.reservationCatalog.size() == 0)
                this.reservationCatalog = new HashMap<>();
            this.reservationCatalog.put(r.getReservationID(),r);

            return r.getReservationID();
        }
        finally{
            this.lock.unlock();
        }
    }
    public void removeReservation(long reservationID){
        this.lock.lock();
        this.reservationCatalog.remove(reservationID);
        this.lock.unlock();
    }

    public Set<Reservation> cancelDay(LocalDate date){

        try{
            this.lock.lock();

            Set<Reservation> ret = new HashSet<>();

            for(Map.Entry<Long, Reservation> entry : this.reservationCatalog.entrySet()){
                var r = entry.getValue();
                if(r.getReservationDate().equals(date)){
                    r.cancel(date);
                    ret.add(this.reservationCatalog.remove(entry.getKey()));
                }
            }
            return ret;
        }
        finally{
            this.lock.unlock();
        }
    }

    private static ReservationCatalog singleton = null;

    public static ReservationCatalog getInstance(){
        if(singleton == null)
            singleton = new ReservationCatalog();

        return singleton;
    }
}
