package listener;

import config.BotConfig;
import database.Database;
import entities.DataPublic;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sendMessage.EmbedMessage;
import server.discord.Guide;
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

    private Database database = new Database();

    private DataPublic dataPublic;

    private long seconds = 120;


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if(event.getAuthor().isBot()) return;

        dataPublic = new DataPublic(event);

        if(dataPublic.getContent().equals("?aboutss")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            dataPublic.getChannel().sendMessage(new EmbedMessage().AboutBot(dataPublic).build()).queue(
                    (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
            );
            return;
        }

        if(dataPublic.getContent().equals("?creditss") || dataPublic.getContent().equals("?creditsss") || dataPublic.getContent().equals("?credits") || dataPublic.getContent().equals("?credit")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).sendCredits();
            return;
        }


        /*
        Only for developer
        */
        if(dataPublic.getAuthorId().equals(BotConfig.SPECIAL_ID)){

            //Close app in case of error
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

            //Check how many threads operates now
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

            //info about me
            if(dataPublic.getContent().equals("?info1")){
                dataPublic.getMessage().delete().queue();
                for(Role role : dataPublic.getMember().getRoles()){
                    System.out.println("Role = " + role.getName() + " id = " + role.getId());
                }
            }

            //testing Greeting message
            if(dataPublic.getContent().equals("?greeting")){
                dataPublic.getMessage().delete().queue();
                dataPublic.getChannel().sendMessage(new EmbedMessage().GreetingMessage(event)).queue();
            }


            //Should add this only for test server!
            if(dataPublic.getContent().startsWith("?дел "))
                new TestMethod(dataPublic).deleteMessages();
        }


        //For lance
        if(dataPublic.getContent().equals("?lance")){
            if(event.getMember().getRoles().contains(dataPublic.getGuild().getRoleById(BotConfig.ROLE_ID)) &&
                (event.getMessage().getCategory().getName().toLowerCase().equals("lance"))){
                dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInfoTemplate(new BattleMetricsData().getServerInfo(BotConfig.SERVER_ID).getList()).build()).queue(
                        (message) -> message.delete().queueAfter(30, TimeUnit.SECONDS)
                );
            }
        }


        if(!giveAccess(dataPublic)) return;

        if(dataPublic.getContent().equals("?helpss")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).sendHelp();
        }

        if(dataPublic.getContent().equals("?guide") || dataPublic.getContent().equals("?guidess")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new Guide(dataPublic, seconds).showGuide();
            return;
        }

        if(dataPublic.getContent().equals("?channel")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).showChannel(database);
            return;
        }

        if(dataPublic.getContent().startsWith("?addchannel")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).addChannel(database);
            return;
        }

        if(dataPublic.getContent().startsWith("?editchannel")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).editChannel(database);
            return;
        }

        if (dataPublic.getContent().equals("?servers") || dataPublic.getContent().equals("?server")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerSquad(dataPublic, database, seconds).showServersList();
            return;
        }

        if (dataPublic.getContent().startsWith("?addserver")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerSquad(dataPublic, database, seconds).addNewServer();
            return;
        }

        if(dataPublic.getContent().startsWith("?clean")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerSquad(dataPublic, database,seconds).eraseServerMessages();
            return;
        }

        if(dataPublic.getContent().startsWith("?deleteserver")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerSquad(dataPublic, database, seconds).deleteServer();
            return;
        }

        if(dataPublic.getContent().startsWith("?access")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).listRoles(database);
            return;
        }

        if(dataPublic.getContent().startsWith("?addrole")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).addRole(database);
        }

        if(dataPublic.getContent().startsWith("?deleterole")){
            dataPublic.getMessage().delete().queueAfter(seconds,TimeUnit.SECONDS);
            new ServerDis(dataPublic, seconds).deleteRole(database);
        }

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
