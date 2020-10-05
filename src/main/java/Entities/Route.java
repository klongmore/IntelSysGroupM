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
        this.stops = new ArrayList<>();
        for(Location l : locations)
        {
            this.stops.add(l);
        }
//        System.out.println(this.stops.size());
    }

    public void setColor(javafx.scene.paint.Color newColor)
    {
        color = new Color((float) newColor.getRed(),
                (float) newColor.getGreen(),
                (float) newColor.getBlue(),
                (float) newColor.getOpacity());
    }

    public void paint(Graphics g)
    {
        g.setColor(color);
    }

    public ArrayList<Location> getStops()
    {
        return this.stops;
    }

    public void setStops(ArrayList<Location> locations)
    {
        this.stops = locations;
    }
}
