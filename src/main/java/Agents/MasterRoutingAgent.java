package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import Entities.Location;
import Entities.Map;
import Entities.Route;
import Interfaces.IDeliveryAgent;
import Program.Utilities;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

// TODO: add agent argument for algorithm type which will then determine the route calculation.
@RequiredServices({@RequiredService(name = "deliveryAgentService", type = IDeliveryAgent.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)), @RequiredService(name = "cms", type = IComponentManagementService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM))})
@Agent
public class MasterRoutingAgent
{
    //GUI Variables
    private Map map;
    private JFrame GUI;

    @AgentFeature
    IRequiredServicesFeature requiredServicesFeature;
    IComponentManagementService cms;

    @AgentBody
    public void body(IInternalAccess agent)
    {
        cms = (IComponentManagementService)requiredServicesFeature.getRequiredService("cms").get();

        //INIT JFRAME VARIABLES
        GUI = new JFrame("Vehicle Routing Problem");
        map = new Map();

        //Generate a random specification.
        map.reMap(Utilities.generateSpecification(60));

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
        JPanel control = new JPanel();
        control.setBorder(BorderFactory.createTitledBorder("Control"));
        control.setLayout(new BorderLayout());

        GridBagConstraints cc = new GridBagConstraints();
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new GridBagLayout());

