package listener;

import config.BotConfig;
import database.Database;
import entities.DataPublic;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sendMessage.EmbedMessage;
import server.discord.ServerDis;
import server.squad.BattleMetricsData;
import server.squad.ServerSquad;
import tests.TestMethod;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Public extends ListenerAdapter {

    Database database = new Database();

    DataPublic dataPublic;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        dataPublic = new DataPublic(event);

        if(dataPublic.getContent().equals("?aboutss")){
            dataPublic.getChannel().sendMessage(new EmbedMessage().AboutBot(dataPublic).build()).queue();
            return;
        }

        if(dataPublic.getContent().equals("?creditss") || dataPublic.getContent().equals("?creditsss")){
            new ServerDis(dataPublic).sendCredits();
            return;
        }

        if(dataPublic.getAuthorId().equals(BotConfig.SPECIAL_ID)){
            //      Access only for creator.

            /*
            Close app in case of something anywhere where bot is added to channel
             */

            if(dataPublic.getContent().equals("?exitss")){
                dataPublic.getMessage().delete().queue();
                dataPublic.getGuild().getJDA().getUserById(BotConfig.SPECIAL_ID).openPrivateChannel().queue(
                        (channel) -> {
                            channel.deleteMessageById(channel.getLatestMessageId()).queue();
                            channel.sendMessage("Bot successfully finished at " + Instant.now()).queue();
                            System.exit(0);
                        }
                );
            }

            if(dataPublic.getContent().startsWith("?tr")){
                dataPublic.getMessage().delete().queue();
                // Get the managed bean for the thread system of the Java
                // virtual machine.
                ThreadMXBean bean = ManagementFactory.getThreadMXBean();

                // Get the current number of live threads including both
                // daemon and non-daemon threads.
                int threadCount = bean.getThreadCount();
                dataPublic.getChannel().sendMessage("Thread Count = " + threadCount).queue(
                        (message) -> message.delete().queueAfter(5, TimeUnit.SECONDS)
                );
                return;
            }

            if(dataPublic.getContent().equals("?info1")){
                dataPublic.getMessage().delete().queue();
                for(Role role : dataPublic.getMember().getRoles()){
                    System.out.println("Role = " + role.getName() + " id = " + role.getId());
                }
            }


            /*
            Should add this only for test server!
             */
            if(dataPublic.getContent().startsWith("?дел "))
                new TestMethod(dataPublic).deleteMessages();
        }

        if(event.getAuthor().isBot()) return;



//        For lance
        if(dataPublic.getContent().equals("?lance")){
            dataPublic.getMessage().delete().queue();
            if(event.getMember().getRoles().contains(dataPublic.getGuild().getRoleById(BotConfig.ROLE_ID)) &&
                (event.getMessage().getCategory().getName().toLowerCase().equals("lance"))){
                dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInfoTemplate(new BattleMetricsData().getServerInfo(BotConfig.SERVER_ID).getList()).build()).queue(
                        (message) -> message.delete().queueAfter(30, TimeUnit.SECONDS)
                );
            }
        }





        if(!giveAccess(dataPublic)) return;

        if(dataPublic.getContent().equals("?helpss")){
            new ServerDis(dataPublic).sendHelp();
        }

        if(dataPublic.getContent().equals("?channel")){
            new ServerDis(dataPublic).showChannel(database);
            return;
        }

        if(dataPublic.getContent().startsWith("?addchannel")){
            new ServerDis(dataPublic).addServer(database);
            return;
        }

        if(dataPublic.getContent().startsWith("?editchannel")){
            new ServerDis(dataPublic).editChannel(database);
            return;
        }

        if (dataPublic.getContent().equals("?servers") || dataPublic.getContent().equals("?server")){
            new ServerSquad(dataPublic, database).showServersList();
            return;
        }

        if (dataPublic.getContent().startsWith("?addserver ")){
            new ServerSquad(dataPublic, database).addNewServer();
            return;
        }

        if(dataPublic.getContent().startsWith("?clean")){
            new ServerSquad(dataPublic, database).eraseServerMessages();
            return;
        }

        if(dataPublic.getContent().startsWith("?deleteserver ")){
            new ServerSquad(dataPublic, database).deleteServer();
            return;
        }

        if(dataPublic.getContent().startsWith("?access")){
            new ServerDis(dataPublic).listRoles(database);
            return;
        }

        if(dataPublic.getContent().startsWith("?addrole")){
            new ServerDis(dataPublic).addRole(database);
        }

        if(dataPublic.getContent().startsWith("?deleterole")){
            new ServerDis(dataPublic).deleteRole(database);
        }
//
//        if(dataPublic.getContent().startsWith("?test1")){
//            new EmbedMessage().TestColor(dataPublic);
//        }

    }


    private boolean giveAccess(DataPublic dataPublic){
        for(Permission permission : dataPublic.getMember().getPermissions())
            if (permission.getName().toLowerCase().equals("manage server"))
                return true;

        List<Long> roles_id = database.getRoleId(dataPublic.getGuild().getIdLong());
        for(long role_id : roles_id)
            for(Role role : dataPublic.getMember().getRoles())
                if (role_id == role.getIdLong())
                    return true;

        return false;
    }
}
