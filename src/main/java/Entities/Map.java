package Entities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//JPanel to represent the routes and locations.
public class Map extends JPanel
{
    //Instance variables
    private ArrayList<Location> locations;
    private ArrayList<Parcel> parcels;
    private ArrayList<Route> routes;
    private Location depot;

    //Initialise Map variables.
    public Map()
    {
        locations = new ArrayList<>();
        parcels = new ArrayList<>();
        routes = new ArrayList<>();
    }

    //Remaps the map to a new configuration.
    public void reMap(Map newMap)
    {
        resetRoutes();
        locations = newMap.locations;
        parcels = newMap.parcels;
        depot = newMap.depot;
    }

    //Adds a parcel, and either adds a location or changes an existing location.
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

    //Outputs the Map in JSON format for saving.
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

    public void setLocations(ArrayList<Location> newLocations) {
        locations = newLocations;
    }
    public ArrayList<Location> getLocations() { return locations; }

    public void setParcels(ArrayList<Parcel> newParcels) {
        parcels = newParcels;
    }
    public ArrayList<Parcel> getParcels(){return parcels;}

    public void setDepot(Location newDepot)
    {
        depot = newDepot;
    }
    public Location getDepot() { return depot; }

    public void setRoutes(ArrayList<Route> newRoutes) {
        routes = newRoutes;
    }
    public void addRoute(Route r)
    {
        routes.add(r);
    }

    public void paint(Graphics g)
    {
        setBorder(BorderFactory.createTitledBorder("Map"));
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

            // draw routes
            if(!routes.isEmpty())
            {
                g.setColor(Color.BLACK);
                g.drawString("Route Length:", 10, 25);
                int keyInc = 0;
                for(Route route : routes)
                {
                    g.setColor(route.getColor());
                    g.fillRect(10, keyInc * 20 + 30, 20, 20);
                    g.setColor(Color.BLACK);
                    g.drawString(""+route.getLength(), 35, keyInc * 20 + 45);
                    route.paint(g);
                    keyInc++;
                }
            }
            // draw locations
            for(Location location : locations)
            {
                g.setColor(Color.BLACK);
                location.setScaledX((int)(scaleX * (location.getX() - MinXLocation.getX())) + scaleOffset);
                location.setScaledY((int)(scaleY * (location.getY() - MinYLocation.getY())) + scaleOffset);
                location.paint(g);
            }

            // draw depot
            g.setColor(Color.RED);
            depot.setScaledX((int)(scaleX * (depot.getX() - MinXLocation.getX())) + scaleOffset);
            depot.setScaledY((int)(scaleY * (depot.getY() - MinYLocation.getY())) + scaleOffset);
            depot.paint(g);
        }
    }

    public void resetRoutes()
    {
        routes = new ArrayList<>();
    }
}
