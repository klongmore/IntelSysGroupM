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

import java.util.ArrayList;
import java.util.List;

@Arguments({@Argument(name="capacity", description = "Delivery Agent parcel capacity", clazz = Integer.class, defaultvalue = "10")})
@RequiredServices(@RequiredService(name="masterRoutingService", type= IMasterRoutingAgent.class, binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM)))
@Agent
public class DeliveryAgent
{
    @AgentArgument
    int capacity;
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    @AgentBody
    public void body (IInternalAccess agent)
    {
        System.out.println(agent.getComponentIdentifier().getLocalName() + " added, with capacity: " + capacity);
        IFuture<IMasterRoutingAgent> fut = requiredServicesFeature.getRequiredService("masterRoutingService");
        fut.addResultListener(new DefaultResultListener<IMasterRoutingAgent>()
        {
            //Triggers when the MRA has a result for its calculateRoute method
            @Override
            public void resultAvailable(IMasterRoutingAgent iMasterRoutingAgent)
            {
                iMasterRoutingAgent.addDeliveryAgent(agent.getComponentIdentifier(), capacity)
                        .addResultListener(l -> getRoute(l));
            }
            public void exceptionOccurred(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public void getRoute(List<Integer[]> list)
    {
        ArrayList<Location> locations = new ArrayList<>();
        // iterates through the integer list to create new locations
        // note: for some reason I couldn't get this to work with a list of Routes,
        // therefore the list of integer arrays must be converted into a list of locations.
//        for(Integer[] i : list)
//        {
//            // uncomment to see output
//            System.out.println(i[0] + ", " + i[1]);
//            locations.add(new Location(i[0], i[1]));
//        }
        Route result = new Route(locations);
    }
}


