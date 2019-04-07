package runnable;

import entities.ServerInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class Task {

    public ServerInfo getServerInfo(String serverId){
        InputStreamReader reader = null;
        try  {
            URL url = null;
            try {
                url = new URL("https://api.battlemetrics.com/servers/" + serverId);
            } catch (MalformedURLException ignored) {}
            try {
                assert url != null;
                reader = new InputStreamReader(url.openStream());
            } catch (IOException ignored) { }
        } catch (Exception ignored) {
        }
        return new ServerInfo(reader) ;
    }
}
