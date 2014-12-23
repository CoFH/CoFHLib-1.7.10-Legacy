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
	public boolean altSinc = false;

	public WorldGenStalagmite(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> block, List<WeightedRandomBlock> gblock) {

		cluster = resource;
		baseBlock = block.toArray(new WeightedRandomBlock[block.size()]);
		genBlock = gblock.toArray(new WeightedRandomBlock[gblock.size()]);
	}

	protected int getHeight(int x, int z, int size, Random rand, int height) {

		if (smooth) {
			if ((x*x + z*z) * 4 >= size * size * 5) return 0;

			final double lim = (altSinc ? 600f : (fat ? 1f : .5f) * 400f) / size;
			final double pi = Math.PI;
			double r;
			r = Math.sqrt((r=((x*lim)/pi))*r + (r=((z*lim)/pi))*r) * pi/180;
			if (altSinc && (x*x + z*z) < (size*1.1f))
				r = size * 0.0082;
			if (r == 0) return height;
			if (!altSinc)
				return (int)Math.round(height * (fat ? Math.sin(r) / r : Math.sin(r=r*pi) / r));
			double sinc = (Math.sin(r) / r);
			return (int)Math.round(Math.min(sinc*height+3, height*(sinc*2+(rand.nextGaussian()*.65*Math.sin(r=r*(pi*4))/r))/2.3));
		} else {
			int absx = x < 0 ? -x : x, absz = (z < 0 ? -z : z);
			int dist = fat ? (absx < absz ? absz + absx / 2 : absx + absz / 2) : absx + absz;
			if (dist == 0) return height;
			return rand.nextInt(height / dist);
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

		int maxHeight = (heightVariance > 0 ? rand.nextInt(heightVariance) : 0) + minHeight;

		int size = (genSize > 0 ? genSize : maxHeight / heightMod);
		if (sizeVariance > 0) size += rand.nextInt(sizeVariance);
		boolean r = false;
		for (int x = -size; x <= size; ++x) {
			for (int z = -size; z <= size; ++z) {
				if (!canGenerateInBlock(world, xStart + x, yStart - 1, zStart + z, baseBlock)) {
					continue;
				}
				int height = getHeight(x, z, size, rand, maxHeight);
				for (int y = 0; y < height; ++y) {
					r |= generateBlock(world, xStart + x, yStart + y, zStart + z, genBlock, cluster);
				}
			}
		}
		return r;
	}
}
