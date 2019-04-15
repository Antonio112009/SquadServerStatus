package listener;

import config.BotConfig;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Private extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        if(!event.getAuthor().getId().equals(BotConfig.SPECIAL_ID))return;
    }
}
