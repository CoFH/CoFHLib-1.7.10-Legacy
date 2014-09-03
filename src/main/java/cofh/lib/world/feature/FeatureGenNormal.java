package cofh.lib.world.feature;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

public class FeatureGenNormal extends FeatureBase {

	final WorldGenerator worldGen;
	final int count;
	final int meanY;
	final int maxVar;

	public FeatureGenNormal(String name, WorldGenerator worldGen, int count, int meanY, int maxVar, GenRestriction biomeRes, boolean regen,
			GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.meanY = meanY;
		this.maxVar = maxVar;
	}

	/* IFeatureGenerator */
	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world, boolean newGen) {

		if (!newGen && !regen) {
			return false;
		}
		if (dimensionRestriction != GenRestriction.NONE) {
			if (dimensionRestriction == GenRestriction.BLACKLIST == dimensions.contains(world.provider.dimensionId)) {
				return false;
			}
		}
		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int y = random.nextInt(maxVar) + random.nextInt(maxVar) + meanY - maxVar;
			int z = blockZ + random.nextInt(16);

			if (biomeRestriction != GenRestriction.NONE) {
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				if (biomeRestriction == GenRestriction.BLACKLIST == (biome != null && biomes.contains(biome.biomeName))) {
					continue;
				}
			}
			generated |= worldGen.generate(world, random, x, y, z);
		}
		return generated;
	}

}
