package server.squad;

import database.Database;
import entities.DataPublic;
import entities.ServerInfo;
import entities.SignedServer;
import sendMessage.EmbedMessage;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


/*
I suppose heere everywhere is embed message
 */
public class ServerSquad {

    private Color success = new Color(0, 226, 30);
    private Color error = new Color(255, 170, 0);
    private Color defaultColor = new Color(249, 29, 84);

    private DataPublic dataPublic;
    private Database database;
    private long seconds;

    public ServerSquad(DataPublic dataPublic, Database database, long seconds) {
        this.dataPublic = dataPublic;
        this.database = database;
        this.seconds = seconds;
    }

    public void addNewServer(){
        String[] arrayLine = dataPublic.getContent().split(" ");

        StringBuilder embedText = new StringBuilder();
        embedText.append("Results of adding servers to the bot:\n\n");

        long channel_id = database.getChannelId(dataPublic.getGuild().getIdLong());
        if(channel_id == 0){
            dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("Error occurred","" +
                    "You haven't assigned channel to the bot.\n" +
                    "\n" +
                    "To add channel - use `?addchannel` command", new Color(255, 170, 0))).queue(
                    (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
            );
            return;
        }

        System.out.println("array = " + arrayLine.length);
        if(arrayLine.length > 1){
            new Thread(
                    () ->{
                        ServerInfo serverInfo = null;
                        for (int i = 0; i < arrayLine.length - 1; i++) {
                            try {
                                serverInfo = new BattleMetricsData().getServerInfo(arrayLine[i+1]);
                                if (serverInfo.getGameName().equals("squad")){
                                    if(!database.checkSignedServer(dataPublic.getGuild().getIdLong(), Long.parseLong(serverInfo.getServerId()))) {
                                        embedText.append("**").append(serverInfo.getServerName()).append("**\n");
                                        embedText.append("\u2705 Server [").append(serverInfo.getServerId()).append("](https://api.battlemetrics.com/servers/").append(serverInfo.getServerId()).append(") - this server successfully assigned to the bot\n\n");
                                        database.insertNewSignedServer(dataPublic.getGuild().getIdLong(), channel_id, Long.parseLong(serverInfo.getServerId()));

                                        ServerInfo finalServerInfo = serverInfo;
                                        ServerInfo finalServerInfo1 = serverInfo;
                                        dataPublic.getGuild().getTextChannelById(channel_id).sendMessage(new EmbedMessage().EmptyEmbed().build()).queue(
                                                (e) ->{
                                                    dataPublic.getGuild().getTextChannelById(channel_id).editMessageById(e.getId(), new EmbedMessage().ServerInfoTemplate(finalServerInfo1.getList()).build()).queue();
                                                    new Database().updateMessageServer(dataPublic.getGuild().getIdLong(),Long.parseLong(finalServerInfo.getServerId()), e.getIdLong());
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
                        new EmbedMessage().ServerInsertInfo(dataPublic, embedText.toString(), dataPublic.getChannel().getIdLong(), seconds);
                    }
            ).start();
        } else {
            dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("" +
                    "Error occurred","" +
                    "You forgot to mention at least one server." +
                    "", error)).queue(
                    (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
            );
        }
    }

    //Случайно удалил серв - надо восстановить
    public void eraseServerMessages(){
        List<SignedServer> arrayList = database.getSignedServers("WHERE guild_id = " + dataPublic.getGuild().getId());
        long channel_id = database.getChannelId(dataPublic.getGuild().getIdLong());
        dataPublic.getGuild().getTextChannelById(channel_id).getIterableHistory().takeAsync(100).thenAccept(dataPublic.getGuild().getTextChannelById(channel_id)::purgeMessages);

        for (SignedServer server_id : arrayList){
             ServerInfo serverInfo = new BattleMetricsData().getServerInfo(String.valueOf(server_id.getServer_id()));
            dataPublic.getGuild().getTextChannelById(channel_id).sendMessage(new EmbedMessage().EmptyEmbed().build()).queue(
                    (e) ->{
                        dataPublic.getGuild().getTextChannelById(channel_id).editMessageById(e.getId(), new EmbedMessage().ServerInfoTemplate(serverInfo.getList()).build()).queue();
                        new Database().updateMessageServer(dataPublic.getGuild().getIdLong(),Long.parseLong(serverInfo.getServerId()), e.getIdLong());
                    });
        }
    }

    /*
    TODO: add beauty here!!!
     */
    public void deleteServer() {
        String[] arrayList = dataPublic.getContent().split(" ");
        if (arrayList.length > 1) {
            for (String server_id : arrayList){
                if(server_id.startsWith("?delete")) continue;
                try {
                    long server_id_long = Long.parseLong(server_id);
                    SignedServer signedServer = database.getSignedServers("WHERE guild_id = " + dataPublic.getGuild().getId() + " AND server_id = " + server_id).get(0);
                    dataPublic.getGuild().getTextChannelById(signedServer.getChannel_id()).deleteMessageById(signedServer.getMessage_id()).queue();
                    database.deleteServer(dataPublic.getGuild().getIdLong(), server_id_long);
                } catch (NumberFormatException e){
                    //TODO: number format error
                } catch (Exception ignore){
                }
            }
            dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("Successful event", "Bot successfully deleted servers", success)).queue(
                    (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
            );
        } else {
            dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("Error occurred","You forgot to mention servers", error)).queue(
                    (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
            );
        }
    }

    public void showServersList() {
        List<SignedServer> serverList = database.getSignedServers("WHERE guild_id = " + dataPublic.getGuild().getId());
        new Thread(
                () ->{
                    StringBuilder text = new StringBuilder("" +
                            "Servers connected to the bot:\n\n");
                    if(serverList.size() == 0){
                        dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("" +
                                "Servers:", "" +
                                "No server assigned to the bot\n" +
                                "\n" +
                                "To assign new server use command `?addserver`",error)).queue();
                    } else {
                        for (SignedServer server : serverList) {
                            ServerInfo info = new BattleMetricsData().getServerInfo(String.valueOf(server.getServer_id()));
                            text.append("ServerId: **").append(server.getServer_id()).append("**\n")
                                    .append("Name: **").append(info.getServerName()).append("**\n")
                                    .append("Status: **").append(info.getStatus()).append("**\n\n");
                        }
                        dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo(text.toString())).queue(
                                (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
                        );
                    }
                }
        ).start();
    }
}
