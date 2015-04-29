package cofh.lib.world;

import static cofh.lib.world.WorldGenMinableCluster.*;
import static java.lang.Math.abs;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.util.WeightedRandomNBTTag;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeon extends WorldGenerator {

    private final List<WeightedRandomBlock> walls;
    private final WeightedRandomBlock[] genBlock;
    private final WeightedRandomNBTTag[] spawners;
    public int minWidthX = 2, maxWidthX = 3;
    public int minWidthZ = 2, maxWidthZ = 3;
    public int minHeight = 3, maxHeight = 3;
    public int minHoles = 1, maxHoles = 5;
    public int maxChests = 2;
    public List<DungeonMob> lootTables = Arrays.asList(new DungeonMob(100, ChestGenHooks.DUNGEON_CHEST));
    public List<WeightedRandomBlock> floor;

    private static Logger log = LogManager.getLogger("CoFHWorld");

    public WorldGenDungeon(List<WeightedRandomBlock> blocks, List<WeightedRandomBlock> material, List<WeightedRandomNBTTag> mobs) {

        walls = blocks;
        floor = walls;
        spawners = mobs.toArray(new WeightedRandomNBTTag[mobs.size()]);
        genBlock = material.toArray(new WeightedRandomBlock[material.size()]);
    }

    @Override
    public boolean generate(World world, Random rand, int xStart, int yStart, int zStart) {

        if (yStart <= 2) {
            return false;
        }

        int height = nextInt(rand, maxHeight - minHeight + 1) + minHeight;
        int xWidth = nextInt(rand, maxWidthX - minWidthX + 1) + minWidthX;
        int zWidth = nextInt(rand, maxWidthZ - minWidthZ + 1) + minWidthZ;
        int holes = 0;
        int x, y, z;

        int floor = yStart - 1, ceiling = yStart + height + 1;

        // The previous algorithm would check that every block in the potential working area
        // was in the valid list.  Air blocks were a touchy problem.  When trying to generate
        // dungeons in The Nether, there are various open spaces.  If the dungeon had a single
        // one of these overhead, it would not generate.
        //
        // This alternative checks that the center of the dungeon--where it would place the
        // monster spawner, is not exposed to air.  It will then assert that the immediate
        // area around it is made of valid blocks in which it can generate.
        //
        // This means the dungeon may override blocks that it was not authorized to.  The
        // rationale though is that dungeons are generally more rare and precious.
        // One circumvention for ores in this is that it can place all the resources it removes
        // into chests that are inside the dungeon.  We don't do this, but it might be a fun idea.
        //
        // It still will scan the entire generation area for holes, and refuse to generate
        // if there are too few or too many
        x = xStart;
        z = zStart;

        // Checking below floor and above the ceiling for where the spawner is targeted to generate.
        for(y = floor - 1; y <= ceiling + 1; ++y) {
            if (!canGenerateInBlock(world, x, y, z, genBlock)) {
                return false;
            }
        }

        // Hole check
        for (x = xStart - xWidth - 1; x <= xStart + xWidth + 1; ++x) {
            for (z = zStart - zWidth - 1; z <= zStart + zWidth + 1; ++z) {
                for (y = floor; y <= ceiling; ++y) {

                    if ((abs(x - xStart) == xWidth + 1 || abs(z - zStart) == zWidth + 1) && y == yStart && world.isAirBlock(x, y, z)
                            && world.isAirBlock(x, y + 1, z)) {
                        ++holes;
                    }
                }
            }
        }

        if (holes < minHoles || holes > maxHoles) {
            return false;
        }

        NBTTagCompound tag = (NBTTagCompound) ((WeightedRandomNBTTag) WeightedRandom.getRandomItem(rand, spawners)).tag;
        ChestGenHooks table = ChestGenHooks.getInfo(((DungeonMob) WeightedRandom.getRandomItem(rand, lootTables)).type);

        // Fill in floor and walls.  Set inner space to air.
        for (x = xStart - xWidth - 1; x <= xStart + xWidth + 1; ++x) {
            for (z = zStart - zWidth - 1; z <= zStart + zWidth + 1; ++z) {
                for (y = yStart + height; y >= floor; --y) {

                    if(y == floor && !world.isAirBlock(x, y - 1, z)) {
                        generateBlock(world, x, y, z, this.floor);
                    } else if(x == xStart - xWidth - 1 || x == xStart + xWidth + 1 ||
                            z == zStart - zWidth - 1 || z == zStart + zWidth + 1) {
                        if(!world.isAirBlock(x, y, z)) {
                            generateBlock(world, x, y, z, walls);
                        }
                    } else {
                        world.setBlockToAir(x, y, z);
                    }
                }
            }
        }

        for (int i = maxChests; i-- > 0;) {
            for (int j = 0; j < 3; ++j) {

                if(nextInt(rand, 2) > 0)
                {
                    x = xStart + nextInt(rand, 2) * (xWidth * 2) - xWidth;
                    z = zStart + nextInt(rand, zWidth * 2 + 1) - zWidth;
                }
                else
                {
                    z = zStart + nextInt(rand, 2) * (zWidth * 2) - zWidth;
                    x = xStart + nextInt(rand, xWidth * 2 + 1) - xWidth;
                }

                // The chest needs to be against a wall in a cardinal direction,
                // and it must have a floor underneath it.
                // Of course, there can't already be a chest in the location.
                // We'll just check that at this point by seeing if the chest
                // candidate location is not air.
                if(!world.isAirBlock(x - 1, yStart, z - 1) ||
                        !world.isAirBlock(x - 1, yStart, z + 1) ||
                        !world.isAirBlock(x + 1, yStart, z - 1) ||
                        !world.isAirBlock(x + 1, yStart, z + 1)) {
                    if(!world.isAirBlock(x, yStart - 1, z) &&
                            world.isAirBlock(x, yStart, z)) {

                        world.setBlock(x, yStart, z, Blocks.chest, 0, 2);
                        TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, yStart, z);

                        // Artifact from the vanilla Minecraft algorithm; we think the chest will just come back
                        // null if it can't be generated--likely from chances of creating a triple chest.
                        if (chest != null) {
                            WeightedRandomChestContent.generateChestContents(rand, table.getItems(rand), chest, table.getCount(rand));
                        }

                        break;
                    }
                }
            }
        }

        world.setBlock(xStart, yStart, zStart, Blocks.mob_spawner, 0, 2);
        TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(xStart, yStart, zStart);

        if (spawner != null) {
            spawner.func_145881_a().readFromNBT(tag);
        } else {
            System.err.println("Failed to fetch mob spawner entity at (" + xStart + ", " + yStart + ", " + zStart + ")");
        }

        return true;
    }

    private static int nextInt(Random rand, int v) {

        if (v <= 1) {
            return 0;
        }
        return rand.nextInt(v);
    }

    private boolean isWall(World world, int x, int y, int z) {

        int metadata = world.getBlockMetadata(x, y, z);
        return WeightedRandomBlock.isBlockContained(world.getBlock(x, y, z), metadata, walls);
    }

}
