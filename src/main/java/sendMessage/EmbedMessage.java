package sendMessage;

import config.BotConfig;
import entities.DataPublic;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EmbedMessage {
    private EmbedBuilder embed = new EmbedBuilder();
    private Color defaultColor = new Color(249, 29, 84);

    public EmbedBuilder ServerInfoTemplate(List<String> serverInfo){
        if(serverInfo.get(2).equals("0")){
            embed.setColor(new Color(255,255,255));
        } else{
//            System.out.println("number = " + serverInfo.get(2) + " number = " + serverInfo.get(1));
            double percent = (Double.parseDouble(serverInfo.get(2)) / Double.parseDouble(serverInfo.get(1))) * 100;
//            System.out.println("percent = " + percent);
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

    public void ServerInsertInfo(DataPublic dataPublic, String text, long channel_id, long seconds){
        embed.setColor(defaultColor);
        embed.setDescription(text);
        dataPublic.getGuild().getTextChannelById(channel_id).sendMessage(embed.build()).queue(
                (m) -> m.delete().queueAfter(seconds, TimeUnit.SECONDS)
        );
    }

    public MessageEmbed ServerInsertInfo(String text){
        return ServerInsertInfo(text, defaultColor).build();
    }

    public MessageEmbed ServerInsertInfo(String title, String text, Color color){
        embed.setColor(color);
        embed.setTitle(title);
        embed.setDescription(text);
        return embed.build();
    }


    public EmbedBuilder ServerInsertInfo(String text, Color color){
        embed.setColor(color);
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


    public EmbedBuilder AboutBot(DataPublic dataPublic){
        JDA api = dataPublic.getGuild().getJDA();
        embed.setTitle(api.getSelfUser().getName());
        embed.setColor(defaultColor);
        embed.setDescription("" +
                "Hi! I am " + api.getSelfUser().getName() + ", a bot build by [Tony Anglichanin](https://github.com/Antonio112009)!\n" +
                "I'm written in Java, using [JDA library](https://github.com/DV8FromTheWorld/JDA) (3.8.3_462) \n" +
                "\n" +
                "To add me to your server - [press this link](https://discordapp.com/oauth2/authorize?client_id=562952086438936586&scope=bot&permissions=8)\n" +
                "\n" +
                "For additional help - contact **" + api.getUserById(BotConfig.SPECIAL_ID).getAsTag() + "**\n");
        embed.addField("General Statistics",
                "Servers: **" + api.getGuilds().size() + "**\n"

                , true);

        return embed;
    }



    //TODO: Greeting message
    public MessageEmbed GreetingMessage(GuildJoinEvent event){
        embed.setColor(defaultColor);
        embed.setAuthor(event.getJDA().getSelfUser().getName(), null,event.getJDA().getSelfUser().getAvatarUrl());
        embed.setTitle("Thank you for adding me!");
        embed.setDescription("To get started you can view the bot guide via `?guide`");
        embed.addField("Bot guide", "`?guide`", true);
        embed.addField("List of commands", "`?helpSS`", true);
        embed.addField("See info about me and credits:", "`?aboutSS`\n`?credit`",true);
        return embed.build();
    }

    public MessageEmbed GreetingMessage(MessageReceivedEvent event) {
        embed.setColor(defaultColor);
        embed.setAuthor("Thank you for adding me!", null, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
        embed.setDescription("To get started you can view the bot guide via `?guide`");
        embed.addField("Bot guide", "`?guide`", true);
        embed.addField("List of commands", "`?helpSS`", true);
        embed.addField("See info about me and credits:", "`?aboutSS` and `?creditss`",true);
        return embed.build();
    }
}
