package Entities;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//A collection of locations used by the DeliveryAgents.
public class Route
{
    private ArrayList<Location> stops;
    private int numParcels;
    Color color;
    private boolean assigned;
    private double length;

    public Route(ArrayList<Location> locations)
    {
        Random rand = new Random();
        this.color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        this.stops = new ArrayList<>();
        this.assigned = false;
        for(Location l : locations)
        {
            this.stops.add(l);
            this.numParcels += l.getNumPackages();
        }
        this.length = 0;
//        System.out.println(this.stops.size());
    }

//    public void setColor(javafx.scene.paint.Color newColor)
//    {
//        color = new Color((float) newColor.getRed(),
//                (float) newColor.getGreen(),
//                (float) newColor.getBlue(),
//                (float) newColor.getOpacity());
//    }

    public void paint(Graphics g)
    {
        g.setColor(color);
        if(!stops.isEmpty())
        {
            Location prevStop = stops.get(0);
            for(Location l : stops)
            {
                g.drawLine(prevStop.getScaledX(), prevStop.getScaledY(), l.getScaledX(), l.getScaledY());
                prevStop = l;
            }
        }
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

    public void assigned()
    {
        this.assigned = true;
    }

    public boolean isAssigned()
    {
        return this.assigned;
    }

    public double getLength() {
        double distance = 0;
        for(int i = 0; i < this.getStops().size() - 1; i++)
        {
            int x1 = this.getStops().get(i).getX();
            int x2 = this.getStops().get(i + 1).getX();
            int y1 = this.getStops().get(i).getY();
            int y2 = this.getStops().get(i + 1).getY();
            distance += Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
        }

        return distance;
    }
}
