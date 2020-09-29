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

//        System.out.println("Hi! I am located at " + X + ", " + Y + ".");
    }

    public void addPackage()
    {
        System.out.println("Adding Package");
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

    public int getScaledX() { return scaledX; }

    public int getScaledY() { return scaledY; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public Integer getNumPackages() { return numPackages; }

    public void makeDepot()
    {
        isDepot = true;
    }

    public boolean isDepot()
    {
        return isDepot;
    }

    public void paint(Graphics g)
    {

    }
}
