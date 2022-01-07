package client;

import java.util.Scanner;

import utils.Menu;
import utils.Pair;

class IO {

    private final Scanner sc;
    private Menu menu;

    IO(){
        this.sc = new Scanner(System.in);
        this.menu = null;
    }

    void println(String s){
        System.out.println(s);
    }

    void print(String s){
        System.out.print(s);
    }

    private void clear(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    void err(String s){
        System.err.println(s);
        System.out.print("Press enter to continue> ");
        sc.nextLine();
        this.clear();
    }

    boolean setMainMenu(Menu m){
        boolean b = this.menu == null;
        if(b)
            this.menu = m;
        return b;
    }

    int runMainMenu(){
        return this.runMenu(this.menu);
    }

    int runMenu(Menu m){
        return m.run();
    }

    Pair<String, String> login(){

        this.println("*** Login ***");

        this.print("Username: ");
        String username = this.sc.nextLine();

        this.print("Password: ");
        String password = new String(System.console().readPassword());

        return new Pair<>(username, password);
    }

    void addNotif(String workingString) {

    }
}