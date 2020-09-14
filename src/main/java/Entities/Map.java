package Entities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//JPanel to represent the routes and locations.
public class Map extends JPanel
{
    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<Parcel> parcels;
    private int scaleOffset = 50;
    private Location depot;

    public Map()
    {
        parcels = new ArrayList<>();
    }

    public void reMap(ArrayList<Parcel> lParcels)
    {
        locations = new ArrayList<>();
        parcels = lParcels;

        for(Parcel parcel : parcels)
        {
            if(!locations.contains(parcel.getDestination()))
            {
                locations.add(parcel.getDestination());
            }
            parcel.getDestination().addPackage();
        }
        repaint();
    }

    public ArrayList<Parcel> getParcels()
    {
        return parcels;
    }

    public void addParcel(int x, int y)
    {
        Location toAdd = null;
        for(Location location : locations)
        {
            if(location.getX() == x && location.getY() == y)
            {
                toAdd = location;
            }
        }

        if(toAdd == null)
        {
            toAdd = new Location(x, y);
            locations.add(toAdd);
        }

        parcels.add(new Parcel(toAdd));
    }

    public void paint(Graphics g)
    {
        this.setBackground(Color.WHITE);

        if(!locations.isEmpty())
        {
            Location MaxXLocation = null;
            Location MinXLocation = null;

            Location MaxYLocation = null;
            Location MinYLocation = null;

            for(Location loc : locations)
            {
                if((MaxXLocation != null && MaxXLocation.getX() < loc.getX()) || MaxXLocation == null)
                {
                    MaxXLocation = loc;
                }

                if((MinXLocation != null && MinXLocation.getX() > loc.getX()) || MinXLocation == null)
                {
                    MinXLocation = loc;
                }

                if((MaxYLocation != null && MaxYLocation.getY() < loc.getY()) || MaxYLocation == null)
                {
                    MaxYLocation = loc;
                }

                if((MinYLocation != null && MinYLocation.getY() > loc.getY()) || MinYLocation == null)
                {
                    MinYLocation = loc;
                }
            }

            MaxXLocation.setScaledX(getWidth() - scaleOffset);
            MinXLocation.setScaledX(scaleOffset);

            MaxYLocation.setScaledY(getHeight() - scaleOffset);
            MinYLocation.setScaledY(scaleOffset);

            System.out.println(MaxXLocation.getScaledX());

            g.setColor(Color.RED);
            //depot.paint(g);

            // draw locations
            for(Location location : locations)
            {
                if(location.getScaledX() == 0)
                {
                    float scale = (float)(MaxXLocation.getScaledX() - 100)/(MaxXLocation.getX() - MinXLocation.getX());
                    System.out.println(scale);

                    location.setScaledX((int)(scale * location.getX()));
                    System.out.println(location.getScaledX());
                }

                g.setColor(Color.BLACK);
                location.paint(g);
            }
        }
    }
}
