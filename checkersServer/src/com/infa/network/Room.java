package com.infa.network;

import game.Board;

import java.io.Serializable;

public class Room implements Serializable
{
    protected static int nextId=0;
    protected int id;
    public Connection ref1;
    public Connection ref2;
    protected Board board;
    protected byte turn;

    Room()
    {
        board = new Board();
        turn = 0;
        this.id = nextId;
        this.nextId++;
        ref1 = null;
        ref2 = null;
    }

    public Board getBoard()
    {
        return board;
    }
}
