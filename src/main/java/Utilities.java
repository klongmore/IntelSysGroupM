import Agents.DeliveryAgent;
import Entities.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

//Static methods that can be used anywhere in the program.
public class Utilities
{
    public static void readSpecification(File spec)
    {
        JSONParser jsonParser = new JSONParser();

        try(FileReader reader = new FileReader(spec))
        {
            JSONObject JSONobj = (JSONObject)jsonParser.parse(reader);

            JSONArray agents = (JSONArray) JSONobj.get("agents");
            JSONArray locations = (JSONArray) JSONobj.get("locations");

            agents.forEach(agent -> new DeliveryAgent(((Long) ((JSONObject) agent).get("capacity")).intValue()));

            locations.forEach(location -> new Location(((Long) ((JSONObject) location).get("X")).intValue(),((Long) ((JSONObject) location).get("Y")).intValue()));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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

        JSONObject agents = new JSONObject();
        agents.put("agents", agentList);

        JSONObject Loc1 = new JSONObject();
        JSONObject Loc2 = new JSONObject();
        JSONObject Loc3 = new JSONObject();
        JSONObject Loc4 = new JSONObject();
        JSONObject Loc5 = new JSONObject();

        Loc1.put("X", 10);
        Loc1.put("Y", 10);

        Loc2.put("X", 20);
        Loc2.put("Y", 10);

        Loc3.put("X", 30);
        Loc3.put("Y", 20);

        Loc4.put("X", 20);
        Loc4.put("Y", 20);

        Loc5.put("X", 30);
        Loc5.put("Y", 10);

        JSONArray locationList = new JSONArray();
        locationList.add(Loc1);
        locationList.add(Loc2);
        locationList.add(Loc3);
        locationList.add(Loc4);
        locationList.add(Loc5);

        JSONObject locations = new JSONObject();
        locations.put("locations", locationList);

        JSONObject total = new JSONObject();
        total.put("locations", locationList);
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
