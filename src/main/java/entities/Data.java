package entities;


import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

@lombok.Data
public class Data {

    private Guild guild;
    private GuildController controller;
    private String content;
    private MessageChannel channel;
    private Member member;
    private Member mentionedMember;
    private TextChannel mentionedChannel;
    private String authorId;
    private String[] command;
    private String[] comment;
    private TextChannel lanceAudit;
    private TextChannel lanceNews;
    private TextChannel lanceOfficer;
    private Role lanceRole;
    private boolean mentioned = true;
    private boolean chatMentioned = true;

    public Data(MessageReceivedEvent event) {

        this.guild = event.getGuild();
        this.controller = guild.getController();
        this.content = event.getMessage().getContentRaw().toLowerCase().replaceAll("\\s{2,}", " ").trim();
        this.channel = event.getChannel();
        this.member = event.getMember();
        this.command = content.split(" ");
        this.comment = content.split("\\+\\+");
        this.authorId = event.getAuthor().getId();

        this.lanceAudit = event.getGuild().getTextChannelsByName("lance_audit", true).get(0);
        this.lanceNews = event.getGuild().getTextChannelsByName("lance_news", true).get(0);
        this.lanceOfficer = event.getGuild().getTextChannelsByName("lance_officer", true).get(0);
        this.lanceRole = event.getGuild().getRolesByName("Lance", true).get(0);
        try {
            this.mentionedMember = event.getMessage().getMentionedMembers().get(0);
        } catch (Exception e){
            mentioned = false;
        }

        try {
            this.mentionedChannel = event.getMessage().getMentionedChannels().get(0);
        } catch (Exception e){
            chatMentioned = false;
        }
    }
}
