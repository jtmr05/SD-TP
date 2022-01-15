package server;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

class Flight {

    private static final int MAX_ALLOWED_PASSENGERS = 200;

    private final String src;
    private final String dst;
    private Map<LocalDate, Integer> booked; //map each day to the number of used seats on that day

    Flight(String src, String dst){
        this.src = src;
        this.dst = dst;
        this.booked = new HashMap<>();
    }

    String getSrc(){
        return this.src;
    }

    String getDst(){
        return this.dst;
    }

    boolean bookSeat(LocalDate date){
        Integer count = this.booked.get(date);
        boolean b = true;

        if(count == null)
            this.booked.put(date, 1);
        else{
            b = count < MAX_ALLOWED_PASSENGERS;
            if(b)
                count++;
        }

        return b;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.src).append(" -> ").append(this.dst);
        return sb.toString();
    }

}

/*
class Teste{
    public static void main(String[] args) {
        LocalDate ld = LocalDate.parse("2007-11-12", DateTimeFormatter.ISO_DATE);
        System.out.println(ld);
    }
} */