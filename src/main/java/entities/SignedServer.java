package entities;

import lombok.Data;

@Data
public class SignedServer {

    private long guild_id;

    private long server_id;

    private long channel_id;

    private long message_id;
}
