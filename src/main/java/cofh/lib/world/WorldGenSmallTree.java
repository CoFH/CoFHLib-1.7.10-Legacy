package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;

import cofh.lib.util.WeightedRandomBlock;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSmallTree extends WorldGenerator {

	private final List<WeightedRandomBlock> leaves;
	private final List<WeightedRandomBlock> trunk;
	private final WeightedRandomBlock[] genBlock;

	public WeightedRandomBlock[] genSurface = null;
	public int minHeight = 5;
	public int heightVariance = 3;
	public boolean treeChecks = true;
	public boolean relaxedGrowth = false;
	public boolean waterLoving = false;

	public WorldGenSmallTree(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> leaf, List<WeightedRandomBlock> block) {

		trunk = resource;
		leaves = leaf;
		genBlock = block.toArray(new WeightedRandomBlock[block.size()]);
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {

		int treeHeight = (heightVariance <= 1 ? 0 : rand.nextInt(heightVariance)) + minHeight;
		int worldHeight = world.getHeight();
		Block block;

		if (y + treeHeight + 1 <= worldHeight) {
			int xOffset;
			int yOffset;
			int zOffset;

			if (genSurface != null && !canGenerateInBlock(world, x, y - 1, z, genSurface))
				return false;

			if (y < worldHeight - treeHeight - 1) {
				if (treeChecks) {
					for (yOffset = y; yOffset <= y + 1 + treeHeight; ++yOffset) {
						int radius;

						if (yOffset >= y + 1 + treeHeight - 2) {
							radius = 2;
						} else {
							radius = relaxedGrowth ? 0 : 1;
						}

						if (yOffset >= 0 & yOffset < worldHeight) {
							if (radius == 0) {
								block = world.getBlock(x, yOffset, z);
								if (!(block.isLeaves(world, x, yOffset, z) ||
										block.isAir(world, x, yOffset, z) ||
										block.isReplaceable(world, x, yOffset, z) ||
										block.canBeReplacedByLeaves(world, x, yOffset, z))) {
									return false;
								}

								if (!waterLoving && yOffset >= y + 1) {
									radius = 1;
									for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
										for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
											block = world.getBlock(xOffset, yOffset, zOffset);

											if (block.getMaterial().isLiquid()) {
												return false;
											}
										}
									}
								}
							}
							else for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
								for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {
									block = world.getBlock(xOffset, yOffset, zOffset);

									if (!(block.isLeaves(world, xOffset, yOffset, zOffset) ||
											block.isAir(world, xOffset, yOffset, zOffset) ||
											block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset))) {
										return false;
									}
								}
							}
						}
						else {
							return false;
						}
					}

					if (genSurface != null && !canGenerateInBlock(world, x, y - 1, z, genSurface))
						return false;
					block = world.getBlock(x, y - 1, z);
					block.onPlantGrow(world, x, y - 1, z, x, y, z);
				}

				boolean r = false;

				for (yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
					int var12 = yOffset - (y + treeHeight),
							center = 1 - var12 / 2;

					for (xOffset = x - center; xOffset <= x + center; ++xOffset) {
						int xPos = xOffset - x, t;
						xPos = (xPos + (t = xPos >> 31)) ^ t;

						for (zOffset = z - center; zOffset <= z + center; ++zOffset) {
							int zPos = zOffset - z;
							zPos = (zPos + (t = zPos >> 31)) ^ t;

							block = world.getBlock(xOffset, yOffset, zOffset);

							if (((xPos != center | zPos != center) || rand.nextInt(2) != 0 && var12 != 0) &&
									(!treeChecks || block.isLeaves(world, xOffset, yOffset, zOffset) ||
											block.isAir(world, xOffset, yOffset, zOffset) ||
											block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset))) {
								r |= generateBlock(world, xOffset + x, yOffset + y, zOffset + z, genBlock, leaves);
							}
						}
					}
				}

				for (yOffset = 0; yOffset < treeHeight; ++yOffset) {
					block = world.getBlock(x, y + yOffset, z);

					if (!treeChecks || block.isAir(world, x, y + yOffset, z)  ||
							block.isLeaves(world, x, y + yOffset, z) ||
							block.isReplaceable(world, x, y + yOffset, z)){
						r |= generateBlock(world, x, yOffset + y, z, genBlock, trunk);
					}
				}

				return r;
			}
		}
		return false;
	}

}
