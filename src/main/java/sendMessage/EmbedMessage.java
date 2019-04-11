package sendMessage;

import config.BotConfig;
import entities.Data;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class EmbedMessage {
    private EmbedBuilder embed = new EmbedBuilder();

    public EmbedBuilder ServerInfoTemplate(List<String> serverInfo){
        if(serverInfo.get(2).equals("0")){
            embed.setColor(new Color(255,255,255));
        } else{
            System.out.println("number = " + serverInfo.get(2) + " number = " + serverInfo.get(1));
            double percent = (Double.parseDouble(serverInfo.get(2)) / Double.parseDouble(serverInfo.get(1))) * 100;
            System.out.println("percent = " + percent);
            if(0 <= percent && percent < 12){
                embed.setColor(new Color(204, 204, 204));
            } else if(12 <= percent && percent < 50){
                embed.setColor(new Color(170, 255, 253));
            } else if(50 <= percent && percent <= 70){
                embed.setColor(new Color(63, 255, 0));
            } else if(70 <= percent && percent <= 80){
                embed.setColor(new Color(255, 170, 0));
            } else {
                embed.setColor(new Color(255, 0, 0));
            }
        }
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

    public EmbedBuilder onJoinDiscordServer(GuildJoinEvent event){
        embed.setColor(new Color(63, 255, 0));
        embed.setThumbnail(event.getGuild().getIconUrl());
        embed.setTitle("Bot added to new discord server:");
        embed.addField("Name of the server:",
                event.getGuild().getName(),false);
        embed.addField("Number of users:",
                String.valueOf(event.getGuild().getMembers().size()), false);
        return embed;
    }


    public EmbedBuilder AboutBot(Data data){
        JDA api = data.getGuild().getJDA();
        embed.setTitle(api.getSelfUser().getName());
        embed.setDescription("" +
                "Hi! I am " + api.getSelfUser().getName() + ", a bot build by [Tony Anglichanin](https://github.com/Antonio112009)!\n" +
                "I'm written in Java, using [JDA library](https://github.com/DV8FromTheWorld/JDA) (3.8.3_462)\n" +
                "For additional help - contact **" + api.getUserById(BotConfig.SPECIAL_ID).getAsTag() + "**\n");
        embed.addField("General Statistics",
                "Servers: **" + api.getGuilds().size() + "**\n"

                , true);

        return embed;
    }
}
