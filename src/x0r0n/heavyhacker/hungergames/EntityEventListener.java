package x0r0n.heavyhacker.hungergames;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

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
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPVP(EntityDamageByEntityEvent event) {
		if(!hunger.IsRunning()) {
			event.setCancelled(true);
			return;
		}
		if(event.getDamager().getType()!=EntityType.PLAYER && event.getEntityType()==EntityType.PLAYER) {
			for(CapsuleInformation c: hunger.capsuleInformation) {
				c.IsPlaying();
				Player p=c.GetPlayer();
				if(p!=null) {continue;}
				if(p.getEntityId()==event.getEntity().getEntityId() && c.IsPlaying()) {
					return;
				}
			}
		} else if(event.getDamager().getType()==EntityType.PLAYER) {
			for(CapsuleInformation c: hunger.capsuleInformation) {
				c.IsPlaying();
				Player p=c.GetPlayer();
				if(p!=null) {continue;}
				if(p.getEntityId()==event.getDamager().getEntityId() && c.IsPlaying()) {
					return;
				}
			}
		}
		event.setCancelled(true);
	}
}