package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenStalagmite extends WorldGenerator {

	protected final List<WeightedRandomBlock> cluster;
	protected final WeightedRandomBlock[] baseBlock;
	protected final WeightedRandomBlock[] genBlock;
	public int minHeight = 7;
	public int heightVariance = 4;
	public int sizeVariance = 2;
	public int heightMod = 5;
	public int genSize = 0;
	public boolean smooth = false;
	public boolean fat = true;

	public WorldGenStalagmite(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> block, List<WeightedRandomBlock> gblock) {

		cluster = resource;
		baseBlock = block.toArray(new WeightedRandomBlock[block.size()]);
		genBlock = gblock.toArray(new WeightedRandomBlock[gblock.size()]);
	}

	protected int getHeight(int x, int z, Random rand, int height) {

		if (smooth) {
			final double pi = Math.PI;
			double r;
			r = Math.sqrt((r=(x/pi))*r + (r=(z/pi))*r) * pi/180;
			if (r == 0) return height;
			return (int)Math.round(height * (fat ? Math.sin(r) / r : Math.sin(r=r*pi) / r));
		} else {
			int absx = x < 0 ? -x : x, absz = (z < 0 ? -z : z);
			int dist = !fat ? (absx < absz ? absz + absx / 2 : absx + absz / 2) : absx + absz;
			return rand.nextInt(height / (dist + 1));
		}
	}

	@Override
	public boolean generate(World world, Random rand, int xStart, int yStart, int zStart) {

		while (world.isAirBlock(xStart, yStart, zStart) && yStart > 0) {
			--yStart;
		}

		if (!canGenerateInBlock(world, xStart, yStart++, zStart, baseBlock)) {
			return false;
		}

		int maxHeight = rand.nextInt(heightVariance) + minHeight;

		int size = genSize > 0 ? genSize : maxHeight / heightMod + rand.nextInt(sizeVariance);
		boolean r = false;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				if (!canGenerateInBlock(world, xStart + x, yStart - 1, zStart + z, baseBlock)) {
					continue;
				}
				int height = getHeight(x, z, rand, maxHeight);
				for (int y = 0; y < height; ++y) {
					r |= generateBlock(world, xStart + x, yStart + y, zStart + z, genBlock, cluster);
				}
			}
		}
		return r;
	}
}
