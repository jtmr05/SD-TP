package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FlightsCatalog {
    private List<Flight> flightsCatalog;

    public FlightsCatalog() {
        this.flightsCatalog = new ArrayList<>();
    }

    public void addFlight(Flight f){
        if (this.flightsCatalog.size() == 0) this.flightsCatalog = new ArrayList<>();
        this.flightsCatalog.add(f);
    }

    public Set<String> allFlightsAvailable(){
        Set<String> res = new TreeSet<>();
        for(Flight f : this.flightsCatalog){
            StringBuilder sb = new StringBuilder();
            sb.append(f.getSrc()).append(" -> ").append(f.getDst());
            res.add(sb.toString());
        }
        return res;
    }
}
