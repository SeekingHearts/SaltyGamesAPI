package me.aaron.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.greatmancode.com.zaxxer.libs.hikari.HikariDataSource;

import me.aaron.main.SaltyGames;
import me.aaron.main.SaltyGamesSettings;

public class MysqlDB extends DataBase {

	private static final String INSERT = "INSERT INTO SGPlayers VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE UUID=?";
	private static final String SELECT = "SELECT token FROM SGPlayers WHERE uuid=?";
	private static final String SAVE = "UPDATE SGPlayers SET token=? WHERE uuid=?";
	
	private String host, database, username, password;
	private int port;
	
	private HikariDataSource hikari;
	
	public MysqlDB(SaltyGames pl) {
		super(pl);
		
		FileConfiguration config = pl.getConfig();
		
		host = config.getString("mysql.host");
		port = config.getInt("mysql.port");
		database = config.getString("mysql.database");
		username = config.getString("mysql.username");
		password = config.getString("mysql.password");
	}
	
	@Override
	public boolean load(boolean async) {
		hikari = new HikariDataSource();
		hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		hikari.addDataSourceProperty("serverName", host);
		hikari.addDataSourceProperty("port", port);
		hikari.addDataSourceProperty("databaseName", database);
		hikari.addDataSourceProperty("user", username);
		hikari.addDataSourceProperty("password", password);
		
		try {
			Connection con = hikari.getConnection();
			Statement state = con.createStatement();
			state.executeUpdate("CREATE TABLE IF NOT EXISTS SGPlayers(UUID VARCHAR(36), name VARCHAR(16), TOKEN int, playSounds BOOL)");
		} catch (SQLException e) {
			e.printStackTrace();
			SaltyGamesSettings.useMySQL = false;
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean getBoolean(UUID uuid, String path) {
		return false;
	}
	
	@Override
	public boolean getBoolean(UUID uuid, String path, boolean defaultValue) {
		return false;
	}

	@Override
	public void set(String uuid, String path, Object b) {}

	@Override
	public void save(boolean async) {}

	@Override
	public int getInt(UUID uuid, String path, int defaultValue) {
		return 0;
	}

	@Override
	public void addStatistics(UUID uuid, String gameID, String gameTypeID, double value, SaveType saveType) {}

	@Override
	public ArrayList<Stat> getTopList(String gameID, String gameTypeID, SaveType saveType, int maxNumber) {
		return null;
	}

	@Override
	public boolean isSet(String path) {
		return false;
	}

	@Override
	public void loadPlayer(SGPlayer player, boolean async) {}

	@Override
	public void savePlayer(SGPlayer player, boolean async) {}
	
}
