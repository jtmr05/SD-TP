package utils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

public class Menu {

    private final String title;
    private final String[] options;
    private final Scanner sc;

    public Menu(String title, String[] options){
        this.title = title;
        this.options = new String[options.length];
        System.arraycopy(options, 0, this.options, 0, options.length);
        this.sc = new Scanner(System.in);
    }

    public Menu(String[] options){
        this("*** Menu ***", options);
    }

    public Menu(String title, Collection<String> options){
        this.title = title;
        this.options = new String[options.size()];
        this.sc = new Scanner(System.in);

        Iterator<String> iter = options.iterator();
        for(int i = 0; i < this.options.length && iter.hasNext(); i++)
            this.options[i] = iter.next();
    }

    public Menu(Collection<String> options){
        this("*** Menu ***", options);
    }

    private void display(){
        System.out.println(this.title+"\n");

        for(int i = 0; i < this.options.length; i++)
            System.out.println((i+1)+". "+this.options[i]);

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

    public int run(){
        this.display();
        return this.readOption();
    }

    public static Menu deserialize(byte[] data){

        String[] args = new String(data, StandardCharsets.UTF_8).split("\0", 2),
                 options = new String[0];
        String title = args[0];

        if(args.length > 1)
            options = args[1].split("\0");

        return new Menu(title, options);
    }

    public static Menu deserialize(String s){

        String[] args = s.split("\0", 2),
                 options = new String[0];
        String title = args[0];

        if(args.length > 1)
            options = args[1].split("\0");

        return new Menu(title, options);
    }

    public String getAsString(){
        StringBuilder sb = new StringBuilder(this.title);
        for(String s : this.options)
            sb.append("\0").append(s);
        return sb.toString();
    }
}