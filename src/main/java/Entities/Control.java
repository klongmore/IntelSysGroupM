package Entities;

import Agents.DeliveryAgent;
import Agents.MasterRoutingAgent;
import Program.Utilities;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

//JPanel that contains the controls that can be used to manipulate the problem.
public class Control extends JPanel
{
    private PlatformConfiguration conf = PlatformConfiguration.getMinimal();
    private IExternalAccess platform = Starter.createPlatform(conf).get();
    private IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class).get();
    public int VehicleCount = 0;
    private Map mapRef;

    public Control()
    {
        conf.setGui(true);
        Starter.createPlatform(conf);
//        CreationInfo MRA_ci = new CreationInfo(SUtil.createHashMap(new String[]{"mapRef"}, new Object[]{mapRef}));
//        cms.createComponent("Master Routing Agent", "Agents.MasterRoutingAgent.class", MRA_ci);

        JPanel GBLPanel = new JPanel();
        GBLPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        GBLPanel.add(AddAgentPanel(), c);

        c.gridy = 1;
        GBLPanel.add(AlgorithmPanel(), c);

        this.setLayout(new BorderLayout());
        this.add(GBLPanel, BorderLayout.PAGE_START);
    }

    private JPanel AlgorithmPanel()
    {
        JPanel result = new JPanel();
        TitledBorder title = BorderFactory.createTitledBorder("Run Algorithm");
        result.setBorder(title);

        JButton GNNbutton = new JButton("Grouped Nearest Neighbour");
        GNNbutton.addActionListener(e->
        {
            //TODO: Call to MRA to run GNN algorithm.
            for(IComponentIdentifier i : cms.getComponentIdentifiers().get())
            {
                
            }
        });

        result.add(GNNbutton);

        result.setMaximumSize(result.getPreferredSize());
        return result;
    }

    private JPanel AddAgentPanel()
    {
        JPanel result = new JPanel();

        TitledBorder title = BorderFactory.createTitledBorder("Add Delivery Agent");
        result.setBorder(title);

        JLabel capacityLabel = new JLabel("Capacity");
        JLabel vehicleCountLabel = new JLabel("Vehicles: " + VehicleCount);
        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 100, 1);
        JSpinner capacitySpinner = new JSpinner(spinnerModel);

        JButton addButton = new JButton("Add Agent");
        addButton.addActionListener(e ->
        {
            vehicleCountLabel.setText("Vehicles: " + ++VehicleCount);
            CreationInfo DA_ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity", "route"}, new Object[]{spinnerModel.getValue(), new Route()}));
            cms.createComponent("Vehicle" + VehicleCount, "Agents.DeliveryAgent.class", DA_ci);
            super.repaint();
        });

        result.add(vehicleCountLabel);
        result.add(capacityLabel);
        result.add(capacitySpinner);
        result.add(addButton);
        result.setMaximumSize(result.getPreferredSize());

        return result;
    }

    public void update(Map map)
    {
        mapRef = map;
//        for(Location location : mapRef.getLocations())
//        {
//            System.out.println(location.getX() + ", " + location.getY());
//        }
        CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"mapRef"}, new Object[]{map}));
        cms.createComponent("Master Routing Agent", "Agents.MasterRoutingAgent.class", ci);
    }
}
