package Program;

import Agents.MasterRoutingAgent;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;

//Runs the VRP solution and sets up the Jadex platform.
public class VehicleRoutingProblem
{
    public static void main(String[] args)
    {
        int nDeliveryAgents = 3;
        int[] capacities = {7, 9, 12};
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.setGui(true);
        config.addComponent(MasterRoutingAgent.class);
        IExternalAccess platform = Starter.createPlatform(config).get();
        IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class).get();
        for(int i=0; i < nDeliveryAgents; i++)
        {
            CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity"}, new Object[]{capacities[i]}));
            cms.createComponent("Delivery Agent", "Agents.DeliveryAgent.class", ci);
        }
    }

//    public void run()
//    {
//
//        //Add Control and Map panels
//        c.gridx = 0;
//        c.gridy = 0;
//        c.gridwidth = 1;
//        c.gridheight = 4;
//        c.weightx = 0;
//        c.weighty = 0.1;
//        c.anchor = GridBagConstraints.CENTER;
//        c.fill = GridBagConstraints.BOTH;
//        control.setBorder(BorderFactory.createTitledBorder("Control"));
//        VRPFrame.add(control, c);
//
//        c.gridx = 1;
//        c.gridwidth = 3;
//        c.weightx = 1;
//        control.getMap().setBorder(BorderFactory.createTitledBorder("Map"));
//        VRPFrame.add(control.getMap(), c);
//        VRPFrame.setMinimumSize(VRPFrame.getSize());
//        VRPFrame.setVisible(true);
//        VRPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    }
}
