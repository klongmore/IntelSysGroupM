package Entities;

import java.awt.*;

//Represents a location at an XY coordinate on the plane.
public class Location
{
    private int x = 0, y = 0;
    private int scaledX = 0, scaledY = 0;
    private final int width = 20;
    private final int height = 20;
    private Integer numPackages = 0;
    private Color color = Color.black;
    private boolean isDepot = false;

    public Location(int X, int Y)
    {
        this.x = X;
        this.y = Y;
    }

    public void addPackage()
    {
        numPackages++;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public void setScaledX(int sX)
    {
        scaledX = sX;
    }
    public int getScaledX(){return scaledX;}

    public void setScaledY(int sY)
    {
        scaledY = sY;
    }
    public int getScaledY(){return scaledY;}

    public Integer getNumPackages() { return numPackages; }

    public void makeDepot()
    {
        color = Color.red;
        isDepot = true;
    }

    public void paint(Graphics g)
    {
        g.setColor(color);
        g.fillRect(scaledX - width/2, scaledY - height/2, width, height);

        if(!isDepot)
        {
            g.setColor(Color.WHITE);
            g.drawString(numPackages.toString(), scaledX - width/6, scaledY + height/3);
        }
    }
}
