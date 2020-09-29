package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

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
public class MasterRoutingAgent
{
    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    @AgentBody
    public void body(IInternalAccess agent)
    {
        ITerminableIntermediateFuture<IChatService> fut = requiredServicesFeature
                .getRequiredServices("chatservices");
        fut.get()
                .forEach((it) -> // -- Java8 Lambda function usage, see: https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
                        it.message(agent.getComponentIdentifier().getName(), "Master routing agent ready"));
    }
}
