package Entities;

import Entities.Location;

import java.util.ArrayList;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    private ArrayList<Location> locations;

    public Route(ArrayList<Location> l)
    {
        locations = l;
    }
}
