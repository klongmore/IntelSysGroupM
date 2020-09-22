package Entities;

import Agents.DeliveryAgent;
import Agents.MasterRoutingAgent;
import Program.Utilities;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//JPanel that contains the controls that can be used to manipulate the problem.
public class Control extends JPanel
{
    private PlatformConfiguration config = PlatformConfiguration.getMinimal();
    public int VehicleCount = 0;

    public Control()
    {
        // Jadex config
//        config.addComponent(DeliveryAgent.class);
        config.addComponent(MasterRoutingAgent.class);
        config.setGui(true);

        JPanel GBLPanel = new JPanel();
        GBLPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        GBLPanel.add(AddAgentPanel(), c);

        this.setLayout(new BorderLayout());
        this.add(GBLPanel, BorderLayout.PAGE_START);

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
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                VehicleCount++;
                vehicleCountLabel.setText("Vehicles: " + VehicleCount);
                config.addComponent(DeliveryAgent.class);
                System.out.println(VehicleCount);
            }
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Starter.createPlatform(config);
            }
        });

        result.add(vehicleCountLabel);
        result.add(new JLabel("<html><br></html>"));
        result.add(capacityLabel);
        result.add(capacitySpinner);
        result.add(addButton);
        result.add(startButton);
        result.setMaximumSize(result.getPreferredSize());

        return result;
    }


}