        TableModel agentModel = new DefaultTableModel(new Object[]{"Agent", "Capacity"}, 0);
        JTable agentTable = new JTable(agentModel);

        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 1000, 1);
        JSpinner capacitySpinner = new JSpinner(spinnerModel);

        JButton addAgentButton = new JButton("Add Agent");
        addAgentButton.addActionListener(e ->
        {
            CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity"}, new Object[]{capacitySpinner.getValue()}));
            IComponentIdentifier agentID = cms.createComponent("Delivery Agent", "Agents.DeliveryAgent.class", ci).getFirstResult();
            System.out.println(agentID);
            ((DefaultTableModel)agentTable.getModel()).addRow(new Object[]{agentID, capacitySpinner.getValue()});
        });

        cc.gridwidth = 1;
        cc.gridx = 0;
        cc.gridy = 0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        innerPanel.add(capacitySpinner, cc);
        cc.gridx = 1;
        innerPanel.add(addAgentButton, cc);

        cc.gridwidth = 2;
        cc.gridx = 0;
        cc.gridy = 1;
        innerPanel.add(agentTable, cc);

        JButton GNNButton = new JButton("Run Grouped Nearest Neighbour");
        GNNButton.addActionListener(e ->
        {
            runAlgorithm("GNN", agent);
        });
        cc.gridy = 2;
        innerPanel.add(GNNButton, cc);

        JButton GAButton = new JButton("Run Genetic Algorithm");
        GAButton.addActionListener(e ->
        {
            runAlgorithm("GA", agent);
        });
        cc.gridy = 3;
        innerPanel.add(GAButton, cc);

        JButton NNButton = new JButton("Run Nearest Neighbour");
        NNButton.addActionListener(e ->
        {
            runAlgorithm("NN", agent);
        });
        cc.gridy = 4;
        innerPanel.add(NNButton, cc);

        JButton PBCSOButton = new JButton("Run PBCSO");
        PBCSOButton.addActionListener(e ->
        {
            runAlgorithm("PBCSO", agent);
        });
        cc.gridy = 5;
        innerPanel.add(PBCSOButton, cc);

        control.add(innerPanel, BorderLayout.PAGE_START);

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
    }

    //Runs an algorithm and assigns routes.
    private void runAlgorithm(String toRun, IInternalAccess agent)
    {
        //Get all agent capacities in a list
        ArrayList<Integer> capacities = new ArrayList<>();
        int capacityTotal = 0;
        ITerminableIntermediateFuture<Object> fut = requiredServicesFeature.getRequiredServices("deliveryAgentService");
        for (Object deliveryAgent : fut.get().toArray())
        {
            IDeliveryAgent toGet = (IDeliveryAgent) deliveryAgent;
            capacities.add(toGet.getCapacity().get());
            capacityTotal += toGet.getCapacity().get();
        }

        if (map.getParcels().size() > capacityTotal)
        {
            JOptionPane.showConfirmDialog(GUI, "Too many parcels to deliver, please add more agents.", "Capacity Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        } else
        {
            //Reset Existing Map Routes
            map.resetRoutes();

            ArrayList<Route> result;
            switch (toRun)
            {
                case "GNN":
                    result = doGNN(capacities);
                    break;
                case "GA":
                    result = doGA(capacities);
                    break;
                case "NN":
                    result = doNN(capacities);
                    break;
                case "PBCSO":
                    result = doPBCSO(capacities);
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
        routes.removeIf(e -> e.getLength() <= 0);
        Utilities.assignColours(routes);
        //Assign agents with a route as close to their capacity as possible.
        for (Object da : requiredServicesFeature.getRequiredServices("deliveryAgentService").get().toArray())
        {
            if (!routes.isEmpty())
            {
                IDeliveryAgent d = (IDeliveryAgent) da;
                Route bestRoute = new Route(new ArrayList<>());
                for (Route r : routes)
                {
                    if (r.getNumParcels() > bestRoute.getNumParcels() && r.getNumParcels() <= d.getCapacity().get())
                    {
                        bestRoute = r;
                    }
                }
                d.setRoute(bestRoute);
                map.addRoute(bestRoute);
                routes.remove(bestRoute);
            }
        }
    }

    private ArrayList<Route> doNN(ArrayList<Integer> capacities)
    {
        // pool of available locations
        ArrayList<Location> pLocations = new ArrayList<>(map.getLocations());
        ArrayList<Route> routes = new ArrayList<>();

        for (Integer d : capacities)
        {
            // rLocations represents the locations to add to the final route
            ArrayList<Location> rLocations = new ArrayList<>();
            // rCapacity tracks the capacity of the route being generated
            int rCapacity = 0;
            // start at depot
            rLocations.add(map.getDepot());
            // init current location
            Location thisLoc = rLocations.get(0);
            while (rCapacity < d)
            {
                if (pLocations.size() == 0)
                {
                    break;
                }
                // find nextLoc
                Location nextLoc = pLocations.get(0);
                for (Location l : pLocations)
                {
                    if (rCapacity + l.getNumParcels() <= d)
                    {
                        nextLoc = l;
                    }
                }
                // look for next location until agent capacity is as full as possible
                double bestDist = Math.hypot(thisLoc.getX() - nextLoc.getX(), thisLoc.getY() - nextLoc.getY());
                for (Location l : pLocations)
                {
                    double newDist = Math.hypot(thisLoc.getX() - l.getX(), thisLoc.getY() - l.getY());
                    if (newDist < bestDist && rCapacity + l.getNumParcels() <= d)
                    {
                        bestDist = newDist;
                        nextLoc = l;
                    }
                }
                if (rCapacity + nextLoc.getNumParcels() <= d)
                {
                    rCapacity += nextLoc.getNumParcels();
                    rLocations.add(nextLoc);
                    thisLoc = nextLoc;
                    pLocations.remove(thisLoc);
                }
                // check if more options available
                boolean more = false;
                for (Location l : pLocations)
                {
                    if (rCapacity + l.getNumParcels() <= d && !rLocations.contains(l))
                    {
                        more = true;
                    }
                }
                if (!more)
                {
                    break;
                }
            }
            rLocations.add(map.getDepot());
            Route dRoute = new Route(rLocations);
            routes.add(dRoute);
        }

        return routes;
    }

    private ArrayList<Route> doGA(ArrayList<Integer> capacities)
    {
        ArrayList<Route> routes = new ArrayList<>();

        // algorithm is run once per agent to find most optimal available route
        for (Integer d : capacities)
        {
            // currently testing with 1000 iterations
            Integer r = 10000;
            // the population stores the evolving routes over the course of the algorithm's life cycle
            ArrayList<Route> population = new ArrayList<>();
            // generate 30 random routes with package counts equal to the lowest delivery agent capacity
            for (int i = 1; i <= 30; i++)
            {
                // copy of map locations to act as pool of random locations to choose from
                ArrayList<Location> mLocations = new ArrayList<>(map.getLocations());
                mLocations.removeIf(l -> inRoute(l, routes) != null);

                // new locations consisting of randomly selected locations
                ArrayList<Location> nLocations = new ArrayList<>();
                // temporary capacity variable to make sure lCapacity is not exceeded
                int tCapacity = 0;
                Random rand = new Random();
                while (tCapacity < d)
                {
                    Location rLoc = mLocations.get(rand.nextInt(mLocations.size()));
                    // if the randomly selected location's package count doesn't exceed the current capacity count,
                    // add it to the new list, remove it from the random pool, and increase capacity count accordingly.
                    if (tCapacity + rLoc.getNumParcels() <= d && !nLocations.contains(rLoc))
                    {
                        nLocations.add(rLoc);
                        mLocations.remove(rLoc);
                        tCapacity += rLoc.getNumParcels();
                    }
                    // check if there are any other possible locations to add, if not, break loop
                    boolean more = false;
                    for (Location l : mLocations)
                    {
                        if (tCapacity + l.getNumParcels() <= d && !nLocations.contains(l))
                        {
                            more = true;
                            break;
                        }
                    }
                    if (!more)
                    {
                        break;
                    }
                }
                // add newly generated route to population
                population.add(new Route(nLocations));
            }
            // begin main loop
            for (int i = 0; i < r; i++)
            {
                // evaluate population of routes and remove the worst 10 according to distance (longest)
                for (int j = 0; j < 10; j++)
                {
                    // find longest route
                    Route lRoute = population.get(0);
                    for (Route route : population)
                    {
                        // calculate route length including distance to and from depot
                        Route tRoute;
                        ArrayList<Location> tLocations = new ArrayList<>(route.getStops());
                        tLocations.add(0, map.getDepot());
                        tLocations.add(map.getDepot());
                        tRoute = new Route(tLocations);
                        if (tRoute.getLength() > lRoute.getLength())
                        {
                            lRoute = route;
                        }
                    }
                    // remove longest route from population
                    population.remove(lRoute);
                }
                // nChildren stores the child routes to be added to the population
                ArrayList<Route> nChildren = new ArrayList<>();
                // randomly generate 10 child routes from parent route locations
                for (int j = 0; j < 10; j++)
                {
                    Random rand = new Random();
                    Route pRoute1;
                    Route pRoute2;
                    // pick two parent routes at random
                    pRoute1 = population.get(rand.nextInt(population.size()));
                    pRoute2 = population.get(rand.nextInt(population.size()));
                    // pLocations represents the pool of possible parent locations
                    ArrayList<Location> pLocations = new ArrayList<>();
                    // combine possible parent locations together into pool pLocations
                    pLocations.addAll(pRoute1.getStops());
                    pLocations.addAll(pRoute2.getStops());
                    // cLocations collects new locations for generating a new child route
                    ArrayList<Location> cLocations = new ArrayList<>();
                    // tCapacity keeps track of child route's capacity as its locations grow
                    int tCapacity = 0;
                    // keep adding new random locations until the child route's length equals either parent's
                    while (cLocations.size() < pRoute1.getStops().size() ||
                            cLocations.size() < pRoute2.getStops().size())
                    {
                        Location rLoc = pLocations.get(rand.nextInt(pLocations.size()));
                        if (!cLocations.contains(rLoc) && tCapacity + rLoc.getNumParcels() <= d)
                        {
                            cLocations.add(rLoc);
                            tCapacity += rLoc.getNumParcels();
                        }
                        // check if there are any other possible locations to add, if not, break loop
                        boolean more = false;
                        for (Location l : pLocations)
                        {
                            if (tCapacity + l.getNumParcels() <= d && !cLocations.contains(l))
                            {
                                more = true;
                                break;
                            }
                        }
                        if (!more)
                        {
                            break;
                        }
                    }
                    nChildren.add(new Route(cLocations));
                }
                population.addAll(nChildren);
            }

            // finalise routes by adding depot and marking locations as visited
            for (Route route : population)
            {
                route.getStops().add(0, map.getDepot());
                route.getStops().add(route.getStops().size(), map.getDepot());
            }
            // find shortest (best) route
            Route sRoute = population.get(0);
            for (Route route : population)
            {
                if (route.getLength() < sRoute.getLength())
                {
                    sRoute = route;
                }
            }

            routes.add(sRoute);
        }

        return routes;
    }

    private ArrayList<Route> doGNN(ArrayList<Integer> capacities)
    {
        //Calculate Threshold
        double thresholdDistance = Utilities.getFurthestDistance(map.getDepot(), map.getLocations()) / 2.5f;

        //Sort capacities in descending order
        capacities.sort(Collections.reverseOrder());

        //Result route list
        ArrayList<Route> routes = new ArrayList<>();
        boolean searchComplete = false;

        //Group locations
        while (!searchComplete)
        {
            ArrayList<Integer> tempCapacities = new ArrayList<>(capacities);
            routes = new ArrayList<>();

            //For every unrouted location
            for (Location l : map.getLocations())
            {
                if (inRoute(l, routes) == null)
                {
                    //For every other location within distance
                    for (Location j : map.getLocations())
                    {
                        if (j != l && (Utilities.getEuclideanDistance(l, j) < thresholdDistance))
                        {
                            Route toAdd = null;
                            Route lRoute = inRoute(l, routes);
                            Route jRoute = inRoute(j, routes);

                            if (jRoute == null && lRoute == null && (j.getNumParcels() + l.getNumParcels() <= tempCapacities.get(0)))//If both j and l are unrouted, create a new route for them
                            {
                                toAdd = new Route();
                                toAdd.addStop(l);
                                toAdd.addStop(j);
                                routes.add(toAdd);
                            } else if (jRoute == null && lRoute != null && (j.getNumParcels() + lRoute.getNumParcels() <= tempCapacities.get(0)))//If l is routed but j is not, add j to l's route
                            {
                                toAdd = lRoute;
                                toAdd.addStop(j);
                            }

                            if (toAdd != null && !tempCapacities.isEmpty() && toAdd.getNumParcels() == tempCapacities.get(0))
                            {
                                tempCapacities.remove(0);
                            }

                            if (routes.size() <= capacities.size() && routeTotal(routes) == map.getParcels().size())
                            {
                                searchComplete = true;
                            }
                        }
                    }
                }
            }
            thresholdDistance *= 1.50f;
        }

        for (Route route : routes)
        {
            sort(route, map.getDepot());
            route.setDepot(map.getDepot());
        }

        return routes;
    }

    private ArrayList<Route> doPBCSO(ArrayList<Integer> capacities)
    {
        // for random selections
        Random rand = new Random();
        // bee population
        ArrayList<ArrayList<ArrayList<Route>>> bees = new ArrayList<>();
        // available locations for bees to 'bee' assigned to
        ArrayList<Location> bLocations = new ArrayList<>(map.getLocations());
        // solution of currently set, procedurally developing routes for existing bees to inherit
        ArrayList<Route> setRoutes = new ArrayList<>();
        // init setRoutes
        for (int i = 0; i < capacities.size(); i++)
        {
            setRoutes.add(new Route(new ArrayList<>()));
        }
        // init gBest, the current global best solution
        ArrayList<Route> gBest = new ArrayList<>();
        // init wBee, the winning bee of each loop iteration that determines next locations in solution routes
        ArrayList<ArrayList<Route>> wBee = new ArrayList<>();
        // generate a bee for each map location
        for (int i = 0; i < bLocations.size(); i++)
        {
            ArrayList<ArrayList<Route>> bee = new ArrayList<>();
            // init nSolution per bee equal to nBees - 1 (nBees = nLocations initially)
            for (int j = 0; j < bLocations.size(); j++)
            {
                // a solution is an array of routes, one route per delivery agent
                ArrayList<Route> solution = new ArrayList<>();
                for (int k = 0; k < capacities.size(); k++)
                {
                    // all routes in all solutions have the same unique starting location per bee
                    ArrayList<Location> startLoc = new ArrayList<>();
                    startLoc.add(bLocations.get(i));
                    Route newRoute = new Route(startLoc);
                    solution.add(newRoute);
                }
                bee.add(solution);
            }
            bees.add(bee);
        }
        // main loop, continues until no bees left, bee with best solution removed each loop
        while (bees.size() > 0)
        {
            // update bees
            for (ArrayList<ArrayList<Route>> bee : bees)
            {
                // reference to bee's unique location
                Location bLocation = bLocations.get(bees.indexOf(bee));
                for (ArrayList<Route> solution : bee)
                {
                    // updated solution
                    ArrayList<Route> uSolution = new ArrayList<>();
                    // every solution will begin with the setRoutes as a base
                    // the next location in every route is the bee's unique location
                    // the remaining locations in every route are random until there are no more random ones
                    // setRoutes will eventually equal gBest
                    // pLocations is the pool of available random locations to fill up each route
                    ArrayList<Location> pLocations = new ArrayList<>(bLocations);
                    pLocations.remove(bLocation);
                    for (int i = 0; i < capacities.size(); i++)
                    {
                        Route newRoute;
                        ArrayList<Location> newLocations = new ArrayList<>();
                        // if there are set routes, add them first
                        if (setRoutes.size() > 0)
                        {
                            newLocations.addAll(setRoutes.get(i).getStops());
                            pLocations.removeAll(setRoutes.get(i).getStops());
                        }
                        newLocations.add(bLocation);
                        newRoute = new Route(newLocations);
                        int tCapacity = newRoute.getNumParcels();
                        // fill remaining capacity with random locations
                        while (tCapacity < capacities.get(i))
                        {
                            if (pLocations.size() == 0)
                            {
                                break;
                            }
                            int rIndex = rand.nextInt(pLocations.size());
                            if (rIndex == pLocations.size())
                            {
                                rIndex--;
                            }
                            Location rLoc = pLocations.get(rIndex);
                            if (tCapacity + rLoc.getNumParcels() <= capacities.get(i) &&
                                    !newLocations.contains(rLoc))
                            {
                                tCapacity += rLoc.getNumParcels();
                                newLocations.add(rLoc);
                                pLocations.remove(rLoc);
                            }
                            boolean more = false;
                            for (Location l : pLocations)
                            {
                                if (tCapacity + l.getNumParcels() <= capacities.get(i) &&
                                        !newLocations.contains(l))
                                {
                                    more = true;
                                    break;
                                }
                            }
                            if (!more)
                            {
                                break;
                            }
                        }
                        newRoute = new Route(newLocations);
                        uSolution.add(newRoute);
                    }
                    bee.set(bee.indexOf(solution), uSolution);
                }
            }
            // update gBest
            if (gBest.size() == 0)
            {
                gBest = bees.get(0).get(0);
            }
            double gBestLongest = gBest.get(0).getLength();
            for (Route r : gBest)
            {
                Route tRoute = r;
                tRoute.getStops().add(0, map.getDepot());
                tRoute.getStops().add(map.getDepot());
                if (tRoute.getLength() > gBestLongest)
                {
                    gBestLongest = r.getLength();
                }
            }
            for (ArrayList<ArrayList<Route>> bee : bees)
            {
                // if the length of the longest route of a solution is shorter than the longest of gBest,
                // that solution becomes the new gBest
                for (ArrayList<Route> solution : bee)
                {
                    double sLongest = solution.get(0).getLength();
                    for (Route r : solution)
                    {
                        Route tRoute = r;
                        tRoute.getStops().add(0, map.getDepot());
                        tRoute.getStops().add(map.getDepot());
                        if (tRoute.getLength() > sLongest)
                        {
                            sLongest = r.getLength();
                        }
                    }
                    if (sLongest < gBestLongest)
                    {
                        gBest = new ArrayList<>(solution);
                    }
                }
            }
            // update winning bee
            wBee = new ArrayList<>(bees.get(0));
            double wBeeAVgValSum = 0;
            for (ArrayList<Route> solution : wBee)
            {
                double sLongest = solution.get(0).getLength();
                for (Route r : solution)
                {
                    Route tRoute = r;
                    tRoute.getStops().add(0, map.getDepot());
                    tRoute.getStops().add(map.getDepot());
                    if (tRoute.getLength() > sLongest)
                    {
                        sLongest = r.getLength();
                    }
                }
                wBeeAVgValSum += sLongest;
            }
            double wBeeAvgVal = wBeeAVgValSum / wBee.size();
            for (ArrayList<ArrayList<Route>> bee : bees)
            {
                double bAvgValSum = 0;
                for (ArrayList<Route> solution : bee)
                {
                    double sLongest = solution.get(0).getLength();
                    for (Route r : solution)
                    {
                        Route tRoute = r;
                        tRoute.getStops().add(0, map.getDepot());
                        tRoute.getStops().add(map.getDepot());
                        if (tRoute.getLength() > sLongest)
                        {
                            sLongest = r.getLength();
                        }
                    }
                    bAvgValSum += sLongest;
                }
                double bAvgVal = bAvgValSum / bee.size();
                if (bAvgVal < wBeeAvgVal)
                {
                    wBee = new ArrayList<>(bee);
                }
            }
            // remove winning bee unique location from bLocations
            bLocations.remove(wBee.get(0).get(0).getStops().get(map.getLocations().size() - bees.size()));
            // update the setRoutes, append new position of winning bee
            for (int i = 0; i < capacities.size(); i++)
            {
                setRoutes.get(i).getStops().add(wBee.get(0).get(0).getStops().get(map.getLocations().size() - bees.size()));
            }
            // remove winning bee from bees
            bees.remove(wBee);
            gBestLongest = gBest.get(0).getLength();
            for (Route r : gBest)
            {
                Route tRoute = r;
                tRoute.getStops().add(0, map.getDepot());
                tRoute.getStops().add(map.getDepot());
                if (tRoute.getLength() > gBestLongest)
                {
                    gBestLongest = r.getLength();
                }
            }
        }
        for (Route r : gBest)
        {
            r.getStops().add(0, map.getDepot());
            r.getStops().add(map.getDepot());
        }
        map.resetRoutes();
        return gBest;
    }

    public void sort(Route route, Location start)
    {
        ArrayList<Location> unvisited = new ArrayList<>(route.getStops());
        Location prevLocation = start;

        for (int i = 0; i < route.getStops().size(); i++)
        {
            Location from = Utilities.getBestLocation(start, prevLocation, unvisited);
            route.moveStop(from, i);
            prevLocation = from;
        }
    }

    public int routeTotal(ArrayList<Route> routes)
    {
        int result = 0;
        for (Route route : routes)
        {
            result += route.getNumParcels();
        }

        return result;
    }

    public Route inRoute(Location l, ArrayList<Route> routes)
    {
        for (Route route : routes)
        {
            if (route.contains(l))
            {
                return route;
            }
        }
        return null;
    }
}
