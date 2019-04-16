package server.discord;

import entities.DataPublic;
import sendMessage.EmbedMessage;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Guide {

    private DataPublic dataPublic;
    private Color defaultColor = new Color(249, 29, 84);

    public Guide(DataPublic dataPublic) {
        this.dataPublic = dataPublic;
    }

    public void showGuide(long seconds){
        String text = "" +
                "\n" +
                "**If you need help - use `?helpSS` command**\n" +
                "\n" +
                ":star: Only following users could manage bot:\n" +
                ":white_small_square:  Users with `MANAGE SERVER` permission\n" +
                ":white_small_square: Users with roles that were assigned to the bot\n" +
                "\n" +
                ":star2: **How to start using bot?**\n\n" +
                ":one: - assign bot to channel using `?addchannel` command\n\n" +
                ":two: - assign server(s) to the bot using `?addserver` command\n\n" +
                ":three: - if assigned channel looks \"ugly\" - use `?clean` command to clean all messages and \"rewrite\" server(s) status\n\n" +
                "\n" +
                ":star2: **Where to find server ID(s)?**\n" +
                "Currently, bot is taking data from [battlemetrics](https://www.battlemetrics.com/servers/squad)\n\n" +
                ":one: Find in search server(s) that you want to add to your server\n\n" +
                ":two: *URL* of the found server would be like: [https://www.battlemetrics.com/servers/squad/3407280](https://www.battlemetrics.com/servers/squad/3407280)\n\n" +
                ":three: **Get only numbers from the end of the URL.**\nIn our example it would be [3407280](https://www.battlemetrics.com/servers/squad/3407280)\n\n" +
                ":four: Use `?addserver` command to add that server.\nExample: `?addserver 3407280`" +
                "";
        dataPublic.getChannel().sendMessage(new EmbedMessage().ServerInsertInfo("Complete Guide (Self-destruct in 2 minute)", text, defaultColor)).queue(
                m -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
        );
    }
}
