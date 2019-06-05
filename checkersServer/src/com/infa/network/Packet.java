package com.infa.network;

import java.io.Serializable;

public class Packet implements Serializable
{
    private byte header;
    private Serializable data;
    public static final byte HEADER_REQUEST_ROOM_LIST = 11;
    public static final byte HEADER_RESPONSE_ROOM_LIST = 12;
    public static final byte HEADER_REQUEST_JOIN_ROOM = 21;
    public static final byte HEADER_RESPONSE_JOIN_ROOM = 22;
    public static final byte HEADER_REQUEST_NEW_ROOM = 31;
    public static final byte HEADER_RESPONSE_NEW_ROOM = 32;
    public static final byte HEADER_REQUEST_LEAVE_ROOM = 41;
    public static final byte HEADER_CHAT_MESSAGE = 51;
    public static final byte HEADER_RESPONSE_ERROR = 61;
    public static final byte HEADER_RESPONSE_USER_HAS_JOINED = 71;
    public static final byte HEADER_RESPONSE_USER_HAS_LEFT = 72;

    public Packet()
    {

    }

    public Packet(byte header, Serializable data)
    {
        this.header = header;
        this.data = data;
    }

    public byte getHeader()
    {
        return header;
    }

    public void setHeader(byte header)
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

}
