package Entities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//JPanel to represent the routes and locations.
public class Map extends JPanel
{
    private ArrayList<Location> locations;
    private ArrayList<Parcel> parcels;
    private ArrayList<Route> routes;
    private Location depot;

    public Map()
    {
        locations = new ArrayList<>();
        parcels = new ArrayList<>();
    }

    public void reMap(Map newMap)
    {
        locations = newMap.locations;
        parcels = newMap.parcels;
        depot = newMap.depot;
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

        toAdd.addPackage();
        parcels.add(new Parcel(toAdd));
    }

    public void setDepot(Location newDepot)
    {
        depot = newDepot;
    }

    public JSONObject mapJSON()
    {
        JSONArray parcelList = new JSONArray();
        for(Parcel parcel : parcels)
        {
            JSONObject loc = new JSONObject();
            loc.put("X", parcel.getDestination().getX());
            loc.put("Y", parcel.getDestination().getY());
            parcelList.add(loc);
        }

        JSONObject jsonDepot = new JSONObject();
        jsonDepot.put("X", depot.getX());
        jsonDepot.put("Y", depot.getY());

        JSONObject mapJSON = new JSONObject();
        mapJSON.put("depot", jsonDepot);
        mapJSON.put("parcels", parcelList);

        return mapJSON;
    }

    public ArrayList<Location> getLocations() { return locations; }
    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }
    public void setParcels(ArrayList<Parcel> parcels) {
        this.parcels = parcels;
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        this.setBackground(Color.WHITE);

        if(!locations.isEmpty())
        {
            Location MaxXLocation = null;
            Location MinXLocation = null;

            Location MaxYLocation = null;
            Location MinYLocation = null;

            ArrayList<Location> tempLocations = new ArrayList<>(locations);
            tempLocations.add(depot);

            for(Location loc : tempLocations)
            {
                if(MaxXLocation == null || MaxXLocation.getX() < loc.getX())
                {
                    MaxXLocation = loc;
                }

                if(MinXLocation == null || MinXLocation.getX() > loc.getX())
                {
                    MinXLocation = loc;
                }

                if(MaxYLocation == null || MaxYLocation.getY() < loc.getY())
                {
                    MaxYLocation = loc;
                }

                if(MinYLocation == null || MinYLocation.getY() > loc.getY())
                {
                    MinYLocation = loc;
                }
            }

            int scaleOffset = 50;
            float scaleX = ((float)getWidth() - scaleOffset * 2)/(MaxXLocation.getX() - MinXLocation.getX());
            float scaleY = ((float)getHeight() - scaleOffset * 2)/(MaxYLocation.getY() - MinYLocation.getY());

            g.setColor(Color.RED);
            depot.setScaledX((int)(scaleX * (depot.getX() - MinXLocation.getX())) + scaleOffset);
            depot.setScaledY((int)(scaleY * (depot.getY() - MinYLocation.getY())) + scaleOffset);
            depot.paint(g);

            // draw locations
            for(Location location : locations)
            {
                g.setColor(Color.BLACK);
                location.setScaledX((int)(scaleX * (location.getX() - MinXLocation.getX())) + scaleOffset);
                location.setScaledY((int)(scaleY * (location.getY() - MinYLocation.getY())) + scaleOffset);
                location.paint(g);
            }
        }
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }
}
