package sendMessage;

import entities.Data;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class EmbedMessage {
    private EmbedBuilder embed = new EmbedBuilder();

    public void ServerInfoTemplate(List<String> serverInfo, Data data, long channel_id){
        embed.setColor(new Color(150, 3, 30));
        if(serverInfo.get(4).startsWith("Fool's Road AAS v4"))
            embed.setThumbnail("http://m.ahod.si/maps/thumbnails/Fools_Road_AAS_v4.jpg");
        else
            embed.setThumbnail("http://m.ahod.si/maps/thumbnails/" + serverInfo.get(4).replace(' ', '_') + ".jpg");
        embed.setAuthor(serverInfo.get(0));
        embed.setDescription("Server status:" + serverInfo.get(3));
        embed.addField("Players", serverInfo.get(2) + "/" + serverInfo.get(1), true);
        embed.addField("Map and mod",serverInfo.get(4) + " " + serverInfo.get(5), true);
        embed.setTimestamp(Instant.now());
        embed.addField("Join server: ",serverInfo.get(6), false);
        data.getGuild().getTextChannelById(channel_id).sendMessage(embed.build()).queue();

    }

    public void ServerInsertInfo(Data data, String text, long channel_id){
        embed.setColor(new Color(0, 0, 100));
        embed.setDescription(text);
        data.getGuild().getTextChannelById(channel_id).sendMessage(embed.build()).queue();
    }
}
