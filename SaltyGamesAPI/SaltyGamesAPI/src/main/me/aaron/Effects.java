package main.me.aaron;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import main.me.aaron.utils.Validator;

public class Effects {
	
	public static void playBloodEffect(final Player p)
    {
        p.getWorld().playEffect(p.getLocation().add(0D, 1D, 0D), Effect.STEP_SOUND, 152);
    }
    
    
    public static void playEffect(final Arena a, final Location l, final String effectname)
    {
        for (final String p_ : a.getAllPlayers())
        {
            if (Validator.isPlayerOnline(p_))
            {
                final Player p = Bukkit.getPlayer(p_);
                final ParticleEffect eff = ParticleEffect.valueOf(effectname);
                eff.setId(55);
                eff.animateReflected(p, l, 1F, 2);
            }
        }
    }
    
    
    public static BukkitTask playFakeBed(final Arena a, final Player p)
    {
        return Effects.playFakeBed(a, p, p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
    }
    
    
    public static BukkitTask playFakeBed(final Arena a, final Player p, final int x, final int y, final int z)
    {
        try
        {
            final Method getHandle = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".entity.CraftPlayer").getMethod("getHandle");
            final Field playerConnection = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer").getField("playerConnection");
            playerConnection.setAccessible(true);
            final Method sendPacket = playerConnection.getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Packet"));
            
            final Constructor<?> packetPlayOutNamedEntityConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutNamedEntitySpawn")
                    .getConstructor(Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityHuman"));
            final Constructor<?> packetPlayOutBedConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutBed").getConstructor();
            
            final int id = -p.getEntityId() - 1000;
            
            final Object packet = packetPlayOutNamedEntityConstr.newInstance(getHandle.invoke(p));
            Effects.setValue(packet, "a", id);
            
            final Object packet_ = packetPlayOutBedConstr.newInstance();
            Effects.setValue(packet_, "a", id);
            Effects.setValue(packet_, "b", x);
            Effects.setValue(packet_, "c", y);
            Effects.setValue(packet_, "d", z);
            
            for (final String p_ : a.getAllPlayers())
            {
                final Player p__ = Bukkit.getPlayer(p_);
                sendPacket.invoke(playerConnection.get(getHandle.invoke(p__)), packet);
                sendPacket.invoke(playerConnection.get(getHandle.invoke(p__)), packet_);
            }
            
            // Move the effect (fake player) to 0 0 0 after 4 seconds
            final ArrayList<String> tempp = new ArrayList<>(a.getAllPlayers());
            final World currentworld = p.getWorld();
            return Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
                try
                {
                    Effects.setValue(packet_, "a", id);
                    Effects.setValue(packet_, "b", 0);
                    Effects.setValue(packet_, "c", 0);
                    Effects.setValue(packet_, "d", 0);
                    for (final String p_ : tempp)
                    {
                        if (Validator.isPlayerOnline(p_))
                        {
                            final Player p__ = Bukkit.getPlayer(p_);
                            if (p__.getWorld() == currentworld)
                            {
                                sendPacket.invoke(playerConnection.get(getHandle.invoke(p__)), packet);
                                sendPacket.invoke(playerConnection.get(getHandle.invoke(p__)), packet_);
                            }
                        }
                    }
                }
                catch (final Exception e)
                {
                    MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                }
            }, 20L * 4);
        }
        catch (final Exception e)
        {
            MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Failed playing fakebed effect", e);
        }
        return null;
    }
    
    
    private static void setValue(final Object instance, final String fieldName, final Object value) throws Exception
    {
        final Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
    
    
    public static void playRespawn(final Player p, final JavaPlugin plugin)
    {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try
            {
                final Method getHandle = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".entity.CraftPlayer").getMethod("getHandle");
                final Field playerConnection = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer").getField("playerConnection");
                final Field minecraftServer = playerConnection.get(getHandle.invoke(p)).getClass().getDeclaredField("minecraftServer");
                minecraftServer.setAccessible(true);
                
                final Object nmsMcServer = minecraftServer.get(playerConnection.get(getHandle.invoke(p)));
                final Object playerlist = nmsMcServer.getClass().getDeclaredMethod("getPlayerList").invoke(nmsMcServer);
                final Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer"),
                        int.class, boolean.class);
                moveToWorld.invoke(playerlist, getHandle.invoke(p), 0, false);
            }
            catch (final Exception e)
            {
                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Failed additional respawn packet", e);
            }
        }, 1L);
    }
    
    
    public static void playTitle(final Player player, final String title, int eindex)
    {
        int enumindex = eindex;
        if (enumindex > 4)
        {
            enumindex = 0;
        }
        try
        {
            final Method getHandle = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".entity.CraftPlayer").getMethod("getHandle");
            final Field playerConnection = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer").getField("playerConnection");
            playerConnection.setAccessible(true);
            final Method sendPacket = playerConnection.getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Packet"));
            Class<?> enumClass = null;
            Object chatComp = null;
                enumClass = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutTitle$EnumTitleAction");
                chatComp = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".ChatComponentText").getConstructor(String.class).newInstance(title);
            final Constructor<?> packetPlayOutTitleConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutTitle").getConstructor();
            final Object packet = packetPlayOutTitleConstr.newInstance();
            Effects.setValue(packet, "a", enumClass.getEnumConstants()[enumindex]);
            Effects.setValue(packet, "b", chatComp);
            
            sendPacket.invoke(playerConnection.get(getHandle.invoke(player)), packet);
        }
        catch (final Exception e)
        {
            MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Failed sending title packet", e);
        }
    }
    
    static HashMap<Integer, Integer> effectlocd        = new HashMap<>();
    static HashMap<Integer, Integer> effectlocd_taskid = new HashMap<>();
    
    
    public static ArrayList<Integer> playHologram(final Player p, final Location l, final String text, final boolean moveDown, final boolean removeAfterCooldown)
    {
        final ArrayList<Integer> ret = new ArrayList<>();
        final boolean setTrue = true;
        if (setTrue)
        {
            try
            {
                final Method getPlayerHandle = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".entity.CraftPlayer").getMethod("getHandle");
                final Field playerConnection = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer").getField("playerConnection");
                playerConnection.setAccessible(true);
                final Method sendPacket = playerConnection.getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Packet"));
                
                final Class<?> craftw = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".CraftWorld");
                final Class<?> w = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".World");
                final Class<?> entity = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Entity");
                final Method getWorldHandle = craftw.getDeclaredMethod("getHandle");
                final Object worldServer = getWorldHandle.invoke(craftw.cast(l.getWorld()));
