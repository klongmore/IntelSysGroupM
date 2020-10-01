package Agents;

//A delivery agent with a capacity constraint. Communicates with the MasterRoutingAgent to be assigned a route.

import Entities.Location;
import Entities.Route;
import Interfaces.IMasterRoutingAgent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

@Arguments({@Argument(name="capacity", description = "Delivery Agent parcel capacity", clazz = Integer.class, defaultvalue = "10"),
            @Argument(name="route",  description = "Delivery Agent route", clazz = Route.class)})
@RequiredServices(@RequiredService(name="routeService", type= IMasterRoutingAgent.class, multiple = true,
        binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true)))
@ProvidedServices(@ProvidedService(name="routeService", type= IMasterRoutingAgent.class, implementation=@Implementation(Agents.MasterRoutingAgent.class)))
@Agent
public class DeliveryAgent
{
    @AgentArgument
    int capacity;
    @AgentArgument
    Route route;

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    @AgentBody
    public void body (IInternalAccess agent)
    {
        System.out.println(agent.getComponentIdentifier().getLocalName() + " added, with capacity: " + capacity);

        IFuture<IMasterRoutingAgent> fut = requiredServicesFeature.getRequiredService("routeService");
        fut.addResultListener(new DefaultResultListener<IMasterRoutingAgent>() {
            @Override
            public void resultAvailable(IMasterRoutingAgent iMasterRoutingAgent) {
                System.out.println("Result available");
                iMasterRoutingAgent.calculateRoute(capacity)
                        .addResultListener(l -> route = l);
            }
        });
    }

    public Route getRoute()
    {
        return route;
    }
}


