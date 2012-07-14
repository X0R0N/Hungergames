package x0r0n.heavyhacker.hungergames;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

public class ServerAttackPlayers {
	private Hungergames hunger;
	ServerAttackPlayers(Hungergames g) {
		hunger=g;
	}
	
	public void run() {	

		/* Shooting fireballs @ Player
		
		Fireball fireball;
		Location loc;
		loc = p.getLocation().toVector().add(new Vector(rand = random.nextInt(10)+1,+8,rand = random.nextInt(10)+1)).toLocation(p.getWorld());
		fireball = p.getWorld().spawn(loc, Fireball.class);
		fireball.setDirection(p.getLocation().toVector().subtract(loc.toVector()));
		fireball.setYield(3);
		p.getWorld().playEffect(p.getLocation(), Effect.GHAST_SHOOT, 0);
		
		*/
		
	}
}