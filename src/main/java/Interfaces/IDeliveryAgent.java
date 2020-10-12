package Interfaces;

import Entities.Route;
import jadex.commons.future.IFuture;

public interface IDeliveryAgent
{
    IFuture<Integer> getCapacity();
    void setRoute(Route newRoute);
}
