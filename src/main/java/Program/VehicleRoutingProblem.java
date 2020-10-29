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
        int[] capacities = {10, 15, 15, 20, 25, 15};
//        int[] capacities = {20};
        PlatformConfiguration config = PlatformConfiguration.getMinimal();
        config.setGui(true);
        config.addComponent(MasterRoutingAgent.class);
        IExternalAccess platform = Starter.createPlatform(config).get();
        IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class).get();
//        CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"algorithm"}, new Object[]{2}));
//        cms.createComponent("Master Routing Agent", "Agents.MasterRoutingAgent.class", ci);
        for(int i=0; i < capacities.length; i++)
        {
            CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity"}, new Object[]{capacities[i]}));
            cms.createComponent("Delivery Agent" + (i + 1), "Agents.DeliveryAgent.class", ci);
        }
    }

//    public void run()
//    {
//        // init map and locations
//        control  = new Control();
//
//        //Set up menus
//        JMenuBar menuBar = new JMenuBar();
//        JMenu mapMenu = new JMenu("Map");
//        menuBar.add(mapMenu);
//
//        JMenu debug = new JMenu("Debug");
//        menuBar.add(debug);
//
//        JMenuItem fileRead = new JMenuItem("Load");
//        fileRead.setToolTipText("Select a JSON File to load the Map from.");
//        fileRead.addActionListener(e->
//        {
//            JFileChooser chooser = new JFileChooser();
//            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
//            chooser.setFileFilter(filter);
//
//            int returnValue = chooser.showOpenDialog(null);
//            if(returnValue == JFileChooser.APPROVE_OPTION)
//            {
//                control.reMap(Utilities.readSpecification(chooser.getSelectedFile()));
//                VRPFrame.repaint();
//            }
//        });
//
//        JMenuItem randomGenerate = new JMenuItem("Generate");
//        randomGenerate.setToolTipText("Randomly generate a Map configuration.");
//        randomGenerate.addActionListener(e->
//        {
//            int numParcels = 0;
//            boolean error = true;
//
//            do
//            {
//                try
//                {
//                    numParcels = Integer.parseInt(JOptionPane.showInputDialog(VRPFrame, "Enter number of parcels:", "Generate Map", JOptionPane.QUESTION_MESSAGE));
//                    error = false;
//                }
//                catch (Exception ex)
//                {
//                    ex.printStackTrace();
//                }
//            }while(error);
//
//            control.reMap(Utilities.generateSpecification(numParcels));
//            VRPFrame.repaint();
//        });
//
//        mapMenu.add(fileRead);
//        mapMenu.add(randomGenerate);
//
//        JMenuItem fileWrite = new JMenuItem("Write Demo File");
//        fileWrite.addActionListener(e->
//        {
//            Utilities.writeDemoSpecification();
//        });
//        debug.add(fileWrite);
//
//        //Set up JFrame
//        VRPFrame = new JFrame("Vehicle Routing Problem");
//
//        VRPFrame.setJMenuBar(menuBar);
//        VRPFrame.setSize(860, 640);
//        VRPFrame.setLayout(new GridBagLayout());
//        GridBagConstraints c = new GridBagConstraints();
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
