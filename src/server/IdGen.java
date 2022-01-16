package server;

class IdGen {

    private static volatile long ID = 0;

    static long get(){
        try{
            return IdGen.ID;
        }
        finally{
            IdGen.ID++;
        }
    }
}
