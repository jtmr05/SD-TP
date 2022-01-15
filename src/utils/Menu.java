package utils;

import java.util.Scanner;

public class Menu {

    private final String title;
    private final String[] options;
    private final Scanner sc;

    Menu(String title, String[] options){
        this.title = title;
        this.options = new String[options.length];
        System.arraycopy(options, 0, this.options, 0, options.length);
        this.sc = new Scanner(System.in);
    }

    Menu(String[] options){
        this("*** Menu ***", options);
    }

    private void display(){
        System.out.println(this.title+"\n");

        for(int i = 0; i < this.options.length; i++)
            System.out.println((i+1) + ". " + this.options[i]);

        System.out.print("0. Exit\n> ");
    }

    private int readOption(){

        int op = -1;

        while(op < 0 || op > this.options.length)
            try{
                op = Integer.parseInt(this.sc.nextLine());

                if(op < 0 || op > this.options.length)
                    System.out.print("Invalid input. Number out of bounds.\n> ");
            }
            catch(NumberFormatException e){
                System.out.print("Invalid input. Not a valid integer.\n> ");
                op = -1; //redundant...
            }

        return op;
    }

    int run(){
        this.display();
        return this.readOption();
    }

    public String getTitle() {
        return this.title;
    }
}