package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import Entities.Location;
import Entities.Map;
import Entities.Route;
import Interfaces.IDeliveryAgent;
import Program.Utilities;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// TODO: add agent argument for algorithm type which will then determine the route calculation.
@RequiredServices(@RequiredService(name = "deliveryAgentService", type = IDeliveryAgent.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)))
@Agent
public class MasterRoutingAgent
{
    //GUI Variables
    private Map map;
    private JFrame GUI;

    //JADEX Variables
    PlatformConfiguration config = PlatformConfiguration.getMinimal();
    IExternalAccess platform = Starter.createPlatform(config).get();
    IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class).get();

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;

    @AgentBody
    public void body(IInternalAccess agent)
    {
        System.out.println(agent.getComponentIdentifier().getLocalName() + " added.");

        //INIT JFRAME VARIABLES
        GUI = new JFrame("Vehicle Routing Problem");
        JPanel control = new JPanel();
        map = new Map();

        //Generate a random specification.
        map.reMap(Utilities.generateSpecification(28));

        //INIT MENUS
        JMenuBar menuBar = new JMenuBar();
        JMenu mapMenu = new JMenu("Map");
        JMenu debug = new JMenu("Debug");
        menuBar.add(mapMenu);
        menuBar.add(debug);

        //Create a menu item to read a specification from a file.
        JMenuItem fileRead = new JMenuItem("Load");
        fileRead.setToolTipText("Select a JSON File to load the Map from.");
        fileRead.addActionListener(e ->
        {
            //Only allow the user to select JSON files.
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
            chooser.setFileFilter(filter);

            //Recreate the map with the new specification.
            int returnValue = chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                map.reMap(Utilities.readSpecification(chooser.getSelectedFile()));
                GUI.repaint();
            }
        });

        //Create a menu item to create a random specification.
        JMenuItem randomGenerate = new JMenuItem("Generate");
        randomGenerate.setToolTipText("Randomly generate a Map configuration.");
        randomGenerate.addActionListener(e ->
        {
            int numParcels = 0;
            boolean error = true;

            do
            {
                try
                {
                    numParcels = Integer.parseInt(JOptionPane.showInputDialog(GUI, "Enter number of parcels:", "Generate Map", JOptionPane.QUESTION_MESSAGE));
                    error = false;
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            } while (error);

            map.reMap(Utilities.generateSpecification(numParcels));
            GUI.repaint();
        });

        mapMenu.add(fileRead);
        mapMenu.add(randomGenerate);

        //Create a menu item to create a debug specification for testing reading.
        JMenuItem fileWrite = new JMenuItem("Write Demo File");
        fileWrite.addActionListener(e ->
        {
            Utilities.writeDemoSpecification();
        });
        debug.add(fileWrite);

        //INIT CONTROL PANEL
        control.setBorder(BorderFactory.createTitledBorder("Control"));
        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 100, 1);
        JSpinner capacitySpinner = new JSpinner(spinnerModel);

        JButton addAgentButton = new JButton("Add Agent");
        addAgentButton.addActionListener(e->
        {

        });

        control.add(capacitySpinner);
        control.add(addAgentButton);

        //INIT MASTER GUI
        GUI.setJMenuBar(menuBar);
        GUI.setSize(860, 640);
        GUI.setMinimumSize(GUI.getSize());
        GUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //Add control panel to GUI
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 4;
        c.weightx = 0;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        GUI.add(control, c);

        //Add Map to GUI
        c.gridx = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        GUI.add(map, c);
        GUI.setVisible(true);
        GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        runAlgorithm("GNN", agent);
    }

    //Runs an algorithm and assigns routes.
    private void runAlgorithm(String toRun, IInternalAccess agent)
    {
        //Get all agent capacities in a list
        ArrayList<Integer> capacities = new ArrayList<>();
        int capacityTotal = 0;
        ITerminableIntermediateFuture<Object> fut = requiredServicesFeature.getRequiredServices("deliveryAgentService");
        for(Object deliveryAgent : fut.get().toArray())
        {
            IDeliveryAgent toGet = (IDeliveryAgent)deliveryAgent;
            capacities.add(toGet.getCapacity().get());
            capacityTotal += toGet.getCapacity().get();
        }

        if(map.getParcels().size() > capacityTotal)
        {
            JOptionPane.showConfirmDialog(GUI, "Too many parcels to deliver, please add more agents.", "Capacity Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            ArrayList<Route> result;
            switch (toRun)
            {
                case "GNN":
                    result = doGNN(capacities);
                    break;
                case "GA":
                    result = doGA(capacities);
                    break;
                case "ACO":
                    result = doACO(capacities);
                    break;
                default:
                    result = new ArrayList<>();
                    break;
            }

            assignRoutes(result);
        }
    }

    //Assigns computed routes to agents.
    private void assignRoutes(ArrayList<Route> routes)
    {
        //Assign agents with a route as close to their capacity as possible.
        for (Object da : requiredServicesFeature.getRequiredServices("deliveryAgentService").get().toArray())
        {
            IDeliveryAgent d = (IDeliveryAgent) da;
            Route bestRoute = new Route(new ArrayList<>());
            for (Route r : routes)
            {
                if (r.getNumParcels() > bestRoute.getNumParcels() && r.getNumParcels() <= d.getCapacity().get() && !r.getAssigned())
                {
                    bestRoute = r;
                    r.setAssigned(true);
                }
            }
            d.setRoute(bestRoute);
            Utilities.assignColours(routes);
            map.addRoute(bestRoute);
        }
    }

    private ArrayList<Route> doACO(ArrayList<Integer> capacities)
    {
        ArrayList<Route> routes = new ArrayList<>();
        return routes;
    }

    private ArrayList<Route> doGA(ArrayList<Integer> capacities)
    {
        ArrayList<Route> routes = new ArrayList<>();
        return routes;
    }

    private ArrayList<Route> doGNN(ArrayList<Integer> capacities)
    {
        //Reset Map Routes
        map.resetRoutes();

        //Calculate Threshold
        double thresholdDistance = Utilities.getFurthestDistance(map.getDepot(), map.getLocations()) / (requiredServicesFeature.getRequiredServices("deliveryAgentService").get().toArray().length / 2.0f);

        //Sort capacities in descending order
        capacities.sort(Collections.reverseOrder());
        int numAgents = capacities.size();

        ArrayList<Route> routes = new ArrayList<>();

        //Group locations
        for(Location l : map.getLocations())
        {
            if(inRoute(l, routes) == null)
            {
                for(Location j : map.getLocations())
                {
                    if(j != l)
                    {
                        double distance = Utilities.getEuclideanDistance(l, j);
                        if(distance < thresholdDistance)
                        {
                            Route addRoute = inRoute(j, routes);
                            if(addRoute != null && (addRoute.getNumParcels() + l.getNumParcels()) < capacities.get(0))
                            {
                                addRoute.addStop(l);
                                if(addRoute.getNumParcels() == capacities.get(0))
                                {
                                    capacities.remove(0);
                                }
                                break;
                            }
                            else if (addRoute == null && routes.size() < numAgents)
                            {
                                Route newRoute = new Route();
                                newRoute.addStop(l);
                                newRoute.addStop(j);
                                routes.add(newRoute);
                                break;
                            }
                        }
                    }
                }
            }
        }
        System.out.println(routes.size());
        return routes;
    }

    public Route inRoute(Location l, ArrayList<Route> routes)
    {
        for(Route route : routes)
        {
            if(route.contains(l))
            {
                return route;
            }
        }
        return null;
    }
}
