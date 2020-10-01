package Entities;

import java.awt.*;

//Represents a location at an XY coordinate on the plane.
public class Location
{
    private int x,y;
    private int scaledX = 0, scaledY = 0;
    private final int width = 20;
    private final int height = 20;
    private Integer numPackages = 0;
    private boolean isDepot = false;

    public Location(int X, int Y)
    {
        x = X;
        y = Y;
    }

    public void addPackage()
    {
        numPackages++;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setScaledX(int sX)
    {
        scaledX = sX;
    }

    public void setScaledY(int sY)
    {
        scaledY = sY;
    }

    public Integer getNumPackages() { return numPackages; }

    public void makeDepot()
    {
        isDepot = true;
    }

    public void paint(Graphics g)
    {
        g.fillRect(scaledX - width/2, scaledY - height/2, width, height);

        if(!isDepot)
        {
            g.setColor(Color.WHITE);
            g.drawString(numPackages.toString(), scaledX - width/6, scaledY + height/3);
        }
    }
}
