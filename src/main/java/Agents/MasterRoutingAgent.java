package Agents;

//Takes constraints from the DeliveryAgents and uses an algorithm to determine the routes.

import Entities.Control;
import Entities.DeliveryAgent;
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
import org.chocosolver.solver.constraints.nary.nValue.amnv.rules.R;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// TODO: add agent argument for algorithm type which will then determine the route calculation.
@Arguments({@Argument(name="algorithm", description = "Integer value indicating algorithm to run", clazz = Integer.class, defaultvalue = "3")})
@ProvidedServices(@ProvidedService(name = "masterRoutingService", type= IMasterRoutingAgent.class))
@Agent
public class MasterRoutingAgent implements IMasterRoutingAgent
{
    private Control control;
    private Map map;
    private JFrame GUI;
    private ArrayList<DeliveryAgent> deliveryAgents;
    @AgentArgument
    int algorithm;

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
        deliveryAgents = new ArrayList<>();

        //Generate a random specification.
        map.reMap(Utilities.generateSpecification(30));

        // GUI MENU
        menuBar.add(mapMenu);
        GUI.setJMenuBar(menuBar);
        GUI.setSize(860, 640);
//        GUI.setLayout(new GridBagLayout());
        GUI.setLayout(new BorderLayout());
//        GridBagConstraints c = new GridBagConstraints();

        // control panel - currently hidden as it is not needed yet
//        c.gridx = 0;
//        c.gridy = 0;
//        c.gridwidth = 1;
//        c.gridheight = 4;
//        c.weightx = 0;
//        c.weighty = 0.1;
//        c.anchor = GridBagConstraints.CENTER;
//        c.fill = GridBagConstraints.BOTH;
//        control.setBorder(BorderFactory.createTitledBorder("Control"));
//        JLabel capacityLabel = new JLabel("Capacity");
//        int vehicleCount = 0;
//        JLabel vehicleCountLabel = new JLabel("Vehicles: " + vehicleCount);
//        SpinnerModel spinnerModel = new SpinnerNumberModel(10, 0, 100, 1);
//        JSpinner capacitySpinner = new JSpinner(spinnerModel);
//
//        JButton addButton = new JButton("Add Agent");
//        control.add(vehicleCountLabel);
//        control.add(capacityLabel);
//        control.add(capacitySpinner);
//        control.add(addButton);
//        GUI.add(control, c);

