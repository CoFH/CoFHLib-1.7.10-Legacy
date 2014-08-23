package cofh.lib.world.feature;

import cofh.lib.util.helpers.BlockHelper;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

public class FeatureOreGenSurface extends FeatureBase {

	final WorldGenerator worldGen;
	final int count;
	final int chance;

	public FeatureOreGenSurface(String name, WorldGenerator worldGen, int count, int chance, GenRestriction biomeRes, boolean regen, GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.chance = chance;
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
		if (chance > 1 && random.nextInt(chance) != 0) {
			return false;
		}
		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		boolean generated = false;
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int z = blockZ + random.nextInt(16);
			if (biomeRestriction != GenRestriction.NONE) {
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				if (biomeRestriction == GenRestriction.BLACKLIST == (biome != null && biomes.contains(biome.biomeName))) {
					continue;
				}
			}

			int y = BlockHelper.getSurfaceBlockY(world, x, z);
			if (world.getBlock(x, y, z).isAir(world, x, y, z)) {
				continue;
			}

			generated |= worldGen.generate(world, random, x, y, z);
		}
		return generated;
	}

}
