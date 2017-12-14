package main.java.me.aaron.saltygamesapi.guns;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Gun {
	
	public double speed = 1D;
	public int shoot_amount = 1;
	public int max_durability = 50;
	public int durability = 50;
	public Class<? extends Projectile> bullet = Egg.class;
	public JavaPlugin pl;
	public double knockback_multiplier = 1.1D;
	public String name = "gun";
	
	boolean canshoot = true;
	public HashMap<String, Boolean> canshoot_ = new HashMap<>();
	
	ArrayList<ItemStack> items;
	ArrayList<ItemStack> icon;
	
	public Gun(final JavaPlugin pl, final String name, final double speed, final int shoot_amount, final int durability, final double knockback_multiplier,
            final Class<? extends Projectile> bullet, final ArrayList<ItemStack> items, final ArrayList<ItemStack> icon) {
		
		this.speed = speed;
		this.shoot_amount = shoot_amount;
		this.max_durability = durability;
		this.durability = durability;
		this.bullet = bullet;
		this.pl = pl;
		this.knockback_multiplier = knockback_multiplier;
		this.name = name;
		this.items = items;
		this.icon = icon;
		
		if (name.equalsIgnoreCase("grenade")) {
			this.bullet = Snowball.class;
		}
	}
	
	public Gun(final JavaPlugin pl, final ArrayList<ItemStack> items, final ArrayList<ItemStack> icon) {
		this(pl, "Gun", 1D, 1, 50, 1.1D, Egg.class, items, icon);
	}
	
	public void shoot(final Player p) {
		if (this.canshoot) {
			for (int i = 0; i < shoot_amount; i++) {
				p.launchProjectile(bullet);
				durability -= 1;
			}
			canshoot = false;
			Bukkit.getScheduler().runTaskLater(pl, () -> Gun.this.canshoot = true, (long) (20D / speed));
		}
	}
	
	public void shoot(final Player p, final int shoot_amount, final int durability, final int speed) {
		if (canshoot_.containsKey(p.getName())) {
			canshoot_.put(p.getName(), true);
		}
		if (canshoot_.get(p.getName())) {
			for (int i = 0; i < shoot_amount + 1; i++) {
				p.launchProjectile(bullet);
				this.durability -= (int) (10D / durability);
			}
			canshoot_.put(p.getName(), false);
			Bukkit.getScheduler().runTaskLater(pl, () -> Gun.this.canshoot_.put(p.getName(), true), (long) (60D / speed));
		}
	}
	
	public void onHit(final Entity ent, final int knockback_multiplier) {
		if (name.equalsIgnoreCase("freeze")) {
			final Player p = (Player) ent;
			p.setWalkSpeed(0.0F);
			Bukkit.getScheduler().runTaskLater(pl, () -> p.setWalkSpeed(0.2F), 20L * 20L * knockback_multiplier);
		} else {
			ent.setVelocity(ent.getLocation().getDirection().multiply((-1D) * knockback_multiplier));
		}
	}
	
	public void reloadGun() {
		durability = max_durability;
		canshoot = true;
	}

}
