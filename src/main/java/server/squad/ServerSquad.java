package server.squad;

import database.Database;
import entities.Data;
import entities.ServerInfo;
import entities.SignedServer;
import sendMessage.EmbedMessage;

import java.awt.*;
import java.util.List;

public class ServerSquad {

    private Data data;
    private Database database;

    public ServerSquad(Data data, Database database) {
        this.data = data;
        this.database = database;
    }

    public void addNewServer(){
        String[] arrayLine = data.getContent().split(" ");

        StringBuilder embedText = new StringBuilder();
        embedText.append("Results of adding servers to the bot:\n\n");

        long channel_id = database.getChannelId(data.getGuild().getIdLong());
        if(channel_id == 0){
            data.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("Error occurred","" +
                    "You haven't assigned channel to the bot.\n" +
                    "\n" +
                    "To add channel - use `?addchannel` command", new Color(255, 170, 0))).queue();
            return;
        }


        if(arrayLine.length > 1){
            new Thread(
                    () ->{
                        ServerInfo serverInfo = null;
                        for (int i = 0; i < arrayLine.length - 1; i++) {
//                            System.out.println("i = " + i);
                            try {
                                serverInfo = new BattleMetricsData().getServerInfo(arrayLine[i+1]);
                                if (serverInfo.getGameName().equals("squad")){
                                    if(!database.checkSignedServer(data.getGuild().getIdLong(), Long.parseLong(serverInfo.getServerId()))) {
                                        embedText.append("**").append(serverInfo.getServerName()).append("**\n");
                                        embedText.append("\u2705 Server [").append(serverInfo.getServerId()).append("](https://api.battlemetrics.com/servers/").append(serverInfo.getServerId()).append(") - this server successfully assigned to the bot\n\n");
                                        database.insertNewSignedServer(data.getGuild().getIdLong(), channel_id, Long.parseLong(serverInfo.getServerId()));

                                        ServerInfo finalServerInfo = serverInfo;
                                        ServerInfo finalServerInfo1 = serverInfo;
                                        data.getGuild().getTextChannelById(channel_id).sendMessage(new EmbedMessage().EmptyEmbed().build()).queue(
                                                (e) ->{
                                                    data.getGuild().getTextChannelById(channel_id).editMessageById(e.getId(), new EmbedMessage().ServerInfoTemplate(finalServerInfo1.getList()).build()).queue();
                                                    new Database().updateMessageServer(data.getGuild().getIdLong(),Long.parseLong(finalServerInfo.getServerId()), e.getIdLong());
                                                });
                                    } else {
                                        //server already exists
                                        embedText.append("\u26A0 Server [").append(serverInfo.getServerId()).append("](https://api.battlemetrics.com/servers/").append(serverInfo.getServerId()).append(") - this server already assigned to the bot \n\n");

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

    //Случайно удалил серв - надо восстановить
    public void eraseServerMessages(){
        List<SignedServer> arrayList = database.getSignedServers("WHERE guild_id = " + data.getGuild().getId());
        long channel_id = database.getChannelId(data.getGuild().getIdLong());
        data.getGuild().getTextChannelById(channel_id).getIterableHistory().takeAsync(100).thenAccept(data.getGuild().getTextChannelById(channel_id)::purgeMessages);

        for (SignedServer server_id : arrayList){
             ServerInfo serverInfo = new BattleMetricsData().getServerInfo(String.valueOf(server_id.getServer_id()));
            data.getGuild().getTextChannelById(channel_id).sendMessage(new EmbedMessage().EmptyEmbed().build()).queue(
                    (e) ->{
                        data.getGuild().getTextChannelById(channel_id).editMessageById(e.getId(), new EmbedMessage().ServerInfoTemplate(serverInfo.getList()).build()).queue();
                        new Database().updateMessageServer(data.getGuild().getIdLong(),Long.parseLong(serverInfo.getServerId()), e.getIdLong());
                    });
        }
    }

    public void deleteServer() {
        String[] arrayList = data.getContent().split(" ");
        if (arrayList.length > 1) {
            for (String server_id : arrayList){
                if(server_id.startsWith("?delete")) continue;
                try {
                    long server_id_long = Long.parseLong(server_id);
                    SignedServer signedServer = database.getSignedServers("WHERE guild_id = " + data.getGuild().getId() + " AND server_id = " + server_id).get(0);
                    data.getGuild().getTextChannelById(signedServer.getChannel_id()).deleteMessageById(signedServer.getMessage_id()).queue();
                    database.deleteServer(data.getGuild().getIdLong(), server_id_long);
                } catch (NumberFormatException e){
                    //TODO: number format error
                } catch (Exception ignore){
                }
            }
            data.getChannel().sendMessage("Bot successfully deleted servers").queue();
        } else {
            data.getChannel().sendMessage("You forgot to mention servers").queue();
        }
    }

    public void showServersList() {
        List<SignedServer> serverList = database.getSignedServers("WHERE guild_id = " + data.getGuild().getId());
        new Thread(
                () ->{
                    StringBuilder text = new StringBuilder("" +
                            "Servers connected to the bot:\n\n");
                    for (SignedServer server : serverList){
                        ServerInfo info = new BattleMetricsData().getServerInfo(String.valueOf(server.getServer_id()));
                        text.append("ServerId: **").append(server.getServer_id()).append("**\n")
                                .append("Name: **").append(info.getServerName()).append("**\n")
                                .append("Status: **").append(info.getStatus()).append("**\n\n");
                    }
                    data.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo(text.toString())).queue();
                }
        ).start();
    }
}
