package com.infa.Network;

import java.io.Serializable;

public class Room implements Serializable
{
    protected static int nextId=0;
    protected int id;
    public Connection ref1;
    public Connection ref2;

    Room()
    {
        this.id = nextId;
        this.nextId++;
        ref1 = null;
        ref2 = null;
    }
}
