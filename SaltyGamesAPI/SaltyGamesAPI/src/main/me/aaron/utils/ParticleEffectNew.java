package main.me.aaron.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import main.me.aaron.MinigamesAPI;

public enum ParticleEffectNew {

	HUGE_EXPLOSION("hugeexplosion", Environment.ANY), // works in any block type
    LARGE_EXPLODE("largeexplode", Environment.ANY), FIREWORK_SPARK("fireworksSpark", Environment.ANY), TOWN_AURA("townaura", Environment.ANY), CRIT("crit", Environment.ANY), MAGIC_CRIT("magicCrit",
            Environment.ANY), SMOKE("smoke", Environment.ANY), MOB_SPELL("mobSpell", Environment.ANY), MOB_SPELL_AMBIENT("mobSpellAmbient", Environment.ANY), SPELL("spell",
                    Environment.ANY), INSTANT_SPELL("instantSpell", Environment.ANY), WITCH_MAGIC("witchMagic", Environment.ANY), NOTE("note", Environment.ANY), PORTAL("portal",
                            Environment.ANY), ENCHANTMENT_TABLE("enchantmenttable", Environment.ANY), EXPLODE("explode", Environment.ANY), FLAME("flame", Environment.ANY), LAVA("lava",
                                    Environment.ANY), FOOTSTEP("footstep", Environment.ANY), LARGE_SMOKE("largesmoke", Environment.ANY), CLOUD("cloud", Environment.ANY), RED_DUST("reddust",
                                            Environment.ANY), SNOWBALL_POOF("snowballpoof", Environment.ANY), DRIP_WATER("dripWater", Environment.ANY), DRIP_LAVA("dripLava",
                                                    Environment.ANY), SNOW_SHOVEL("snowshovel", Environment.ANY), SLIME("slime", Environment.ANY), HEART("heart",
                                                            Environment.ANY), ANGRY_VILLAGER("angryVillager", Environment.ANY), HAPPY_VILLAGER("happerVillager", Environment.ANY),
    // ICONCRACK is not reliable and should not be added to the API, across different sized texture packs it displays a different item)
    ICONCRACK("iconcrack_%id%", Environment.UKNOWN), // Guessing it is any, but didn't test
    TILECRACK("tilecrack_%id%_%data%", Environment.UKNOWN), // Guessing it is any, but didn't test
    SPLASH("splash", Environment.AIR), // only works in air
    BUBBLE("bubble", Environment.IN_WATER), // only works in water
    SUSPEND("suspend", Environment.UKNOWN), // Can't figure out what this does
    DEPTH_SUSPEND("depthSuspend", Environment.UKNOWN); // Can't figure out what this does
    
    private final String      packetName;
    private final Environment environment;
    
    private int               xStack, yStack, zStack;
    private int               _id   = 1;
    private int               _data = 0;
    
    ParticleEffectNew(final String packetName, final Environment environment)
    {
        this.packetName = packetName;
        this.environment = environment;
    }
    
    public void setStack(final int stackXAxis, final int stackYAxis, final int stackZAxis)
    {
        this.xStack = stackXAxis;
        this.yStack = stackYAxis;
        this.zStack = stackZAxis;
    }
    
    public void setId(final int id)
    {
        this._id = id;
    }
    
    public void setData(final int data)
    {
        this._data = data;
    }
    
    public Environment getEnvironment()
    {
        return this.environment;
    }
    
    public enum Environment
    {
        ANY, AIR, IN_WATER, UKNOWN;
    }
    
    private static void setValue(final Object instance, final String fieldName, final Object value) throws Exception
    {
        final Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
    
    public void animateReflected(final Player p, final Location location, final float speed, final int count)
    {
        try
        {
            final Method getHandle = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".entity.CraftPlayer").getMethod("getHandle");
            final Field playerConnection = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer").getField("playerConnection");
            playerConnection.setAccessible(true);
            final Method sendPacket = playerConnection.getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Packet"));
            
            String packetname = "PacketPlayOutWorldParticles";
            if (MinigamesAPI.getAPI().internalServerVersion.equalsIgnoreCase("v1_6_R2"))
            {
                packetname = "Packet63WorldParticles";
            }
            
            final Constructor packetConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + "." + packetname).getConstructor();
            
            final Object packet = packetConstr.newInstance();
            ParticleEffectNew.setValue(packet, "a", this.packetName.replace("%id%", "" + this._id).replace("%data%", "" + this._data));
            ParticleEffectNew.setValue(packet, "b", (float) location.getX());
            ParticleEffectNew.setValue(packet, "c", (float) location.getY());
            ParticleEffectNew.setValue(packet, "d", (float) location.getZ());
            ParticleEffectNew.setValue(packet, "e", 0F);
            ParticleEffectNew.setValue(packet, "f", 0F);
            ParticleEffectNew.setValue(packet, "g", 0F);
            ParticleEffectNew.setValue(packet, "h", speed);
            ParticleEffectNew.setValue(packet, "i", count);
            
            sendPacket.invoke(playerConnection.get(getHandle.invoke(p)), packet);
        }
        catch (final Exception e)
        {
            MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
        }
    }
}
