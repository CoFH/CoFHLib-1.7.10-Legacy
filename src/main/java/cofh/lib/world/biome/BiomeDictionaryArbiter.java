package cofh.lib.world.biome;

import java.util.HashMap;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public class BiomeDictionaryArbiter {

	private static HashMap<Biome, Type[]> types = new HashMap<Biome, Type[]>();
	private static HashMap<Type, Biome[]> biomes = new HashMap<Type, Biome[]>();
	private static boolean loaded = Loader.instance().isInState(LoaderState.AVAILABLE);

	public static Type[] getTypesForBiome(Biome biome) {

		if (loaded) {
			Type[] r = types.get(biome);
			if (r == null) {
				types.put(biome, r = BiomeDictionary.getTypesForBiome(biome));
			}
			return r;
		}
		loaded = Loader.instance().isInState(LoaderState.AVAILABLE);
		return BiomeDictionary.getTypesForBiome(biome);
	}

	public static Biome[] getTypesForBiome(Type type) {

		if (loaded) {
			Biome[] r = biomes.get(type);
			if (r == null) {
				biomes.put(type, r = BiomeDictionary.getBiomesForType(type));
			}
			return r;
		}
		loaded = Loader.instance().isInState(LoaderState.AVAILABLE);
		return BiomeDictionary.getBiomesForType(type);
	}

	private BiomeDictionaryArbiter() {

		throw new IllegalArgumentException();
	}
}
