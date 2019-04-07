
import config.BotConfig;
import listener.Public;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import runnable.Threads;

public class Launch {


    public static void main(String[] args) {

        try {

            JDA api = new JDABuilder(AccountType.BOT)
                    .setToken(BotConfig.TOKEN)
                    .setStatus(OnlineStatus.ONLINE)
                    .setGame(Game.watching("Type ?help"))
                    .setAutoReconnect(true)
                    .build();
            api.addEventListener(new Public());
            new Threads(api).start();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
