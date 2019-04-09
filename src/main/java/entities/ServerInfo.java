package entities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ServerInfo {

    private JsonElement jElement;
    private JsonObject data;

    public ServerInfo(InputStreamReader json) {
        this.jElement = new JsonParser().parse(json);
        data = jElement.getAsJsonObject().getAsJsonObject("data");
    }

    public String getServerId(){
        return data.getAsJsonObject("attributes").get("id").getAsString();
    }

    public String getServerName(){
        return data.getAsJsonObject("attributes").get("name").getAsString();
    }

    public String getMaxPlayers(){
        return data.getAsJsonObject("attributes").get("maxPlayers").getAsString();
    }

    public String getCurrentPlayers(){
        return data.getAsJsonObject("attributes").get("players").getAsString();
    }

    public String getStatus(){
        return data.getAsJsonObject("attributes").get("status").getAsString();
    }

    public String getMap(){
        return data.getAsJsonObject("attributes").getAsJsonObject("details").get("map").getAsString();
    }

    public String getMode(){
        return data.getAsJsonObject("attributes").getAsJsonObject("details").get("gameMode").getAsString();
    }

    public String getGameName(){
        return data.getAsJsonObject("relationships").getAsJsonObject("game").getAsJsonObject("data").get("id").getAsString();
    }

    public String getUrlConnection(){
        return "steam://connect/" + data.getAsJsonObject("attributes").get("ip").getAsString() + ":" + data.getAsJsonObject("attributes").get("portQuery").getAsString();
    }

    public List<String> getList(){
        List<String> list = new ArrayList<>();
        list.add(getServerName());
        list.add(getMaxPlayers());
        list.add(getCurrentPlayers());
        list.add(getStatus());
        list.add(getMap());
        list.add(getMode());
        list.add(getUrlConnection());
        list.add(getGameName());
        list.add(getServerId());

        return list;
    }
}
