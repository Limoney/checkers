package com.infa.network;

import game.Board;

import java.util.Scanner;

public class ServerMain
{
    public static void main(String[] args)
    {
        System.out.println("com.infa.network.Server");
        Server srv = new Server();
        srv.startListening(25565);

        Board a = new Board();
        a.show();

        Scanner s = new Scanner(System.in);
        boolean run = true;
        while(run)
        {
            String cmd = s.next();
            switch(cmd)
            {
                case "exit":
                    run = false;
                    srv.close();
                break;
                case "rooms":
                    if(srv.rooms.size()!=0)
                    {
                        srv.rooms.forEach(e->{
                            System.out.println("room "+e.id);
                            System.out.println("ref1 id " +e.ref1.getId());
                            if(e.ref2!=null)System.out.println("ref2 id "+e.ref2.getId());
                        });
                    }
                    else System.out.println("no open rooms");
            }
        }
    }
}

/*TODO
thread nie jest Serializable więc go nie wysle
mysze zmienic rooma albo wysylać cos innego - done

 */