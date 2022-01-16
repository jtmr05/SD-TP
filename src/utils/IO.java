package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import client.ClientController;
import static common.Consts.UserType;

public class IO {

    private final Scanner sc;
    private Menu menu;
    private MenuHandler[] handlers;
    private final List<String> unread;
    private final List<String> read;


    public IO(){
        this.sc = new Scanner(System.in);
        this.menu = null;
        this.handlers = null;
        this.unread = new LinkedList<>();
        this.read = new LinkedList<>();
    }

    public void println(String s){
        System.out.println(s);
    }

    public void print(String s){
        System.out.print(s);
    }

    private void clear(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void enterKeyEvent(){
        this.print("Press enter to continue> ");
        this.sc.nextLine();
        this.clear();
    }

    public void err(String s){
        System.err.println(s);
        this.enterKeyEvent();
    }

    public void setMainMenu(UserType t, ClientController c){

        String[] ops;

        switch(t){

            case REGULAR -> {
                ops = new String[]{
                    "Make reservation",
                    "Cancel reservation",
                    "Get flights list",
                    "Show notifications",
                    "Clear notifications"
                };

                int i = 0;
                this.handlers = new MenuHandler[ops.length];
                this.handlers[i++] = c::makeReservation;
                this.handlers[i++] = c::cancelReservation;
                this.handlers[i++] = c::getFlightsList;
                this.handlers[i++] = c::showNotifs;
                this.handlers[i++] = c::clearNotifs;
            }

            case ADMIN -> {
                ops = new String[]{
                    "Make reservation",
                    "Cancel reservation",
                    "Get flights list",
                    "Show notifications",
                    "Clear notifications",
                    "New flight",
                    "Close day"
                };

                int i = 0;
                this.handlers = new MenuHandler[ops.length];
                this.handlers[i++] = c::makeReservation;
                this.handlers[i++] = c::cancelReservation;
                this.handlers[i++] = c::getFlightsList;
                this.handlers[i++] = c::showNotifs;
                this.handlers[i++] = c::clearNotifs;
                this.handlers[i++] = c::newFlight;
                this.handlers[i++] = c::closeDay;
            }

            default ->
                ops = null;
        }

        if(!this.isMenuAvailable())
            this.menu = new Menu("*** Flight Reservation ***", ops);
    }

    public int runMainMenu(){
        this.clear();
        int op;

        if((op = this.menu.run()) > 0)
            this.handlers[op-1].execute();

        return op;
    }

    public void showFlights(Collection<String> flights){

        int i = 0;

        for(String s : flights)
            this.println("\t**"+(i++)+". " + s);
        this.enterKeyEvent();
    }

    public String showAvailableCities(Collection<String> cities){

        int i = 0;

        for(String s : cities)
            this.println("\t**"+(i++)+". " + s);

        this.print("Type the cities you wish to fly to (separated by spaces)> ");

        return this.sc.nextLine();
    }

    public void showNotifs(){

        int i = 0;

        final int size = this.unread.size();

        for(; i < size; i++){
            String s = this.unread.remove(0);
            this.read.add(0, s);
        }

        i = 0;
        for(String s : this.read)
            this.println("\t**"+(i++)+". " + s);


        this.enterKeyEvent();
    }

    public void clearNotifs(){
        this.read.clear();
    }

    public Pair<String, String> login(){

        this.println("*** Login ***");

        this.print("Username: ");
        String username = this.sc.nextLine();

        this.print("Password: ");
        String password = new String(this.sc.nextLine());

        return new Pair<>(username, password);
    }

    public void addNotif(String s) {
        this.unread.add(0, s);
    }

    public void leave(){
        this.println("Goodbye...");
    }

    public boolean isMenuAvailable(){
        return this.menu != null;
    }

    public String cancel(){
        this.clear();
        this.print("Id of reservation to cancel> ");
        return this.sc.nextLine();
    }

    public String newFlight(){
        this.clear();

        String src, dst;

        this.print("Flight origin: ");
        src = this.sc.nextLine();

        this.print("Flight destination: ");
        dst = this.sc.nextLine();

        return src + " -> " + dst;
    }

    public LocalDate readDate(){
        this.print("Date to cancel flights: ");
        return LocalDate.parse(this.sc.nextLine(), DateTimeFormatter.ISO_DATE);
    }
}