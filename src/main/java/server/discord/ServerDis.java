package server.discord;

import database.Database;
import entities.Data;



public class ServerDis {

    private Data data;

    public ServerDis(Data data) {
        this.data = data;
    }

    /*
    Добавляет чат сервера в БД
     */
    public void addServer(Database database) {
        if (data.isChatMentioned()) {
            if (database.checkDiscordServer(data.getGuild().getIdLong())) {
                String reply = "You have already assigned bot to channel";
                try {
                    reply += ": **" + data.getGuild().getTextChannelById(database.getDiscordServers("WHERE guild_id = " + data.getGuild().getId()).get(0).getChannel_id()).getName() + "**";
                } catch (Exception e) {
                    reply += " which I cannot find at the moment. Please, make some edits";
                }
                data.getChannel().sendMessage(reply + "\n\nTo change chat - use command **?editserver**").queue();
                return;
            }
            dbAddServer();
        } else {
            data.getChannel().sendMessage("No chat mentioned").queue();
        }
    }

    /*
    Пермаментно изменяет чат сервера в БД
     */
    public void editServer(Database database){
        if (data.isChatMentioned()) {
            if (database.checkDiscordServer(data.getGuild().getIdLong())) {
                int result = database.editDiscordServer(data.getGuild().getIdLong(),data.getMentionedChannel().getIdLong());
                if(result == 1)
                    data.getChannel().sendMessage("Bot successfully changed assigned chat").queue();
                else
                    data.getChannel().sendMessage("Bot failed to change assigned chat").queue();
            } else {
                dbAddServer();
            }
        } else {
            data.getChannel().sendMessage("No chat mentioned").queue();
        }
    }

    private void dbAddServer() {
        int result = new Database().insertNewServer(data.getGuild().getIdLong(), data.getMentionedChannel().getIdLong(), true, "en");
        if (result == 1)
            data.getChannel().sendMessage("Bot successfully assigned to channel!").queue();
        else
            data.getChannel().sendMessage("Bot failed to add data to database. WARNING: Internal error!").queue();
    }
}