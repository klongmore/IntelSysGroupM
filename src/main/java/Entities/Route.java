package Entities;

import java.awt.*;
import java.util.ArrayList;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    ArrayList<Location> stops;
    Color color;

    public Route()
    {
        stops = new ArrayList<Location>();
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

    public ArrayList<Location> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Location> locations) {
        stops = locations;
    }
}
