package Interfaces;

import Entities.Location;
import Entities.Route;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.List;

public interface IMasterRoutingAgent {
    IFuture<List<Integer[]>> calculateRoute (int capacity);
}