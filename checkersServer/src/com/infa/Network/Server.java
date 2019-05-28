package com.infa.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server
{
    protected ServerSocket server;
    //protected ArrayList<com.infa.network.Connection> clients;

    protected Thread listeningThread;
    protected boolean keepListening;

    //protected ArrayList<Thread> reciveThreads;
    protected boolean keepReceiving;

    protected HashMap<Connection, Thread> clients_;

    protected ArrayList<Room> rooms;

    protected static int nextConnectionId=0;

    protected boolean isClosed;//moze i troche redundant

    public Server()
    {
        keepListening = true;
        keepReceiving = true;
        isClosed = false;
        //reciveThreads = new ArrayList<Thread>();
        rooms = new ArrayList<Room>();
    }

    public void startListening(int port)
    {
        keepListening = true;
        keepReceiving = true;
        listeningThread = new Thread()
        {
            public void run()
            {
                try
                {
                    server = new ServerSocket(port);
                    clients_ = new HashMap<Connection, Thread>();
                    //clients = new ArrayList<com.infa.network.Connection>();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                System.out.println("com.infa.network.Server Listening on port " + port);
                while(keepListening)
                {
                    try
                    {
                        Socket client = server.accept();
                        Connection c = new Connection();
                        c.setId(nextConnectionId++);
                        System.out.println("Client has joined on port " + client.getPort()+". com.infa.network.Connection id "+c.getId());
                        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                        c.setOut(out);
                        c.setIn(in);
                        c.setSocket(client);
                        clients_.put(c,addReciveThread(c));
                        clients_.get(c).start();
                        //c.send(new Packet(Packet.HEADER_CHAT_MESSAGE,"com.infa.network.Server: hello"));
                    }
                    catch (IOException e)
                    {
                        if(keepListening)e.printStackTrace();
                    }
                }
            }
        };
        listeningThread.start();
    }

    public void sendToAll(Packet data)
    {
        for(Connection c : clients_.keySet())
        {
            c.send(data);
        }
    }

    public void send(int id,Packet data)
    {
        for(Connection c : clients_.keySet())
        {
            if(c.getId()==id)
            {
                c.send(data);
                break;
            }
        }
    }

    public int getClientCount()
    {
        return clients_.size();
    }

//    public void startReceiving()
//    {
//        for(com.infa.network.Connection c : clients)
//        {
//            addReciveThread(c);
//        }
//    }

    protected Thread addReciveThread(Connection c)
    {
        return new Thread()
        {
            public boolean live = true;
            @Override
            public void run()
            {
                while(live)
                {
                    try
                    {
                        Packet data = (Packet)c.getIn().readObject();

                        byte header = data.getHeader();
                        switch (header)
                        {
                            case Packet.HEADER_CHAT_MESSAGE: System.out.println(data.getData());
                            break;
                            case Packet.HEADER_REQUEST_ROOM_LIST:
                            {
                                ArrayList<Integer> openRooms = new ArrayList<Integer>();
                                for(Room r : rooms)
                                {
                                    if(r.ref2==null) openRooms.add(r.id);
                                }
                                Packet p = new Packet(Packet.HEADER_RESPONSE_ROOM_LIST,openRooms);
                                c.send(p);
                            }
                            break;
                            case Packet.HEADER_REQUEST_NEW_ROOM:
                            {
                                //i wil refactor all of this this later
                                //just want it to work
                                //check if user isn't already in room
                                Room tmp;
                                if(c.getCurrentRoomRef()==null)
                                {
                                    tmp = new Room();
                                    tmp.ref1 = c;
                                    rooms.add(tmp);
                                    c.setCurrentRoomRef(tmp);
                                }
                                else
                                {
                                    //after user leaves room will become empty so it will be removed
                                    if(c.getCurrentRoomRef().ref2==null)
                                    {
                                        tmp = new Room();
                                        tmp.ref1 = c;
                                        rooms.add(tmp);
                                        leaveRoom(c);
                                        c.setCurrentRoomRef(tmp);
                                    }
                                    //user is not alone in room so it will stay open
                                    //this is here in case user manages to send this header while in room
                                    else
                                    {
                                        leaveRoom(c);
                                        tmp = new Room();
                                        tmp.ref1 = c;
                                        rooms.add(tmp);
                                        c.setCurrentRoomRef(tmp);
                                    }
                                    Packet p = new Packet();
                                    p.setHeader(Packet.HEADER_RESPONSE_NEW_ROOM);
                                    p.setData(tmp.getBoard());
                                    c.send(p);//tu kończe bo lenia mam - zrobić odbiór po drugiej stronie
                                }
                            }
                            break;
                            case Packet.HEADER_REQUEST_LEAVE_ROOM:
                            {
                                leaveRoom(c);
                            }
                            break;
                            case Packet.HEADER_REQUEST_JOIN_ROOM:
                            {
                                int roomId;
                                try
                                {
                                    roomId = Integer.parseInt(data.getData().toString());
                                }
                                catch(NumberFormatException e)
                                {
                                    Packet p = new Packet(Packet.HEADER_RESPONSE_ERROR,"Invalid room ID");
                                    c.send(p);
                                    break;
                                }

                                Room tmp = null;
                                for (Room e : rooms)
                                {
                                    if (e.id == roomId) tmp = e;
                                    c.setCurrentRoomRef(e);
                                    Packet p = new Packet(Packet.HEADER_RESPONSE_JOIN_ROOM,"replace me with board object");
                                    c.send(p);
                                }
                                if(tmp!=null)
                                {
                                    if(tmp.ref2==null)
                                    {
                                        tmp.ref2 = c;
                                        Packet p = new Packet(Packet.HEADER_RESPONSE_JOIN_ROOM,"replace me with board object");
                                        c.send(p);
                                    }
                                    else
                                    {
                                        //send error
                                        Packet p = new Packet(Packet.HEADER_RESPONSE_ERROR,"Room is full");
                                        c.send(p);
                                    }
                                }
                                else
                                {
                                    //send error
                                    Packet p = new Packet(Packet.HEADER_RESPONSE_ERROR,"Unable to find room");
                                    c.send(p);
                                }
                                break;
                            }
                        }

                        //send to all
//                        if( data.toString().charAt(data.toString().length()-1) =='>')//> temp
//                        {
//                            for(com.infa.network.Connection cc : clients_.keySet())
//                            {
//                                if(cc.getId()!=c.getId())
//                                {
//                                    cc.send(data);
//                                    break;
//                                }
//                            }
//                        }
                    }
                    catch (IOException | ClassNotFoundException e)
                    {
                        leaveRoom(c);
                        System.out.println("Client with connection id: "+c.getId()+" disconnected");
                        //reciveThreads.remove(c);
                        clients_.remove(c);
                        //keepReceiving = false;
                        live = false;
                    }
                }
            }
        };
        //reciveThreads.add(reciveThread);
    }

    public void close()
    {
        try
        {
            keepListening=false;
            keepReceiving=false;

            for(Connection c : clients_.keySet())
            {
                c.close();
            }
            clients_.clear();
            server.close();
            isClosed = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("com.infa.network.Server closed.");
    }

    public void closeAllConnections()
    {
        for(Connection c : clients_.keySet())
        {
            c.close();
        }
        clients_.clear();
    }

    public boolean isClosed()
    {
        return isClosed;
    }

    private void leaveRoom(Connection c)
    {
        if(c.getCurrentRoomRef()==null) return;
        if(c.getCurrentRoomRef().ref2==null)
        {
            rooms.remove(c.getCurrentRoomRef());
            c.setCurrentRoomRef(null);
        }
        else
        {
            if(c.getCurrentRoomRef().ref1.equals(c))
            {
                c.getCurrentRoomRef().ref1 = c.getCurrentRoomRef().ref2;
                c.getCurrentRoomRef().ref2 = null;
                c.setCurrentRoomRef(null);
            }
            else
            {
                c.getCurrentRoomRef().ref2 = null;
                c.setCurrentRoomRef(null);
            }
            System.out.println(c.getId()+" left ");
        }
    }

}
