package me.aaron;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MatchTimer {
	
	private int seconds;
	
	private long matchDuration;
	
	private long matchTime;
	
	private boolean paused;
	
	private LocalDateTime lastStart = LocalDateTime.now();
	
	private BukkitTask timerTask;
	
	private Runnable arenaAbort;
	
	private Consumer<Integer> arenaNotify;
	
	private boolean warn60 = true;
	private boolean warn30 = true;
	private boolean warn20 = true;
	private boolean warn10 = true;
	private boolean warn5 = true;
	private boolean warn4 = true;
	private boolean warn3 = true;
	private boolean warn2 = true;
	private boolean warn1 = true;
	
	private static final Logger LOGGER = Logger.getLogger(MatchTimer.class.getName());
	
	public MatchTimer(int maxSecs, Runnable arenaAbort, Consumer<Integer> arenaNotify) {
		setMaxSeconds(maxSecs);
		this.arenaAbort = arenaAbort;
		this.arenaNotify = arenaNotify;
	}
	
	public int getMaxSeconds() {
		return seconds;
	}
	
	public void setMaxSeconds(int secs) {
		this.seconds = secs;
		this.matchTime = this.seconds * 1000L;
	}

	public void pause()
    {
        if (!this.paused)
        {
            this.paused = true;
            this.matchDuration += this.lastStart.until(LocalDateTime.now(), ChronoUnit.MILLIS);
        }
    }

    public void resume()
    {
        if (this.paused)
        {
            this.paused = false;
            this.lastStart = LocalDateTime.now();
        }
    }

    public void resetAndResume()
    {
        this.paused = false;
        this.lastStart = LocalDateTime.now();
        this.matchDuration = 0;
        this.fixWarnings();
    }

    public void resetAndPause()
    {
        this.paused = true;
        this.matchDuration = 0;
        this.fixWarnings();
    }

    public long getDurationMillis()
    {
        if (this.paused)
        {
            return this.matchDuration;
        }
        return this.matchDuration + this.lastStart.until(LocalDateTime.now(), ChronoUnit.MILLIS);
    }

    public long getMaxMillis()
    {
        return this.matchTime;
    }

    public void addMaxMillis(Plugin plugin, long millis)
    {
        this.matchTime += millis;
        if (this.matchTime > 0 && this.timerTask == null)
        {
            this.startTimer(plugin);
        }
        this.fixWarnings();
    }

    public void substractMaxMillis(long millis)
    {
        this.matchTime -= millis;
        if (this.matchTime <= 0)
        {
            this.stopTimer();
        }
        this.fixWarnings();
    }

    public void setMaxMillis(long millis)
    {
        this.matchTime = millis;
        if (this.matchTime <= 0)
        {
            this.stopTimer();
        }
        this.fixWarnings();
    }

    public void addDurationMillis(long millis)
    {
        this.matchDuration += millis;
        this.fixWarnings();
    }

    public void substractDurationMillis(long millis)
    {
        this.matchDuration -= millis;
        this.fixWarnings();
    }

    public void setDurationMillis(long millis)
    {
        this.matchDuration = millis;
        this.fixWarnings();
    }
    
    public void onArenaStart(Plugin plugin)
    {
        this.matchTime = this.seconds * 1000L;
        this.matchDuration = 0;
        this.lastStart = LocalDateTime.now();
        this.paused = false;
        if (this.matchTime > 0)
        {
            this.startTimer(plugin);
        }
        this.fixWarnings();
    }

        
    public void onArenaStop()
    {
        this.stopTimer();
    }

    
    private void startTimer(Plugin plugin)
    {
        if (this.timerTask == null)
        {
            this.timerTask = new BukkitRunnable() {
                
                @Override
                public void run()
                {
                    MatchTimer.this.onTimer(MatchTimer.this.timerTask);
                }
            }.runTaskTimer(plugin, 20L, 20L);
        }
    }

    
    private void stopTimer()
    {
        if (this.timerTask != null)
        {
            this.timerTask.cancel();
            this.timerTask = null;
        }
    }
    
    
    private void fixWarnings()
    {
        long delta = this.getMaxMillis() - this.getDurationMillis();
        final int sec = (int) (delta / 1000);
        this.warn60 = sec >= 60;
        this.warn30 = sec >= 30;
        this.warn20 = sec >= 20;
        this.warn10 = sec >= 10;
        this.warn5 = sec >= 5;
        this.warn4 = sec >= 4;
        this.warn3 = sec >= 3;
        this.warn2 = sec >= 2;
        this.warn1 = sec >= 1;
    }
    
    
    private void onTimer(BukkitTask task)
    {
        if (!this.paused)
        {
            long delta = this.getMaxMillis() - this.getDurationMillis();
            if (delta <= 0)
            {
                this.timerTask.cancel();
                this.timerTask = null;
                this.arenaAbort.run();
            }
            else
            {
                final int sec = (int) (delta / 1000);
                if (this.warn60 && sec <= 60)
                {
                    this.arenaNotify.accept(60);
                    this.warn60 = false;
                }
                else if (this.warn30 && sec <= 30)
                {
                    this.arenaNotify.accept(30);
                    this.warn30 = false;
                }
                else if (this.warn20 && sec <= 20)
                {
                    this.arenaNotify.accept(20);
                    this.warn20 = false;
                }
                else if (this.warn10 && sec <= 10)
                {
                    this.arenaNotify.accept(10);
                    this.warn10 = false;
                }
                else if (this.warn5 && sec <= 5)
                {
                    this.arenaNotify.accept(5);
                    this.warn5 = false;
                }
                else if (this.warn4 && sec <= 4)
                {
                    this.arenaNotify.accept(4);
                    this.warn4 = false;
                }
                else if (this.warn3 && sec <= 3)
                {
                    this.arenaNotify.accept(3);
                    this.warn3 = false;
                }
                else if (this.warn2 && sec <= 2)
                {
                    this.arenaNotify.accept(2);
                    this.warn2 = false;
                }
                else if (this.warn1 && sec <= 1)
                {
                    this.arenaNotify.accept(1);
                    this.warn1 = false;
                }
            }
        }
    }
}
