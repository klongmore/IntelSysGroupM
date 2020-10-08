package Entities;

import Program.Utilities;
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
    private ArrayList<Location> locations;
    private ArrayList<Parcel> parcels;
    private ArrayList<Route> routes;
    private Location depot;

    //Creates a new map and initialises the lists.
    public Map()
    {
        locations = new ArrayList<>();
        parcels = new ArrayList<>();
        routes = new ArrayList<>();
    }

    //ReMaps the map with new list data.
    public void reMap(Map newMap)
    {
        locations = newMap.locations;
        parcels = newMap.parcels;
        depot = newMap.depot;
    }

<<<<<<< HEAD
//    public ArrayList<Parcel> getParcels()
//    {
//        return parcels;
//    }

=======
    //Adds a parcel to the parcels list, and creates a new location if a location doesn't already exist.
>>>>>>> ef1cbe1e59cef262aa4b801fd989a20de10636e5
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

<<<<<<< HEAD
    public void setDepot(Location newDepot)
    {
        depot = newDepot;
    }
    public Location getDepot() { return depot; }

=======
    //Outputs the Map as a JSON object for saving.
>>>>>>> ef1cbe1e59cef262aa4b801fd989a20de10636e5
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

    //Locations getter and setter
    public ArrayList<Location> getLocations() { return locations; }
    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    //Parcels getter and setter
    public ArrayList<Parcel> getParcels()
    {
        return parcels;
    }
    public void setParcels(ArrayList<Parcel> parcels) {
        this.parcels = parcels;
    }

    //Routes setter
    public void setRoutes(ArrayList<Route> routes)
    {
        this.routes = routes;
        Utilities.assignColours(this.routes);
    }

    //Depot setter
    public void setDepot(Location newDepot)
    {
        depot = newDepot;
    }

    //Paint method for showing the map
    public void paint(Graphics g)
    {
        //Super paint ensures that the border around the map is painted
        super.paint(g);
        this.setBackground(Color.WHITE);

        //Scaling code to ensure that the map is appropriately scaled when the viewport size is changed.
        if (!locations.isEmpty())
        {
            Location MaxXLocation = null;
            Location MinXLocation = null;

            Location MaxYLocation = null;
            Location MinYLocation = null;

            ArrayList<Location> tempLocations = new ArrayList<>(locations);
            tempLocations.add(depot);

            for (Location loc : tempLocations)
            {
                if (MaxXLocation == null || MaxXLocation.getX() < loc.getX())
                {
                    MaxXLocation = loc;
                }

                if (MinXLocation == null || MinXLocation.getX() > loc.getX())
                {
                    MinXLocation = loc;
                }

                if (MaxYLocation == null || MaxYLocation.getY() < loc.getY())
                {
                    MaxYLocation = loc;
                }

                if (MinYLocation == null || MinYLocation.getY() > loc.getY())
                {
                    MinYLocation = loc;
                }
            }

            int scaleOffset = 50;
            float scaleX = ((float) getWidth() - scaleOffset * 2) / (MaxXLocation.getX() - MinXLocation.getX());
            float scaleY = ((float) getHeight() - scaleOffset * 2) / (MaxYLocation.getY() - MinYLocation.getY());

<<<<<<< HEAD
            // draw routes
            if(!routes.isEmpty())
            {
                for(Route route : routes)
                {
                    route.paint(g);
                }
            }
            // draw locations
            for(Location location : locations)
=======
            depot.setScaledX((int) (scaleX * (depot.getX() - MinXLocation.getX())) + scaleOffset);
            depot.setScaledY((int) (scaleY * (depot.getY() - MinYLocation.getY())) + scaleOffset);
            depot.paint(g);

            //Draws the locations at their scaled locations.
            for (Location location : locations)
>>>>>>> ef1cbe1e59cef262aa4b801fd989a20de10636e5
            {
                location.setScaledX((int) (scaleX * (location.getX() - MinXLocation.getX())) + scaleOffset);
                location.setScaledY((int) (scaleY * (location.getY() - MinYLocation.getY())) + scaleOffset);
                location.paint(g);
            }

            // draw depot
            g.setColor(Color.RED);
            depot.setScaledX((int)(scaleX * (depot.getX() - MinXLocation.getX())) + scaleOffset);
            depot.setScaledY((int)(scaleY * (depot.getY() - MinYLocation.getY())) + scaleOffset);
            depot.paint(g);
        }
    }
<<<<<<< HEAD

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route r)
    {
        this.routes.add(r);
    }

    public void generateTestRoute()
    {
        Random rand = new Random();
        ArrayList<Location> refLocations = new ArrayList<>(locations);
        ArrayList<Location> rLocations = new ArrayList<>();
        rLocations.add(depot);
        Location rLocation;
        while(!refLocations.isEmpty())
        {
            rLocation = refLocations.get(rand.nextInt(refLocations.size()));
            if(!rLocation.visited())
            {
                rLocation.visit();
                rLocations.add(rLocation);
                refLocations.remove(rLocation);
            }
        }
        routes.add(new Route(rLocations));
    }

    public void generateRandomRoute(int n)
    {
        Random rand = new Random();
        ArrayList<Location> rLocations = new ArrayList<>();
        rLocations.add(depot);
        Location rLocation;
        while(rLocations.size() < n + 1)
        {
            rLocation = locations.get(rand.nextInt(locations.size()));
            if(!rLocation.visited())
            {
                rLocation.visit();
                rLocations.add(rLocation);
            }
        }
        rLocations.add(depot);
        routes.add(new Route(rLocations));
    }

    public void resetLocationGroups()
    {
        for(Location l : locations)
        {
            l.ungroup();
        }
    }

    public double getFurthestDistance(Location refLocation, ArrayList<Location> list)
    {
        double distance;
        double furthestDistance = 0;
        for(Location l : list)
        {
            distance = Math.hypot(refLocation.getX() - l.getX(), refLocation.getY() - l.getY());
            if(distance > furthestDistance)
            {
                furthestDistance = distance;
            }
        }
        return furthestDistance;
    }

    public Location getClosestLocation(Location refLocation, ArrayList<Location> list)
    {
        double distance;
        double closestDistance = 99999;
        Location closestLocation = list.get(0);
        for(Location l : list)
        {
            distance = Math.hypot(refLocation.getX() - l.getX(), refLocation.getY() - l.getY());
            if(distance < closestDistance)
            {
                closestDistance = distance;
                closestLocation = l;
            }
        }

        return closestLocation;
    }
=======
>>>>>>> ef1cbe1e59cef262aa4b801fd989a20de10636e5
}
