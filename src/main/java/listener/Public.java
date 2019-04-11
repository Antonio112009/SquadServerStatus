package listener;

import config.BotConfig;
import database.Database;
import entities.Data;
import entities.SignedServer;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
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

    Data data;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        data = new Data(event);

        if(event.getAuthor().isBot()) return;

        if(data.getContent().equals("?info1")){
            for(Role role : data.getMember().getRoles()){
                System.out.println("Role = " + role.getName() + " id = " + role.getId());
            }
        }


//        For lance
        if(data.getContent().equals("?lance")){
            data.getMessage().delete().queue();
            if(event.getMember().getRoles().contains(data.getGuild().getRoleById(BotConfig.ROLE_ID)) &&
                (event.getMessage().getCategory().getName().toLowerCase().equals("lance"))){
                data.getChannel().sendMessage(new EmbedMessage().ServerInfoTemplate(new BattleMetricsData().getServerInfo(BotConfig.SERVER_ID).getList()).build()).queue(
                        (message) -> message.delete().queueAfter(30, TimeUnit.SECONDS)
                );
            }
        }



        if(data.getAuthorId().equals(BotConfig.SPECIAL_ID)){
            //      Access only for creator.

            /*
            Close app in case of something anywhere where bot is added to channel
             */

            if(data.getContent().equals("?exitss")){
                data.getMessage().delete().queue();
                data.getGuild().getJDA().getUserById(BotConfig.SPECIAL_ID).openPrivateChannel().queue(
                        (channel) -> {
                            channel.deleteMessageById(channel.getLatestMessageId()).queue();
                            channel.sendMessage("Bot successfully finished at " + Instant.now()).queue();
                            System.exit(0);
                        }
                );
            }

            if(data.getContent().startsWith("?tr")){
                // Get the managed bean for the thread system of the Java
                // virtual machine.
                ThreadMXBean bean = ManagementFactory.getThreadMXBean();

                // Get the current number of live threads including both
                // daemon and non-daemon threads.
                int threadCount = bean.getThreadCount();
                data.getChannel().sendMessage("Thread Count = " + threadCount).queue(
                        (message) -> message.delete().queueAfter(10, TimeUnit.SECONDS)
                );
                return;
            }


            /*
            Should add this only for test server!
             */
            if(data.getContent().startsWith("?дел "))
                new TestMethod(data).deleteMessages();
        }





        if(!giveAccess(data)) return;

        if(data.getContent().equals("?helpss")){
            new ServerDis(data).sendHelp();
        }

        if(data.getContent().equals("?aboutss")){
            data.getChannel().sendMessage(new EmbedMessage().AboutBot(data).build()).queue();
        }


        if (data.getContent().equals("?info")){
            for (Permission permission : data.getMember().getPermissions()){
                if(permission.getName().equals("Manage Server"))
                    System.out.println(permission.getName());
            }
        }

        if(data.getContent().startsWith("?addchannel")){
            new ServerDis(data).addServer(database);
            return;
        }

        if(data.getContent().startsWith("?editchannel")){
            new ServerDis(data).editServer(database);
            return;
        }

        if (data.getContent().equals("?servers") || data.getContent().equals("?server")){
            new ServerSquad(data, database).showServersList();
            return;
        }

        if (data.getContent().startsWith("?addserver ")){
            new ServerSquad(data, database).addNewServer();
            return;
        }

        if(data.getContent().startsWith("?clean")){
            new ServerSquad(data, database).eraseServerMessages();
            return;
        }

        if(data.getContent().startsWith("?deleteserver")){
            new ServerSquad(data, database).deleteServer();
            return;
        }

        if(data.getContent().startsWith("?access")){
            new ServerDis(data).listRoles(database);
            return;
        }

        if(data.getContent().startsWith("?addrole")){
            new ServerDis(data).addRole(database);
        }

        if(data.getContent().startsWith("?deleterole")){
            new ServerDis(data).deleteRole(database);
        }
    }


    private boolean giveAccess(Data data){
        for(Permission permission : data.getMember().getPermissions())
            if (permission.getName().toLowerCase().equals("manage server"))
                return true;

        List<Long> roles_id = database.getRoleId(data.getGuild().getIdLong());
        for(long role_id : roles_id)
            for(Role role : data.getMember().getRoles())
                if (role_id == role.getIdLong())
                    return true;

        return false;
    }
}
