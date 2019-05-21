package com.infa.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tile
{
    private boolean isEmpty;
    private Color color;
    private double x;
    private double y;
    private double w;
    private double h;
    private Pawn owner;

    public Tile(double x, double y,double w, double h,Color c)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        color = c;
        owner = null;
    }

    public void update()
    {

    }

    public void draw(GraphicsContext gc)
    {
        gc.setFill(color);
        gc.fillRect(x,y,w,h);
    }

    public Pawn getOwner()
    {
        return owner;
    }

    public void setOwner(Pawn owner)
    {
        this.owner = owner;
    }

    public boolean hasOwner()
    {
        return !(owner == null);
    }
    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getWidth()
    {
        return w;
    }

    public void setWidth(double w)
    {
        this.w = w;
    }

    public double getHeight()
    {
        return h;
    }

    public void setHeight(double h)
    {
        this.h = h;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setSize(double width, double height)
    {
        w = width;
        h = height;
    }
    public void setPosition(double x, double y)
    {
       this.x = x;
       this.y = y;
    }


}
