package Program;

import Entities.Control;
import Entities.Map;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

//Runs the VRP solution. Either constructs random locations given a number, or constructs locations given a file specification
public class VehicleRoutingProblem implements Runnable
{
    JFrame VRPFrame;
    Map map;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new VehicleRoutingProblem());
    }

    public void run()
    {
        //Set up menus
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenu debug = new JMenu("Debug");
        menuBar.add(debug);

        JMenuItem fileRead = new JMenuItem("Open File");
        fileRead.addActionListener(e->
        {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
            chooser.setFileFilter(filter);

            int returnValue = chooser.showOpenDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION)
            {
                map.reMap(Utilities.readSpecification(chooser.getSelectedFile()).getParcels());
                VRPFrame.repaint();
            }
        });
        menu.add(fileRead);

        JMenuItem fileWrite = new JMenuItem("Write Demo File");
        fileWrite.addActionListener(e->
        {
            Utilities.writeDemoSpecification();
        });
        debug.add(fileWrite);

        //Set up JFrame
        VRPFrame = new JFrame("Vehicle Routing Problem");

        VRPFrame.setJMenuBar(menuBar);
        VRPFrame.setSize(860, 640);
        VRPFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //Add Control and Map panels
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 4;
        c.weightx = 0;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        JPanel panel = new Control();
        panel.setBorder(BorderFactory.createTitledBorder("Control"));
        VRPFrame.add(panel, c);

        c.gridx = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        map = new Map();
        map.setBorder(BorderFactory.createTitledBorder("Map"));
        VRPFrame.add(map, c);

        VRPFrame.setVisible(true);
        VRPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
