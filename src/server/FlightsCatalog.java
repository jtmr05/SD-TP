package server;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlightsCatalog {

    private List<Flight> flightsCatalog;
    private final Lock lock;

    private FlightsCatalog() {
        this.flightsCatalog = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public void addFlight(Flight f){
        try{
            this.lock.lock();
            if(this.flightsCatalog.size() == 0)
                this.flightsCatalog = new ArrayList<>();
            this.flightsCatalog.add(f);
        }
        finally{
            this.lock.unlock();
        }
    }

    public Set<String> allFlightsAvailable(){
        try{
            this.lock.lock();
            Set<String> res = new TreeSet<>();
            for(Flight f : this.flightsCatalog){
                StringBuilder sb = new StringBuilder();
                sb.append(f.toString());
                res.add(sb.toString());
            }
            return res;
        }
        finally{
            this.lock.unlock();
        }
    }

    LocalDate getAvailableDate(String src, String dst, int offset){

        LocalDate ld = null;

        for(Flight f : this.flightsCatalog)
            if(f.getSrc().equalsIgnoreCase(src) && f.getDst().equalsIgnoreCase(dst)){

                ld = LocalDate.now().plusDays(offset);
                int d = 0;

                do{
                    ld.plusDays(d++);
                }
                while(!f.bookSeat(ld));
            }

        return ld;
    }

    Flight getFlight(String src, String dst){

        try{
            this.lock.lock();
            for(Flight f : this.flightsCatalog)
                if(f.getSrc().equalsIgnoreCase(src) && f.getDst().equalsIgnoreCase(dst))
                    return f;

            return null;
        }
        finally{
            this.lock.unlock();
        }
    }

    private static FlightsCatalog singleton = null;

    public static FlightsCatalog getInstance(){
        if(singleton == null)
            singleton = new FlightsCatalog();

        return singleton;
    }
}
