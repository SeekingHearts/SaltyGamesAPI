package me.aaron.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.aaron.ArenaConfigStrings;
import me.aaron.MinigamesAPI;

public class MainSQL {

	JavaPlugin pl = null;
	private boolean mysql = true;
	MySQL MySQL;

	boolean oldFormat = false;

	public MainSQL(final JavaPlugin pl, final boolean mysql) {
		this.pl = pl;
		this.mysql = mysql;

		if (mysql) {
			this.MySQL = new MySQL(pl.getConfig().getString(ArenaConfigStrings.CONFIG_MYSQL_HOST), "3306",
					pl.getConfig().getString(ArenaConfigStrings.CONFIG_MYSQL_DATABASE),
					pl.getConfig().getString(ArenaConfigStrings.CONFIG_MYSQL_USER),
					pl.getConfig().getString(ArenaConfigStrings.CONFIG_MYSQL_PW));
		}

		if (pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED) && this.MySQL != null) {
			try {
				createTables();
			} catch (final Exception e) {
				pl.getLogger().log(Level.SEVERE, "Fehler beim initialisieren von MySQL. Deaktiviere...", e);
				pl.getConfig().set(ArenaConfigStrings.CONFIG_MYSQL_ENABLED, false);
				pl.saveConfig();
			}
		} else if (pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED) && this.MySQL == null) {
			pl.getLogger().log(Level.SEVERE, "Fehler beim initialisieren von MySQL. Deaktiviere...");
			pl.getConfig().set(ArenaConfigStrings.CONFIG_MYSQL_ENABLED, false);
			pl.saveConfig();
		}
	}

	public void createTables() {
		if (!pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
			return;
		if (!this.mysql)
			return;

		final Connection con = this.MySQL.open();

		try {
			con.createStatement().execute("CREATE DATABASE IF NOT EXISTS `"
					+ pl.getConfig().getString(ArenaConfigStrings.CONFIG_MYSQL_DATABASE) + "`");
			con.createStatement().execute("CREATE TABLE IF NOT EXISTS " + pl.getName()
					+ " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, player VARCHAR(100), points INT, wins INT, loses INT, kills INT)");
			
			final ResultSet rs = con.createStatement().executeQuery("SHOP COLUMNS FROm `" + pl.getName() + "` LIKE 'kills'");
			if (!rs.isBeforeFirst()) {
				con.createStatement().execute("ALTER TABLE " + pl.getName() + " ADD kills INT");
			}
			
			final ResultSet rs_ = con.createStatement().executeQuery("SHOP COLUMNS FROm `" + pl.getName() + "` LIKE 'deaths'"); 
			if (!rs_.isBeforeFirst()) {
				con.createStatement().execute("ALTER TABLE " + pl.getName() + " ADD deaths INT");
			}
			
			final ResultSet rs__ = con.createStatement().executeQuery("SHOP COLUMNS FROm `" + pl.getName() + "` LIKE 'uuid'"); 
			if (!rs__.isBeforeFirst()) {
				con.createStatement().execute("ALTER TABLE " + pl.getName() + " ADD uuid VARCHAR(100)");
				this.oldFormat = true;
			}
		} catch (final SQLException e) {
			MinigamesAPI.getAPI().getLogger().log(Level.SEVERE, "Fehler", e);
		}
	}
	
	public void updateWinnerStats(final Player p, final int reward, final boolean addwin)
    {
        if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
        {
            return;
        }
        if (!this.mysql)
        {
            // TODO SQLite
        }
        final String uuid = p.getUniqueId().toString();
        final Connection c = this.MySQL.open();
        
        final int wincount = addwin ? 1 : 0;
        
        try
        {
            if (this.oldFormat)
            {
                c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET uuid='" + uuid + "' WHERE player='" + p.getName() + "'");
            }
            final ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + this.pl.getName() + " WHERE uuid='" + uuid + "'");
            if (!res3.isBeforeFirst())
            {
                // there's no such user
                c.createStatement().executeUpdate("INSERT INTO " + this.pl.getName() + " VALUES('0', '" + p.getName() + "', '" + Integer.toString(reward) + "', '" + Integer.toString(wincount)
                        + "', '0', '0', '0', '" + uuid + "')");
                return;
            }
            res3.next();
            final int points = res3.getInt("points") + reward;
            final int wins = res3.getInt("wins") + wincount;
            
            c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET points='" + Integer.toString(points) + "', wins='" + Integer.toString(wins) + "' WHERE uuid='" + uuid + "'");
            
        }
        catch (final SQLException e)
        {
        	MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
        }
    }
    
    public void updateLoserStats(final Player p)
    {
        if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
        {
            return;
        }
        if (!this.mysql)
        {
            // TODO SQLite
        }
        final String uuid = p.getUniqueId().toString();
        final Connection c = this.MySQL.open();
        
        try
        {
            if (this.oldFormat)
            {
                c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET uuid='" + uuid + "' WHERE player='" + p.getName() + "'");
            }
            final ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + this.pl.getName() + " WHERE uuid='" + uuid + "'");
            if (!res3.isBeforeFirst())
            {
                // there's no such user
                c.createStatement().executeUpdate("INSERT INTO " + this.pl.getName() + " VALUES('0', '" + p.getName() + "', '0', '0', '1', '0', '0', '" + uuid + "')");
                return;
            }
            res3.next();
            final int loses = res3.getInt("loses") + 1;
            
            c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET loses='" + Integer.toString(loses) + "' WHERE uuid='" + uuid + "'");
            
        }
        catch (final SQLException e)
        {
        	MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
        }
    }
    
    public void updateKillerStats(final Player p, final int kills_)
    {
        if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
        {
            return;
        }
        if (!this.mysql)
        {
            // TODO SQLite
        }
        final String uuid = p.getUniqueId().toString();
        final Connection c = this.MySQL.open();
        
        try
        {
            if (this.oldFormat)
            {
                c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET uuid='" + uuid + "' WHERE player='" + p.getName() + "'");
            }
            final ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + this.pl.getName() + " WHERE uuid='" + uuid + "'");
            if (!res3.isBeforeFirst())
            {
                // there's no such user
                c.createStatement().executeUpdate("INSERT INTO " + this.pl.getName() + " VALUES('0', '" + p.getName() + "', '0', '0', '0', '1', '0', '" + uuid + "')");
                return;
            }
            res3.next();
            final int kills = res3.getInt("kills") + kills_;
            
            c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET kills='" + Integer.toString(kills) + "' WHERE uuid='" + uuid + "'");
            
        }
        catch (final SQLException e)
        {
        	MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
        }
    }
    
    public void updateDeathStats(final Player p, final int deaths_)
    {
        if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
        {
            return;
        }
        if (!this.mysql)
        {
            // TODO SQLite
        }
        final String uuid = p.getUniqueId().toString();
        final Connection c = this.MySQL.open();
        
        try
        {
            if (this.oldFormat)
            {
                c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET uuid='" + uuid + "' WHERE player='" + p.getName() + "'");
            }
            final ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + this.pl.getName() + " WHERE uuid='" + uuid + "'");
            if (!res3.isBeforeFirst())
            {
                // there's no such user
                c.createStatement().executeUpdate("INSERT INTO " + this.pl.getName() + " VALUES('0', '" + p.getName() + "', '0', '0', '0', '0', '1', '" + uuid + "')");
                return;
            }
            res3.next();
            final int deaths = res3.getInt("deaths") + deaths_;
            
            c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET deaths='" + Integer.toString(deaths) + "' WHERE uuid='" + uuid + "'");
            
        }
        catch (final SQLException e)
        {
        	MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
        }
    }
    
    public int getPoints(final Player p)
    {
        if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
        {
            return -1;
        }
        if (!this.mysql)
        {
            // TODO SQLite
        }
        final String uuid = p.getUniqueId().toString();
        final Connection c = this.MySQL.open();
        
        try
        {
            if (this.oldFormat)
            {
                c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET uuid='" + uuid + "' WHERE player='" + p.getName() + "'");
            }
            final ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + this.pl.getName() + " WHERE uuid='" + uuid + "'");
            
            if (res3.isBeforeFirst())
            {
                res3.next();
                final int credits = res3.getInt("points");
                return credits;
            }
//            else
//            {
//                // log("New User detected.");
//            }
        }
        catch (final SQLException e)
        {
            //
        }
        return -1;
    }
    
    public int getWins(final Player p)
    {
        if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_MYSQL_ENABLED))
        {
            return -1;
        }
        if (!this.mysql)
        {
            // TODO SQLite
        }
        final String uuid = p.getUniqueId().toString();
        final Connection c = this.MySQL.open();
        
        try
        {
            if (this.oldFormat)
            {
                c.createStatement().executeUpdate("UPDATE " + this.pl.getName() + " SET uuid='" + uuid + "' WHERE player='" + p.getName() + "'");
            }
            final ResultSet res3 = c.createStatement().executeQuery("SELECT * FROM " + this.pl.getName() + " WHERE uuid='" + uuid + "'");
            
            if (res3.isBeforeFirst())
            {
                res3.next();
                final int wins = res3.getInt("wins");
                return wins;
            }
//            else
//            {
//                // log("New User detected.");
//            }
        }
        catch (final SQLException e)
        {
            //
        }
        return -1;
    }
}
