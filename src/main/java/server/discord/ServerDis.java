package server.discord;

import config.BotConfig;
import database.Database;
import entities.Data;
import net.dv8tion.jda.core.entities.Role;
import sendMessage.EmbedMessage;

import java.awt.*;
import java.util.List;


public class ServerDis {

    private Data data;
    private EmbedMessage embed = new EmbedMessage();

    private Color success = new Color(0, 226, 30);
    private Color error = new Color(255, 170, 0);
    private Color defaultColor = new Color(249, 29, 84);

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
                data.getChannel().sendMessage(embed.ServerInsertInfo(reply + "\n\nTo change chat - use command **?editchannel**")).queue();
                return;
            }
            dbAddServer();
        } else {
            data.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred", "No channel mentioned", error)).queue();
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
                    data.getChannel().sendMessage(embed.ServerInsertInfo("Successful event", "Bot successfully changed assigned chat", success)).queue();
                else
                    data.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred","Bot failed to change assigned chat", error)).queue();
            } else {
                dbAddServer();
            }
        } else {
            data.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred", "No channel mentioned", error)).queue();
        }
    }

    private void dbAddServer() {
        int result = new Database().insertNewServer(data.getGuild().getIdLong(), data.getMentionedChannel().getIdLong(), true, "en");
        if (result == 1)
            data.getChannel().sendMessage(embed.ServerInsertInfo("Successful event", "Bot successfully assigned to channel!", success)).queue();
        else
            data.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred", "Bot failed to add data to database. WARNING: Internal error!", error)).queue();
    }

    /*
    Todo: add successfull event
     */
    public void addRole(Database database){
        if(data.isRoleMentioned()){
            StringBuilder allText = new StringBuilder();
            List<Long> roles_id = database.getRoleId(data.getGuild().getIdLong());
            for(Role role : data.getMessage().getMentionedRoles()) {
                boolean exists = false;
                for (long role_id : roles_id){
                    if(role_id == role.getIdLong())
                        exists = true;
                }

                if(exists) {
                    allText.append("\u26A0 Role **").append(role.getName()).append("** already added to the bot\n\n");
                } else {
                    database.insertNewAuthorisedRoles(data.getGuild().getIdLong(), role.getIdLong());
                    allText.append("\u2705  Role **").append(role.getName()).append("** successfully added to the bot\n\n");
                }
            }
            data.getChannel().sendMessage(embed.ServerInsertInfo("Results:", allText.toString(), defaultColor)).queue();
        } else {
            data.getChannel().sendMessage("You forgot to mention roles").queue();
        }
    }

    public void listRoles(Database database){
        String text = "Here's a list who has access to the bot:\n" +
                "\n" +
                "All members with Permission **`MANAGE SERVER`**\n";
        for(long role_id : database.getRoleId(data.getGuild().getIdLong())){
            text += "Member with role - **" + data.getGuild().getRoleById(role_id).getName() + "**\n";
        }

        text += "\nTo add more roles - use command `?addrole`\n" +
                "To delete role - use command `?deleterole`";
        data.getChannel().sendMessage(embed.ServerInsertInfo(text)).queue();
    }

    public void deleteRole(Database database) {
        if(data.isRoleMentioned()){
            for(Role role : data.getMessage().getMentionedRoles()){
                database.deleteRole(role.getIdLong());
            }
            data.getChannel().sendMessage(embed.ServerInsertInfo("Successful event", "Successfully deleted mentioned roles!", success)).queue();
        } else {
            data.getChannel().sendMessage( embed.ServerInsertInfo("Error occurred", "You forgot to mention roles", error)).queue();
        }

    }



    public void sendHelp(){
        String text = "" +
                "**General:**\n" +
                "`?helpSS` - see list of commands\n" +
                "`?aboutSS` - see info about bot\n" +
                "`?credits` - see contributed to the project (in dev)" +
                "\n" +
                "**Only people who have `MANAGE SERVER` permission and/or added to the bot roles could use bot commands!**\n" +
                "\n" +
                "**Channel manipulations:**\n" +
                "`?addchannel #CHANNEL` - assign channel to the bot to post servers' status\n" +
                "`?editchannel #CHANNEL` - override already assigned channel to the bot\n" +
                "\n" +
                "**Servers manipulations:**\n" +
                "`?servers` - list all servers assigned to the bot\n" +
                "`?addserver server1 server2 .. serverN` - assign server/servers to the bot\n" +
                "`?deleteserver server1 server2 .. serverN` - delete server/servers from the bot\n" +
                "`?clean` - if added servers makes channel ugly... Use this command) It's harmless!\n" +
                "\n" +
                "Example: `?addserver 3272036` or `?addserver 3272036 2125740`\n" +
                "\n" +
                "**Role manipulations:**\n" +
                "`?access` - show list of roles who have access permission to the bot\n" +
                "`?addrole @ROLE1 @ROLE2 .. @ROLE` - add role/roles that can manage bot\n" +
                "`?deleterole @ROLE1 @ROLE2 .. @ROLE` - delete role/roles that can manage bot\n" +
                "\n" +
                "Example: `?addrole @admin` or `?addrole @admin @moderator`\n" +
                "\n" +
                "For additional help - contact **" + data.getGuild().getJDA().getUserById(BotConfig.SPECIAL_ID).getAsTag() + "**\n";
        data.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("List of the commands:", text, defaultColor)).queue();
    }


}