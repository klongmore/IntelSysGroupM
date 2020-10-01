package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import Entities.Location;
import Entities.Map;
import Entities.Route;
import Interfaces.IMasterRoutingAgent;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

import java.util.ArrayList;

@Arguments(@Argument(name="mapRef", description = "Reference to locations map", clazz = Entities.Map.class))
@ProvidedServices(@ProvidedService(type= IMasterRoutingAgent.class))
@Agent
public class MasterRoutingAgent implements IMasterRoutingAgent
{
    @AgentArgument
    Map mapRef;

//    @AgentBody
//    public void body(IInternalAccess agent) {
//        System.out.println(agent.getComponentIdentifier().getLocalName() + " added.");
//    }
    @Override
    public IFuture<Route> calculateRoute(int capacity) {
        // returns dummy route for testing

        return new Future<>(new Route());
    }
}
