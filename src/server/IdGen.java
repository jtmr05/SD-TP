package server;

class IdGen {

    private static volatile long ID;

    static long get(){
        try{
            return IdGen.ID;
        }
        finally{
            IdGen.ID++;
        }
    }
}
