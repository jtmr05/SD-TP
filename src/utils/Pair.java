package utils;

public class Pair<A, B> {

    private final A a;
    private final B b;

    public Pair(A a, B b){
        this.a = a;
        this.b = b;
    }

    public A getA(){
        return this.a;
    }

    public B getB(){
        return this.b;
    }

    public boolean equals(Object o){
        if(o == this)
            return true;

        if(o == null || o.getClass() != this.getClass())
            return false;

        Pair<A, B> that = (Pair<A, B>) o;
        return that.getA().equals(this.a) && that.getB().equals(this.b);
    }
}
