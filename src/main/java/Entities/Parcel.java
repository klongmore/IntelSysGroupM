package Entities;

public class Parcel
{
    private final Location destination;

    public Parcel(Location dest)
    {
        destination = dest;
    }

    public Location getDestination()
    {
        return destination;
    }
}
