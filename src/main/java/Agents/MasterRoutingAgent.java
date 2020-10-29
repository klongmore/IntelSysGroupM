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
@Arguments({@Argument(name="algorithm", description = "Integer value indicating algorithm to run", clazz = Integer.class, defaultvalue = "4")})
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
        map.reMap(Utilities.generateSpecification(100));

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
                    r = 10000;
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
        // PBCSO (incomplete)
        else if (algorithm == 3)
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
            for (int i = 0; i < deliveryAgents.size(); i++) {
                setRoutes.add(new Route(new ArrayList<>()));
            }
            // init gBest, the current global best solution
            ArrayList<Route> gBest = new ArrayList<>();
            // init wBee, the winning bee of each loop iteration that determines next locations in solution routes
            ArrayList<ArrayList<Route>> wBee = new ArrayList<>();
            // generate a bee for each map location
            for(int i = 0; i < bLocations.size(); i++)
            {
                ArrayList<ArrayList<Route>> bee = new ArrayList<>();
                // init nSolution per bee equal to nBees - 1 (nBees = nLocations initially)
                for (int j = 0; j < bLocations.size(); j++) {
                    // a solution is an array of routes, one route per delivery agent
                    ArrayList<Route> solution = new ArrayList<>();
                    for (int k = 0; k < deliveryAgents.size(); k++) {
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
            while(bees.size() > 0)
            {
                // update bees
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    // reference to bee's unique location
                    Location bLocation = bLocations.get(bees.indexOf(bee));
                    for(ArrayList<Route> solution : bee)
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
                        for(int i = 0; i < deliveryAgents.size(); i++)
                        {
                            Route newRoute;
                            ArrayList<Location> newLocations = new ArrayList<>();
                            // if there are set routes, add them first
                            if(setRoutes.size() > 0)
                            {
                                newLocations.addAll(setRoutes.get(i).getStops());
                                pLocations.removeAll(setRoutes.get(i).getStops());
                            }
                            newLocations.add(bLocation);
                            newRoute = new Route(newLocations);
                            int tCapacity = newRoute.getNumParcels();
                            // fill remaining capacity with random locations
                            while(tCapacity < deliveryAgents.get(i).getCapacity())
                            {
                                if(pLocations.size() == 0)
                                {
                                    break;
                                }
                                int rIndex = rand.nextInt(pLocations.size());
                                if(rIndex == pLocations.size())
                                {
                                    rIndex--;
                                }
                                Location rLoc = pLocations.get(rIndex);
                                if(tCapacity + rLoc.getNumPackages() <= deliveryAgents.get(i).getCapacity() &&
                                    !newLocations.contains(rLoc))
                                {
                                    tCapacity += rLoc.getNumPackages();
                                    newLocations.add(rLoc);
                                    pLocations.remove(rLoc);
                                }
                                boolean more = false;
                                for(Location l : pLocations)
                                {
                                    if (tCapacity + l.getNumPackages() <= deliveryAgents.get(i).getCapacity() &&
                                            !newLocations.contains(l))
                                    {
                                        more = true;
                                        break;
                                    }
                                }
                                if(!more)
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
                if(gBest.size() == 0)
                {
                    gBest = bees.get(0).get(0);
                }
                double gBestLongest = gBest.get(0).getLength();
                for(Route r : gBest)
                {
                    Route tRoute = r;
                    tRoute.getStops().add(0, map.getDepot());
                    tRoute.getStops().add(map.getDepot());
                    if(tRoute.getLength() > gBestLongest)
                    {
                        gBestLongest = r.getLength();
                    }
                }
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    // if the length of the longest route of a solution is shorter than the longest of gBest,
                    // that solution becomes the new gBest
                    for(ArrayList<Route> solution : bee)
                    {
                        double sLongest = solution.get(0).getLength();
                        for(Route r : solution)
                        {
                            Route tRoute = r;
                            tRoute.getStops().add(0, map.getDepot());
                            tRoute.getStops().add(map.getDepot());
                            if(tRoute.getLength() > sLongest)
                            {
                                sLongest = r.getLength();
                            }
                        }
                        if(sLongest < gBestLongest)
                        {
                            gBest = new ArrayList<>(solution);
                        }
                    }
                }
                // update winning bee
                wBee = new ArrayList<>(bees.get(0));
                double wBeeAVgValSum = 0;
                for(ArrayList<Route> solution : wBee)
                {
                    double sLongest = solution.get(0).getLength();
                    for(Route r : solution)
                    {
                        Route tRoute = r;
                        tRoute.getStops().add(0, map.getDepot());
                        tRoute.getStops().add(map.getDepot());
                        if(tRoute.getLength() > sLongest)
                        {
                            sLongest = r.getLength();
                        }
                    }
                    wBeeAVgValSum += sLongest;
                }
                double wBeeAvgVal = wBeeAVgValSum/wBee.size();
                for(ArrayList<ArrayList<Route>> bee : bees)
                {
                    double bAvgValSum = 0;
                    for(ArrayList<Route> solution : bee)
                    {
                        double sLongest = solution.get(0).getLength();
                        for(Route r : solution)
                        {
                            Route tRoute = r;
                            tRoute.getStops().add(0, map.getDepot());
                            tRoute.getStops().add(map.getDepot());
                            if(tRoute.getLength() > sLongest)
                            {
                                sLongest = r.getLength();
                            }
                        }
                        bAvgValSum += sLongest;
                    }
                    double bAvgVal = bAvgValSum/bee.size();
                    if(bAvgVal < wBeeAvgVal)
                    {
                        wBee = new ArrayList<>(bee);
                    }
                }
                // remove winning bee unique location from bLocations
                bLocations.remove(wBee.get(0).get(0).getStops().get(map.getLocations().size() - bees.size()));
                // update the setRoutes, append new position of winning bee
                for (int i = 0; i < deliveryAgents.size(); i++) {
                    setRoutes.get(i).getStops().add(wBee.get(0).get(0).getStops().get(map.getLocations().size() - bees.size()));
                }
                // remove winning bee from bees
                bees.remove(wBee);
                gBestLongest = gBest.get(0).getLength();
                for(Route r : gBest)
                {
                    Route tRoute = r;
                    tRoute.getStops().add(0, map.getDepot());
                    tRoute.getStops().add(map.getDepot());
                    if(tRoute.getLength() > gBestLongest)
                    {
                        gBestLongest = r.getLength();
                    }
                }
            }
            for(Route r : gBest)
            {
                r.getStops().add(0, map.getDepot());
                r.getStops().add(map.getDepot());
            }
            map.clearRoutes();
            for(DeliveryAgent d : deliveryAgents)
            {
                d.setRoute(gBest.get(deliveryAgents.indexOf(d)));
                map.addRoute(gBest.get(deliveryAgents.indexOf(d)));
            }
        }
        // Alex's final hope for dopamine before bed algorithm (Nearest neighbour?)
        else if(algorithm == 4)
        {
            // pool of available locations
            ArrayList<Location> pLocations = new ArrayList<>(map.getUnassignedLocations());
            for(DeliveryAgent d : deliveryAgents)
            {
                if(!d.hasRoute())
                {
                    System.out.println(pLocations.size());
                    // rLocations represents the locations to add to the final route
                    ArrayList<Location> rLocations = new ArrayList<>();
                    // rCapacity tracks the capacity of the route being generated
                    int rCapacity = 0;
                    // start at depot
                    rLocations.add(map.getDepot());
                    // init current location
                    Location thisLoc = rLocations.get(0);
                    while(rCapacity < d.getCapacity())
                    {
                        if(pLocations.size() == 0)
                        {
                            break;
                        }
                        // find nextLoc
                        Location nextLoc = pLocations.get(0);
                        for(Location l : pLocations)
                        {
                            if(rCapacity + l.getNumPackages() <= d.getCapacity())
                            {
                                nextLoc = l;
                            }
                        }
                        // look for next location until agent capacity is as full as possible
                        double bestDist = Math.hypot(thisLoc.getX() - nextLoc.getX(), thisLoc.getY() - nextLoc.getY());
                        for(Location l : pLocations)
                        {
                            double newDist = Math.hypot(thisLoc.getX() - l.getX(), thisLoc.getY() - l.getY());
                            if(newDist < bestDist && rCapacity + l.getNumPackages() <= d.getCapacity())
                            {
                                bestDist = newDist;
                                nextLoc = l;
                            }
                        }
                        if(rCapacity + nextLoc.getNumPackages() <= d.getCapacity())
                        {
                            rCapacity += nextLoc.getNumPackages();
                            rLocations.add(nextLoc);
                            thisLoc = nextLoc;
                            pLocations.remove(thisLoc);
                        }
                        // check if more options available
                        boolean more = false;
                        for(Location l : pLocations)
                        {
                            if(rCapacity + l.getNumPackages() <= d.getCapacity() &&
                                    !rLocations.contains(l))
                            {
                                more = true;
                            }
                        }
                        if(!more)
                        {
                            break;
                        }
                    }
                    rLocations.add(map.getDepot());
                    Route dRoute = new Route(rLocations);
                    for(Location l : dRoute.getStops())
                    {
                        l.visit();
                    }
                    d.setRoute(dRoute);
                    map.addRoute(dRoute);
                }
            }
        }
    }

    @Override
    public IFuture<List<Integer[]>> addDeliveryAgent(IComponentIdentifier id, int capacity)
    {
        deliveryAgents.add(new DeliveryAgent(id, capacity));
        update();
        GUI.repaint();
        int sum = 0;
        for(DeliveryAgent d : deliveryAgents)
        {
            sum += d.getRoute().getLength();
        }
        System.out.println("Route average length: " + sum/deliveryAgents.size());
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
