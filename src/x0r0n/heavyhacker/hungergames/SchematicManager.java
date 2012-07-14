package x0r0n.heavyhacker.hungergames;

import jnbt.ByteArrayTag;
import jnbt.CompoundTag;
import jnbt.IntTag;
import jnbt.ListTag;
import jnbt.NBTConstants;
import jnbt.NBTInputStream;
import jnbt.NBTOutputStream;
import jnbt.ShortTag;
import jnbt.StringTag;
import jnbt.Tag;
import jnbt.DataException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.World;

public class SchematicManager {
	private Hungergames hunger;
	
	SchematicManager(Hungergames g) {
		hunger=g;
	}
	
	public boolean Load(File file, Location loc) throws IOException, DataException {
		FileInputStream stream = new FileInputStream(file);
		NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));
		
		Vector origin = new Vector();
		Vector offset = new Vector();
		
		CompoundTag schematicTag = (CompoundTag)nbtStream.readTag();
		if(!schematicTag.getName().equals("Schematic")) {
			throw new DataException("Tag \"Schematic\" does not exist or is not first!");
		}
		
		Map<String, Tag> schematic = schematicTag.getValue();
		if(!schematic.containsKey("Blocks")) {
			throw new DataException("Schematic file is missing a \"Blocks\" tag!");
		}
		
		short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
		short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
		short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
		
		try {
			int originX = getChildTag(schematic, "WEOriginX", IntTag.class).getValue();
			int originY = getChildTag(schematic, "WEOriginY", IntTag.class).getValue();
			int originZ = getChildTag(schematic, "WEOriginZ", IntTag.class).getValue();
			
			origin = new Vector(originX, originY, originZ);
		} catch( DataException e) {
			
		}
		
		try {
			int offsetX = getChildTag(schematic, "WEOffsetX", IntTag.class).getValue();
			int offsetY = getChildTag(schematic, "WEOffsetY", IntTag.class).getValue();
			int offsetZ = getChildTag(schematic, "WEOffsetZ", IntTag.class).getValue();
			
			offset = new Vector(offsetX, offsetY, offsetZ);
		} catch(DataException e) {
			
		}
		
		byte[] rawBlocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
		short[] blocks = new short[rawBlocks.length];
		
		if(schematic.containsKey("AddBlocks")) {
			byte[] addBlockIds = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
			
			for(int i=0, index=0; i<addBlockIds.length && index < blocks.length; ++i) {
				blocks[index]=(short)(addBlockIds[i]&0xF << 8 + rawBlocks[index++]);
				if(index < blocks.length) {
					blocks[index]=(short)(((addBlockIds[i]<<4)&0xF)<<8+rawBlocks[index++]);
				}
			}
		}
		
		List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
		Map<Vector, Map<String, Tag>> tileEntitiesMap = new HashMap<Vector, Map<String, Tag>>();
		
		for(Tag tag: tileEntities) {
			if(!(tag instanceof CompoundTag))
				continue;
			
			CompoundTag t = (CompoundTag)tag;
			
			int x = 0;
			int y = 0;
			int z = 0;
			
			Map<String, Tag> values = new HashMap<String, Tag>();
			
			for(Map.Entry<String, Tag> entry: t.getValue().entrySet()) {
				if(entry.getKey().equals("x")) {
					if(entry.getValue() instanceof IntTag) {
						x=((IntTag)entry.getValue()).getValue();
					}
				} else if(entry.getKey().equals("y")) {
					if(entry.getValue() instanceof IntTag) {
						y=((IntTag)entry.getValue()).getValue();
					}
				} else if(entry.getKey().equals("z")) {
					if(entry.getValue() instanceof IntTag) {
						z=((IntTag)entry.getValue()).getValue();
					}
				}
				
				values.put(entry.getKey(), entry.getValue());
			}
			
			Vector vec = new Vector(x,y,z);
			tileEntitiesMap.put(vec,values);
		}
		
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				for(int z = 0; z < length; ++z) {
					int index = y*width*length+z*width+x;
					
					Vector pt = new Vector(x,y,z);
					Vector base = loc.toVector();
					
					pt.add(base);
					
					Location blk = new Location(loc.getWorld(),pt.getX()-1,pt.getY(),pt.getZ()-1);
					//hunger.getLogger().info("Block ID: " + rawBlocks[index]);
					blk.getBlock().setTypeId(rawBlocks[index]);
				}
			}
		}
		
		return true;
	}
	
	private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws DataException {
		if(!items.containsKey(key)) {
			throw new DataException("Schematic file is missing a \"" + key + "\" tag!");
		}
		Tag tag = items.get(key);
		if(!expected.isInstance(tag)) {
			throw new DataException(key + " tag is not of tag type " + expected.getName());
		}
		
		return expected.cast(tag);
	}
}
