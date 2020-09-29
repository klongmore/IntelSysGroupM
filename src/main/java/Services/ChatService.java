package Services;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class ChatService implements IChatService {
    @ServiceComponent
    IInternalAccess agent;
    @ServiceComponent
    IRequiredServicesFeature requiredServicesFeature;

    private IClockService clock;
    private final DateFormat format = new SimpleDateFormat("hh:mm:ss");

    @ServiceStart
    public IFuture<Void> startService() {
        final Future<Void> ret = new Future<Void>();
        IFuture<IClockService> fut = requiredServicesFeature.getRequiredService("clockservice");
        fut.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret) {
            public void customResultAvailable(IClockService result) {
                clock = result;
                ret.setResult(null);
            }
        });
        return ret;
    }

    public Future<Void> message(String sender, String text) {
        System.out.println(agent.getComponentIdentifier().getName() +
                " received at: " + format.format(clock.getTime()) +
                " from: " + sender +
                "message: " + text);
        return null;
    }
}
