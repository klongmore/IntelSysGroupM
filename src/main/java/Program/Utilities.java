package Program;

import Agents.DeliveryAgent;
import Entities.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

//Static methods that can be used anywhere in the program.
public class Utilities
{
    public static Map readSpecification(File spec)
    {
        Map result = new Map();

        JSONParser jsonParser = new JSONParser();

        try(FileReader reader = new FileReader(spec))
        {
            JSONObject JSONobj = (JSONObject)jsonParser.parse(reader);

            JSONArray agents = (JSONArray) JSONobj.get("agents");
            JSONArray parcels = (JSONArray) JSONobj.get("parcels");

            agents.forEach(agent -> new DeliveryAgent(((Long) ((JSONObject) agent).get("capacity")).intValue()));

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

        //Generate
        int diff = new Random().nextInt(4);
        int numLocations = numParcels - diff;

        for(int i = 0; i < numLocations; i++)
        {

        }

        return result;
    }

    public static void saveSpecification(Map map)
    {

    }

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

        Loc1.put("X", 40);
        Loc1.put("Y", 40);

        Loc2.put("X", 120);
        Loc2.put("Y", 120);

        Loc3.put("X", 100000);
        Loc3.put("Y", 100);

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

        JSONObject total = new JSONObject();
        total.put("parcels", parcelList);
        total.put("agents", agentList);

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
