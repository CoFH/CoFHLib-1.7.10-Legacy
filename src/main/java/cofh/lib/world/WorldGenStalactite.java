package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenStalactite extends WorldGenStalagmite {

	public WorldGenStalactite(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> block, List<WeightedRandomBlock> gblock) {

		super(resource, block, gblock);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos posStart) {

		//TODO refactor to BlockPos use
		int xStart = posStart.getX();
		int yStart = posStart.getY();
		int zStart = posStart.getZ();

		int end = world.getActualHeight();
		while (world.isAirBlock(posStart) && yStart < end) {
			++yStart;
		}

		if (!canGenerateInBlock(world, xStart, yStart--, zStart, baseBlock)) {
			return false;
		}

		int maxHeight = rand.nextInt(heightVariance) + minHeight;

		int size = genSize > 0 ? genSize : maxHeight / heightMod + rand.nextInt(sizeVariance);
		boolean r = false;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				if (!canGenerateInBlock(world, xStart + x, yStart + 1, zStart + z, baseBlock)) {
					continue;
				}
				int height = getHeight(x, z, size, rand, maxHeight);
				for (int y = 0; y < height; ++y) {
					r |= generateBlock(world, xStart + x, yStart - y, zStart + z, genBlock, cluster);
				}
			}
		}
		return r;
	}
}
