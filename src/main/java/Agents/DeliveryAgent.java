package Agents;

//A delivery agent with a capacity constraint. Communicates with the MasterRoutingAgent to be assigned a route.

import Services.ChatService;
import Services.IChatService;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.*;


@Agent
@RequiredServices({
        @RequiredService(name="clockservice", type= IClockService.class,
                binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM)),
        @RequiredService(name="chatservices", type = IChatService.class, multiple = true,
                binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM, dynamic = true))})
@ProvidedServices(@ProvidedService(type= IChatService.class, implementation=@Implementation(ChatService.class)))
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

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    @AgentArgument
    protected Integer capacity;

    @AgentBody
    public void body(IInternalAccess agent)
    {
        ITerminableIntermediateFuture<IChatService> fut = requiredServicesFeature
                .getRequiredServices("chatservices");
        fut.get()
                .forEach((it) -> // -- Java8 Lambda function usage, see: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
                        it.message(agent.getComponentIdentifier().getName(), "Delivery Agent ready with a capacity of " + capacity.toString()));
    }
}
