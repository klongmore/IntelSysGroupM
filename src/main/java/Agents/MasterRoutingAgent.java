package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import Interfaces.IMasterRoutingAgent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

import java.util.ArrayList;
import java.util.List;

@ProvidedServices(@ProvidedService(type= IMasterRoutingAgent.class))
@Agent
public class MasterRoutingAgent implements IMasterRoutingAgent
{
    @Override
    public IFuture<List<Integer[]>> calculateRoute(int capacity) {
        // returns dummy route for testing
        Future<List<Integer[]>> result = new Future<>();
        List<Integer[]> list = new ArrayList<>();
        // creates a list of integer arrays, index 0 = x value, index 1 = y value for location generation.
        for(int i = 1; i < 11; i++)
        {
            list.add(new Integer[]{i, i});
        }
        result.setResult(list);
        for(Integer[] j : list)
        {
                // uncomment to see output
//            System.out.println(j[0] + ", " + j[1]);
        }
        return result;
    }
}
