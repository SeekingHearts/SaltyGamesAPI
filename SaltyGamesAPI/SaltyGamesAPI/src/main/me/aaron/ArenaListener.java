package main.me.aaron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.me.aaron.utils.ChangeCause;
import main.me.aaron.utils.Cuboid;
import main.me.aaron.utils.Util;
import main.me.aaron.utils.Validator;
import main.me.aaron.utils.Util.CompassPlayer;

public class ArenaListener implements Listener {
	
	private JavaPlugin pl = null;
	
	private PluginInstance pli = null;
	
	private String minigame = "minigame";
	
	private ArrayList<String> cmds = new ArrayList<>();
	
	private String leave_cmd = "/leave";
	
	public int loseY = 4;
	
	public ArenaListener(final JavaPlugin pl, final PluginInstance pinstance, final String minigame)
    {
        this.pl = pl;
        this.pli = pinstance;
        this.setName(minigame);
        this.leave_cmd = pl.getConfig().getString(ArenaConfigStrings.CONFIG_LEAVE_COMMAND);
    }
    
    
    public ArenaListener(final JavaPlugin pl, final PluginInstance pinstance, final String minigame, final ArrayList<String> cmds)
    {
        this(pl, pinstance, minigame);
        this.cmds = cmds;
    }
    
    // *************************
    // ***** smart reset support
    // *************************
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onExplode(final EntityExplodeEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (event.getEntity() != null)
                    {
                        if (c.containsLocWithoutY(event.getEntity().getLocation()))
                        {
                            for (final Block b : event.blockList())
                            {
                                a.getSmartReset().addChanged(b, b.getType().equals(Material.CHEST));
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onExplode2(final BlockExplodeEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    for (final Block b : event.blockList())
                    {
                        if (c.containsLocWithoutY(b.getLocation()))
                        {
                            a.getSmartReset().addChanged(b, b.getType().equals(Material.CHEST));
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(final BlockFromToEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (c.containsLocWithoutYD(event.getBlock().getLocation()))
                    {
                        if (a.getArenaState() == ArenaState.INGAME)
                        {
                            a.getSmartReset().addChanged(event.getToBlock(), event.getToBlock().getType().equals(Material.CHEST), ChangeCause.FROM_TO);
                        }
                        else if (a.getArenaState() == ArenaState.RESTARTING)
                        {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION && a.getArenaState() == ArenaState.INGAME)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (c.containsLocWithoutY(event.getBlock().getLocation()))
                    {
                        a.getSmartReset().addChanged(event.getBlock(), event.getBlock().getType().equals(Material.CHEST), ChangeCause.FADE);
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPhysics(final BlockPhysicsEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        if (c.containsLocWithoutY(event.getBlock().getLocation()))
                        {
                            if (event.getChangedType() == Material.CARPET || event.getChangedType() == Material.BED_BLOCK)
                            {
                                return;
                            }
                            a.getSmartReset().addChanged(event.getBlock(), event.getBlock().getType().equals(Material.CHEST), ChangeCause.PHYSICS);
                        }
                    }
                    else if (a.getArenaState() == ArenaState.RESTARTING)
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onBlockRedstone(final BlockRedstoneEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.getSmartReset().addChanged(event.getBlock(), false);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event)
    {
        // disallow fire spread while the arena restarts
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.getSmartReset().addChanged(event.getBlock().getLocation(), Material.AIR, (byte) 0);
                    }
                    else if (a.getArenaState() == ArenaState.RESTARTING)
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent event)
    {
        if (event.getEntity() instanceof Enderman)
        {
            for (final Arena a : this.pli.getArenas())
            {
                if (a.getArenaType() == ArenaType.REGENERATION)
                {
                    final Cuboid c = a.getBoundaries();
                    if (c != null)
                    {
                        if (c.containsLocWithoutY(event.getEntity().getLocation()))
                        {
                            a.getSmartReset().addChanged(event.getBlock(), event.getBlock().getType().equals(Material.CHEST), ChangeCause.ENTITY_CHANGE);
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onLeavesDecay(final LeavesDecayEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION && a.getArenaState() == ArenaState.INGAME)
            {
                final Cuboid c = a.getBoundaries();
                if (c != null)
                {
                    if (c.containsLocWithoutY(event.getBlock().getLocation()))
                    {
                        a.getSmartReset().addChanged(event.getBlock(), event.getBlock().getType().equals(Material.CHEST));
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (Validator.isArenaValid(this.pl, a) && a.getArenaType() == ArenaType.REGENERATION)
            {
                final Cuboid c = new Cuboid(Util.getComponentForArena(this.pl, a.getInternalName(), ArenaConfigStrings.BOUNDS_LOW),
                        Util.getComponentForArena(this.pl, a.getInternalName(), ArenaConfigStrings.BOUNDS_HIGH));
                if (c != null)
                {
                    if (c.containsLocWithoutY(event.getBlock().getLocation()))
                    {
                        a.getSmartReset().addChanged(event.getBlock(), event.getBlock().getType().equals(Material.CHEST), ChangeCause.BURN);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onStructureGrow(final StructureGrowEvent event)
    {
        for (final Arena a : this.pli.getArenas())
        {
            if (a.getArenaType() == ArenaType.REGENERATION && a.getArenaState() == ArenaState.INGAME)
            {
                final Cuboid c = new Cuboid(Util.getComponentForArena(this.pl, a.getInternalName(), ArenaConfigStrings.BOUNDS_LOW),
                        Util.getComponentForArena(this.pl, a.getInternalName(), ArenaConfigStrings.BOUNDS_HIGH));
                if (c != null)
                {
                    final Location start = event.getLocation();
                    if (c.containsLocWithoutY(start))
                    {
                        a.getSmartReset().addChanged(start.getBlock(), false);
                        for (final BlockState bs : event.getBlocks())
                        {
                            final Block b = bs.getBlock();
                            a.getSmartReset().addChanged(b.getLocation(), Material.AIR, (byte) 0);
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak2(final BlockBreakEvent event)
    {
        final Player p = event.getPlayer();
        if (this.pli.containsGlobalPlayer(p.getName()))
        {
            final Arena a = this.pli.global_players.get(p.getName());
            if (event.getBlock().getType() != Material.AIR)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation(), event.getBlock().getType(), event.getBlock().getData());
            }
        }
    }
    
    // *******************
    // ***** cancel events
    // *******************
    
    
    @EventHandler
    public void onPlayerDrop(final PlayerDropItemEvent event)
    {
        if (this.pli.containsGlobalPlayer(event.getPlayer().getName()))
        {
            final Arena a = this.pli.global_players.get(event.getPlayer().getName());
            if (a != null)
            {
                if (a.getArenaState() != ArenaState.INGAME && a.getArcadeInstance() == null && !a.isArcadeMain())
                {
                    event.setCancelled(true);
                }
                if (a.getArenaState() == ArenaState.INGAME && this.pli.containsGlobalLost(event.getPlayer().getName()))
                {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event)
    {
        this.pli.getSpectatorManager();
        // spectators shall not pick up items
        if (this.pli.containsGlobalLost(event.getPlayer().getName()) || SpectatorManager.isSpectating(event.getPlayer()))
        {
            final Arena a = this.pli.global_lost.get(event.getPlayer().getName());
            if (a != null)
            {
                if (a.getArenaState() == ArenaState.INGAME && a.getArcadeInstance() == null)
                {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if (event.getWhoClicked() instanceof Player)
        {
            final Player p = (Player) event.getWhoClicked();
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                final Arena a = this.pli.global_players.get(p.getName());
                if (a != null)
                {
                    if (a.getArenaState() == ArenaState.STARTING && a.getArcadeInstance() == null && !a.isArcadeMain())
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onHunger(final FoodLevelChangeEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            final Player p = (Player) event.getEntity();
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                if (!this.pli.global_players.get(p.getName()).isArcadeMain())
                {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    
    @EventHandler
    public void NoDamageEntityInLobby(final EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            final Player p = (Player) event.getDamager();
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                final Arena arena = this.pli.global_players.get(p.getName());
                if (arena.getArenaState() == ArenaState.JOIN || (arena.getArenaState() == ArenaState.STARTING))
                {
                    final Entity e = event.getEntity();
                    if (e instanceof ArmorStand || e instanceof ItemFrame || e instanceof Painting || e instanceof Minecart)
                    {
                        event.setCancelled(false);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void NoClickEntityInLobby(final PlayerInteractEntityEvent event) throws IOException
    {
        final Player p = event.getPlayer();
        final Entity e = event.getRightClicked();
        if (!(e instanceof Player))
        {
            final Arena arena = this.pli.global_players.get(p.getName());
            if (arena != null)
            {
                if (arena.getArenaState() == ArenaState.JOIN || (arena.getArenaState() == ArenaState.STARTING))
                {
                    if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND) || event.getRightClicked().getType().equals(EntityType.MINECART)
                            || event.getRightClicked().getType().equals(EntityType.MINECART_CHEST) || event.getRightClicked().getType().equals(EntityType.MINECART_HOPPER)
                            || event.getRightClicked().getType().equals(EntityType.ITEM_FRAME) || event.getRightClicked().getType().equals(EntityType.PAINTING))
                    {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPaintingBreak(final HangingBreakByEntityEvent event)
    {
        if (event.getRemover() instanceof Player)
        {
            final String p_ = ((Player) event.getRemover()).getName();
            if (this.pli.containsGlobalPlayer(p_))
            {
                event.setCancelled(true);
            }
        }
        
    }
    
    // ******************
    // ***** other events
    // ******************
    
    
    @EventHandler
    public void Space(final PlayerMoveEvent event)
    {
        final Player p = event.getPlayer();
        if (this.pli.containsGlobalPlayer(p.getName()))
        {
            final Arena a = this.pli.global_players.get(p.getName());
            if (a != null)
            {
                if (a.getArenaState() == ArenaState.INGAME)
                {
                    if (!ArenaListener.isSpectating(p))
                    {
                        for (final Entity e : this.getEntitiesByLocation(p.getLocation(), 30D))
                        {
                            
                            if (e instanceof Player)
                            {
                                final Player sp = (Player) e;
                                
                                if (ArenaListener.isSpectating(sp))
                                {
                                    sp.setVelocity(sp.getLocation().getDirection().setY(0.05D));
                                    sp.setVelocity(sp.getLocation().getDirection().multiply(-2));
                                }
                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVMove(final VehicleMoveEvent event)
    {
        if (event.getVehicle().getPassenger() instanceof Player)
        {
            final Player p = (Player) event.getVehicle().getPassenger();
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                final Arena a = this.pli.global_players.get(p.getName());
                if (!this.pli.containsGlobalLost(p.getName()) && !this.pli.global_arcade_spectator.containsKey(p.getName()))
                {
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        if (MinigamesAPI.debug)
                        {
                            pl.getLogger().log(Level.INFO, "Player " + p + " moved ingame to " + event.getTo());
                        }
                        if (event.getTo().getBlockY() + this.loseY < a.getSpawns().get(0).getBlockY())
                        {
//                            if (a.getArenaType() == ArenaType.JUMPNRUN)
//                            {
//                                Util.teleportPlayerFixed(p, a.getSpawns().get(0));
//                            }
//                            else
//                            {
//                                a.spectate(p.getName());
//                            }
                            a.spectate(p.getName());
                            return;
                        }
                        if (a.getBoundaries() != null)
                        {
                            if (!a.getBoundaries().containsLocWithoutY(event.getTo()))
                            {
                                if (MinigamesAPI.debug)
                                {
                                    pl.getLogger().log(Level.INFO, "Player " + p + " left arena bounds.");
                                }
                                a.spectate(p.getName());
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(final PlayerMoveEvent event)
    {
        try
        {
            final Player p = event.getPlayer();
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                final Arena a = this.pli.global_players.get(p.getName());
                if (!this.pli.containsGlobalLost(p.getName()) && !this.pli.global_arcade_spectator.containsKey(p.getName()))
                {
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        if (MinigamesAPI.debug)
                        {
                            pl.getLogger().log(Level.INFO, "Player " + p + " moved ingame to " + p.getLocation());
                        }
                        if (p.getLocation().getBlockY() + this.loseY < a.getSpawns().get(0).getBlockY())
                        {
                            if (a.getArenaType() == ArenaType.JUMPNRUN)
                            {
                                Util.teleportPlayerFixed(p, a.getSpawns().get(0));
                            }
                            else
                            {
                                a.spectate(p.getName());
                            }
                            return;
                        }
                        if (a.getBoundaries() != null)
                        {
                            if (!a.getBoundaries().containsLocWithoutY(p.getLocation()))
                            {
                                if (MinigamesAPI.debug)
                                {
                                    pl.getLogger().log(Level.INFO, "Player " + p + " left arena bounds.");
                                }
                                Util.pushBack(a.getSpawns().get(0), p);
                            }
                        }
                    }
                    else if (a.getArenaState() == ArenaState.STARTING || a.getArenaState() == ArenaState.JOIN)
                    {
                        if (!a.isArcadeMain())
                        {
                            if (!a.getIngameCountdownStarted())
                            {
                                if (p.getLocation().getBlockY() < 0)
                                {
                                    try
                                    {
                                        Util.teleportPlayerFixed(p, a.getWaitingLobbyTemp());
                                    }
                                    catch (final Exception e)
                                    {
                                        this.pl.getLogger().warning("Waiting lobby for arena " + a.getInternalName() + " missing, please fix by setting it. " + e.getMessage());
                                    }
                                }
                                if (a.getLobbyBoundaries() != null && !a.skip_join_lobby)
                                {
                                    if (!a.getLobbyBoundaries().containsLocWithoutY(p.getLocation()))
                                    {
                                        Util.pushBack(a.getWaitingLobbyTemp(), p);
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        if (this.pli.spectator_move_y_lock && event.getPlayer().getLocation().getBlockY() < (a.getSpawns().get(0).getBlockY() + 30D)
                                || event.getPlayer().getLocation().getBlockY() > (a.getSpawns().get(0).getBlockY() + 30D))
                        {
                            final float b = p.getLocation().getYaw();
                            final float c = p.getLocation().getPitch();
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, () -> {
                                p.setAllowFlight(true);
                                p.setFlying(true);
                                if (p.isInsideVehicle())
                                {
                                    final Entity ent = p.getVehicle();
                                    p.leaveVehicle();
                                    ent.eject();
                                }
                                p.teleport(new Location(p.getWorld(), p.getLocation().getBlockX(), (a.getSpawns().get(0).getBlockY() + 30D), p.getLocation().getBlockZ(), b, c));
                            }, 1);
                            return;
                        }
                        
                        if (a.getSpecBoundaries() != null)
                        {
                            if (!a.getSpecBoundaries().containsLocWithoutY(p.getLocation()))
                            {
                                Util.pushBack(a.getSpawns().get(0).clone().add(0D, 30D, 0D), p);
                            }
                            return;
                        }
                        if (a.getBoundaries() != null)
                        {
                            if (!a.getBoundaries().containsLocWithoutY(p.getLocation()))
                            {
                                Util.pushBack(a.getSpawns().get(0).clone().add(0D, 30D, 0D), p);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e)
        {
            if (MinigamesAPI.debug)
            {
                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
            }
        }
        
    }
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event)
    {
        if (this.pli.containsGlobalPlayer(event.getEntity().getName()))
        {
            event.setDeathMessage(null);
            final Player p = event.getEntity();
            
            p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20 * 3, 50));
            
            final Arena arena = this.pli.global_players.get(p.getName());
            if (arena.getArenaState() == ArenaState.JOIN || (arena.getArenaState() == ArenaState.STARTING && !arena.getIngameCountdownStarted()))
            {
                if (arena.isArcadeMain())
                {
                    Util.teleportPlayerFixed(p, arena.getWaitingLobbyTemp());
                }
            }
            
            arena.global_drops.addAll(event.getDrops());
            
            arena.spectate(p.getName());
            
            this.pli.global_lost.put(p.getName(), arena);
            
            int count = 0;
            // for (String p_ : pli.global_players.keySet()) {
            // if (Validator.isPlayerOnline(p_)) {
            // if (pli.global_players.get(p_).getInternalName().equalsIgnoreCase(arena.getInternalName())) {
            // if (!pli.containsGlobalLost(p_)) {
            // count++;
            // }
            // }
            // }
            // }
            for (final String p_ : arena.getAllPlayers())
            {
                if (Validator.isPlayerOnline(p_))
                {
                    if (!this.pli.containsGlobalLost(p_))
                    {
                        count++;
                    }
                }
            }
            final int count_ = count;
            
            Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
                if (ArenaListener.this.pli.containsGlobalPlayer(p.getName()) && count_ > 1)
                {
                    arena.spectate(p.getName());
                }
                for (final String p_ : arena.getAllPlayers())
                {
                    if (Validator.isPlayerOnline(p_))
                    {
                        final Player p__ = Bukkit.getPlayer(p_);
                        Util.sendMessage(ArenaListener.this.pl, p__, ArenaListener.this.pli.getMessagesConfig().broadcast_players_left.replaceAll("<count>", arena.getPlayerCount()));
                    }
                }
            }, 5);
            
            if (this.pli.last_man_standing)
            {
                if (count < 2)
                {
                    // last man standing
                    arena.stopArena();
                }
            }
        }
    }
    
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            final Player p = (Player) event.getEntity();
            if (this.pli.containsGlobalPlayer(p.getName()) && this.pli.containsGlobalLost(p.getName()))
            {
                final Arena a = this.pli.global_players.get(p.getName());
                if (a.getArenaState() == ArenaState.INGAME && a.getArcadeInstance() == null && !a.getAlwaysPvP())
                {
                    event.setCancelled(true);
                }
            }
            if (event.getCause().equals(DamageCause.ENTITY_ATTACK))
            {
                if (this.pli.containsGlobalPlayer(p.getName()))
                {
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (a.getArenaState() != ArenaState.INGAME && a.getArcadeInstance() == null && !a.getAlwaysPvP())
                    {
                        event.setCancelled(true);
                    }
                    if (this.pli.blood_effects && (a.getArenaState() == ArenaState.INGAME || a.getAlwaysPvP()) && !a.isArcadeMain())
                    {
                        Effects.playBloodEffect(p);
                    }
                }
                this.pli.getSpectatorManager();
                if (this.pli.containsGlobalLost(p.getName()) || SpectatorManager.isSpectating(p))
                {
                    event.setCancelled(true);
                }
            }
            else if (event.getCause().equals(DamageCause.FALL))
            {
                if (this.pli.containsGlobalPlayer(p.getName()))
                {
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (a.getArenaState() != ArenaState.INGAME && a.getArcadeInstance() != null)
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            final Player p = (Player) event.getEntity();
            Player attacker = null;
            if (event.getDamager() instanceof Projectile)
            {
                final Projectile projectile = (Projectile) event.getDamager();
                if (projectile.getShooter() instanceof Player)
                {
                    attacker = (Player) projectile.getShooter();
                }
            }
            else if (event.getDamager() instanceof Player)
            {
                attacker = (Player) event.getDamager();
            }
            else
            {
                return;
            }
            
            if (p != null && attacker != null)
            {
                if (this.pli.containsGlobalPlayer(p.getName()) && this.pli.containsGlobalPlayer(attacker.getName()))
                {
                    this.pli.getSpectatorManager();
                    if (SpectatorManager.isSpectating(p))
                    {
                        if (event.getDamager() instanceof Arrow)
                        {
                            p.teleport(p.getLocation().add(0, 3D, 0));
                            
                            final Arrow arr = attacker.launchProjectile(Arrow.class);
                            arr.setShooter(attacker);
                            arr.setVelocity(((Arrow) event.getDamager()).getVelocity());
                            arr.setBounce(false);
                            
                            event.setCancelled(true);
                            event.getDamager().remove();
                        }
                        else if (event.getDamager() instanceof Egg)
                        {
                            p.teleport(p.getLocation().add(0, 3D, 0));
                            
                            final Egg egg = attacker.launchProjectile(Egg.class);
                            egg.setShooter(attacker);
                            egg.setVelocity(((Egg) event.getDamager()).getVelocity());
                            egg.setBounce(false);
                            
                            event.setCancelled(true);
                            event.getDamager().remove();
                        }
                        else if (event.getDamager() instanceof Snowball)
                        {
                            p.teleport(p.getLocation().add(0, 3D, 0));
                            
                            final Snowball sb = attacker.launchProjectile(Snowball.class);
                            sb.setShooter(attacker);
                            sb.setVelocity(((Snowball) event.getDamager()).getVelocity());
                            sb.setBounce(false);
                            
                            event.setCancelled(true);
                            event.getDamager().remove();
                        }
                        else if (event.getDamager() instanceof EnderPearl)
                        {
                            p.teleport(p.getLocation().add(0, 3D, 0));
                            
                            final EnderPearl sb = attacker.launchProjectile(EnderPearl.class);
                            sb.setShooter(attacker);
                            sb.setVelocity(((EnderPearl) event.getDamager()).getVelocity());
                            sb.setBounce(false);
                            
                            event.setCancelled(true);
                            event.getDamager().remove();
                        }
                        event.setCancelled(true);
                        return;
                    }
                    if (this.pli.containsGlobalLost(attacker.getName()))
                    {
                        event.setCancelled(true);
                        return;
                    }
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.setLastDamager(p.getName(), attacker.getName());
                        if (this.pli.damage_identifier_effects)
                        {
                            ChatColor c = ChatColor.YELLOW;
                            if (event.getDamage() >= 5D)
                            {
                                c = ChatColor.GOLD;
                            }
                            if (event.getDamage() >= 9D)
                            {
                                c = ChatColor.RED;
                            }
                            Effects.playHologram(attacker, p.getLocation(), c + Double.toString(event.getDamage()), true, true);
                        }
                    }
                    else if (a.getArenaState() == ArenaState.RESTARTING)
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event)
    {
        final Player p = event.getPlayer();
        if (this.pli.containsGlobalPlayer(p.getName()))
        {
            final Arena a = this.pli.global_players.get(p.getName());
            if (a.getArenaState() != ArenaState.INGAME || this.pli.containsGlobalLost(p.getName()))
            {
                event.setCancelled(true);
                return;
            }
            this.pli.getSpectatorManager();
            if (SpectatorManager.isSpectating(p))
            {
                event.setCancelled(true);
                return;
            }
            a.getSmartReset().addChanged(event.getBlock(), event.getBlock().getType().equals(Material.CHEST), ChangeCause.BREAK);
            if (event.getBlock().getType() == Material.DOUBLE_PLANT)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, -1D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, -1D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock().getType().equals(Material.CHEST));
            }
            if (event.getBlock().getType() == Material.SNOW || event.getBlock().getType() == Material.SNOW_BLOCK)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +3D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +3D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +2D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +2D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock().getType().equals(Material.CHEST));
            }
            if (event.getBlock().getType() == Material.CARPET)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +3D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +3D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +2D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +2D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock().getType().equals(Material.CHEST));
            }
            if (event.getBlock().getType() == Material.CACTUS)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +4D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +4D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +3D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +3D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +2D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +2D, 0D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(0D, +1D, 0D).getBlock().getType().equals(Material.CHEST));
            }
            if (event.getBlock().getType() == Material.BED_BLOCK)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(1D, 0D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(1D, 0D, 1D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(-1D, 0D, 0D).getBlock(),
                        event.getBlock().getLocation().clone().add(1D, 0D, -1D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, 0D, 1D).getBlock(),
                        event.getBlock().getLocation().clone().add(-1D, 0D, 1D).getBlock().getType().equals(Material.CHEST));
                a.getSmartReset().addChanged(event.getBlock().getLocation().clone().add(0D, 0D, -1D).getBlock(),
                        event.getBlock().getLocation().clone().add(-1D, 0D, -1D).getBlock().getType().equals(Material.CHEST));
            }
        }
        if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN)
        {
            final Arena arena = Util.getArenaBySignLocation(this.pl, event.getBlock().getLocation());
            if (arena != null)
            {
                this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena.getInternalName() + ".sign", null);
                this.pli.getArenasConfig().saveConfig();
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
    {
        if (this.pli.containsGlobalPlayer(event.getPlayer().getName()))
        {
            final Arena a = this.pli.global_players.get(event.getPlayer().getName());
            final Block start = event.getBlockClicked();
            if (!a.getBoundaries().containsLocWithoutY(start.getLocation()))
            {
                event.setCancelled(true);
                return;
            }
            for (int x = -2; x < 2; x++)
            {
                for (int y = -2; y < 2; y++)
                {
                    for (int z = -2; z < 2; z++)
                    {
                        final Block b = start.getLocation().clone().add(x, y, z).getBlock();
                        a.getSmartReset().addChanged(b, b.getType().equals(Material.CHEST));
                    }
                }
            }
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        final Player p = event.getPlayer();
        if (this.pli.containsGlobalPlayer(p.getName()))
        {
            final Arena a = this.pli.global_players.get(p.getName());
            this.pli.getSpectatorManager();
            if (a.getArenaState() != ArenaState.INGAME || this.pli.containsGlobalLost(p.getName()) || SpectatorManager.isSpectating(p))
            {
                event.setCancelled(true);
                return;
            }
            // TODO Is this clever? We should add every block change to smart reset.
            // currently BedWars has its own onBlockPlace
            if (event.getBlockReplacedState().getType() != Material.AIR)
            {
                a.getSmartReset().addChanged(event.getBlock().getLocation(), event.getBlockReplacedState().getType(), event.getBlockReplacedState().getData().getData());
            }
        }
        if (this.pli.getStatsInstance().skullsetup.contains(p.getName()))
        {
            if (event.getBlock().getType() == Material.SKULL_ITEM || event.getBlock().getType() == Material.SKULL)
            {
                if (event.getItemInHand().hasItemMeta())
                {
                    this.pli.getStatsInstance().saveSkull(event.getBlock().getLocation(), Integer.parseInt(event.getItemInHand().getItemMeta().getDisplayName()));
                    this.pli.getStatsInstance().skullsetup.remove(p.getName());
                }
            }
        }
    }
    
    // *****************
    // ***** sign events
    // *****************
    
    
    @EventHandler
    public void onSignUse(final PlayerInteractEvent event)
    {
        if (event.hasBlock())
        {
            if (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN)
            {
                if (event.getClickedBlock().getType() == Material.FIRE)
                {
                    return;
                }
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                {
                    return;
                }
                final Sign s = (Sign) event.getClickedBlock().getState();
                // people will most likely do strange formats, so let's just try
                // to get signs by location rather than locally by reading the sign
                final Arena arena = Util.getArenaBySignLocation(this.pl, event.getClickedBlock().getLocation());
                if (arena != null)
                {
                    final Player p = event.getPlayer();
                    if (!arena.containsPlayer(p.getName()))
                    {
                        arena.joinPlayerLobby(p.getName());
                        Util.updateSign(this.pl, arena);
                    }
                    else
                    {
                        Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().you_already_are_in_arena.replaceAll("<arena>", arena.getInternalName()));
                    }
                }
                else
                {
                    // try getting random sign
                    final Location l = Util.getComponentForArenaRaw(this.pl, "random", "sign");
                    if (this.checkLocationMatchesSign(l, s))
                    {
                        for (final Arena a : this.pli.getArenas())
                        {
                            if (a.getArenaState() == ArenaState.JOIN || a.getArenaState() == ArenaState.STARTING)
                            {
                                if (!a.containsPlayer(event.getPlayer().getName()))
                                {
                                    a.joinPlayerLobby(event.getPlayer().getName());
                                    Util.updateSign(this.pl, a);
                                    return;
                                }
                            }
                        }
                    }
                    // try getting leave sign
                    if (this.pli.containsGlobalPlayer(event.getPlayer().getName()))
                    {
                        if (this.pli.getArenasConfig().getConfig().isSet("arenas.leave"))
                        {
                            for (final String str : this.pli.getArenasConfig().getConfig().getConfigurationSection("arenas.leave.").getKeys(false))
                            {
                                final Location loc = Util.getComponentForArenaRaw(this.pl, "leave." + str, "sign");
                                if (this.checkLocationMatchesSign(loc, s))
                                {
                                    this.pli.global_players.get(event.getPlayer().getName()).leavePlayer(event.getPlayer().getName(), false, false);
                                    Util.updateSign(this.pl, this.pli.getArenaByName("leave"));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            else if (event.getClickedBlock().getType() == Material.CHEST)
            {
                final Player p = event.getPlayer();
                if (this.pli.containsGlobalPlayer(p.getName()))
                {
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.getSmartReset().addChanged(event.getClickedBlock(), true);
                    }
                }
            }
            else if (event.getClickedBlock().getType() == Material.TNT)
            {
                final Player p = event.getPlayer();
                if (this.pli.containsGlobalPlayer(p.getName()))
                {
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.getSmartReset().addChanged(event.getClickedBlock(), false);
                        // TODO maybe add radius of blocks around this tnt manually
                    }
                }
            }
            else if (event.getPlayer().getItemInHand().getType() == Material.WATER_BUCKET || event.getPlayer().getItemInHand().getType() == Material.WATER
                    || event.getPlayer().getItemInHand().getType() == Material.LAVA_BUCKET || event.getPlayer().getItemInHand().getType() == Material.LAVA)
            {
                final Player p = event.getPlayer();
                if (this.pli.containsGlobalPlayer(p.getName()))
                {
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (!a.getBoundaries().containsLocWithoutY(event.getClickedBlock().getLocation()))
                    {
                        event.setCancelled(true);
                        return;
                    }
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.getSmartReset().addChanged(event.getClickedBlock(), event.getClickedBlock().getType().equals(Material.CHEST));
                        // a.getSmartReset().addChanged(event.getClickedBlock().getLocation().add(0D, 1D, 0D));
                    }
                }
            }
            else if (event.getClickedBlock().getType() == Material.DISPENSER || event.getClickedBlock().getType() == Material.DROPPER)
            {
                final Player p = event.getPlayer();
                if (this.pli.containsGlobalPlayer(p.getName()))
                {
                    final Arena a = this.pli.global_players.get(p.getName());
                    if (a.getArenaState() == ArenaState.INGAME)
                    {
                        a.getSmartReset().addChanged(event.getClickedBlock(), false);
                    }
                }
            }
        }
        
        this.pli.getSpectatorManager();
        if (this.pli.containsGlobalLost(event.getPlayer().getName()) || SpectatorManager.isSpectating(event.getPlayer()))
        {
            event.setCancelled(true);
        }
        
        if (event.hasItem())
        {
            final Player p = event.getPlayer();
            if (!this.pli.containsGlobalPlayer(p.getName()))
            {
                return;
            }
            final Arena a = this.pli.global_players.get(p.getName());
            if (a.isArcadeMain())
            {
                return;
            }
            if (event.getItem().getTypeId() == this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_CLASS_SELECTION_ITEM))
            {
                if (a.getArenaState() != ArenaState.INGAME)
                {
                    this.pli.getClassesHandler().openGUI(p.getName());
                    event.setCancelled(true);
                }
            }
            else if (event.getItem().getTypeId() == this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_EXIT_ITEM))
            {
                if (a.getArenaState() != ArenaState.INGAME)
                {
                    a.leavePlayer(p.getName(), false, false);
                    event.setCancelled(true);
                }
                else
                {
                    if (this.pli.containsGlobalLost(p.getName()))
                    {
                        a.leavePlayer(p.getName(), false, false);
                        event.setCancelled(true);
                    }
                }
            }
            else if (event.getItem().getTypeId() == this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_SPECTATOR_ITEM))
            {
                if (this.pli.containsGlobalLost(p.getName()))
                {
                    this.pli.getSpectatorManager().openSpectatorGUI(p, a);
                    event.setCancelled(true);
                }
            }
            else if (event.getItem().getTypeId() == this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_ACHIEVEMENT_ITEMS))
            {
                if (this.pli.isAchievementGuiEnabled())
                {
                    if (a.getArenaState() != ArenaState.INGAME)
                    {
                        this.pli.getArenaAchievements().openGUI(p.getName(), false);
                        event.setCancelled(true);
                    }
                }
            }
            else if (event.getItem().getTypeId() == this.pl.getConfig().getInt(ArenaConfigStrings.CONFIG_SHOP_SELECTION_ITEM))
            {
                if (a.getArenaState() != ArenaState.INGAME)
                {
                    this.pli.getShopHandler().openGUI(p.getName());
                    event.setCancelled(true);
                }
            }
            else if (event.getItem().getTypeId() == this.pl.getConfig()
                    .getInt(ArenaConfigStrings.CONFIG_EXTRA_LOBBY_ITEM_PREFIX + "item0" + ArenaConfigStrings.CONFIG_EXTRA_LOBBY_ITEM_ITEM_SUFFIX))
            {
                if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_EXTRA_LOBBY_ITEM_PREFIX + "item0" + ArenaConfigStrings.CONFIG_EXTRA_LOBBY_ITEM_ENABLED_SUFFIX))
                {
                    if (a.getArenaState() != ArenaState.INGAME)
                    {
                        p.performCommand(this.pl.getConfig().getString(ArenaConfigStrings.CONFIG_EXTRA_LOBBY_ITEM_PREFIX + "item0" + ArenaConfigStrings.CONFIG_EXTRA_LOBBY_ITEM_COMMAND_SUFFIX));
                    }
                }
            }
            if (event.getItem().getType() == Material.COMPASS)
            {
                if (a.getArenaState() == ArenaState.INGAME)
                {
                    if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_COMPASS_TRACKING_ENABLED))
                    {
                        final CompassPlayer temp = Util.getNearestPlayer(p, a);
                        if (temp.getPlayer() != null)
                        {
                            p.sendMessage(this.pli.getMessagesConfig().compass_player_found.replaceAll("<player>", temp.getPlayer().getName()).replaceAll("<distance>",
                                    Integer.toString((int) Math.round(temp.getDistance()))));
                            p.setCompassTarget(temp.getPlayer().getLocation());
                        }
                        else
                        {
                            p.sendMessage(this.pli.getMessagesConfig().compass_no_player_found);
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onSignChange(final SignChangeEvent event)
    {
        final Player p = event.getPlayer();
        if (event.getLine(0).toLowerCase().equalsIgnoreCase(this.getName()))
        {
            if (event.getPlayer().hasPermission(MinigamesAPI.getAPI().getPermissionPrefix() + ".sign") || event.getPlayer().isOp())
            {
                if (!event.getLine(1).equalsIgnoreCase(""))
                {
                    final String arena = event.getLine(1);
                    if (arena.equalsIgnoreCase("random"))
                    {
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.world", p.getWorld().getName());
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.location.x", event.getBlock().getLocation().getBlockX());
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.location.y", event.getBlock().getLocation().getBlockY());
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.location.z", event.getBlock().getLocation().getBlockZ());
                        this.pli.getArenasConfig().saveConfig();
                        Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().successfully_set.replaceAll("<component>", "arena (random) sign"));
                        Util.updateSign(this.pl, event, arena);
                    }
                    else if (arena.equalsIgnoreCase("leave"))
                    {
                        int count = 0;
                        if (this.pli.getArenasConfig().getConfig().isSet("arenas.leave"))
                        {
                            for (final String s : this.pli.getArenasConfig().getConfig().getConfigurationSection("arenas.leave.").getKeys(false))
                            {
                                count++;
                            }
                        }
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + "." + count + ".sign.world", p.getWorld().getName());
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + "." + count + ".sign.location.x", event.getBlock().getLocation().getBlockX());
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + "." + count + ".sign.location.y", event.getBlock().getLocation().getBlockY());
                        this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + "." + count + ".sign.location.z", event.getBlock().getLocation().getBlockZ());
                        this.pli.getArenasConfig().saveConfig();
                        Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().successfully_set.replaceAll("<component>", "arena (leave) sign"));
                        Util.updateSign(this.pl, event, arena);
                    }
                    else
                    {
                        if (Validator.isArenaValid(this.pl, arena))
                        {
                            this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.world", p.getWorld().getName());
                            this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.loc.x", event.getBlock().getLocation().getBlockX());
                            this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.loc.y", event.getBlock().getLocation().getBlockY());
                            this.pli.getArenasConfig().getConfig().set(ArenaConfigStrings.ARENAS_PREFIX + arena + ".sign.loc.z", event.getBlock().getLocation().getBlockZ());
                            this.pli.getArenasConfig().saveConfig();
                            Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().successfully_set.replaceAll("<component>", "arena sign"));
                        }
                        else
                        {
                            Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().arena_invalid.replaceAll("<arena>", arena));
                            event.getBlock().breakNaturally();
                        }
                        
                        final Arena a = this.pli.getArenaByName(arena);
                        if (a != null)
                        {
                            a.setSignLocation(event.getBlock().getLocation());
                            Util.updateSign(this.pl, a, event);
                        }
                        else
                        {
                            Util.sendMessage(this.pl, p, this.pli.getMessagesConfig().arena_not_initialized);
                        }
                    }
                }
            }
        }
    }
    
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event)
    {
        final Player p = event.getPlayer();
        this.pli.getStatsInstance().update(p.getName());
        if (this.pli.containsGlobalPlayer(p.getName()))
        {
            this.pli.global_players.remove(p.getName());
        }
        if (this.pli.containsGlobalLost(p.getName()))
        {
            this.pli.global_lost.remove(p.getName());
        }
        if (this.pl.getConfig().isSet("temp.left_players." + p.getName()))
        {
            Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
                Util.teleportPlayerFixed(p, Util.getMainLobby(ArenaListener.this.pl));
                p.setFlying(false);
                try
                {
                    p.getInventory().clear();
                    p.updateInventory();
                    if (ArenaListener.this.pl.getConfig().isSet("temp.left_players." + p.getName() + ".items"))
                    {
                        for (final String key : ArenaListener.this.pl.getConfig().getConfigurationSection("temp.left_players." + p.getName() + ".items").getKeys(false))
                        {
                            p.getInventory().addItem(ArenaListener.this.pl.getConfig().getItemStack("temp.left_players." + p.getName() + ".items." + key));
                        }
                    }
                    final Arena arena = ArenaListener.this.pli.global_players.get(p.getName());
                    p.updateInventory();
                    p.setWalkSpeed(0.2F);
                    p.removePotionEffect(PotionEffectType.JUMP);
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    p.setGameMode(GameMode.SURVIVAL);
                    Util.updateSign(ArenaListener.this.pl, arena);
                    ArenaListener.this.pli.getSpectatorManager().setSpectate(p, false);
                }
                catch (final Exception e)
                {
                    MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "exception", e);
                    Util.sendMessage(ArenaListener.this.pl, p, ChatColor.RED + "Failed restoring your stuff. Did the server restart/reload while you were offline?");
                }
                ArenaListener.this.pl.getConfig().set("temp.left_players." + p.getName(), null);
                ArenaListener.this.pl.saveConfig();
            }, 5);
        }
        
        if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_BUNGEE_GAME_ON_JOIN))
        {
            int c = 0;
            final List<String> arenas = new ArrayList<>();
            for (final String arena : this.pli.getArenasConfig().getConfig().getKeys(false))
            {
                if (!arena.equalsIgnoreCase("mainlobby") && !arena.equalsIgnoreCase("strings") && !arena.equalsIgnoreCase("config"))
                {
                    c++;
                    arenas.add(arena);
                }
            }
            if (c < 1)
            {
                MinigamesAPI.getAPI().getLogger().severe("Couldn't find any arena even though game_on_join was turned on. Please setup an arena to fix this!");
                return;
            }
            
            Bukkit.getScheduler().runTaskLater(MinigamesAPI.getAPI(), () -> {
                if (p != null)
                {
                    ArenaListener.this.pli.getArenas().get(0).joinPlayerLobby(p.getName());
                    final Arena arena = ArenaListener.this.pli.global_players.get(p.getName());
                    Util.updateSign(ArenaListener.this.pl, arena);
                }
            }, 30L);
        }
    }
    
    
    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event)
    {
        if (this.pli.containsGlobalPlayer(event.getPlayer().getName()))
        {
            final Arena arena = this.pli.global_players.get(event.getPlayer().getName());
            MinigamesAPI.getAPI().getLogger().info(event.getPlayer().getName() + " quit while in arena " + arena.getInternalName() + ".");
            int count = 0;
            for (final String p_ : this.pli.global_players.keySet())
            {
                if (this.pli.global_players.get(p_).getInternalName().equalsIgnoreCase(arena.getInternalName()))
                {
                    count++;
                }
            }
            
            arena.leavePlayer(event.getPlayer().getName(), true, false);
            
            try
            {
                Util.updateSign(this.pl, arena);
            }
            catch (final Exception e)
            {
                MinigamesAPI.getAPI().getLogger().log(Level.WARNING, "Error occurred while refreshing sign. ", e);
            }
        }
        if (MinigamesAPI.getAPI().globalParty.containsKey(event.getPlayer().getName()))
        {
            MinigamesAPI.getAPI().globalParty.get(event.getPlayer().getName()).disband();
        }
        Party party_ = null;
        for (final Party party : MinigamesAPI.getAPI().globalParty.values())
        {
            if (party.containsPlayer(event.getPlayer().getUniqueId()))
            {
                party_ = party;
                break;
            }
        }
        if (party_ != null)
        {
            party_.removePlayer(event.getPlayer().getUniqueId());
        }
    }
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(final AsyncPlayerChatEvent event)
    {
        final Player p = event.getPlayer();
        if (!this.pli.chat_enabled)
        {
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                event.setCancelled(true);
                return;
            }
        }
        if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_CHAT_SHOW_SCORE_IN_ARENA))
        {
            if (this.pli.containsGlobalPlayer(event.getPlayer().getName()))
            {
                event.setFormat(ChatColor.GRAY + "[" + ChatColor.GREEN + this.pli.getStatsInstance().getPoints(event.getPlayer().getName()) + ChatColor.GRAY + "] " + event.getFormat());
            }
        }
        if (this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_CHAT_PER_ARENA_ONLY))
        {
            if (this.pli.containsGlobalPlayer(p.getName()))
            {
                final String msg = String.format(event.getFormat(), p.getName(), event.getMessage());
                for (final Player receiver : event.getRecipients())
                {
                    if (this.pli.containsGlobalPlayer(receiver.getName()))
                    {
                        if (this.pli.global_players.get(receiver.getName()) == this.pli.global_players.get(p.getName()))
                        {
                            receiver.sendMessage("§7" + msg);
                        }
                    }
                }
                event.setCancelled(true);
            }
        }
    }
    
    
    @EventHandler
    public void onPlayerCommandPreprocessEvent(final PlayerCommandPreprocessEvent event)
    {
        if (event.getMessage().equalsIgnoreCase(this.leave_cmd) || event.getMessage().equalsIgnoreCase("/l"))
        {
            if (this.pli.containsGlobalPlayer(event.getPlayer().getName()))
            {
                final Arena arena = this.pli.global_players.get(event.getPlayer().getName());
                arena.leavePlayer(event.getPlayer().getName(), false, false);
                event.setCancelled(true);
                return;
            }
        }
        if (this.pli.containsGlobalPlayer(event.getPlayer().getName()) && !event.getPlayer().isOp())
        {
            if (!this.pl.getConfig().getBoolean(ArenaConfigStrings.CONFIG_DISABLE_COMMANDS_IN_ARENA))
            {
                return;
            }
            if (this.pl.getConfig().getString(ArenaConfigStrings.CONFIG_COMMAND_WHITELIST).toLowerCase().contains(event.getMessage().toLowerCase()))
            {
                return;
            }
            boolean cont = false;
            for (final String cmd : this.cmds)
            {
                if (event.getMessage().toLowerCase().startsWith(cmd.toLowerCase()))
                {
                    cont = true;
                }
            }
            if (!cont)
            {
                Util.sendMessage(this.pl, event.getPlayer(), this.pli.getMessagesConfig().you_can_leave_with.replaceAll("<cmd>", this.leave_cmd));
                event.setCancelled(true);
                return;
            }
        }
    }
    
    
    // TP Fix start
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event)
    {
        if (event.getCause().equals(TeleportCause.UNKNOWN) && this.pli.spectator_mode_1_8)
        {
            // Don't hide/show players when 1.8 spectator mode is enabled
            return;
        }
        final Player player = event.getPlayer();
        if (this.pli.containsGlobalPlayer(player.getName()))
        {
            final int visibleDistance = 16;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.pl, () -> {
                final List<Player> nearby = ArenaListener.this.getPlayersWithin(player, visibleDistance);
                ArenaListener.this.updateEntities(nearby, false);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ArenaListener.this.pl, () -> ArenaListener.this.updateEntities(nearby, true), 1);
            }, 5L);
        }
    }
    
    
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent evt)
    {
        for (final Arena arena : this.pli.getArenas())
        {
            Cuboid c = arena.getBoundaries();
            if (c != null && c.containsLoc(evt.getLocation()))
            {
                evt.setCancelled(true);
                return;
            }
            c = arena.getLobbyBoundaries();
            if (c != null && c.containsLoc(evt.getLocation()))
            {
                evt.setCancelled(true);
                return;
            }
            c = arena.getSpecBoundaries();
            if (c != null && c.containsLoc(evt.getLocation()))
            {
                evt.setCancelled(true);
                return;
            }
        }
    }
    
    
    @EventHandler
    public void onMobTarget(EntityTargetEvent evt)
    {
        if (!(evt.getEntity() instanceof Player) && evt.getTarget() instanceof Player)
        {
            final Player target = (Player) evt.getTarget();
            if (this.pli.containsGlobalPlayer(target.getName()))
            {
                evt.setCancelled(true);
            }
        }
    }
    
    // *************************
    // ***** helpers / utilities
    // *************************
    
    
    @Deprecated
    public static boolean isSpectating(final Player p)
    {
        return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("spectators").hasPlayer(p);
    }
    
    
    private List<Entity> getEntitiesByLocation(final Location loc, final double d)
    {
        final List<Entity> ent = new ArrayList<>();
        for (final Entity e : loc.getWorld().getEntities())
        {
            if (e.getLocation().distanceSquared(loc) <= d)
            {
                ent.add(e);
            }
        }
        return ent;
    }
    
    
    private boolean checkLocationMatchesSign(final Location l, final Sign s)
    {
        if (l != null)
        {
            if (l.getWorld() != null)
            {
                if (l.getWorld().getName().equalsIgnoreCase(s.getLocation().getWorld().getName()))
                {
                    if (l.distance(s.getLocation()) < 1)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void updateEntities(final List<Player> players, final boolean visible)
    {
        for (final Player observer : players)
        {
            for (final Player player : players)
            {
                if (observer.getEntityId() != player.getEntityId())
                {
                    if (visible && !this.pli.containsGlobalLost(player.getName()))
                    {
                        observer.showPlayer(player);
                    }
                    else
                    {
                        observer.hidePlayer(player);
                    }
                }
            }
        }
    }
    
    private List<Player> getPlayersWithin(final Player player, final int distance)
    {
        final List<Player> res = new ArrayList<>();
        final int d2 = distance * distance;
        for (final Player p : Bukkit.getServer().getOnlinePlayers())
        {
            if (p.getWorld() == player.getWorld() && p.getLocation().distanceSquared(player.getLocation()) <= d2)
            {
                res.add(p);
            }
        }
        return res;
    }
    
    public String getName()
    {
        return this.minigame;
    }
    
    public void setName(final String minigame)
    {
        this.minigame = minigame;
    }

}
