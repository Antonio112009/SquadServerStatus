package sendMessage;

import entities.Data;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class EmbedMessage {
    private EmbedBuilder embed = new EmbedBuilder();

    public EmbedBuilder ServerInfoTemplate(List<String> serverInfo){
        embed.setColor(new Color(124,252,0));
        if(serverInfo.get(4).startsWith("Fool's Road AAS v4"))
            embed.setThumbnail("http://m.ahod.si/maps/thumbnails/Fools_Road_AAS_v4.jpg");
        else
            embed.setThumbnail("http://m.ahod.si/maps/thumbnails/" + serverInfo.get(4).replace(' ', '_') + ".jpg");
        embed.setAuthor(serverInfo.get(0));
        embed.setDescription("Server status: **" + serverInfo.get(3) + "**\n" +
                "Server id: **" + serverInfo.get(8) + "**");
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

    public EmbedBuilder ServerInsertInfo(String text){
        embed.setColor(new Color(0, 0, 100));
        embed.setDescription(text);
        return embed;
    }

    public EmbedBuilder EmptyEmbed(){
        embed.setColor(new Color(255,0,0));
        embed.setTitle("Server is unavailable");
        embed.setDescription("Status: doesn't exist");
        embed.setTimestamp(Instant.now());
        return embed;
    }


    public EmbedBuilder aboutBot(Data data){
        JDA api = data.getGuild().getJDA();
        embed.setTitle(api.getSelfUser().getName());
        embed.setDescription("" +
                "Hi! I am " + api.getSelfUser().getName() + ", a bot build by [Tony Anglichanin](https://github.com/Antonio112009)!\n" +
                "I'm written in Java, using [JDA library](https://github.com/DV8FromTheWorld/JDA) (3.8.3_462)\n" +
                "For additional help - contact **" + api.getUserById(389016583076446218L).getAsTag() + "**\n");
        embed.addField("General Statistics",
                "Servers: **" + api.getGuilds().size() + "**\n"

                , true);

        return embed;
    }
}
