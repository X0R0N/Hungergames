package x0r0n.heavyhacker.hungergames;

public class PlayerInformation {
	private Hungergames hunger;
	private boolean isHungerPlayer=false;
	private org.bukkit.entity.Player player;
	private Integer district=0;
	
	PlayerInformation(Hungergames g) {
		hunger=g;
	}

	public void Reset() {
		isHungerPlayer=false;
		district=0;
	}
	
	public void SetHungerPlayer(boolean p) {
		isHungerPlayer=p;
	}
	public boolean IsHungerPlayer() {
		return isHungerPlayer;
	}
	
	public void SetDistrict(Integer p) {
		if(p==0)
			isHungerPlayer=false;
		else
			isHungerPlayer=true;
		district=p;
	}
	public Integer GetDistrict() {
		return district;
	}
	
	public void SetPlayer(org.bukkit.entity.Player p) {
		player=p;
	}
	public org.bukkit.entity.Player GetPlayer() {
		return player;
	}
}
