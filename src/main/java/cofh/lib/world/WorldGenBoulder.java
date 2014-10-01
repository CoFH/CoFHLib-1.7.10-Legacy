package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBoulder extends WorldGenerator
{
	private final List<WeightedRandomBlock> cluster;
	private final WeightedRandomBlock[] genBlock;
	private final int size;
	public int sizeVariance = 2;
	public int clusters = 3;

	public WorldGenBoulder(List<WeightedRandomBlock> resource, int minSize, List<WeightedRandomBlock> block) {

		cluster = resource;
		size = minSize;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, int xCenter, int yCenter, int zCenter) {

		final int minSize = size, var = sizeVariance;
		boolean r = false;
		for (int i = clusters; i --> 0; ) {

			while (yCenter > minSize && world.isAirBlock(xCenter, yCenter - 1, zCenter)) {
				--yCenter;
			}
			if (yCenter <= (minSize + var + 1)) {
				return false;
			}

			if (canGenerateInBlock(world, xCenter, yCenter - 1, zCenter, genBlock)) {

				int xWidth = minSize + rand.nextInt(var);
				int yWidth = minSize + rand.nextInt(var);
				int zWidth = minSize + rand.nextInt(var);
				float maxDist = (xWidth + yWidth + zWidth) * 0.333F + 0.5F;
				maxDist *= maxDist;

				for (int x = -xWidth; x <= xWidth; ++x) {
					final int xDist = x * x;

					for (int z = -zWidth; z <= zWidth; ++z) {
						final int xzDist = xDist + z * z;

						for (int y = -yWidth; y <= yWidth; ++y) {

							if (xzDist + y * y <= maxDist) {
								r |= generateBlock(world, xCenter + x, yCenter + y, zCenter + z, cluster);
							}
						}
					}
				}
			}

			xCenter += rand.nextInt(var + minSize * 2) - (minSize + var/2);
			zCenter += rand.nextInt(var + minSize * 2) - (minSize + var/2);
			yCenter += rand.nextInt(var * 3) - var;
		}

		return r;
	}
}