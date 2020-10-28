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
    private Integer numParcels = 0;
    private boolean isDepot = false;

    //Constructs a location at a given XY coordinate.
    public Location(int X, int Y)
    {
        x = X;
        y = Y;
    }

    //Adds a package to the location.
    public void addPackage()
    {
        numParcels++;
    }

    //Coord getters.
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    //Scaled X coord getter and setter.
    public void setScaledX(int sX)
    {
        scaledX = sX;
    }
    public int getScaledX() { return scaledX; }

    //Scaled Y coord getter and setter.
    public void setScaledY(int sY)
    {
        scaledY = sY;
    }
    public int getScaledY() { return scaledY; }

    //Returns the number of parcels to be delivered.
    public Integer getNumParcels() { return numParcels; }

    //Designates the location as a Depot.
    public void makeDepot()
    {
        isDepot = true;
    }

    //Paint method for Map.
    public void paint(Graphics g)
    {
        g.fillRect(scaledX - width/2, scaledY - height/2, width, height);

        if(!isDepot)
        {
            g.setColor(Color.WHITE);
            g.drawString(numParcels.toString(), scaledX - width/6, scaledY + height/3);
        }
    }
}
