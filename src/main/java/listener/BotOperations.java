package listener;

import config.BotConfig;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import sendMessage.EmbedMessage;

public class BotOperations extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //Nothify creater about new server... Why not?)
        event.getJDA().getUserById(BotConfig.SPECIAL_ID).openPrivateChannel().queue(
                (channel) ->
                    channel.sendMessage(new EmbedMessage().onJoinDiscordServer(event).build()).queue()
        );

//        event.getGuild().getDefaultChannel().sendMessage()
    }
}
