package server;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Flight {

    private static final int MAX_ALLOWED_PASSENGERS = 200;

    private final String src;
    private final String dst;
    private final Map<LocalDate, Integer> booked; //map each day to the number of used seats on that day
    private final Lock lock;

    Flight(String src, String dst){
        this.src = src;
        this.dst = dst;
        this.booked = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    String getSrc(){
        return this.src;
    }

    String getDst(){
        return this.dst;
    }

    boolean bookSeat(LocalDate date){
        try{
            this.lock.lock();
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
        finally{
            this.lock.unlock();
        }
    }

    void cancelSeat(LocalDate date){
        this.lock.lock();
        Integer count = this.booked.get(date);

        if(count != null && count > 0)
            count--;

        this.lock.unlock();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(this.src).append(" -> ").append(this.dst);
        return sb.toString();
    }

    static Flight fromString(String s){
        String[] args = s.split(" -> ");
        return new Flight(args[0], args[1]);
    }
}
