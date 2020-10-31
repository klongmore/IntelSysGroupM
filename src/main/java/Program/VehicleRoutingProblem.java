package Program;

import Agents.MasterRoutingAgent;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

//Runs the VRP solution and sets up the Jadex platform.
public class VehicleRoutingProblem
{
    public static void main(String[] args)
    {
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.addComponent(MasterRoutingAgent.class);
        Starter.createPlatform(config).get();
    }
}
