package cofh.lib.world.biome;

import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeInfo {

	private final Object data;
	private final boolean whitelist;
	private final int type;
	private final int hash;

	public BiomeInfo(String name) {
		data = name;
		hash = name.hashCode();
		whitelist = true;
		type = 0;
	}

	public BiomeInfo(Object d, int t, boolean wl) {
		data = d;
		hash = 0;
		whitelist = wl;
		type = t;
	}

	public boolean isBiomeEqual(BiomeGenBase biome, Random rand) {
		if (biome != null)
			switch (type) {
			default:break;
			case 0:
				String name = biome.biomeName;
				return name.hashCode() == hash && name.equals(data);
			case 1:
				return biome.getTempCategory() == data == whitelist;
			case 2:
				return BiomeDictionary.isBiomeOfType(biome, (Type)data) == whitelist;
			}
		return !whitelist;
	}

}
