package Entities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//JPanel to represent the routes and locations.
public class Map extends JPanel
{
    public ArrayList<Location> locations = new ArrayList<>();;
    public ArrayList<Parcel> parcels;

    public Map()
    {
        parcels = new ArrayList<>();
    }

    public Map(ArrayList<Parcel> lParcels)
    {
        parcels = lParcels;

        for(Parcel parcel : parcels)
        {
            if(!locations.contains(parcel.getDestination()))
            {
                locations.add(parcel.getDestination());
            }
            parcel.getDestination().addPackage();
        }
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

        toAdd.addPackage();
    }

    @Override
    public void paintComponents(Graphics g)
    {
        super.paintComponents(g);
        this.setBackground(Color.WHITE);

        Graphics2D g2D = (Graphics2D) g;

        // draw locations
        g2D.setColor(Color.BLACK);
        for(Location location : locations)
        {
            location.paint(g);
        }
    }
}
