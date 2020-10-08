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
    private boolean visited = false;
    private boolean grouped = false;

    public Location(int X, int Y)
    {
        this.x = X;
        this.y = Y;
    }

    public void addPackage()
    {
        numPackages++;
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
    public int getScaledX() { return scaledX; }

    public void setScaledY(int sY)
    {
        scaledY = sY;
    }
    public int getScaledY() { return scaledY; }

    public Integer getNumPackages() { return numPackages; }

    public boolean visited() { return visited; }

    public void visit() { visited = true; }

    public boolean isGrouped() { return grouped; }

    public void group() { this.grouped = true; }

    public void ungroup() { this.grouped = false; }

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

    public static Comparator<Location> createComparator(Location l)
    {
        final Location lFinal = new Location(l.getX(), l.getY());
        return new Comparator<Location>() {
            @Override
            public int compare(Location l1, Location l2) {
                double ds1 = Math.hypot(l1.getX() - lFinal.getX(), l1.getY() - lFinal.getY());
                double ds2 = Math.hypot(l2.getX() - lFinal.getX(), l2.getY() - lFinal.getY());
                return Double.compare(ds1, ds2);
            }
        };
    }
}
