package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class MasterRoutingAgent
{
    @AgentBody
    public void body()
    {
        System.out.println("Master Routing Agent ready.");
    }
}
