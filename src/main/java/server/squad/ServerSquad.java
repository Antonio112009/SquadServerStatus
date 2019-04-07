package server.squad;

import config.BotConfig;
import database.Database;
import entities.Data;
import entities.ServerInfo;
import runnable.Task;
import sendMessage.EmbedMessage;

public class ServerSquad {

    private Data data;
    private Database database;

    public ServerSquad(Data data) {
        this.data = data;
        database = new Database();
    }

    public void addNewServer(){
        String[] arrayLine = data.getContent().split(" ");

        StringBuilder embedText = new StringBuilder();
        embedText.append("Results of adding servers to the bot:\n\n");

        if(arrayLine.length > 1){
            new Thread(
                    () ->{
                        ServerInfo serverInfo = null;
                        for (int i = 0; i < arrayLine.length - 1; i++) {
                            System.out.println("i = " + i);
                            try {
                                serverInfo = new Task().getServerInfo(arrayLine[i+1]);
                                if (serverInfo.getGameName().equals("squad")){
                                    if(!database.checkSignedServer(data.getGuild().getIdLong(), Long.parseLong(serverInfo.getServerId()))) {
                                        embedText.append("**").append(serverInfo.getServerName()).append("**\n");
                                        embedText.append("\u2705 Server [").append(serverInfo.getServerId()).append("](https://api.battlemetrics.com/servers/").append(serverInfo.getServerId()).append(") - this server successfully assigned to the bot\n\n");
                                        database.insertNewSignedServer(data.getGuild().getIdLong(), Long.parseLong(serverInfo.getServerId()));
                                    } else {
                                        //server already exists
                                        embedText.append("\u26A0 Server [").append(serverInfo.getServerId()).append("](https://api.battlemetrics.com/servers/").append(serverInfo.getServerId()).append(") - this server already assigned to the bot \n");

                                    }
                                } else {
                                    //Server isn't for squad
                                    embedText.append(":name_badge: Server [").append(serverInfo.getServerId()).append("](https://api.battlemetrics.com/servers/").append(serverInfo.getServerId()).append(") - this isn't Squad game server\n\n");
                                }
                            } catch (Exception e){
                                embedText.append("\u274C Server [").append(arrayLine[i+1]).append("](https://en.wikipedia.org/wiki/HTTP_404) - this server cannot be added as it has incorrect input\n\n");
                            }
                        }
                        new EmbedMessage().ServerInsertInfo(data, embedText.toString(), data.getChannel().getIdLong());
                    }
            ).start();
        } else {
            data.getChannel().sendMessage("You forgot to mention servers").queue();
        }
    }
}
