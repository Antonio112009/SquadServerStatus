package runnable;

import database.Database;
import entities.SignedServer;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import sendMessage.EmbedMessage;
import server.squad.BattleMetricsData;

import java.util.List;

public class Task {

    private JDA api;
    private Database  database;

    public Task(JDA api, Database database) {
        this.api = api;
        this.database = database;
    }

    void UpdateMessages(){
        List<SignedServer> serversList = database.getSignedServers();
        for(SignedServer server : serversList){
            new Thread(
                    () -> {
                        if(server.getMessage_id() != 0){
                            try{
//                                System.out.println("guild = " + server.getGuild_id() + " channel_id = " + server.getChannel_id() + " Message = " +server.getMessage_id());
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

    void StatusUpdate(){
        if(api.getPresence().getGame().getName().toLowerCase().startsWith("help")){
            api.getPresence().setGame(Game.watching("Bot info - ?aboutSS"));
            return;
        }

        if(api.getPresence().getGame().getName().toLowerCase().startsWith("bot")){
            api.getPresence().setGame(Game.watching("help - ?helpSS"));
            return;
        }
    }
}
