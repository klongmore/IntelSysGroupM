package Program;

import Entities.Location;
import Entities.Map;
import Entities.Route;
import Interfaces.IDeliveryAgent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

//Static methods that can be used anywhere in the program.
public class Utilities
{
    private static final Color[] KELLY_COLOURS = {
            Color.web("0xFFB300"),
            Color.web("0x803E75"),
            Color.web("0xFF6800"),
            Color.web("0xA6BDD7"),
            Color.web("0xC10020"),
            Color.web("0xCEA262"),
            Color.web("0x817066"),
            Color.web("0x007D34"),
            Color.web("0xF6768E"),
            Color.web("0x00538A"),
            Color.web("0xFF7A5C"),
            Color.web("0x53377A"),
            Color.web("0xFF8E00"),
            Color.web("0xB32851"),
            Color.web("0xF4C800"),
            Color.web("0x7F180D"),
            Color.web("0x93AA00"),
            Color.web("0x593315"),
            Color.web("0xF13A13"),
            Color.web("0x232C16")
    };

    public static double getFurthestDistance(Location refLocation, ArrayList<Location> list)
    {
        double furthest = 0;
        for(Location l : list)
        {
            double tempFurthest = getEuclideanDistance(refLocation, l);
            if(tempFurthest > furthest)
            {
                furthest = tempFurthest;
            }
        }
        return furthest;
    }

    public static double getEuclideanDistance(Location location, Location other)
    {
        if(location != null && other != null)
        {
            double y = Math.abs(location.getY() - other.getY());
            double x = Math.abs(location.getX() - other.getX());
            return Math.sqrt(y*y + x*x);
        }
        return Double.POSITIVE_INFINITY;
    }

    public static Location getBestLocation(Location start, Location from, ArrayList<Location> locations)
    {
        Location result = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for(Location to : locations)
        {
            double testDistance = (getEuclideanDistance(from, to) + getEuclideanDistance(start, to))/2;
            if(testDistance < bestDistance && to != from)
            {
                result = to;
                bestDistance = testDistance;
            }
        }
        locations.remove(result);
        return result;
    }

    public static Map readSpecification(File spec, IComponentManagementService cms, TableModel model)
    {
        Map result = new Map();

        JSONParser jsonParser = new JSONParser();

        try(FileReader reader = new FileReader(spec))
        {
            JSONObject JSONobj = (JSONObject)jsonParser.parse(reader);

            JSONArray agents = (JSONArray) JSONobj.get("agents");
            JSONArray parcels = (JSONArray) JSONobj.get("parcels");
            JSONObject depot = (JSONObject) JSONobj.get("depot");

            agents.forEach(agent ->
            {
                int capacity = Integer.parseInt(((JSONObject)agent).get("capacity").toString());
                CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity"}, new Object[]{capacity}));
                ITuple2Future agentInfo = cms.createComponent("Delivery Agent", "Agents.DeliveryAgent.class", ci);
                ((DefaultTableModel)model).addRow(new Object[]{agentInfo.getFirstResult(), capacity});
            });

            Location setDepot = new Location(((Long)depot.get("X")).intValue(), ((Long)depot.get("Y")).intValue());
            setDepot.makeDepot();
            result.setDepot(setDepot);
            parcels.forEach(parcel -> result.addParcel(((Long) ((JSONObject) parcel).get("X")).intValue(),((Long) ((JSONObject) parcel).get("Y")).intValue()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    public static Map generateSpecification(int numParcels)
    {
        Map result = new Map();

        Random rand = new Random();
        int locationDeviation = rand.nextInt((numParcels/4) + 1);

        for(int i = 0; i < numParcels - locationDeviation; i++)
        {
            int posX = rand.nextInt(500);
            int posY = rand.nextInt(500);
            result.addParcel(posX, posY);

            if(locationDeviation != 0)
            {
                result.addParcel(posX, posY);
                locationDeviation--;
                numParcels--;
            }
        }

        int posX = rand.nextInt(500);
        int posY = rand.nextInt(500);
        Location depot = new Location(posX, posY);
        depot.makeDepot();
        result.setDepot(depot);

        return result;
    }

    public static void saveSpecification(Map map, IRequiredServicesFeature requiredServicesFeature)
    {
        JSONObject output = map.mapJSON();

        JSONArray agents = new JSONArray();
        ITerminableIntermediateFuture<Object> fut = requiredServicesFeature.getRequiredServices("deliveryAgentService", true);
        for (Object deliveryAgent : fut.get())
        {
            IDeliveryAgent toGet = (IDeliveryAgent) deliveryAgent;
            JSONObject capacity = new JSONObject();
            capacity.put("capacity", toGet.getCapacity().get());
            agents.add(capacity);
        }

        output.put("agents", agents);

        try(FileWriter file = new FileWriter("VRP.json"))
        {
            file.write(output.toJSONString());
            file.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void assignColours(ArrayList<Route> routes)
    {
        for(int i = 0; i < routes.size(); i++)
        {
            routes.get(i).setColor(KELLY_COLOURS[i]);
        }
    }
}