//                Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutSpawnEntity").getConstructor(entity, int.class);
                final Constructor<?> packetPlayOutSpawnEntityLivingConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutSpawnEntityLiving")
                        .getConstructor(Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityLiving"));
//                Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutAttachEntity").getConstructor(int.class, entity, entity);
                final Constructor<?> packetPlayOutEntityDestroyConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutEntityDestroy")
                        .getConstructor(int[].class);
                final Constructor<?> packetPlayOutEntityVelocity = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutEntityVelocity")
                        .getConstructor(int.class, double.class, double.class, double.class);
                
                // EntityArmorStand
                final Constructor<?> entityArmorStandConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityArmorStand").getConstructor(w);
                final Object entityArmorStand = entityArmorStandConstr.newInstance(worldServer);
                final Method setLoc2 = entityArmorStand.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                setLoc2.invoke(entityArmorStand, l.getX(), l.getY() - 1D, l.getZ(), 0F, 0F);
                final Method setCustomName = entityArmorStand.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setCustomName", String.class);
                setCustomName.invoke(entityArmorStand, text);
                final Method setCustomNameVisible = entityArmorStand.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setCustomNameVisible", boolean.class);
                setCustomNameVisible.invoke(entityArmorStand, true);
                final Method getArmorStandId = entityArmorStand.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getId");
                final int armorstandId = (Integer) (getArmorStandId.invoke(entityArmorStand));
                final Method setInvisble = entityArmorStand.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setInvisible", boolean.class);
                setInvisble.invoke(entityArmorStand, true);
                
                Effects.effectlocd.put(armorstandId, 12); // send move packet 12 times
                
                // Send EntityArmorStand packet
                final Object horsePacket = packetPlayOutSpawnEntityLivingConstr.newInstance(entityArmorStand);
                sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), horsePacket);
                
                // Send velocity packets to move the entities slowly down
                if (moveDown)
                {
                    Effects.effectlocd_taskid.put(armorstandId, Bukkit.getScheduler().runTaskTimer(MinigamesAPI.getAPI(), () -> {
                        try
                        {
                            final int i = Effects.effectlocd.get(armorstandId);
                            final Object packet = packetPlayOutEntityVelocity.newInstance(armorstandId, 0D, -0.05D, 0D);
                            sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), packet);
                            if (i < -1)
                            {
                                final int taskid = Effects.effectlocd_taskid.get(armorstandId);
                                Effects.effectlocd_taskid.remove(armorstandId);
                                Effects.effectlocd.remove(armorstandId);
                                Bukkit.getScheduler().cancelTask(taskid);
                                return;
                            }
                            Effects.effectlocd.put(armorstandId, Effects.effectlocd.get(armorstandId) - 1);
                        }
                        catch (final Exception e)
                        {
                            if (MinigamesAPI.debug)
                            {
                                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                            }
                        }
                    }, 2L, 2L).getTaskId());
                }
                
                // Remove both entities (and thus the hologram) after 2 seconds
                if (removeAfterCooldown)
                {
                    Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
                        try
                        {
                            final Object destroyPacket = packetPlayOutEntityDestroyConstr.newInstance(new int[] { armorstandId });
                            sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), destroyPacket);
                        }
                        catch (final Exception e)
                        {
                            if (MinigamesAPI.debug)
                            {
                                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                            }
                        }
                    }, 20L * 2);
                }
                
                ret.add(armorstandId);
                
            }
            catch (final Exception e)
            {
                if (MinigamesAPI.debug)
                {
                    MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                }
            }
            return ret;
        }
        try
        {
            // If player is on 1.8, we'll have to use armor stands, otherwise just use the old 1.7 technique
            final boolean playerIs1_8 = true;
            
            final Method getPlayerHandle = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".entity.CraftPlayer").getMethod("getHandle");
            final Field playerConnection = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityPlayer").getField("playerConnection");
            playerConnection.setAccessible(true);
            final Method sendPacket = playerConnection.getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Packet"));
            
            final Class<?> craftw = Class.forName("org.bukkit.craftbukkit." + MinigamesAPI.getAPI().internalServerVersion + ".CraftWorld");
            final Class<?> w = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".World");
            final Class<?> entity = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".Entity");
            final Method getWorldHandle = craftw.getDeclaredMethod("getHandle");
            final Object worldServer = getWorldHandle.invoke(craftw.cast(l.getWorld()));
            final Constructor<?> packetPlayOutSpawnEntityConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutSpawnEntity")
                    .getConstructor(entity, int.class);
            final Constructor<?> packetPlayOutSpawnEntityLivingConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutSpawnEntityLiving")
                    .getConstructor(Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityLiving"));
            final Constructor<?> packetPlayOutAttachEntityConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutAttachEntity")
                    .getConstructor(int.class, entity, entity);
            final Constructor<?> packetPlayOutEntityDestroyConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutEntityDestroy")
                    .getConstructor(int[].class);
            final Constructor<?> packetPlayOutEntityVelocity = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".PacketPlayOutEntityVelocity")
                    .getConstructor(int.class, double.class, double.class, double.class);
            
            // WitherSkull
            final Constructor<?> witherSkullConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityWitherSkull").getConstructor(w);
            final Object witherSkull = witherSkullConstr.newInstance(worldServer);
            final Method setLoc = witherSkull.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
            setLoc.invoke(witherSkull, l.getX(), l.getY() + 33D, l.getZ(), 0F, 0F);
            final Method getWitherSkullId = witherSkull.getClass().getSuperclass().getSuperclass().getDeclaredMethod("getId");
            final int witherSkullId = (Integer) (getWitherSkullId.invoke(witherSkull));
            
            // EntityHorse
            final Constructor<?> entityHorseConstr = Class.forName("net.minecraft.server." + MinigamesAPI.getAPI().internalServerVersion + ".EntityHorse").getConstructor(w);
            final Object entityHorse = entityHorseConstr.newInstance(worldServer);
            final Method setLoc2 = entityHorse.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setLocation", double.class,
                    double.class, double.class, float.class, float.class);
            setLoc2.invoke(entityHorse, l.getX(), l.getY() + (playerIs1_8 ? -1D : 33D), l.getZ(), 0F, 0F);
            final Method setAge = entityHorse.getClass().getSuperclass().getSuperclass().getDeclaredMethod("setAge", int.class);
            setAge.invoke(entityHorse, -1000000);
            final Method setCustomName = entityHorse.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setCustomName", String.class);
            setCustomName.invoke(entityHorse, text);
            final Method setCustomNameVisible = entityHorse.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setCustomNameVisible", boolean.class);
            setCustomNameVisible.invoke(entityHorse, true);
            final Method getHorseId = entityHorse.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("getId");
            final int horseId = (Integer) (getHorseId.invoke(entityHorse));
            
            if (playerIs1_8)
            {
                // Set horse (later armor stand) invisible
                final Method setInvisble = entityHorse.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setInvisible",
                        boolean.class);
                setInvisble.invoke(entityHorse, true);
            }
            
            Effects.effectlocd.put(horseId, 12); // send move packet 12 times
            
            // Send Witherskull+EntityHorse packet
            final Object horsePacket = packetPlayOutSpawnEntityLivingConstr.newInstance(entityHorse);
            if (playerIs1_8)
            {
                // Set entity id to 30 (armor stand):
                Effects.setValue(horsePacket, "b", 30);
                // Fix datawatcher values to prevent crashes (ofc armor stands expect other data than horses):
                final Field datawatcher = entityHorse.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("datawatcher");
                datawatcher.setAccessible(true);
                final Object datawatcherInstance = datawatcher.get(entityHorse);
                final Field d = datawatcherInstance.getClass().getDeclaredField("d");
                d.setAccessible(true);
                final Map<?, ?> dmap = (Map<?, ?>) d.get(datawatcherInstance);
                dmap.remove(10);
                // These are the Rotation ones
                dmap.remove(11);
                dmap.remove(12);
                dmap.remove(13);
                dmap.remove(14);
                dmap.remove(15);
                dmap.remove(16);
                final Method a = datawatcherInstance.getClass().getDeclaredMethod("a", int.class, Object.class);
                a.invoke(datawatcherInstance, 10, (byte) 0);
            }
            sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), horsePacket);
            if (!playerIs1_8)
            {
                final Object witherPacket = packetPlayOutSpawnEntityConstr.newInstance(witherSkull, 64);
                sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), witherPacket);
            }
            
            // Send attach packet
            if (!playerIs1_8)
            {
                final Object attachPacket = packetPlayOutAttachEntityConstr.newInstance(0, entityHorse, witherSkull);
                sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), attachPacket);
            }
            
            // Send velocity packets to move the entities slowly down
            if (moveDown)
            {
                Effects.effectlocd_taskid.put(horseId, Bukkit.getScheduler().runTaskTimer(MinigamesAPI.getAPI(), () -> {
                    try
                    {
                        final int i = Effects.effectlocd.get(horseId);
                        final Object packet = packetPlayOutEntityVelocity.newInstance(horseId, 0D, -0.05D, 0D);
                        sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), packet);
                        if (!playerIs1_8)
                        {
                            final Object packet2 = packetPlayOutEntityVelocity.newInstance(witherSkullId, 0D, -0.05D, 0D);
                            sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), packet2);
                        }
                        if (i < -1)
                        {
                            final int taskid = Effects.effectlocd_taskid.get(horseId);
                            Effects.effectlocd_taskid.remove(horseId);
                            Effects.effectlocd.remove(horseId);
                            Bukkit.getScheduler().cancelTask(taskid);
                            return;
                        }
                        Effects.effectlocd.put(horseId, Effects.effectlocd.get(horseId) - 1);
                    }
                    catch (final Exception e)
                    {
                        if (MinigamesAPI.debug)
                        {
                            MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                        }
                    }
                }, 2L, 2L).getTaskId());
            }
            
            // Remove both entities (and thus the hologram) after 2 seconds
            if (removeAfterCooldown)
            {
                Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
                    try
                    {
                        final Object destroyPacket = packetPlayOutEntityDestroyConstr.newInstance(new int[] { witherSkullId, horseId });
                        sendPacket.invoke(playerConnection.get(getPlayerHandle.invoke(p)), destroyPacket);
                    }
                    catch (final Exception e)
                    {
                        if (MinigamesAPI.debug)
                        {
                            MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                        }
                    }
                }, 20L * 2);
            }
            
            ret.add(witherSkullId);
            ret.add(horseId);
            
        }
        catch (final Exception e)
        {
            if (MinigamesAPI.debug)
            {
                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
            }
        }
        return ret;
    }
    
    
    public static void sendGameModeChange(final Player p, final int gamemode)
    {
        // NOT_SET(-1, ""), SURVIVAL(0, "survival"), CREATIVE(1, "creative"), ADVENTURE(2, "adventure"), SPECTATOR(3, "spectator");
        
        if (gamemode == 3)
        {
            return;
        }
        
        p.setGameMode(GameMode.getByValue(gamemode));
    }

}
