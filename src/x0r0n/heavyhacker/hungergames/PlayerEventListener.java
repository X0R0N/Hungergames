package x0r0n.heavyhacker.hungergames;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Fireball;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerEventListener implements Listener {
	private Hungergames hunger;
	Random random = new Random();
	private int rand;
	private double spawnrange;
	private double spawnheight;

	PlayerEventListener(Hungergames g) {
		hunger=g;
		spawnrange = hunger.config.getDouble("settings.spawnrange",60.0);
		spawnheight = hunger.config.getVector("capsules.1",new Vector(182,50,266)).getY()+10;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {		
		for(CapsuleInformation c: hunger.capsuleInformation) {
			if(!hunger.IsRunning()) {
				Location loc=new Location(event.getPlayer().getWorld(),c.GetVector().getX(),c.GetVector().getY(),c.GetVector().getZ());
				if(event.getTo().distance(loc)<1 && c.GetPlayer()==null) {
					c.InsertPlayer(event.getPlayer());
					break;
				}
			} else {
				Location loc=new Location(event.getPlayer().getWorld(),c.GetVector().getX(),c.GetVector().getY()+14.0,c.GetVector().getZ());
				if(event.getFrom().distance(loc)<1) {
					loc.add(new Vector(0,-1,0));
					if(loc.getBlock().getType()==Material.AIR) {
						loc.getBlock().setType(Material.IRON_FENCE);
						hunger.Launch(event.getPlayer(), -0.05);
						event.getPlayer().setHealth(20);
						event.getPlayer().setFoodLevel(20);
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntityType()==EntityType.PLAYER) {
			if (!hunger.IsRunning()) {
				event.setCancelled(true);
			} else if (((LivingEntity) event.getEntity()).getHealth() <= event.getDamage()) {
				hunger.die((Player) event.getEntity());
				event.setCancelled(true);
			} else if (event.getEntity().getLocation().distance(event.getEntity().getWorld().getSpawnLocation())<spawnrange && event.getEntity().getLocation().getY()<spawnheight) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreateSign(SignChangeEvent sign) {
		for (int i=0;i<4;++i) {
			if (sign.getLine(i).equalsIgnoreCase("[MAGIC]")) {
				sign.setLine(0,ChatColor.MAGIC+"45t34zsuh45a");
				sign.setLine(1,ChatColor.GREEN+" eddy123 ");
				sign.setLine(2,ChatColor.GREEN+"HolyWater");
				sign.setLine(3,ChatColor.MAGIC+"45t34zsuh45a");
			}
		}
	}
}
