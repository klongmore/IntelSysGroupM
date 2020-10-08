package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import Entities.Control;
import Entities.Location;
import Entities.Map;
import Entities.Route;
import Interfaces.IMasterRoutingAgent;
import Program.Utilities;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: add agent argument for algorithm type which will then determine the route calculation.
@ProvidedServices(@ProvidedService(name = "routeService", type= IMasterRoutingAgent.class))
@Agent
public class MasterRoutingAgent implements IMasterRoutingAgent
{
    int numDeliveryAgents = 0;
    Control control;
    Map map;
    JFrame GUI;

    @AgentBody
    public void body(IInternalAccess agent)
    {
        System.out.println(agent.getComponentIdentifier().getLocalName() + " added.");

        // INIT LOCAL VARIABLES
        GUI = new JFrame("Vehicle Routing Problem");
        JMenuBar menuBar = new JMenuBar();
        JMenu mapMenu = new JMenu("Map");
        control = new Control();
        map = new Map();

        //Generate a random specification.
        map.reMap(Utilities.generateSpecification(20));

        // GUI MENU
        menuBar.add(mapMenu);
        GUI.setJMenuBar(menuBar);
        GUI.setSize(860, 640);
        GUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // control panel
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 4;
        c.weightx = 0;
        c.weighty = 0.1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        control.setBorder(BorderFactory.createTitledBorder("Control"));
        JLabel capacityLabel = new JLabel("Capacity");
        int vehicleCount = 0;
        JLabel vehicleCountLabel = new JLabel("Vehicles: " + vehicleCount);
        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 100, 1);
        JSpinner capacitySpinner = new JSpinner(spinnerModel);

        JButton addButton = new JButton("Add Agent");
        control.add(vehicleCountLabel);
        control.add(capacityLabel);
        control.add(capacitySpinner);
        control.add(addButton);
        GUI.add(control, c);

        // map panel
        c.gridx = 1;
        c.gridwidth = 3;
        c.weightx = 1;
        map.setBorder(BorderFactory.createTitledBorder("Map"));
        GUI.add(map, c);
        GUI.setMinimumSize(GUI.getSize());
        GUI.setVisible(true);
        GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void updateMap(int c)
    {
        // nearest neighbour
        map.setRoutes(new ArrayList<>());
        map.resetLocationGroups();

        // finding furthest distance
        double thresholdDistance = map.getFurthestDistance(map.getDepot(), map.getLocations()) / 2.5;

        // determine location groups
        ArrayList<ArrayList<Location>> locationGroups = new ArrayList<>();
        ArrayList<Location> group = new ArrayList<>();
        for(Location l : map.getLocations())
        {
            if(!l.isGrouped())
            {
                l.group();
                group.add(l);
                for(Location j : map.getLocations())
                {
                    if(!j.isGrouped())
                    {
                        // if the iterated location is within distance and not yet grouped, group it
                        double distance = Math.hypot(l.getX() - j.getX(), l.getY() - j.getY());
                        if(distance < thresholdDistance)
                        {
                            j.group();
                            group.add(j);
                        }
                    }
                }
            }
            locationGroups.add(new ArrayList<>(group));
            group.clear();
        }

        // generate routes from the determined groups
        for(ArrayList<Location> g : locationGroups)
        {
            // connect the next closest location to the previous
            Location closest = map.getDepot();
            ArrayList<Location> newList = new ArrayList<>();
            ArrayList<Location> refList = new ArrayList<>(g);
            for(int i = 0; i < g.size(); i++)
            {
                Location nextClosest = map.getClosestLocation(closest, refList);
                newList.add(nextClosest);
                closest = nextClosest;
                refList.remove(closest);
            }
            // set depot as first and last locations in route
            newList.add(0, map.getDepot());
            newList.add(map.getDepot());
            map.addRoute(new Route(newList));
        }
    }

    @Override
    public IFuture<List<Integer[]>> calculateRoute(int capacity)
    {
        numDeliveryAgents++;
        updateMap(capacity);
        Future<List<Integer[]>> result = new Future<>();
        List<Integer[]> list = new ArrayList<>();

        // creates a list of integer arrays, index 0 = x value, index 1 = y value for location generation.
        for(int i = 1; i < 11; i++)
        {
            list.add(new Integer[]{i, i});
        }
        result.setResult(list);

        // return route to Delivery Agent
        return result;
    }
}
