package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlightsCatalog {

    private final List<Flight> flightsCatalog;
    private final Lock lock;

    private FlightsCatalog() {
        this.flightsCatalog = new ArrayList<>();
        this.flightsCatalog.add(new Flight("Tokyo", "Lisbon"));
        this.flightsCatalog.add(new Flight("Lisbon", "Tokyo"));
        this.flightsCatalog.add(new Flight("Berlin", "Tokyo"));
        this.flightsCatalog.add(new Flight("Lisbon", "Paris"));
        this.lock = new ReentrantLock();
    }

    public void addFlight(Flight f){
        try{
            this.lock.lock();
            this.flightsCatalog.add(f);
            System.out.println(f.toString() + " was created and added");
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
