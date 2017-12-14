package main.me.aaron.achievements;

public class AAchievement {
	
	String name;
	boolean done;
	String playername;
	
	public AAchievement(final String name, final String playername, final boolean done) {
		this.name = name;
		this.playername = playername;
		this.done = done;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(final boolean done) {
		this.done = done;
	}
	
	public String getAchievementNameRaw() {
		return name;
	}

}
