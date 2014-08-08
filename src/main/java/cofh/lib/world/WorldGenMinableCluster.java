package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMinableCluster extends WorldGenerator {

	public static final List<WeightedRandomBlock> fabricateList(WeightedRandomBlock resource) {

		List<WeightedRandomBlock> list = new ArrayList<WeightedRandomBlock>();
		list.add(resource);
		return list;
	}

	public static final List<Block> fabricateList(Block resource) {

		List<Block> list = new ArrayList<Block>();
		list.add(resource);
		return list;
	}

	private final List<WeightedRandomBlock> cluster;
	private final int genClusterSize;
	private final Block[] genBlock;

	public WorldGenMinableCluster(ItemStack ore, int clusterSize) {

		this(new WeightedRandomBlock(ore), clusterSize);
	}

	public WorldGenMinableCluster(WeightedRandomBlock resource, int clusterSize) {

		this(fabricateList(resource), clusterSize);
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize) {

		this(resource, clusterSize, Blocks.stone);
	}

	public WorldGenMinableCluster(ItemStack ore, int clusterSize, Block block) {

		this(new WeightedRandomBlock(ore, 1), clusterSize, block);
	}

	public WorldGenMinableCluster(WeightedRandomBlock resource, int clusterSize, Block block) {

		this(fabricateList(resource), clusterSize, block);
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize, Block block) {

		this(resource, clusterSize, fabricateList(block));
	}

	public WorldGenMinableCluster(List<WeightedRandomBlock> resource, int clusterSize, List<Block> block) {

		cluster = resource;
		genClusterSize = clusterSize > 32 ? 32 : clusterSize;
		genBlock = block.toArray(new Block[block.size()]);
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {

		if (genClusterSize < 4) {
			return generateTiny(world, random, x, y, z);
		}
		float f = random.nextFloat() * (float) Math.PI;
		double d0 = x + 8 + MathHelper.sin(f) * genClusterSize / 8.0F;
		double d1 = x + 8 - MathHelper.sin(f) * genClusterSize / 8.0F;
		double d2 = z + 8 + MathHelper.cos(f) * genClusterSize / 8.0F;
		double d3 = z + 8 - MathHelper.cos(f) * genClusterSize / 8.0F;
		double d4 = y + random.nextInt(3) - 2;
		double d5 = y + random.nextInt(3) - 2;

		for (int l = 0; l <= genClusterSize; l++) {
			double d6 = d0 + (d1 - d0) * l / genClusterSize;
			double d7 = d4 + (d5 - d4) * l / genClusterSize;
			double d8 = d2 + (d3 - d2) * l / genClusterSize;
			double d9 = random.nextDouble() * genClusterSize / 16.0D;
			double d10 = (MathHelper.sin(l * (float) Math.PI / genClusterSize) + 1.0F) * d9 + 1.0D;
			double d11 = (MathHelper.sin(l * (float) Math.PI / genClusterSize) + 1.0F) * d9 + 1.0D;
			int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
			int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
			int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
			int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
			int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
			int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

			for (int k2 = i1; k2 <= l1; k2++) {
				double d12 = (k2 + 0.5D - d6) / (d10 / 2.0D);

				if (d12 * d12 < 1.0D) {
					for (int l2 = j1; l2 <= i2; l2++) {
						double d13 = (l2 + 0.5D - d7) / (d11 / 2.0D);

						if (d12 * d12 + d13 * d13 < 1.0D) {
							for (int i3 = k1; i3 <= j2; i3++) {
								double d14 = (i3 + 0.5D - d8) / (d10 / 2.0D);
								Block block = world.getBlock(k2, l2, i3);

								if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {

									l: for (int j = 0, e = genBlock.length; j < e; ++j) {
										Block genBlock = this.genBlock[j];
										if (block.isReplaceableOreGen(world, k2, l2, i3, genBlock)) {
											WeightedRandomBlock ore = (WeightedRandomBlock) WeightedRandom.getRandomItem(world.rand, cluster);
											world.setBlock(k2, l2, i3, ore.block, ore.metadata, 2);
											break l;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean generateTiny(World world, Random random, int x, int y, int z) {

		for (int i = 0; i < genClusterSize; i++) {
			int d0 = x + random.nextInt(2);
			int d1 = y + random.nextInt(2);
			int d2 = z + random.nextInt(2);
			Block block = world.getBlock(d0, d1, d2);

			l: for (int j = 0, e = genBlock.length; j < e; ++j) {
				Block genBlock = this.genBlock[j];
				if (block.isReplaceableOreGen(world, d0, d1, d2, genBlock)) {
					WeightedRandomBlock ore = (WeightedRandomBlock) WeightedRandom.getRandomItem(world.rand, cluster);
					world.setBlock(d0, d1, d2, ore.block, ore.metadata, 2);
					break l;
				}
			}
		}
		return true;
	}

}
