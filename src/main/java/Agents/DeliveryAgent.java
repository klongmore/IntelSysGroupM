package Agents;

//A delivery agent with a capacity constraint. Communicates with the MasterRoutingAgent to be assigned a route.
import Entities.Route;
import Interfaces.IDeliveryAgent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

@Arguments({@Argument(name="capacity", description = "Delivery Agent parcel capacity", clazz = Integer.class)})
@ProvidedServices(@ProvidedService(name = "deliveryAgentService", type= IDeliveryAgent.class))
@Agent
public class DeliveryAgent implements IDeliveryAgent
{
    Route route;

    @AgentArgument
    Integer capacity;
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
    public IFuture<Void> setRoute(Route newRoute)
    {
        System.out.println("Agent with capacity " + capacity + " has been assigned route with " + newRoute.getNumParcels() + " parcels.");
        route = newRoute;
        return new Future<Void>();
    }
}


