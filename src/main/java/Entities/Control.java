package Entities;

import javax.swing.*;
import javax.swing.border.TitledBorder;

//JPanel that contains the controls that can be used to manipulate the problem.
public class Control extends JPanel
{
    public Control()
    {
        this.add(AddAgentPanel());
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

        return result;
    }
}
