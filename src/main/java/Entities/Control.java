package Entities;

import Program.Utilities;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

//JPanel that contains the controls that can be used to manipulate the problem.
public class Control extends JPanel
{
    public Control()
    {
        Panel GBLPanel = new Panel();
        GBLPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        GBLPanel.add(AddAgentPanel(), c);

        c.gridy=1;
        GBLPanel.add(GeneratePanel(), c);

        this.setLayout(new BorderLayout());
        this.add(GBLPanel, BorderLayout.PAGE_START);
    }

    private JPanel GeneratePanel()
    {
        JPanel result = new JPanel();

        TitledBorder title = BorderFactory.createTitledBorder("Generate Parcels");
        result.setBorder(title);

        JButton generateButton = new JButton("Generate");
        generateButton.addActionListener(e->
        {
            //Utilities.generateSpecification();
        });

        result.add(generateButton);

        return result;
    }

    private JPanel AddAgentPanel()
    {
        JPanel result = new JPanel();

        TitledBorder title = BorderFactory.createTitledBorder("Add Delivery Agent");
        result.setBorder(title);

        JLabel capacityLabel = new JLabel("Capacity");
        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 100, 1);
        JSpinner capacitySpinner = new JSpinner(spinnerModel);

        JButton addButton = new JButton("Add Agent");

        result.add(capacityLabel);
        result.add(capacitySpinner);
        result.add(addButton);
        result.setMaximumSize(result.getPreferredSize());

        return result;
    }
}
