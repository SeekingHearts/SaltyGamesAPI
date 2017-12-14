package main.java.me.aaron.saltygamesapi;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class ArenaLogger {

private Logger pluginLogger;
    
    
    private String arenaName;
    
    
    public ArenaLogger(Logger logger, String arenaName)
    {
        this.pluginLogger = logger;
        this.arenaName = arenaName;
    }
    
    
    private String getArenaPrefix()
    {
        return "[arena:" + this.arenaName + "] "; //$NON-NLS-1$//$NON-NLS-2$
    }
    
    
    public void severe(String msg)
    {
        log(Level.SEVERE, msg);
    }
    
    
    public void warning(String msg)
    {
        log(Level.WARNING, msg);
    }
    
    
    public void info(String msg)
    {
        log(Level.INFO, msg);
    }
    
    
    public void config(String msg)
    {
        log(Level.CONFIG, msg);
    }
    
    
    public void fine(String msg)
    {
        log(Level.FINE, msg);
    }
    
    
    public void finer(String msg)
    {
        log(Level.FINER, msg);
    }
    
    
    public void finest(String msg)
    {
        log(Level.FINEST, msg);
    }
    
    
    public void log(Level level, String msg)
    {
        this.pluginLogger.log(level, getArenaPrefix() + msg);
    }
    
    
    public void log(Level level, Supplier<String> msgSupplier)
    {
        this.pluginLogger.log(level, () -> (getArenaPrefix() + msgSupplier.get()));
    }
    
    
    public void log(Level level, String msg, Object param1)
    {
        this.pluginLogger.log(level, getArenaPrefix() + msg, param1);
    }
    
    
    public void log(Level level, String msg, Object params[])
    {
        this.pluginLogger.log(level, getArenaPrefix() + msg, params);
    }
    
    
    public void log(Level level, String msg, Throwable thrown)
    {
        this.pluginLogger.log(level, getArenaPrefix() + msg, thrown);
    }
    
    
    public void log(Level level, Throwable thrown, Supplier<String> msgSupplier)
    {
        this.pluginLogger.log(level, thrown, () -> (getArenaPrefix() + msgSupplier.get()));
    }
    
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg)
    {
        this.pluginLogger.logp(level, sourceClass, sourceMethod, getArenaPrefix() + msg);
    }
    
    
    public void logp(Level level, String sourceClass, String sourceMethod, Supplier<String> msgSupplier)
    {
        this.pluginLogger.logp(level, sourceClass, sourceMethod, () -> (getArenaPrefix() + msgSupplier.get()));
    }
    
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1)
    {
        this.pluginLogger.logp(level, sourceClass, sourceMethod, getArenaPrefix() + msg, param1);
    }
    
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object params[])
    {
        this.pluginLogger.logp(level, sourceClass, sourceMethod, getArenaPrefix() + msg, params);
    }
    
    
    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown)
    {
        this.pluginLogger.logp(level, sourceClass, sourceMethod, getArenaPrefix() + msg, thrown);
    }
    
    
    public void logp(Level level, String sourceClass, String sourceMethod, Throwable thrown, Supplier<String> msgSupplier)
    {
        this.pluginLogger.logp(level, sourceClass, sourceMethod, thrown, () -> (getArenaPrefix() + msgSupplier.get()));
    }
    
    
    public void entering(String sourceClass, String sourceMethod)
    {
        this.pluginLogger.logp(Level.FINER, sourceClass, sourceMethod, getArenaPrefix() + "ENTRY"); //$NON-NLS-1$
    }
    
    
    public void entering(String sourceClass, String sourceMethod, Object param1)
    {
        this.pluginLogger.logp(Level.FINER, sourceClass, sourceMethod, getArenaPrefix() + "ENTRY {0}", param1); //$NON-NLS-1$
    }
    
    
    public void entering(String sourceClass, String sourceMethod, Object params[])
    {
        String msg = getArenaPrefix() + "ENTRY"; //$NON-NLS-1$
        if (params == null)
        {
            logp(Level.FINER, sourceClass, sourceMethod, msg);
            return;
        }
        if (!isLoggable(Level.FINER))
            return;
        for (int i = 0; i < params.length; i++)
        {
            msg = msg + " {" + i + "}"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.pluginLogger.logp(Level.FINER, sourceClass, sourceMethod, msg, params);
    }
    
    
    public void exiting(String sourceClass, String sourceMethod)
    {
        this.pluginLogger.logp(Level.FINER, sourceClass, sourceMethod, getArenaPrefix() + "RETURN"); //$NON-NLS-1$
    }
    
    
    public void exiting(String sourceClass, String sourceMethod, Object result)
    {
        this.pluginLogger.logp(Level.FINER, sourceClass, sourceMethod, getArenaPrefix() + "RETURN {0}", result); //$NON-NLS-1$
    }
    
    
    public void throwing(String sourceClass, String sourceMethod, Throwable thrown)
    {
        if (!isLoggable(Level.FINER))
        {
            return;
        }
        LogRecord lr = new LogRecord(Level.FINER, getArenaPrefix() + "THROW"); //$NON-NLS-1$
        lr.setSourceClassName(sourceClass);
        lr.setSourceMethodName(sourceMethod);
        lr.setThrown(thrown);
        this.pluginLogger.log(lr);
    }
    
    
    public void severe(Supplier<String> msgSupplier)
    {
        log(Level.SEVERE, msgSupplier);
    }
    
    
    public void warning(Supplier<String> msgSupplier)
    {
        log(Level.WARNING, msgSupplier);
    }
    
    
    public void info(Supplier<String> msgSupplier)
    {
        log(Level.INFO, msgSupplier);
    }
    
    
    public void config(Supplier<String> msgSupplier)
    {
        log(Level.CONFIG, msgSupplier);
    }
    
    
    public void fine(Supplier<String> msgSupplier)
    {
        log(Level.FINE, msgSupplier);
    }
    
    
    public void finer(Supplier<String> msgSupplier)
    {
        log(Level.FINER, msgSupplier);
    }
    
    
    public void finest(Supplier<String> msgSupplier)
    {
        log(Level.FINEST, msgSupplier);
    }
    
    
    public Level getLevel()
    {
        return this.pluginLogger.getLevel();
    }
    
    
    public boolean isLoggable(Level level)
    {
        return this.pluginLogger.isLoggable(level);
    }
    
    
    public static void debug(final String msg)
    {
        if (MinigamesAPI.debug)
        {
            Bukkit.getConsoleSender().sendMessage("[" + System.currentTimeMillis() + " MGLIB-DBG] " + msg); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
	
}
