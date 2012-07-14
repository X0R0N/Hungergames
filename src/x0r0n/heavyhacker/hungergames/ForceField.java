package x0r0n.heavyhacker.hungergames;

import org.bukkit.entity.Player;
import org.bukkit.entity.Fireball;
import org.bukkit.Effect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import java.util.Random;
import org.bukkit.util.Vector;

public class ForceField implements java.lang.Runnable {
	private double mapRange1;
	private double mapRange2;
	private double mapRange3;
	Random random = new Random();
	private int rand;
	private Hungergames hunger;
	
	ForceField(Hungergames g) {
		hunger=g;
		mapRange1=hunger.config.getDouble("maprange.warn_Player",450.0);
		mapRange2=hunger.config.getDouble("maprange.attack_player",470.0);
		mapRange3=hunger.config.getDouble("maprange.kill_player",500.0);
	}
	
	public void run() {	
		for(Player p: hunger.getServer().getWorlds().get(0).getPlayers()) {
			if(p.getLocation().distance(p.getWorld().getSpawnLocation())>mapRange3) {
				p.getWorld().strikeLightningEffect(p.getLocation());
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(2,-1,0)));
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(-2,-1,0)));
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(0,-1,2)));
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(0,-1,-2)));
				p.getWorld().createExplosion(p.getLocation(), 0);
				hunger.die(p);
				p.sendMessage(ChatColor.RED + "[INFO] You got warned!");
			} else if(p.getLocation().distance(p.getWorld().getSpawnLocation())>mapRange2) {
				p.getWorld().strikeLightningEffect(p.getLocation());
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(2,-1,0)));
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(-2,-1,0)));
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(0,-1,2)));
				p.getWorld().strikeLightningEffect(p.getLocation().add(new Vector(0,-1,-2)));
				p.getWorld().createExplosion(p.getLocation(), 0);
				p.sendMessage(ChatColor.RED + "[WARNING] Better go back!");
				if (p.getHealth()>3) {
					p.damage(3);
				} else {
					hunger.die(p);
				}
			} else if(p.getLocation().distance(p.getWorld().getSpawnLocation())>mapRange1) {
				p.sendMessage(ChatColor.RED + "[WARNING] You are near the force field!");
			}
		}
	}
}
