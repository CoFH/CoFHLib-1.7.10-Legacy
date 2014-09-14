package cofh.lib.world.biome;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public final class BiomeInfo {

	private final boolean whitelist;
	private final int type;
	private final int hash;
	private final Object data;

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

	public final boolean isBiomeEqual(BiomeGenBase biome) {
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
