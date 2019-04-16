package listener;

import config.BotConfig;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sendMessage.EmbedMessage;

import java.awt.*;

public class BotOperations extends ListenerAdapter {


    private Color defaultColor = new Color(249, 29, 84);

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //Nothify creater about new server... Why not?)
        event.getJDA().getUserById(BotConfig.SPECIAL_ID).openPrivateChannel().queue(
                (channel) ->
                    channel.sendMessage(new EmbedMessage().onJoinDiscordServer(event).build()).queue()
        );

        TextChannel defChannel = event.getGuild().getDefaultChannel();

        if(defChannel != null){
            defChannel.sendMessage(new EmbedMessage().GreetingMessage(event)).queue();
        } else {
//            event.getGuild().
        }
    }
}
