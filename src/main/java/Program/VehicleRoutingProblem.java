package Program;

import Agents.DeliveryAgent;
import Agents.MasterRoutingAgent;
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
    JFrame VRPFrame;
    Map map;
    ArrayList<Location> locations;
    Graphics2D g;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new VehicleRoutingProblem());
    }

    public void run()
    {
        // init map and locations
        map = new Map();
        locations = new ArrayList<>();

        //Set up menus
        JMenuBar menuBar = new JMenuBar();
        JMenu mapMenu = new JMenu("Map");
        menuBar.add(mapMenu);

        JMenu debug = new JMenu("Debug");
        menuBar.add(debug);

        JMenuItem fileRead = new JMenuItem("Load");
        fileRead.setToolTipText("Select a JSON File to load the Map from.");
        fileRead.addActionListener(e->
        {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
            chooser.setFileFilter(filter);

            int returnValue = chooser.showOpenDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION)
            {
                map.reMap(Utilities.readSpecification(chooser.getSelectedFile()));
                VRPFrame.repaint();
            }
        });

        JMenuItem randomGenerate = new JMenuItem("Generate");
        randomGenerate.setToolTipText("Randomly generate a Map configuration.");
        randomGenerate.addActionListener(e->
        {
            int numParcels = 0;
            boolean error = true;

            do
            {
                try
                {
                    numParcels = Integer.parseInt(JOptionPane.showInputDialog(VRPFrame, "Enter number of parcels:", "Generate Map", JOptionPane.QUESTION_MESSAGE));
                    error = false;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }while(error);

            map.reMap(Utilities.generateSpecification(numParcels, locations));
            VRPFrame.repaint();
        });

        mapMenu.add(fileRead);
        mapMenu.add(randomGenerate);

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
//        JPanel mapContainerPanel = new JPanel();
//        mapContainerPanel.setBorder(BorderFactory.createTitledBorder("Map"));
//        mapContainerPanel.add(map);
//        VRPFrame.add(mapContainerPanel, c);
        map.setBorder(BorderFactory.createTitledBorder("Map"));
        VRPFrame.add(map, c);
        VRPFrame.setMinimumSize(VRPFrame.getSize());
        VRPFrame.setVisible(true);
        VRPFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // generate locations
        Random rand = new Random();
        for (int i = 0; i < 10; i++)
        {
            locations.add(new Location(rand.nextInt(map.getWidth()), rand.nextInt(map.getHeight())));
        }
        locations.get(0).makeDepot();
        map.setLocations(locations);
    }
}
