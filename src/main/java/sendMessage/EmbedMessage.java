package sendMessage;

import entities.Data;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class EmbedMessage {
    private EmbedBuilder embed = new EmbedBuilder();

    public EmbedBuilder ServerInfoTemplate(List<String> serverInfo){
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
        return embed;
    }

    public void ServerInsertInfo(Data data, String text, long channel_id){
        embed.setColor(new Color(0, 0, 100));
        embed.setDescription(text);
        data.getGuild().getTextChannelById(channel_id).sendMessage(embed.build()).queue();
    }

    public EmbedBuilder EmptyEmbed(){
        embed.setColor(new Color(255,0,0));
        embed.setTitle("Server is unavailable");
        embed.setDescription("Status: doesn't exist");
        embed.setTimestamp(Instant.now());
        return embed;
    }
}
