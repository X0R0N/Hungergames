package x0r0n.heavyhacker.hungergames;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityEventListener implements Listener {
	private Hungergames hunger;
	private double spawnRange;
	EntityEventListener(Hungergames g) {
		hunger=g;
		spawnRange=g.config.getDouble("settings.spawnrange",60.0);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onExplode(ExplosionPrimeEvent event) {
		if(Math.abs(event.getEntity().getLocation().distance(event.getEntity().getWorld().getSpawnLocation()))<spawnRange) {
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(),0);
			event.getEntity().remove();
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if(event.getEntityType()==EntityType.OCELOT) {
			event.setCancelled(true);
		}
		if(event.getEntityType()==EntityType.SHEEP) {
			event.setCancelled(true);
		}
		if(event.getEntityType()==EntityType.SQUID) {
			event.setCancelled(true);
		}
		if(event.getEntityType()==EntityType.SLIME) {
			event.setCancelled(true);
		}
	}
}