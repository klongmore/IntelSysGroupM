package Interfaces;

import jadex.commons.future.IFuture;
import java.util.List;

public interface IMasterRoutingAgent
{
    IFuture<List<Integer[]>> calculateRoute (int capacity);
}