        // map panel
//        c.gridx = 1;
//        c.gridwidth = 3;
//        c.weightx = 1;
        map.setBorder(BorderFactory.createTitledBorder("Map"));
        GUI.add(map, BorderLayout.CENTER);
        GUI.setMinimumSize(GUI.getSize());
        GUI.setVisible(true);
        GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void update()
    {
        // Grouped Nearest Neighbour (GNN)
        if(algorithm == 1)
        {
            // nearest neighbour
            map.setRoutes(new ArrayList<>());
            map.resetLocationGroups();
            // find lowest delivery agent capacity
            int lCapacity = deliveryAgents.get(0).getCapacity();
            for (DeliveryAgent d : deliveryAgents)
            {
                if(d.getCapacity() < lCapacity)
                {
                    lCapacity = d.getCapacity();
                }
            }
            // finding furthest distance
            double thresholdDistance = map.getFurthestDistance(map.getDepot(), map.getUnassignedLocations()) / 2.5;

            // determine location groups
            ArrayList<ArrayList<Location>> locationGroups = new ArrayList<>();
            ArrayList<Location> group = new ArrayList<>();
            for(Location l : map.getUnassignedLocations())
            {
                if(!l.isGrouped())
                {
                    l.group();
                    group.add(l);
                    for(Location j : map.getUnassignedLocations())
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
            ArrayList<Route> computedRoutes = new ArrayList<>();
            for(ArrayList<Location> g : locationGroups)
            {
                // init depot as first location in route and thus the current closest location
                Location closest = map.getDepot();
                ArrayList<Location> newList = new ArrayList<>();
                // refList contains a copy of the current location group, but locations are...
                //      ...removed after they are determined to be the next closest.
                ArrayList<Location> refList = new ArrayList<>(g);
                for(int i = 0; i < g.size(); i++)
                {
                    // keep track of capacity so that it doesn't exceed lowest delivery agent capacity
                    int tCapacity = 0;
                    // find next closest from the remaining locations in the refList
                    Location nextClosest = map.getClosestLocation(closest, refList);
                    if(tCapacity + nextClosest.getNumPackages() <= lCapacity)
                    {
                        tCapacity += nextClosest.getNumPackages();
                        // connect the next closest location to the previous
                        newList.add(nextClosest);
                        closest = nextClosest;
                        // remove new closest location from refList location pool
                        refList.remove(closest);
                    }
                    else
                    {
                        break;
                    }
                }
                // set depot as last locations in route
                newList.add(0, map.getDepot());
                newList.add(map.getDepot());
                computedRoutes.add((new Route(newList)));
            }
            // assign routes
            for(DeliveryAgent d : deliveryAgents)
            {
                Route bestRoute = new Route(new ArrayList<>());
                for(Route r : computedRoutes)
                {
                    if(r.getNumParcels() > bestRoute.getNumParcels() &&
                            r.getNumParcels() <= d.getCapacity() &&
                            !r.isAssigned())
                    {
                        bestRoute = r;
                    }
                }
                if(bestRoute.getLength() > 0)
                {
                    d.setRoute(bestRoute);
                    bestRoute.assigned();
                    map.addRoute(d.getRoute());
                }
            }
        }
        // Evolutionary Algorithm
        else if(algorithm == 2)
        {
            // algorithm is run once per agent to find most optimal available route
            for(DeliveryAgent d : deliveryAgents)
            {
                if(!d.hasRoute())
                {
                    // variable 'r' is our iteration count, initially the below value
                    int r = map.getLocations().size()^2;
                    // currently testing with 1000 iterations
                    r = 1000;
                    // store current agent capacity as the package limit
                    int dCapacity = d.getCapacity();
                    // the population stores the evolving routes over the course of the algorithm's life cycle
                    ArrayList<Route> population = new ArrayList<>();
                    // generate 30 random routes with package counts equal to the lowest delivery agent capacity
                    for(int i = 1; i <= 30; i++)
                    {
                        // copy of map locations to act as pool of random locations to choose from
                        ArrayList<Location> mLocations = new ArrayList<>(map.getUnassignedLocations());
                        // new locations consisting of randomly selected locations
                        ArrayList<Location> nLocations = new ArrayList<>();
                        // temporary capacity variable to make sure lCapacity is not exceeded
                        int tCapacity = 0;
                        Random rand = new Random();
                        while(tCapacity < dCapacity)
                        {
                            Location rLoc = mLocations.get(rand.nextInt(mLocations.size()));
                            // if the randomly selected location's package count doesn't exceed the current capacity count,
                            // add it to the new list, remove it from the random pool, and increase capacity count accordingly.
                            if(tCapacity + rLoc.getNumPackages() <= dCapacity && !nLocations.contains(rLoc))
                            {
                                nLocations.add(rLoc);
                                mLocations.remove(rLoc);
                                tCapacity += rLoc.getNumPackages();
                            }
                            // check if there are any other possible locations to add, if not, break loop
                            boolean more = false;
                            for(Location l : mLocations)
                            {
                                if (tCapacity + l.getNumPackages() <= dCapacity && !nLocations.contains(l)) {
                                    more = true;
                                    break;
                                }
                            }
                            if(!more)
                            {
                                break;
                            }
                        }
                        // add newly generated route to population
                        population.add(new Route(nLocations));
                    }
                    // begin main loop
                    for(int i = 0; i < r; i++)
                    {
                        // evaluate population of routes and remove the worst 10 according to distance (longest)
                        for(int j = 0; j < 10; j++)
                        {
                            // find longest route
                            Route lRoute = population.get(0);
                            for(Route route : population)
                            {
                                // calculate route length including distance to and from depot
                                Route tRoute;
                                ArrayList<Location> tLocations = new ArrayList<>();
                                tLocations.addAll(route.getStops());
                                tLocations.add(0, map.getDepot());
                                tLocations.add(map.getDepot());
                                tRoute = new Route(tLocations);
                                if(tRoute.getLength() > lRoute.getLength())
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
                        for (int j = 0; j < 10; j++) {
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
                            while(cLocations.size() < pRoute1.getStops().size() ||
                                    cLocations.size() < pRoute2.getStops().size())
                            {
                                Location rLoc = pLocations.get(rand.nextInt(pLocations.size()));
                                if(!cLocations.contains(rLoc) && tCapacity + rLoc.getNumPackages() <= d.getCapacity())
                                {
                                    cLocations.add(rLoc);
                                    tCapacity += rLoc.getNumPackages();
                                }
                                // check if there are any other possible locations to add, if not, break loop
                                boolean more = false;
                                for(Location l : pLocations)
                                {
                                    if (tCapacity + l.getNumPackages() <= d.getCapacity() && !cLocations.contains(l)) {
                                        more = true;
                                        break;
                                    }
                                }
                                if(!more)
                                {
                                    break;
                                }
                            }
                            nChildren.add(new Route(cLocations));
                        }
                        for(Route child : nChildren)
                        {
                            population.add(child);
                        }
                    }

                    // finalise routes by adding depot and marking locations as visited
                    for(Route route : population)
                    {
                        route.getStops().add(0, map.getDepot());
                        route.getStops().add(route.getStops().size(), map.getDepot());
                    }
                    // find shortest (best) route
                    Route sRoute = population.get(0);
                    for(Route route : population)
                    {
                        if(route.getLength() < sRoute.getLength())
                        {
                            sRoute = route;
                        }
                    }
                    // mark route as assigned
                    sRoute.assigned();
                    // mark locations as visited so that they are not considered in future calculations
                    for(Location l : sRoute.getStops())
                    {
                        l.visit();
                    }
                    d.setRoute(sRoute);
                    map.addRoute(sRoute);
                }
            }
        }
        // Progressive Bee Colony Swarm Optimisation
        else if (algorithm == 3)
        {
            map.clearRoutes();
            int nLocations = map.getLocations().size();
            int nDeliveryAgents = deliveryAgents.size();
            ArrayList<ArrayList<ArrayList<Route>>> bees = new ArrayList<>();
            // generate a bee for each map location
            for(int i = 0; i < nLocations; i++)
            {
                ArrayList<ArrayList<Route>> bee = new ArrayList<>();
                bees.add(bee);
            }
            // initialise gBest - the current best solution amongst all bees ever
            ArrayList<Route> gBest = new ArrayList<>();
            // initialise winning bee pBest
            ArrayList<Route> wBeePBest = new ArrayList<>();
            // main loop
            while(bees.size() > 0)
            {
                // generate random solutions for each bee
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    // number of solutions equal to total bees - 1
                    for(int i = 0; i < bees.size() - 1; i++)
                    {
                        // each solution consists of routes, one route per delivery agent
                        ArrayList<Route> solution = new ArrayList<>();
                        // create pool of random map locations to choose from
                        ArrayList<Location> pLocations = new ArrayList<>(map.getLocations());
                        // list of all currently picked locations in solution
                        ArrayList<Location> sLocations = new ArrayList<>();
                        Random rand = new Random();
                        for(DeliveryAgent d : deliveryAgents)
                        {
                            // new Route for each delivery agent in solution
                            Route dRoute;
                            // list of randomly picked locations to form new route
                            ArrayList<Location> rLocations = new ArrayList<>();
                            // temporary variable to track capacity of currently generating route
                            int tCapacity = 0;
                            // if a winning bee has been determined previously, use its starting points
                            if(!wBeePBest.isEmpty())
                            {
                                Location initLoc = wBeePBest.get(deliveryAgents.indexOf(d)).getStops().get(0);
                                rLocations.add(initLoc);
                                sLocations.add(initLoc);
                                pLocations.remove(initLoc);
                                tCapacity += initLoc.getNumPackages();
                            }
                            // create route that utilises agent capacity as fully as possible
                            while(tCapacity <= d.getCapacity())
                            {
                                // break if no more locations in pool
                                if(pLocations.size() == 0)
                                {
                                    break;
                                }
                                // pick random location from pool
                                Location rLoc = pLocations.get(rand.nextInt(pLocations.size()));
                                // check if adding random location would exceed current capacity count
                                if(tCapacity + rLoc.getNumPackages() <= d.getCapacity() && !sLocations.contains(rLoc))
                                {
                                    // update capacity, add location, remove from pool
                                    tCapacity += rLoc.getNumPackages();
                                    rLocations.add(rLoc);
                                    sLocations.add(rLoc);
                                    pLocations.remove(rLoc);
                                }
                                // check if there are any other possible locations to add, if not, break loop
                                boolean more = false;
                                for(Location l : pLocations)
                                {
                                    if (tCapacity + l.getNumPackages() <= d.getCapacity() &&
                                            !rLocations.contains(l) &&
                                            !sLocations.contains(l)){
                                        more = true;
                                        break;
                                    }
                                }
                                if(!more)
                                {
                                    break;
                                }
                            }
                            dRoute = new Route(rLocations);
                            solution.add(dRoute);
                        }
                        bee.add(solution);
                    }
                }
                // update gBest
                if(gBest.size() == 0)
                {
                    // initialise first gBest to first solution of first bee
                    gBest = new ArrayList<>(bees.get(0).get(0));
                }
                // calculate average of gBest for reference comparison
                double gBestSum = 0;
                for(Route r : gBest)
                {
                    gBestSum += r.getLength();
                }
                double gBestAvg = gBestSum/gBest.size();
                // go through bees
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    // each solution
                    for(ArrayList<Route> solution : bee)
                    {
                        double sum = 0;
                        // each route in solution
                        for(Route r : solution)
                        {
                            // create temporary route with depot added
                            ArrayList<Location> tLocations = new ArrayList<>();
                            tLocations.addAll(r.getStops());
                            tLocations.add(0, map.getDepot());
                            tLocations.add(map.getDepot());
                            Route tRoute = new Route(tLocations);
                            sum += tRoute.getLength();
                        }
                        double avg = sum/solution.size();
                        // if better avg, update gBest
                        if(avg < gBestAvg)
                        {
                            gBest = new ArrayList<>(solution);
                        }
                    }
                }
                // find winning bee - the bee with the best average across all of its solutions
                double hNewAvgVal = 0;
                ArrayList<ArrayList<Route>> wBee = new ArrayList<>();
                ArrayList<ArrayList<Route>> wBeeRef = new ArrayList<>();
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    double newAvgSum = 0;
                    for(ArrayList<Route> solution : bee)
                    {
//                        System.out.println(solution.size());
                        double sSum = 0;
                        for(Route r : solution)
                        {
                            // create temporary route with depot added
                            ArrayList<Location> tLocations = new ArrayList<>();
                            tLocations.addAll(r.getStops());
                            tLocations.add(0, map.getDepot());
                            tLocations.add(map.getDepot());
                            Route tRoute = new Route(tLocations);
                            sSum += tRoute.getLength();
                        }
                        double sAvg = sSum/solution.size();
                        newAvgSum += sAvg;
                    }
                    double newAvgVal = newAvgSum/bee.size();
                    if(hNewAvgVal == 0)
                    {
                        hNewAvgVal = newAvgVal;
                        wBee = new ArrayList<>(bee);
                    }
                    if(newAvgVal < hNewAvgVal)
                    {
                        hNewAvgVal = newAvgVal;
                        wBee = new ArrayList<>(bee);
                        wBeeRef = bee;
                    }
                }
                // find winning bee's pBest solution
                double wBeePBestAvg = 0;
                for(ArrayList<Route> solution : wBee)
                {
                    double sSum = 0;
                    double sAvg;
                    for(Route r : solution)
                    {
                        // create temporary route with depot added
                        ArrayList<Location> tLocations = new ArrayList<>();
                        tLocations.addAll(r.getStops());
                        tLocations.add(0, map.getDepot());
                        tLocations.add(map.getDepot());
                        Route tRoute = new Route(tLocations);
                        sSum += tRoute.getLength();
                    }
                    sAvg = sSum/solution.size();
                    if(wBeePBestAvg == 0)
                    {
                        wBeePBestAvg = sAvg;
                        wBeePBest = solution;
                    }
                    if(sAvg < wBeePBestAvg)
                    {
                        wBeePBestAvg = sAvg;
                        wBeePBest = solution;
                    }
                }
                // remove wBee from bee population
                bees.remove(wBeeRef);
                // reset bees
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    bee.clear();
                }
            }
            for(DeliveryAgent d : deliveryAgents)
            {
                ArrayList<Location> dLocations = new ArrayList<>(gBest.get(deliveryAgents.indexOf(d)).getStops());
                dLocations.add(0, map.getDepot());
                dLocations.add(map.getDepot());
                Route dRoute = new Route(dLocations);
                d.setRoute(dRoute);
                map.addRoute(dRoute);
            }
//            // run once per delivery agent for best possible route per agent
//            for(DeliveryAgent d : deliveryAgents)
//            {
//                if(!d.hasRoute())
//                {
//                    // reference for current delivery agent capacity
//                    int dCapacity = d.getCapacity();
//                    // reference for unassigned map locations
//                    ArrayList<Location> uLocations = map.getUnassignedLocations();
//                    // each bee will be represented by an ArrayList of Locations
//                    ArrayList<ArrayList<Route>> bees = new ArrayList<>();
//                    // create a new bee for each unassigned location, and set that location as the first in its first route
//                    for(Location l : uLocations)
//                    {
//                        ArrayList<Route> bee = new ArrayList<>();
////                    bee.get(0).getStops().add(l);
//                        ArrayList<Location> locations = new ArrayList<>();
//                        locations.add(l);
//                        bee.add(new Route(locations));
//                        bees.add(bee);
//                    }
//                    // init gBest route
//                    Route gBest = new Route(new ArrayList<>());
//                    // loop continues until no bees left
//                    while(bees.size() > 0)
//                    {
//                        // System.out.println(bees.size());
//                        // generate (nBees - 1) random routes for each bee
//                        for(ArrayList<Route> bee : bees)
//                        {
//                            Random rand = new Random();
//                            for(int i = 0; i <= bees.size() - 1; i++)
//                            {
//                                // keep track of solution's package count
//                                int tCapacity = 0;
//                                // pool of locations to choose once at random
//                                ArrayList<Location> pLocations = new ArrayList<>(uLocations);
//                                // new Locations
//                                ArrayList<Location> nLocations = new ArrayList<>();
//                                if(i == 0)
//                                {
//                                    nLocations.add(bee.get(0).getStops().get(0));
//                                    bee.remove(0);
//                                }
//                                while(tCapacity < dCapacity)
//                                {
//                                    if(pLocations.size() == 0)
//                                    {
//                                        break;
//                                    }
//                                    Location rLoc = pLocations.get(rand.nextInt(pLocations.size()));
//                                    if(tCapacity + rLoc.getNumPackages() <= dCapacity)
//                                    {
//                                        tCapacity += rLoc.getNumPackages();
//                                        nLocations.add(rLoc);
//                                        //bee.get(i).getStops().add(rLoc);
//                                        pLocations.remove(rLoc);
////                                    System.out.println(nLocations.size());
//                                    }
//                                    // check if there are any other possible locations to add, if not, break loop
//                                    boolean more = false;
//                                    for(Location l : pLocations)
//                                    {
//                                        if (tCapacity + l.getNumPackages() <= dCapacity &&
//                                                !nLocations.contains(l)) {
//                                            more = true;
//                                            break;
//                                        }
//                                    }
//                                    if(!more)
//                                    {
//                                        break;
//                                    }
//                                }
////                            System.out.println(nLocations.size());
////                            System.out.println("Bee: " + bees.indexOf(bee) + ", Random route #: " + i);
//                                bee.add(new Route(nLocations));
//                            }
//                        }
//                        if(gBest.getLength() == 0)
//                        {
//                            ArrayList<Location> tLocations = new ArrayList<>();
//                            tLocations.addAll(bees.get(0).get(0).getStops());
//                            tLocations.add(0, map.getDepot());
//                            tLocations.add(map.getDepot());
//                            gBest = new Route(tLocations);
//                        }
//                        // update gBest
//                        for(ArrayList<Route> bee : bees)
//                        {
//                            for(Route r : bee)
//                            {
//                                ArrayList<Location> tLocations = new ArrayList<>();
//                                tLocations.addAll(r.getStops());
//                                tLocations.add(0, map.getDepot());
//                                tLocations.add(map.getDepot());
//                                Route tRoute = new Route(tLocations);
//                                if(tRoute.getLength() < gBest.getLength())
//                                {
//                                    gBest = tRoute;
//                                }
//                            }
//                        }
//                        // init winning bee as first bee
//                        ArrayList<Route> winningBee = new ArrayList<>(bees.get(0));
//                        ArrayList<Route> refWinningBee = bees.get(0);
//                        // init highestNewAvgVal as first bee's newAvgVal
//                        double highestNewAvgVal;
//                        int sum = 0;
//                        for(Route r : bees.get(0))
//                        {
//                            sum += r.getLength();
//                        }
//                        highestNewAvgVal = sum/bees.get(0).size();
//                        // find highest newAvgVal across all bees
//                        for(ArrayList<Route> bee : bees)
//                        {
//                            sum = 0;
//                            for(Route r : bee)
//                            {
//                                sum += r.getLength();
//                            }
//                            double newAvgVal = sum/bee.size();
//                            if(newAvgVal < highestNewAvgVal)
//                            {
//                                winningBee = new ArrayList<>(bee);
//                                refWinningBee = bee;
//                            }
//                        }
//                        // reset bees and set starting position of winning bee as new starting position for all bees
//                        for(ArrayList<Route> bee : bees)
//                        {
//                            bee.clear();
//                            ArrayList<Location> sLocation = new ArrayList<>();
//                            sLocation.add(winningBee.get(0).getStops().get(0));
//                            bee.add(new Route(sLocation));
//                        }
//                        // remove winning bee from bees
//                        bees.remove(refWinningBee);
//                    }
//                    for(Location l : gBest.getStops())
//                    {
//                        l.visit();
//                    }
//                    System.out.println("gBest length: " + gBest.getLength());
//                    System.out.println("gBest packages: " + gBest.getNumParcels());
//                    d.setRoute(gBest);
//                    map.addRoute(gBest);
//                }
//            }
        }
    }

    @Override
    public IFuture<List<Integer[]>> addDeliveryAgent(IComponentIdentifier id, int capacity)
    {
        deliveryAgents.add(new DeliveryAgent(id, capacity));
        update();
        GUI.repaint();
        Future<List<Integer[]>> result = new Future<>();
        List<Integer[]> list = new ArrayList<>();

        // creates a list of integer arrays, index 0 = x value, index 1 = y value for location generation.
//        for(int i = 1; i < 11; i++)
//        {
//            list.add(new Integer[]{i, i});
//        }
        result.setResult(list);

        // return route information to Delivery Agent
        return result;
    }
}
