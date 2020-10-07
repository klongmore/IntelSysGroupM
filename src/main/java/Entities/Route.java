package Entities;

import java.awt.*;
import java.util.ArrayList;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    private ArrayList<Location> stops;
    Color color;

    public Route(ArrayList<Location> locations)
    {
        this.stops = locations;
    }

    public void setColor(javafx.scene.paint.Color newColor)
    {
        color = new Color((float) newColor.getRed(),
                (float) newColor.getGreen(),
                (float) newColor.getBlue(),
                (float) newColor.getOpacity());
    }

    //Paints a route by iterating through route members
    public void paint(Graphics g)
    {
        g.setColor(color);

        for(int i = 1; i < stops.size(); i++)
        {
            Location earlyStop = stops.get(i - 1);
            Location lateStop = stops.get(i);
            g.drawLine(earlyStop.getScaledX(), earlyStop.getScaledY(), lateStop.getScaledX(), lateStop.getScaledY());
        }

        for(Location location : stops)
        {
            location.setColor(color);
            location.paint(g);
        }
    }

    //Stops getter and setter
    public ArrayList<Location> getStops()
    {
        return this.stops;
    }
    public void setStops(ArrayList<Location> locations)
    {
        this.stops = locations;
    }
}
