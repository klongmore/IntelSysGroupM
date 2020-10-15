package Entities;

import java.awt.*;
import java.util.Comparator;

//Represents a location at an XY coordinate on the plane.
public class Location
{
    public int x = 0, y = 0;
    private int scaledX = 0, scaledY = 0;
    private final int width = 20;
    private final int height = 20;
    private Integer numPackages = 0;
    private boolean isDepot = false;
    private boolean grouped = false;

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
    public int getScaledX() { return scaledX; }

    public void setScaledY(int sY)
    {
        scaledY = sY;
    }
    public int getScaledY() { return scaledY; }

    public Integer getNumPackages() { return numPackages; }

    public boolean isGrouped() { return grouped; }

    public void group() { grouped = true; }

    public void ungroup() { grouped = false; }

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
