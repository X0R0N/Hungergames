package x0r0n.heavyhacker.hungergames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class BlockEventListener implements Listener {
	private Hungergames hunger;
	private double spawnRange;
	BlockEventListener(Hungergames g) {
		hunger=g;
		spawnRange=g.config.getDouble("settings.spawnrange",60.0);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.getPlayer().isOp() && Math.abs(event.getBlock().getLocation().distance(event.getBlock().getWorld().getSpawnLocation()))<spawnRange) {
			event.setCancelled(true);
		} else {
			//faplog
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.getPlayer().isOp() && Math.abs(event.getBlock().getLocation().distance(event.getBlock().getWorld().getSpawnLocation()))<spawnRange) {
			event.setCancelled(true);
		} else {
			//faplog
		}
	}
}
