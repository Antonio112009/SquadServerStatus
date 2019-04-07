package database;

import entities.DiscordServers;
import entities.SignedServer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private Connection connection;

    private Statement statement;
    private PreparedStatement preparedStatement;

    public int insertNewServer(long guild_id, long channel_id, boolean active, String language){
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `discord_servers` (`guild_id`, `channel_id`, `active`, `lang`) VALUES (?,?,?,?)");

            preparedStatement.setLong(1, guild_id);
            preparedStatement.setLong(2, channel_id);
            preparedStatement.setBoolean(3, active);
            preparedStatement.setString(4, language);


            return preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            closeDatabase();
        }
    }

    public int insertNewSignedServer(long guild_id, long server_id){
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO `signed_servers` (`guild_id`, `server_id`) VALUES (?,?)");

            preparedStatement.setLong(1, guild_id);
            preparedStatement.setLong(2, server_id);


            return preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            closeDatabase();
        }
    }

    public int updateMessageServer(long discord_id, long server_id, long message_id){
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE signed_servers SET message_id = ? WHERE server_id = ? and guild_id = ?");

            preparedStatement.setLong(1, message_id);
            preparedStatement.setLong(2, server_id);
            preparedStatement.setLong(3, discord_id);


            return preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            closeDatabase();
        }
    }

    public List<DiscordServers> getDiscordServers(){
        return getDiscordServers("");
    }

    public List<DiscordServers> getDiscordServers(String additional){
        List<DiscordServers> list = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM discord_servers " + additional;

            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DiscordServers discordServers = new DiscordServers();
                discordServers.setGuild_id(resultSet.getLong("guild_id"));
                discordServers.setChannel_id(resultSet.getLong("channel_id"));
                discordServers.setActive(resultSet.getBoolean("active"));
                discordServers.setLanguage(resultSet.getString("lang"));
                list.add(discordServers);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
           closeDatabase();
        }
        return list;
    }


    public List<SignedServer> getSignedServers(){
        return getSignedServers("");
    }

    public List<SignedServer> getSignedServers(String additional){
        List<SignedServer> list = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM signed_servers " + additional;

            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SignedServer signedServer = new SignedServer();
                signedServer.setGuild_id(resultSet.getLong("guild_id"));
                signedServer.setServer_id(resultSet.getLong("server_id"));
                list.add(signedServer);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return list;
    }

    public long getChannelId(long discord_id) {
        long answer = 0L;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT channel_id FROM discord_servers WHERE guild_id = " + discord_id;

            preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                answer = resultSet.getLong("channel_id");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabase();
        }
        return answer;
    }

    public int editDiscordServer(long guild_id, long channel_id){
        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE discord_servers SET " +
                    "channel_id = ? WHERE guild_id = ?");

            preparedStatement.setLong(1, channel_id);
            preparedStatement.setLong(2, guild_id);


            return preparedStatement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            return 0;
        } finally {
            closeDatabase();
        }
    }

    public boolean checkDiscordServer(long discord_id){
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM discord_servers WHERE guild_id = (?)";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, discord_id);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            closeDatabase();
        }
    }

    public boolean checkSignedServer(long discord_id, long server_id){
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM signed_servers WHERE guild_id = ? AND server_id = ?";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, discord_id);
            preparedStatement.setLong(2, server_id);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            closeDatabase();
        }
    }

    private void closeDatabase() {
        if (connection != null) try { connection.close(); } catch (SQLException e) {e.printStackTrace();}
        if (preparedStatement != null) try { preparedStatement.close(); } catch (SQLException e) {e.printStackTrace();}
    }
}
