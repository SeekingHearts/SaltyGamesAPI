package me.aaron.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import me.aaron.MinigamesAPI;

public class MySQL extends Database {
	
	String user = "";
	String database = "";
	String password = "";
	String port = "";
	String hostname = "";
	Connection con = null;
	
	public MySQL(final String username, final String database, final String password, final String port, final String hostname) {
		this.user = username;
		this.database = database;
		this.password = password;
		this.port = port;
		this.hostname = hostname;
	}
	
	public Connection open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + "port" + "/" + database, user, password);
			return con;
		} catch (final SQLException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.SEVERE, "Es konnte keine Verbindung zum MySQL Server aufgebaut werden!", e);
		} catch (final ClassNotFoundException e) {
			MinigamesAPI.getAPI().getLogger().severe("JDBC Treiber nicht gefunden!");
		}
		return con;
	}
	
	public boolean checkConnection() {
		if (con != null)
			return true;
		return false;
	}

	public Connection getCon() {
		return con;
	}
	
	public void closeConnection(Connection con) {
		try {
			con.close();
		} catch (final SQLException e) {
			if (MinigamesAPI.debug) {
				MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Fehler", e);
			}
		}
	}
	
}
