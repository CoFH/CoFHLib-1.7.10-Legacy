package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenGeode extends WorldGenerator
{
	private final List<WeightedRandomBlock> cluster;
	private final List<WeightedRandomBlock> outline;
	private final WeightedRandomBlock[] genBlock;

	public WorldGenGeode(List<WeightedRandomBlock> resource,
			List<WeightedRandomBlock> material, List<WeightedRandomBlock> cover) {

		cluster = resource;
		genBlock = material.toArray(new WeightedRandomBlock[material.size()]);
		outline = cover;
	}

	@Override
	public boolean generate(World world, Random rand, int xStart, int yStart, int zStart)
	{
		xStart -= 8;
		zStart -= 8;

		while (yStart > 5 && world.isAirBlock(xStart, yStart, zStart))
			--yStart;
		if (yStart <= 5)
			return false;

		yStart -= 4;
		boolean[] spawnBlock = new boolean[16 * 16 * 8];

		for (int i = 0, e = rand.nextInt(4) + 4; i < e; ++i) {
			double xSize = rand.nextDouble() * 6.0D + 3.0D;
			double ySize = rand.nextDouble() * 4.0D + 2.0D;
			double zSize = rand.nextDouble() * 6.0D + 3.0D;
			double xCenter = rand.nextDouble() * (16.0D - xSize - 2.0D) + 1.0D + xSize / 2.0D;
			double yCenter = rand.nextDouble() * (8.0D - ySize - 4.0D) + 2.0D + ySize / 2.0D;
			double zCenter = rand.nextDouble() * (16.0D - zSize - 2.0D) + 1.0D + zSize / 2.0D;

			for (int x = 1; x < 15; ++x) {
				for (int z = 1; z < 15; ++z) {
					for (int y = 1; y < 7; ++y) {
						double xDist = (x - xCenter) / (xSize / 2.0D);
						double yDist = (y - yCenter) / (ySize / 2.0D);
						double zDist = (z - zCenter) / (zSize / 2.0D);
						double dist = xDist * xDist + yDist * yDist + zDist * zDist;

						if (dist < 1.0D)
							spawnBlock[(x * 16 + z) * 8 + y] = true;
					}
				}
			}
		}

		int x;
		int y;
		int z;

		for (x = 0; x < 16; ++x) {
			for (z = 0; z < 16; ++z) {
				for (y = 0; y < 8; ++y) {
                	boolean flag = !spawnBlock[(x * 16 + z) * 8 + y] && (
                			(x < 15 && spawnBlock[((x + 1) * 16 + z) * 8 + y]) ||
                			(x >  0 && spawnBlock[((x - 1) * 16 + z) * 8 + y]) ||
							(z < 15 && spawnBlock[(x * 16 + (z + 1)) * 8 + y]) ||
							(z >  0 && spawnBlock[(x * 16 + (z - 1)) * 8 + y]) ||
							(y <  7 && spawnBlock[(x * 16 + z) * 8 + (y + 1)]) ||
							(y >  0 && spawnBlock[(x * 16 + z) * 8 + (y - 1)]));

					if (flag) {
						Material material = world.getBlock(xStart + x, yStart + y, zStart + z).getMaterial();

						if (!material.isSolid())
							return false;
					}
				}
			}
		}

		boolean r = false;
		for (x = 0; x < 16; ++x) {
			for (z = 0; z < 16; ++z) {
				for (y = 0; y < 8; ++y) {
					if (spawnBlock[(x * 16 + z) * 8 + y])
						if (!generateBlock(world, x, y, z, genBlock, cluster)) {
							spawnBlock[(x * 16 + z) * 8 + y] = false;
						} else
							r = true;
				}
			}
		}

		for (x = 0; x < 16; ++x) {
            for (z = 0; z < 16; ++z) {
                for (y = 0; y < 8; ++y) {
                	boolean flag = !spawnBlock[(x * 16 + z) * 8 + y] && (
                			(x < 15 && spawnBlock[((x + 1) * 16 + z) * 8 + y]) ||
                			(x >  0 && spawnBlock[((x - 1) * 16 + z) * 8 + y]) ||
							(z < 15 && spawnBlock[(x * 16 + (z + 1)) * 8 + y]) ||
							(z >  0 && spawnBlock[(x * 16 + (z - 1)) * 8 + y]) ||
							(y <  7 && spawnBlock[(x * 16 + z) * 8 + (y + 1)]) ||
							(y >  0 && spawnBlock[(x * 16 + z) * 8 + (y - 1)]));

                    if (flag) {
                        r |= generateBlock(world, x, y, z, outline);
                    }
                }
            }
        }

		return r;
	}
}