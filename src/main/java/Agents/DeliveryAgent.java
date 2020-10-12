package Agents;

//A delivery agent with a capacity constraint. Communicates with the MasterRoutingAgent to be assigned a route.
import Entities.Location;
import Entities.Route;
import Interfaces.IDeliveryAgent;
import Interfaces.IMasterRoutingAgent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Arguments({@Argument(name="capacity", description = "Delivery Agent parcel capacity", clazz = Integer.class)})
@ProvidedServices(@ProvidedService(name = "deliveryAgentService", type= IDeliveryAgent.class))
@Agent
public class DeliveryAgent implements IDeliveryAgent
{
    @AgentArgument
    Integer capacity;
    Route route;
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    @AgentBody
    public void body (IInternalAccess agent)
    {
        System.out.println(agent.getComponentIdentifier().getLocalName() + " added, with capacity: " + capacity);
    }

    @Override
    public IFuture<Integer> getCapacity()
    {
        Future<Integer> result = new Future<>();
        result.setResult(capacity);
        return result;
    }

    @Override
    public void setRoute(Route newRoute)
    {
        route = newRoute;
    }
}


