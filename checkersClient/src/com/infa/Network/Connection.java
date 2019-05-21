package com.infa.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Connection implements Serializable
{
    protected Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;

    protected Thread reciveThread;
    protected boolean keepReceiving;

    protected int id;
    protected boolean isConnected;

    protected ConnectionStates state;

    protected Room currentRoomRef;

    protected Packet recivedData;
    protected boolean isDataReady;


    public Connection()
    {
        state = ConnectionStates.FRESH;
        currentRoomRef = null;
        keepReceiving = true;
        isConnected = false;
    }

    public void connect(String ip, int port)
    {
        try
        {
            socket = new Socket(ip,port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            state = ConnectionStates.CONNECTED;
            startReceiving();
        }
        catch (IOException e)
        {
            System.err.println("Unable to connect to the server");
        }
    }

    public void send(Serializable data)
    {
        try
        {
            out.writeObject(data);
            isDataReady = false;
        }
        catch (IOException e)
        {
            System.out.println("Socket not connected to the server.");
        }
    }

//    public void sendToAll(Packet data)
//    {
//        //todo jak zrobie klase pakietu
//        try
//        {
//            out.writeObject(data);
//        }
//        catch (IOException e)
//        {
//            System.out.println("Socket not connected to the server");
//        }
//    }

    protected void startReceiving()
    {
        reciveThread = new Thread()
        {
            @Override
            public void run()
            {
                while(keepReceiving)
                {
                    try
                    {
                        recivedData = (Packet) in.readObject();
                        isDataReady = true;

                        //byte header = data.getHeader();
//                        switch (header)
//                        {
//                            case Packet.HEADER_CHAT_MESSAGE: System.out.println(data.getData());
//                        break;
//                        case Packet.HEADER_RESPONSE_ROOM_LIST:
//                            ArrayList<Integer>  rooms = (ArrayList<Integer>) data.getData();
//                            if(rooms.size()!=0)
//                            {
//                                rooms.forEach(e->{
//                                    System.out.println("room "+e);
//                                });
//                            }
//                            else System.out.println("no open rooms");
//                            break;
//                        case Packet.HEADER_RESPONSE_ERROR:
//                            System.out.println(data.getData());
//                            break;
//                        }

                    }
                    catch (IOException | ClassNotFoundException e)
                    {
                        keepReceiving=false;
                        try
                        {
                            socket.close();
                        }
                        catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                        if(state!=ConnectionStates.LOCALLY_CLOSED)state = ConnectionStates.REMOTELY_CLOSED;
                        isConnected = false;
                        System.out.println("Connection with server has been broken");
                        e.printStackTrace();
                    }
                }

            }
        };
        reciveThread.start();
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
    }

    public ObjectOutputStream getOut()
    {
        return out;
    }

    public void setOut(ObjectOutputStream out)
    {
        this.out = out;
    }

    public ObjectInputStream getIn()
    {
        return in;
    }

    public void setIn(ObjectInputStream in)
    {
        this.in = in;
    }

    public boolean isKeepReceiving()
    {
        return keepReceiving;
    }

    public void setKeepReceiving(boolean keepReceiving)
    {
        this.keepReceiving = keepReceiving;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public void close()
    {
        keepReceiving=false;
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        state = ConnectionStates.LOCALLY_CLOSED;
    }

    public boolean isConnected(){return isConnected;}

    public ConnectionStates getState()
    {
        return state;
    }

    public Room getCurrentRoomRef()
    {
        return currentRoomRef;
    }

    public void setCurrentRoomRef(Room currentRoomRef)
    {
        this.currentRoomRef = currentRoomRef;
    }

    public Packet getRecivedData()
    {
        return recivedData;
    }

    public void setRecivedData(Packet recivedData)
    {
        this.recivedData = recivedData;
    }

    public boolean isDataReady()
    {
        return isDataReady;
    }

    public void setDataReady(boolean dataReady)
    {
        isDataReady = dataReady;
    }


}
