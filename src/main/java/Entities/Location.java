package Entities;

import java.awt.*;

//Represents a location at an XY coordinate on the plane.
public class Location
{
    private int x,y;
    private final int width = 20;
    private final int height = 20;
    private int numPackages;

    public Location(int X, int Y)
    {
        x = X;
        y = Y;

        System.out.println("Hi! I am located at " + X + ", " + Y + ".");
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

    public void paint(Graphics g)
    {
        g.fillRect(x - width/2, y - height/2, width, height);
    }
}
