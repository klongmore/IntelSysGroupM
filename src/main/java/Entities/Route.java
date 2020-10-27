package Entities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    private ArrayList<Location> stops = new ArrayList<>();
    private int numParcels = 0;
    private Color color;

    public Route() {

    }

    public Route(ArrayList<Location> locations)
    {
        this.stops = new ArrayList<>();
        for(Location l : locations)
        {
            this.stops.add(l);
            this.numParcels += l.getNumParcels();
        }
    }

    public void addStop(Location l)
    {
        stops.add(l);
        numParcels += l.getNumParcels();
    }

    public void moveStop(Location from, int to)
    {
        stops.add(to, stops.remove(stops.indexOf(from)));
    }

    public void setDepot(Location l)
    {
        stops.add(0, l);
        stops.add(l);
    }

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

    public ArrayList<Location> getStops()
    {
        return this.stops;
    }
    public void setStops(ArrayList<Location> locations)
    {
        this.stops = locations;
    }

    public int getNumParcels() {
        return numParcels;
    }
    public void setNumParcels(int num)
    {
        numParcels = num;
    }

    public boolean contains(Location l)
    {
        return stops.contains(l);
    }

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
