package Entities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    private ArrayList<Location> stops;
    Color color;

    public Route(ArrayList<Location> locations)
    {
<<<<<<< HEAD
        Random rand = new Random();
        this.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        this.stops = new ArrayList<>();
        for(Location l : locations)
        {
            this.stops.add(l);
        }
//        System.out.println(this.stops.size());
=======
        this.stops = locations;
>>>>>>> ef1cbe1e59cef262aa4b801fd989a20de10636e5
    }

//    public void setColor(javafx.scene.paint.Color newColor)
//    {
//        color = new Color((float) newColor.getRed(),
//                (float) newColor.getGreen(),
//                (float) newColor.getBlue(),
//                (float) newColor.getOpacity());
//    }

    //Paints a route by iterating through route members
    public void paint(Graphics g)
    {
        g.setColor(color);
<<<<<<< HEAD
        if(!stops.isEmpty())
        {
            Location prevStop = stops.get(0);
            for(Location l : stops)
            {
                g.drawLine(prevStop.getScaledX(), prevStop.getScaledY(), l.getScaledX(), l.getScaledY());
                prevStop = l;
            }
=======

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
>>>>>>> ef1cbe1e59cef262aa4b801fd989a20de10636e5
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
