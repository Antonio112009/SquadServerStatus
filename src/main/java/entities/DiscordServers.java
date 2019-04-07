package entities;

@lombok.Data
public class DiscordServers {

    private long guild_id;

    private long channel_id;

    private boolean active;

    private String language;
}
