package server.squad;

import entities.ServerInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class BattleMetricsData {
    public ServerInfo getServerInfo(String serverId){
        System.out.println("Read server id = " + serverId);
        InputStreamReader reader = null;
        try  {
            URL url = null;
            try {
                url = new URL("https://api.battlemetrics.com/servers/" + serverId);
            } catch (MalformedURLException ignored) {}
            try {
                assert url != null;
                reader = new InputStreamReader(url.openStream());
                System.out.println("Read successfully!");
//            } catch (IOException ignored) {
            } catch (IOException e) {
                System.out.println("IO error:");
                e.printStackTrace();
            }
//        } catch (Exception ignored) {
        } catch (Exception e) {
            System.out.println("EXCEPTION error:");
            e.printStackTrace();

        }
        return new ServerInfo(reader) ;
    }
}
