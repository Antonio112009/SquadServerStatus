package server.discord;

import database.Database;
import entities.Data;
import net.dv8tion.jda.core.entities.Role;
import sendMessage.EmbedMessage;

import java.util.List;


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
                data.getChannel().sendMessage(reply + "\n\nTo change chat - use command **?editchannel**").queue();
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

    /*
    Todo: add successfull event
     */
    public void addRole(Database database){
        if(data.isRoleMentioned()){
            List<Long> roles_id = database.getRoleId(data.getGuild().getIdLong());
            for(Role role : data.getMessage().getMentionedRoles()) {
                boolean exists = false;
                for (long role_id : roles_id){
                    if(role_id == role.getIdLong())
                        exists = true;
                }

                if(exists) {
                    data.getChannel().sendMessage("role " + role.getName() + " already added to the bot").queue();
                } else {
                    database.insertNewAuthorisedRoles(data.getGuild().getIdLong(), role.getIdLong());
                    data.getChannel().sendMessage("role " + role.getName() + " successfully added to the bot").queue();
                }

            }
        } else {
            data.getChannel().sendMessage("You forgot to mention roles").queue();
        }
    }

    public void listRoles(Database database){
        String text = "Here's a list who has access to the bot:\n" +
                "\n" +
                "All members with Permission `MANAGE SERVER`\n";
        for(long role_id : database.getRoleId(data.getGuild().getIdLong())){
            text += "Member with role - **" + data.getGuild().getRoleById(role_id).getName() + "**\n";
        }

        text += "\nTo add more roles - use command `?addrole`\n" +
                "To delete access to the bot - `?deleterole`";
        data.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo(text).build()).queue();
    }

    public void sendHelp(){
        String text = "" +
                "**List of commands:**\n" +
                "\n" +
                "**General:**\n" +
                "`?helpSS` - see list of commands\n" +
                "`?aboutSS` - see info about bot\n" +
                "\n" +
                "**Channel manipulations:**\n" +
                "`?addchannel #CHANNEL` - assign channel to the bot to post servers' status\n" +
                "`?editchannel #CHANNEL` - override already assigned channel to the bot\n" +
                "\n" +
                "**Servers manipulations:**\n" +
                "`?addserver server1 server2 .. serverN` - assign server/servers to the bot\n" +
                "Example: `?addserver 3272036` or `?addserver 3272036 2125740`\n" +
                "\n" +
                "**Role manipulations:**\n" +
                "`?addrole list` - show list of roles who has permission to the bot besides users tih `MANAGE SERVER` permission\n" +
                "`?addrole @ROLE1 @ROLE2 .. @ROLE` - add role/roles that can manage bot\n" +
                "\n" +
                "For additional help, contact **Tony Anglichanin#3069**";
        data.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo(text).build()).queue();
    }
}