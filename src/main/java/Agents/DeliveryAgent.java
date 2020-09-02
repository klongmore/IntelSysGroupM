package Agents;

//A delivery agent with a capacity constraint. Communicates with the MasterRoutingAgent to be assigned a route.
public class DeliveryAgent
{
    private int capacity;

    public DeliveryAgent(int cap)
    {
        capacity = cap;
        System.out.println("Hi! I have a capacity of " + capacity + ".");
    }
}
