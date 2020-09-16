package Agents;

//A delivery agent with a capacity constraint. Communicates with the MasterRoutingAgent to be assigned a route.

import jadex.micro.annotation.*;

@Agent
@Arguments(@Argument(name="capacity", description = "Delivery Agent parcel capacity", clazz = Integer.class, defaultvalue = "10"))
public class DeliveryAgent
{
//    private int capacity;
//
//    public DeliveryAgent(int cap)
//    {
//        capacity = cap;
//        System.out.println("Hi! I have a capacity of " + capacity + ".");
//    }
    @AgentArgument
    protected Integer capacity;

    @AgentBody
    public void body()
    {
        System.out.println("Delivery Agent ready with a capacity of " + capacity.toString());
    }
}
