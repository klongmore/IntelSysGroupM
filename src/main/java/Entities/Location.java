package Entities;

//Represents a location at an XY coordinate on the plane.
public class Location
{
    private int x,y;

    public Location(int X, int Y)
    {
        x = X;
        y = Y;

        System.out.println("Hi! I am located at " + X + ", " + Y + ".");
    }
}
