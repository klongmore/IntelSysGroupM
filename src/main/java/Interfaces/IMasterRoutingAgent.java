package Interfaces;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import java.util.List;

public interface IMasterRoutingAgent
{
    IFuture<List<Integer[]>> addDeliveryAgent (IComponentIdentifier id, int capacity);
}