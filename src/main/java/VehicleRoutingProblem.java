import Entities.Control;
import Entities.Location;
import Entities.Map;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//Runs the VRP solution. Either constructs random locations given a number, or constructs locations given a file specification
public class VehicleRoutingProblem implements Runnable
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new VehicleRoutingProblem());
    }

    public void run()
    {
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
                Utilities.readSpecification(chooser.getSelectedFile());
            }
        });
        menu.add(fileRead);

        JMenuItem fileWrite = new JMenuItem("Write Demo File");
        fileWrite.addActionListener(e->
        {
            Utilities.writeDemoSpecification();
        });
        debug.add(fileWrite);

        JFrame VRPFrame = new JFrame("Vehicle Routing Problem");

        VRPFrame.setJMenuBar(menuBar);
        VRPFrame.setSize(860, 640);
        VRPFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

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
        panel = new Map();
        panel.setBorder(BorderFactory.createTitledBorder("Map"));
        VRPFrame.add(panel, c);

        VRPFrame.setVisible(true);
        VRPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // generate locations
        Random random = new Random();
        int numLocations = 50;
        Location[] locations = new Location[numLocations + 1];
        locations[0] = new Location(VRPFrame.getWidth()/2, VRPFrame.getHeight()/2);
        for(int i = 1; i < numLocations + 1; i++)
        {
            locations[i] = new Location(random.nextInt(VRPFrame.getWidth()), random.nextInt(VRPFrame.getHeight()));
        }
        ((Map) panel).locations = locations;
    }
}
