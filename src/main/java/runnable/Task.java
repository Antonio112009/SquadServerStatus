package runnable;

import database.Database;
import entities.SignedServer;
import net.dv8tion.jda.core.JDA;
import sendMessage.EmbedMessage;
import server.squad.BattleMetricsData;

import java.util.List;

public class Task {

    private JDA api;

    public Task(JDA api) {
        this.api = api;
    }

    void UpdateMessages(){
        Database database = new Database();
        List<SignedServer> serversList = database.getSignedServers();
        for(SignedServer server : serversList){
            new Thread(
                    () -> {
                        if(server.getMessage_id() != 0){
                            try{
                                System.out.println("guild = " + server.getGuild_id() + " channel_id = " + server.getChannel_id());
                                api.getGuildById(server.getGuild_id())
                                        .getTextChannelById(server.getChannel_id())
                                        .editMessageById(
                                                server.getMessage_id(),
                                                new EmbedMessage().ServerInfoTemplate(new BattleMetricsData().getServerInfo(String.valueOf(server.getServer_id())).getList()).build()).queue();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
            ).start();
        }
    }
}
