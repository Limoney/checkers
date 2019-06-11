package com.infa.network;

import java.io.Serializable;
import java.math.BigInteger;

public class Packet implements Serializable
{
    private int header;
    private Serializable data;

    public static final int HEADER_REQUEST_ROOM_LIST = 1;
    public static final int HEADER_RESPONSE_ROOM_LIST = 2;
    public static final int HEADER_REQUEST_JOIN_ROOM = 4;
    public static final int HEADER_RESPONSE_JOIN_ROOM = 8;
    public static final int HEADER_REQUEST_NEW_ROOM = 16;
    public static final int HEADER_RESPONSE_NEW_ROOM = 32;
    public static final int HEADER_REQUEST_LEAVE_ROOM = 64;
    public static final int HEADER_CHAT_MESSAGE = 128;
    public static final int HEADER_RESPONSE_ERROR = 256;
    public static final int HEADER_RESPONSE_USER_HAS_JOINED = 512;
    public static final int HEADER_RESPONSE_USER_HAS_LEFT = 1024;

    public Packet()
    {

    }

    public Packet(int header, Serializable data)
    {
        this.header = header;
        this.data = data;
    }

    public int getHeader()
    {
        return header;
    }

    public void setHeader(int header)
    {
        this.header = header;
    }

    public Serializable getData()
    {
        return data;
    }

    public void setData(Serializable data)
    {
        this.data = data;
    }

    public boolean checkHeader(int header) throws Exception
    {
        int index =  findFirstOne(header);
        if(index==-1) throw new Exception("Invalid header");
        if(intToBinaryString(this.header).charAt(index)=='1') return true;
        else return false;
    }

    public static String intToBinaryString(int n)
    {
        String s = "";
        while (n > 0)
        {
            s =  ( (n % 2 ) == 0 ? "0" : "1") +s;
            n = n / 2;
        }
        while(s.length()<Integer.SIZE)
        {
            s="0"+s;
        }
        return s;
    }

    private static int findFirstOne(int x)
    {
        String binaryStr = intToBinaryString(x);
        int length = binaryStr.length();
        for(int i=0; i<length; i++)
        {
            if(binaryStr.charAt(i)=='1')  return i;
        }
        return -1;
    }
}
