package Entities;

import java.awt.*;
import java.util.ArrayList;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    //Stops and parcel count for constraint assignment.
    private ArrayList<Location> stops = new ArrayList<>();
    private int numParcels = 0;
    private Color color;

    //Empty constructor for Serialization.
    public Route()
    {

    }

    //Constructs a route based on a list of locations.
    public Route(ArrayList<Location> locations)
    {
        stops = new ArrayList<>();
        for(Location l : locations)
        {
            stops.add(l);
            numParcels += l.getNumParcels();
        }
    }

    //Adds a stop to the existing Route.
    public void addStop(Location l)
    {
        stops.add(l);
        numParcels += l.getNumParcels();
    }

    //Moves a stop within the Route. Used predominantly for GNN sorting.
    public void moveStop(Location from, int to)
    {
        stops.add(to, stops.remove(stops.indexOf(from)));
    }

    //Sets the depot location at the beginning and end of the Route.
    public void setDepot(Location l)
    {
        stops.add(0, l);
        stops.add(l);
    }

    //Color getter and setter for Serialization.
    public void setColor(javafx.scene.paint.Color fxColor)
    {
        color = new Color((float) fxColor.getRed(),
                (float) fxColor.getGreen(),
                (float) fxColor.getBlue(),
                (float) fxColor.getOpacity());
    }
    public Color getColor()
    {
        return color;
    }

    //Stops getter and setter for Serialization.
    public ArrayList<Location> getStops()
    {
        return this.stops;
    }
    public void setStops(ArrayList<Location> locations)
    {
        this.stops = locations;
    }

    //Numparcels getter and setter for Serialization.
    public int getNumParcels() {
        return numParcels;
    }
    public void setNumParcels(int num)
    {
        numParcels = num;
    }

    //Checks if the route contains a location.
    public boolean contains(Location l)
    {
        return stops.contains(l);
    }

    //Paint method for Map.
    public void paint(Graphics g)
    {
        if(!stops.isEmpty())
        {
            Location prevStop = stops.get(0);
            for(Location l : stops)
            {
                g.setColor(color);
                g.drawLine(prevStop.getScaledX(), prevStop.getScaledY(), l.getScaledX(), l.getScaledY());
                prevStop = l;
            }
        }
    }
}
