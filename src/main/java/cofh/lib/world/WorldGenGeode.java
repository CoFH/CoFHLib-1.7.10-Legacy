package cofh.lib.world;

import cofh.lib.util.WeightedRandomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.List;
import java.util.Random;

import static cofh.lib.world.WorldGenMinableCluster.canGenerateInBlock;
import static cofh.lib.world.WorldGenMinableCluster.generateBlock;

public class WorldGenGeode extends WorldGenerator {

    private final List<WeightedRandomBlock> cluster;
    private final List<WeightedRandomBlock> outline;
    private final WeightedRandomBlock[] genBlock;
    public List<WeightedRandomBlock> fillBlock = null;
    public boolean hollow = false;
    public int width = 16;
    public int height = 8;

    public WorldGenGeode(List<WeightedRandomBlock> resource, List<WeightedRandomBlock> material, List<WeightedRandomBlock> cover) {

        cluster = resource;
        genBlock = material.toArray(new WeightedRandomBlock[material.size()]);
        outline = cover;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int xStart = pos.getX();
        int yStart = pos.getY();
        int zStart = pos.getZ();
        int heightOff = height / 2;
        int widthOff = width / 2;
        xStart -= widthOff;
        zStart -= widthOff;

        if (yStart <= heightOff) {
            return false;
        }

        yStart -= heightOff;
        boolean[] spawnBlock = new boolean[width * width * height];
        boolean[] hollowBlock = new boolean[width * width * height];

        int W = width - 1, H = height - 1;

        for (int i = 0, e = rand.nextInt(4) + 4; i < e; ++i) {
            double xSize = rand.nextDouble() * 6.0D + 3.0D;
            double ySize = rand.nextDouble() * 4.0D + 2.0D;
            double zSize = rand.nextDouble() * 6.0D + 3.0D;
            double xCenter = rand.nextDouble() * (width - xSize - 2.0D) + 1.0D + xSize / 2.0D;
            double yCenter = rand.nextDouble() * (height - ySize - 4.0D) + 2.0D + ySize / 2.0D;
            double zCenter = rand.nextDouble() * (width - zSize - 2.0D) + 1.0D + zSize / 2.0D;
            double minDist = hollow ? rand.nextGaussian() * 0.15 + 0.4 : 0;

            for (int x = 1; x < W; ++x) {
                for (int z = 1; z < W; ++z) {
                    for (int y = 1; y < H; ++y) {
                        double xDist = (x - xCenter) / (xSize / 2.0D);
                        double yDist = (y - yCenter) / (ySize / 2.0D);
                        double zDist = (z - zCenter) / (zSize / 2.0D);
                        double dist = xDist * xDist + yDist * yDist + zDist * zDist;

                        if (dist < 1.0D) {
                            spawnBlock[(x * width + z) * height + y] = hollow ? dist > minDist : true;
                        }
                        if (hollow) {
                            hollowBlock[(x * width + z) * height + y] = dist <= minDist;
                        }
                    }
                }
            }
        }

        int x;
        int y;
        int z;

        for (x = 0; x < width; ++x) {
            for (z = 0; z < width; ++z) {
                for (y = 0; y < height; ++y) {
                    boolean flag = (fillBlock != null && hollowBlock[(x * width + z) * height + y]) || spawnBlock[(x * width + z) * height + y] || ((x < W && spawnBlock[((x + 1) * width + z) * height + y]) || (x > 0 && spawnBlock[((x - 1) * width + z) * height + y]) || (z < W && spawnBlock[(x * width + (z + 1)) * height + y]) || (z > 0 && spawnBlock[(x * width + (z - 1)) * height + y]) || (y < H && spawnBlock[(x * width + z) * height + (y + 1)]) || (y > 0 && spawnBlock[(x * width + z) * height + (y - 1)]));

                    if (flag && !canGenerateInBlock(world, xStart + x, yStart + y, zStart + z, genBlock)) {
                        return false;
                    }
                }
            }
        }

        boolean r = false;
        for (x = 0; x < width; ++x) {
            for (z = 0; z < width; ++z) {
                for (y = 0; y < height; ++y) {
                    if (spawnBlock[(x * width + z) * height + y]) {
                        boolean t = generateBlock(world, xStart + x, yStart + y, zStart + z, cluster);
                        r |= t;
                        if (!t) {
                            spawnBlock[(x * width + z) * height + y] = false;
                        }
                    }
                }
            }
        }

        for (x = 0; x < width; ++x) {
            for (z = 0; z < width; ++z) {
                for (y = 0; y < height; ++y) {
                    if (fillBlock != null && hollowBlock[(x * width + z) * height + y]) {
                        r |= generateBlock(world, xStart + x, yStart + y, zStart + z, fillBlock);
                    } else {
                        boolean flag = !spawnBlock[(x * width + z) * height + y] && ((x < W && spawnBlock[((x + 1) * width + z) * height + y]) || (x > 0 && spawnBlock[((x - 1) * width + z) * height + y]) || (z < W && spawnBlock[(x * width + (z + 1)) * height + y]) || (z > 0 && spawnBlock[(x * width + (z - 1)) * height + y]) || (y < H && spawnBlock[(x * width + z) * height + (y + 1)]) || (y > 0 && spawnBlock[(x * width + z) * height + (y - 1)]));

                        if (flag) {
                            r |= generateBlock(world, xStart + x, yStart + y, zStart + z, outline);
                        }
                    }
                }
            }
        }

        return r;
    }
}
