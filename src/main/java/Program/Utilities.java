package Program;

import Entities.Location;
import Entities.Map;
import Entities.Route;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
            Color.web("0x232C16"),
    };


    //Reads a JSON specification and generates a Map.
    public static Map readSpecification(File spec)
    {
        Map result = new Map();

        JSONParser jsonParser = new JSONParser();

        try(FileReader reader = new FileReader(spec))
        {
            JSONObject JSONobj = (JSONObject)jsonParser.parse(reader);

            JSONArray agents = (JSONArray) JSONobj.get("agents");
            JSONArray parcels = (JSONArray) JSONobj.get("parcels");
            JSONObject depot = (JSONObject) JSONobj.get("depot");

            //TODO: Add Agents based on the agents in the JSON file

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

    //Generates a random specification that is then loaded to the map.
    public static Map generateSpecification(int numParcels)
    {
        Map result = new Map();

        Random rand = new Random();
        int locationDeviation = rand.nextInt(numParcels/4);

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

//        int posX = rand.nextInt(500);
//        int posY = rand.nextInt(500);
//        Location depot = new Location(posX, posY);
        Location depot = new Location(250, 250);
        depot.makeDepot();
        result.setDepot(depot);

        return result;
    }

    public static void saveSpecification(Map map)
    {

    }

    public static void assignColours(ArrayList<Route> routes)
    {
        for(int i = 0; i < routes.size(); i++)
        {
            routes.get(i).setColor(KELLY_COLOURS[i]);
        }
    }

    //Random demo specification for testing
    public static void writeDemoSpecification()
    {
        JSONObject DA1 = new JSONObject();
        JSONObject DA2 = new JSONObject();
        JSONObject DA3 = new JSONObject();
        JSONObject DA4 = new JSONObject();

        DA1.put("capacity", 2);
        DA2.put("capacity", 5);
        DA3.put("capacity", 6);
        DA4.put("capacity", 10);

        JSONArray agentList = new JSONArray();
        agentList.add(DA1);
        agentList.add(DA2);
        agentList.add(DA3);
        agentList.add(DA4);

        JSONObject Loc1 = new JSONObject();
        JSONObject Loc2 = new JSONObject();
        JSONObject Loc3 = new JSONObject();
        JSONObject Loc4 = new JSONObject();
        JSONObject Loc5 = new JSONObject();
        JSONObject Loc6 = new JSONObject();

        Loc1.put("X", 40);
        Loc1.put("Y", 40);

        Loc2.put("X", 120);
        Loc2.put("Y", 120);

        Loc3.put("X", 400);
        Loc3.put("Y", 100);

        Loc6.put("X", 400);
        Loc6.put("Y", 100);

        Loc4.put("X", 160);
        Loc4.put("Y", 200);

        Loc5.put("X", 300);
        Loc5.put("Y", 90);

        JSONArray parcelList = new JSONArray();
        parcelList.add(Loc1);
        parcelList.add(Loc2);
        parcelList.add(Loc3);
        parcelList.add(Loc4);
        parcelList.add(Loc5);
        parcelList.add(Loc6);

        JSONObject depot = new JSONObject();
        depot.put("X", 200);
        depot.put("Y", 300);

        JSONObject total = new JSONObject();
        total.put("parcels", parcelList);
        total.put("agents", agentList);
        total.put("depot", depot);

        try(FileWriter file = new FileWriter("VRP.json"))
        {
            file.write(total.toJSONString());
            file.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
