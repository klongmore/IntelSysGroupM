package Entities;

import jadex.bridge.IComponentIdentifier;

public class DeliveryAgent {
    private IComponentIdentifier id;
    private int capacity;
    private Route route;

    public DeliveryAgent(IComponentIdentifier id, int capacity)
    {
        this.id = id;
        this.capacity = capacity;
    }

    public IComponentIdentifier getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public Route getRoute() {
        return route;
    }

    public boolean hasRoute()
    {
        if(this.route != null && this.route.getStops().size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setRoute(Route route) {
        this.route = route;
        if(route.getNumParcels() > 0)
        {
            System.out.println(this.id + " of capacity: " + this.capacity + " assigned route with: "
                    + this.route.getNumParcels() + " packages. Total route distance: " + this.route.getLength());
        }
        else
        {
            System.out.println(this.id + " was not found a suitable route.");
        }
    }
}
