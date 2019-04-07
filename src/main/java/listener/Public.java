package listener;

import config.BotConfig;
import database.Database;
import entities.Data;
import entities.SignedServer;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import runnable.Task;
import sendMessage.EmbedMessage;
import server.discord.ServerDis;
import server.squad.BattleMetricsData;
import server.squad.ServerSquad;
import tests.TestMethod;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Public extends ListenerAdapter {

    Database database = new Database();

    Data data;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        data = new Data(event);

        if(event.getAuthor().isBot()) return;

        if(data.getContent().startsWith("?addchannel")){
            new ServerDis(data).addServer(database);
            return;
        }

        if(data.getContent().startsWith("?editchannel")){
            new ServerDis(data).editServer(database);
            return;
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

        if (data.getContent().startsWith("?addserver ")){
            new ServerSquad(data).addNewServer();
            return;
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

        if(data.getContent().startsWith("!дел "))
            new TestMethod(data).deleteMessages();
    }
}
