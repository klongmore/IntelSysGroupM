package Interfaces;

import Entities.Route;
import jadex.commons.future.IFuture;

public interface IMasterRoutingAgent {
    IFuture<Route> calculateRoute (int capacity);
}