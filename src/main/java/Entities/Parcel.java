package Entities;

public class Parcel
{
    private Location destination;

    public Parcel(Location dest)
    {
        destination = dest;
    }

    public Location getDestination()
    {
        return destination;
    }
}
