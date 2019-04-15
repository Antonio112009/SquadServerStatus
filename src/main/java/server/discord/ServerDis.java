package server.discord;

import config.BotConfig;
import database.Database;
import entities.DataPublic;
import net.dv8tion.jda.core.entities.Role;
import sendMessage.EmbedMessage;

import java.awt.*;
import java.util.List;


public class ServerDis {

    private DataPublic dataPublic;
    private EmbedMessage embed = new EmbedMessage();

    private Color success = new Color(0, 226, 30);
    private Color error = new Color(255, 170, 0);
    private Color defaultColor = new Color(249, 29, 84);

    public ServerDis(DataPublic dataPublic) {
        this.dataPublic = dataPublic;
    }

    /*
    Добавляет чат сервера в БД
     */
    public void addServer(Database database) {
        if (dataPublic.isChatMentioned()) {
            if (database.checkDiscordServer(dataPublic.getGuild().getIdLong())) {
                String reply = "You have already assigned bot to channel";
                try {
                    reply += ": **" + dataPublic.getGuild().getTextChannelById(database.getDiscordServers("WHERE guild_id = " + dataPublic.getGuild().getId()).get(0).getChannel_id()).getName() + "**";
                } catch (Exception e) {
                    reply += " which I cannot find at the moment. Please, make some edits";
                }
                dataPublic.getChannel().sendMessage(embed.ServerInsertInfo(reply + "\n\nTo change chat - use command **?editchannel**")).queue();
                return;
            }
            dbAddServer();
        } else {
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred", "No channel mentioned", error)).queue();
        }
    }

    /*
    Пермаментно изменяет чат сервера в БД
     */
    public void editChannel(Database database){
        if (dataPublic.isChatMentioned()) {
            if (database.checkDiscordServer(dataPublic.getGuild().getIdLong())) {
                int result = database.editDiscordServer(dataPublic.getGuild().getIdLong(), dataPublic.getMentionedChannel().getIdLong());
                result *= database.editDiscordServerSigned(dataPublic.getGuild().getIdLong(), dataPublic.getMentionedChannel().getIdLong());
                System.out.println(result);
                if(result != 0)
                    dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Successful event", "Bot successfully changed assigned chat", success)).queue();
                else
                    dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred","Bot failed to change assigned chat", error)).queue();
            } else {
                dbAddServer();
            }
        } else {
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred", "No channel mentioned", error)).queue();
        }
    }

    private void dbAddServer() {
        int result = new Database().insertNewServer(dataPublic.getGuild().getIdLong(), dataPublic.getMentionedChannel().getIdLong(), true, "en");
        if (result == 1)
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Successful event", "Bot successfully assigned to channel **" + dataPublic.getMentionedChannel().getName() + "** !", success)).queue();
        else
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Error occurred", "Bot failed to add dataPublic to database. WARNING: Internal error!", error)).queue();
    }

    public void showChannel(Database database){
        long channel_id = database.getChannelId(dataPublic.getGuild().getIdLong());
        if(channel_id != 0){
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Result:", "" +
                    "Bot is assigned to channel **" + dataPublic.getGuild().getTextChannelById(channel_id).getName() + "**\n" +
                    "\n" +
                    "To change assigned channel to another one use command `?editchannel`" +
                    "", success)).queue();
        } else {
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Result:", "" +
                    "Bot is not assigned to any channel!\n" +
                    "\n" +
                    "To assign bot to channel use command `?addchannel`" +
                    "", error)).queue();
        }
    }

    /*
    Todo: add successfull event
    upd1: idk the problem...
     */
    public void addRole(Database database){
        if(dataPublic.isRoleMentioned()){
            StringBuilder allText = new StringBuilder();
            List<Long> roles_id = database.getRoleId(dataPublic.getGuild().getIdLong());
            for(Role role : dataPublic.getMessage().getMentionedRoles()) {
                boolean exists = false;
                for (long role_id : roles_id){
                    if(role_id == role.getIdLong())
                        exists = true;
                }

                if(exists) {
                    allText.append("\u26A0 Role **").append(role.getName()).append("** already added to the bot\n\n");
                } else {
                    database.insertNewAuthorisedRoles(dataPublic.getGuild().getIdLong(), role.getIdLong());
                    allText.append("\u2705  Role **").append(role.getName()).append("** successfully added to the bot\n\n");
                }
            }
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Result:", allText.toString(), defaultColor)).queue();
        } else {
            dataPublic.getChannel().sendMessage("You forgot to mention roles").queue();
        }
    }

    public void listRoles(Database database){
        String text = "Here's a list who has access to the bot:\n" +
                "\n" +
                "All members with Permission **`MANAGE SERVER`**\n";
        for(long role_id : database.getRoleId(dataPublic.getGuild().getIdLong())){
            text += "Member with role - **" + dataPublic.getGuild().getRoleById(role_id).getName() + "**\n";
        }

        text += "\nTo add more roles - use command `?addrole`\n" +
                "To delete role - use command `?deleterole`";
        dataPublic.getChannel().sendMessage(embed.ServerInsertInfo(text)).queue();
    }

    /*
    Todo: add success or fail events
    upd1: I suppose, I finished that
     */
    public void deleteRole(Database database) {
        StringBuilder text = new StringBuilder();
        if(dataPublic.isRoleMentioned()){
            for(Role role : dataPublic.getMessage().getMentionedRoles()){

                if(database.deleteRole(role.getIdLong()) == 1){
                    text.append("\u2705 Role **").append(role.getName()).append("** - successfully deleted from access list\n\n");
                } else {
                    text.append("\u274C Role **").append(role.getName()).append("** - does not exist in access list\n\n");
                }
            }
            dataPublic.getChannel().sendMessage(embed.ServerInsertInfo("Result:", text.toString(), success)).queue();
        } else {
            dataPublic.getChannel().sendMessage( embed.ServerInsertInfo("Error occurred", "You forgot to mention roles", error)).queue();
        }

    }



    public void sendHelp(){
        String text = "" +
                "**General:**\n" +
                "`?helpSS` - see list of commands\n" +
                "`?aboutSS` - see info about bot\n" +
                "`?credits` - see contributed to the project (in dev)\n" +
                "\n" +
                "**Only people who have `MANAGE SERVER` permission and/or added to the bot roles could use bot commands!**\n" +
                "\n" +
                "**Channel manipulations:**\n" +
                "`?channel` - show assigned channel to the bot" +
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
                "For additional help - contact **" + dataPublic.getGuild().getJDA().getUserById(BotConfig.SPECIAL_ID).getAsTag() + "**\n";
        dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("List of the commands:", text, defaultColor)).queue();
    }


    public void sendCredits(){
        String text = "" +
                "I would like to say thank you to everyone who helped me or gave me advises:" +
                "**" +
                "[ProG]Aibo,\nVirus.exe,\n[BORN]Enj0y,\n508|CPL-Gerrit,\n508th|SPC-Llamageddon,\n508th|SGT-Ekberg,\nGatzby." +
                "**";

        dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("Credits:", text, defaultColor)).queue();
    }


}