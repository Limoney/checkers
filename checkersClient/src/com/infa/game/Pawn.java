package com.infa.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Pawn
{

    private Color color;
    private Color colorCopy;
    private static double tileWidth;
    private static double tileHeight;
    private static float sizeMult = 0.4f;
    private double x;
    private double y;
    private double radius;

    Pawn(float x, float y, Color color)
    {
        resize();
        this.x =x;
        this.y = y;
        this.color = color;
        this.colorCopy = color;
    }

    public static void setTileWidth(double tileWidth)
    {
        Pawn.tileWidth = tileWidth;
    }

    public static void setTileHeight(double tileHeight)
    {
        Pawn.tileHeight = tileHeight;
    }

    public void update()
    {
        //this.setLayoutX(this.getLayoutX()+0.1);
    }
    public void draw(GraphicsContext gc)
    {
        gc.setFill(colorCopy);
        gc.fillOval(x,y,radius,radius);
        gc.setFill(Color.RED);
        gc.fillOval(x + radius*0.5,y + radius*0.5,1,1);
    }

    public void resize()
    {
//        this.radius = (tileWidth*sizeMult);
//        this.setRadiusY(tileHeight*sizeMult);
//        this.setCenterX(this.getRadiusX() + (tileWidth * sizeMult * 0.25));
//        this.setCenterY(this.getRadiusY() + (tileHeight * sizeMult * 0.25));
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

    public double getRadius()
    {
        return radius;
    }

    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    public void setPosition(double x,double y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean isSelected(double mouseX,  double mouseY)
    {
        double pcenterX = x + radius*0.5;
        double pcenterY = y + radius*0.5;
        double distance = Math.hypot(mouseX-pcenterX, mouseY-pcenterY);
        if( distance <=radius*0.5)
        {
            //System.out.println(distance +"    "+radius*0.5);
            this.colorCopy = Color.RED;
            return true;
        }
        else
        {
            //System.out.println("nope");
            this.colorCopy = this.color;
            return false;
        }
    }
}
