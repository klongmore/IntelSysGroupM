package Entities;

import Agents.DeliveryAgent;
import Agents.MasterRoutingAgent;
import Program.Utilities;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
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

//JPanel that contains the controls that can be used to manipulate the problem.
public class Control extends JPanel
{
    private PlatformConfiguration conf = PlatformConfiguration.getMinimal();
    private IExternalAccess platform = Starter.createPlatform(conf).get();
    private IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class).get();
    public int VehicleCount = 0;

    public Control()
    {
        Starter.createPlatform(conf);
        conf.addComponent(MasterRoutingAgent.class);
        conf.setGui(true);

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
            CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity"}, new Object[]{spinnerModel.getValue()}));
            cms.createComponent("Vehicle" + VehicleCount, "Agents.DeliveryAgent.class", ci);
        });

        result.add(vehicleCountLabel);
        result.add(capacityLabel);
        result.add(capacitySpinner);
        result.add(addButton);
        result.setMaximumSize(result.getPreferredSize());

        return result;
    }


}
