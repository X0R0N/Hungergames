package x0r0n.heavyhacker.hungergames;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.util.Vector;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

public class ChestManager {
	private List<ItemStack> randomItems=new ArrayList<ItemStack>();
	private List<ItemStack> sponsorItems=new ArrayList<ItemStack>();
	private List<ItemStack> feastItems=new ArrayList<ItemStack>();
	private List<Vector> spawnchests=new ArrayList<Vector>();
	private Hungergames hunger;
	private Random rand=new Random();
	ChestManager(Hungergames g) {
		hunger=g;
		randomItems=(List<ItemStack>) g.config.getList("randomItems");
		sponsorItems=(List<ItemStack>) g.config.getList("sponsorItems");
		feastItems=(List<ItemStack>) g.config.getList("feastItems");
		spawnchests=(List<Vector>) g.config.getList("spawnchests");
	}
	
	public void FillSpawnChests() {
		for(Vector vec: spawnchests) {
			Block b=hunger.getServer().getWorlds().get(0).getBlockAt((int)vec.getX(),(int)vec.getY(),(int)vec.getZ());
			if(b==null){continue;}
			if(b.getType()!=Material.CHEST){
				hunger.getLogger().info("[ERROR] Chest expected, found "+b.getType()+" at "+vec.getX()+","+vec.getZ()+","+vec.getY()+"!");
				continue;
			}
			FillChest(4,(Chest)b.getState(),randomItems);
		}
	}
	
	public void FillChest(int count, Chest c, List<ItemStack> l) {
		c.getBlockInventory().clear();
		for(int i=0; i<count; ++i) {
			boolean found=false;
			while(!found) {
				int index=rand.nextInt(c.getBlockInventory().getSize());
				
				if(c.getBlockInventory().getItem(index)!=null)
					continue;
				
				c.getBlockInventory().setItem(index,l.get(rand.nextInt(l.size())));
				found=true;
			}
		}
	}
}