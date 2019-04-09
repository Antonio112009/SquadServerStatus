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

//      Access only for creator.

        /*
        Close app in case of something anywhere where bot is added to channel
         */
        if(data.getAuthorId().equals(BotConfig.SPECIAL_ID)){
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
        }





        if(!giveAccess(data)) return;

        if(data.getContent().equals("?helpss")){
            new ServerDis(data).sendHelp();
        }

        if(data.getContent().equals("?aboutss")){
            data.getChannel().sendMessage(new EmbedMessage().aboutBot(data).build()).queue();
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

        if (data.getContent().startsWith("?addserver ")){
            new ServerSquad(data).addNewServer();
            return;
        }

        if(data.getContent().startsWith("?addrole list")){
            new ServerDis(data).listRoles(database);
            return;
        }

        if(data.getContent().startsWith("?addrole")){
            new ServerDis(data).addRole(database);
        }

        if(data.getContent().equals("?servers")){
            System.out.println("Show list of servers");
        }


















        if(data.getContent().equals("?test1")) {
            new Thread(
                        () -> {
                            for (SignedServer server : new Database().getSignedServers()) {
                                System.out.println(server.getServer_id());
                                data.getChannel().sendMessage(new EmbedMessage().ServerInfoTemplate(new BattleMetricsData().getServerInfo(String.valueOf(server.getServer_id())).getList()).build()).queue();
                            }
                        }
                    ).start();
        }

        if(data.getContent().startsWith("?tr")){
            // Get the managed bean for the thread system of the Java
            // virtual machine.
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();

            // Get the current number of live threads including both
            // daemon and non-daemon threads.
            int threadCount = bean.getThreadCount();
            data.getChannel().sendMessage("Thread Count = " + threadCount).queue();
            return;
        }

        if(data.getContent().equals("?test2"))
            new TestMethod(data).createTestMessage(BotConfig.TESTCHANNEL_ID);

        if(data.getContent().startsWith("?test3")) {
            event.getJDA().getPresence().setGame(Game.playing(data.getContent().split("\\+\\+")[1]));
            event.getJDA().getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
        }

        if(data.getContent().startsWith("?test4")){
            event.getJDA().getPresence().setGame(Game.playing("Type \\?help"));
            event.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
        }

        if(data.getContent().startsWith("!дел "))
            new TestMethod(data).deleteMessages();
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
