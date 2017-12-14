package main.java.me.aaron.saltygamesapi.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class Cuboid {
	
	private final Location highPoints;
    private final Location lowPoints;
    
    public Cuboid(final Location startLoc, final Location endLoc)
    {
        
        if (startLoc != null && endLoc != null)
        {
            final int lowx = Math.min(startLoc.getBlockX(), endLoc.getBlockX());
            final int lowy = Math.min(startLoc.getBlockY(), endLoc.getBlockY());
            final int lowz = Math.min(startLoc.getBlockZ(), endLoc.getBlockZ());
            
            final int highx = Math.max(startLoc.getBlockX(), endLoc.getBlockX());
            final int highy = Math.max(startLoc.getBlockY(), endLoc.getBlockY());
            final int highz = Math.max(startLoc.getBlockZ(), endLoc.getBlockZ());
            
            this.highPoints = new Location(startLoc.getWorld(), highx, highy, highz);
            this.lowPoints = new Location(startLoc.getWorld(), lowx, lowy, lowz);
        }
        else
        {
            this.highPoints = null;
            this.lowPoints = null;
        }
        
    }
    
    public boolean isAreaWithinArea(final Cuboid area)
    {
        return (this.containsLoc(area.highPoints) && this.containsLoc(area.lowPoints));
    }
    
    public boolean containsLoc(final Location loc)
    {
        if (loc == null || !loc.getWorld().equals(this.highPoints.getWorld()))
        {
            return false;
        }
        
        return this.lowPoints.getBlockX() <= loc.getBlockX() && this.highPoints.getBlockX() >= loc.getBlockX() && this.lowPoints.getBlockZ() <= loc.getBlockZ()
                && this.highPoints.getBlockZ() >= loc.getBlockZ() && this.lowPoints.getBlockY() <= loc.getBlockY() && this.highPoints.getBlockY() >= loc.getBlockY();
    }
    
    public boolean containsLocWithoutY(final Location loc)
    {
        if (this.highPoints == null || this.lowPoints == null)
        {
            return false;
        }
        if (loc == null || !loc.getWorld().equals(this.highPoints.getWorld()))
        {
            return false;
        }
        
        return this.lowPoints.getBlockX() <= loc.getBlockX() && this.highPoints.getBlockX() >= loc.getBlockX() && this.lowPoints.getBlockZ() <= loc.getBlockZ()
                && this.highPoints.getBlockZ() >= loc.getBlockZ();
    }
    
    public boolean containsLocWithoutYD(final Location loc)
    {
        if (this.highPoints == null || this.lowPoints == null)
        {
            return false;
        }
        if (loc == null || !loc.getWorld().equals(this.highPoints.getWorld()))
        {
            return false;
        }
        
        return this.lowPoints.getBlockX() <= loc.getBlockX() + 2 && this.highPoints.getBlockX() >= loc.getBlockX() - 2 && this.lowPoints.getBlockZ() <= loc.getBlockZ() + 2
                && this.highPoints.getBlockZ() >= loc.getBlockZ() - 2;
    }
    
    public long getSize()
    {
        return Math.abs(this.getXSize() * this.getYSize() * this.getZSize());
    }
    
    public Location getRandomLocation()
    {
        final World world = this.getWorld();
        final Random randomGenerator = new Random();
        
        Location result = new Location(world, this.highPoints.getBlockX(), this.highPoints.getBlockY(), this.highPoints.getZ());
        
        if (this.getSize() > 1)
        {
            final double randomX = this.lowPoints.getBlockX() + randomGenerator.nextInt(this.getXSize());
            final double randomY = this.lowPoints.getBlockY() + randomGenerator.nextInt(this.getYSize());
            final double randomZ = this.lowPoints.getBlockZ() + randomGenerator.nextInt(this.getZSize());
            
            result = new Location(world, randomX, randomY, randomZ);
        }
        
        return result;
    }
    
    public Location getRandomLocationForMobs()
    {
        final Location temp = this.getRandomLocation();
        
        return new Location(temp.getWorld(), temp.getBlockX() + 0.5, temp.getBlockY() + 0.5, temp.getBlockZ() + 0.5);
    }
    
    public int getXSize()
    {
        return (this.highPoints.getBlockX() - this.lowPoints.getBlockX()) + 1;
    }
    
    public int getYSize()
    {
        return (this.highPoints.getBlockY() - this.lowPoints.getBlockY()) + 1;
    }
    
    public int getZSize()
    {
        return (this.highPoints.getBlockZ() - this.lowPoints.getBlockZ()) + 1;
    }
    
    public Location getHighLoc()
    {
        return this.highPoints;
    }
    
    public Location getLowLoc()
    {
        return this.lowPoints;
    }
    
    public World getWorld()
    {
        return this.highPoints.getWorld();
    }
    
    public Map<String, Object> save()
    {
        final Map<String, Object> root = new LinkedHashMap<>();
        
        root.put("World", this.highPoints.getWorld().getName());
        root.put("X1", this.highPoints.getBlockX());
        root.put("Y1", this.highPoints.getBlockY());
        root.put("Z1", this.highPoints.getBlockZ());
        root.put("X2", this.lowPoints.getBlockX());
        root.put("Y2", this.lowPoints.getBlockY());
        root.put("Z2", this.lowPoints.getBlockZ());
        
        return root;
    }
    
    public static Cuboid load(final Map<String, Object> root) throws IllegalArgumentException
    {
        if (root == null)
        {
            throw new IllegalArgumentException("Invalid root map!");
        }
        
        final String owner = (String) root.get("Owner");
        final World world = Bukkit.getServer().getWorld((String) root.get("World"));
        final int x1 = (Integer) root.get("X1");
        final int y1 = (Integer) root.get("Y1");
        final int z1 = (Integer) root.get("Z1");
        final int x2 = (Integer) root.get("X2");
        final int y2 = (Integer) root.get("Y2");
        final int z2 = (Integer) root.get("Z2");
        
        final Cuboid newArea = new Cuboid(new Location(world, x1, y1, z1), new Location(world, x2, y2, z2));
        
        return newArea;
    }
    
    @Override
    public String toString()
    {
        return new StringBuilder("(").append(this.lowPoints.getBlockX()).append(", ").append(this.lowPoints.getBlockY()).append(", ").append(this.lowPoints.getBlockZ()).append(") to (")
                .append(this.highPoints.getBlockX()).append(", ").append(this.highPoints.getBlockY()).append(", ").append(this.highPoints.getBlockZ()).append(")").toString();
    }
    
    public String toRaw()
    {
        return new StringBuilder(this.getWorld().getName()).append(",").append(this.lowPoints.getBlockX()).append(",").append(this.lowPoints.getBlockY()).append(",").append(this.lowPoints.getBlockZ())
                .append(",").append(this.highPoints.getBlockX()).append(",").append(this.highPoints.getBlockY()).append(",").append(this.highPoints.getBlockZ()).toString();
    }

}
