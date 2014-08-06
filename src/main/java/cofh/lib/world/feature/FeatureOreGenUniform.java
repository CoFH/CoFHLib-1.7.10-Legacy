package cofh.lib.world.feature;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class FeatureOreGenUniform extends FeatureBase {

	final WorldGenerator worldGen;
	final int count;
	final int minY;
	final int maxY;

	public FeatureOreGenUniform(String name, WorldGenerator worldGen, int count, int minY, int maxY, GenRestriction biomeRes, boolean regen,
			GenRestriction dimRes) {

		super(name, biomeRes, regen, dimRes);
		this.worldGen = worldGen;
		this.count = count;
		this.minY = minY;
		this.maxY = maxY;
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

		if (biomeRestriction != GenRestriction.NONE) {
			if (biomeRestriction == GenRestriction.BLACKLIST == biomes.contains(world.getBiomeGenForCoords(chunkX, chunkZ).biomeName.toLowerCase())) {
				return false;
			}
		}
		for (int i = 0; i < count; i++) {
			int x = blockX + random.nextInt(16);
			int y = minY + random.nextInt(maxY - minY);
			int z = blockZ + random.nextInt(16);
			worldGen.generate(world, random, x, y, z);
		}
		return true;
	}

}
