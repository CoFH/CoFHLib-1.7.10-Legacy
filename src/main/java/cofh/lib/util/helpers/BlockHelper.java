package cofh.lib.util.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains various helper functions to assist with {@link Block} and Block-related manipulation and interaction.
 *
 * @author King Lemming
 */
public final class BlockHelper {

	private BlockHelper() {

	}

	public static int MAX_ID = 1024;
	public static byte[] rotateType = new byte[MAX_ID];
	public static final int[][] SIDE_COORD_MOD = { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { -1, 0, 0 }, { 1, 0, 0 } };
	public static float[][] SIDE_COORD_AABB = { { 1, -2, 1 }, { 1, 2, 1 }, { 1, 1, 1 }, { 1, 1, 2 }, { 1, 1, 1 }, { 2, 1, 1 } };
	public static final byte[] SIDE_LEFT = { 4, 5, 5, 4, 2, 3 };
	public static final byte[] SIDE_RIGHT = { 5, 4, 4, 5, 3, 2 };
	public static final byte[] SIDE_OPPOSITE = { 1, 0, 3, 2, 5, 4 };
	public static final byte[] SIDE_ABOVE = { 3, 2, 1, 1, 1, 1 };
	public static final byte[] SIDE_BELOW = { 2, 3, 0, 0, 0, 0 };

	// These assume facing is towards negative - looking AT side 1, 3, or 5.
	public static final byte[] ROTATE_CLOCK_Y = { 0, 1, 4, 5, 3, 2 };
	public static final byte[] ROTATE_CLOCK_Z = { 5, 4, 2, 3, 0, 1 };
	public static final byte[] ROTATE_CLOCK_X = { 2, 3, 1, 0, 4, 5 };

	public static final byte[] ROTATE_COUNTER_Y = { 0, 1, 5, 4, 2, 3 };
	public static final byte[] ROTATE_COUNTER_Z = { 4, 5, 2, 3, 1, 0 };
	public static final byte[] ROTATE_COUNTER_X = { 3, 2, 0, 1, 4, 5 };

	public static final byte[] INVERT_AROUND_Y = { 0, 1, 3, 2, 5, 4 };
	public static final byte[] INVERT_AROUND_Z = { 1, 0, 2, 3, 5, 4 };
	public static final byte[] INVERT_AROUND_X = { 1, 0, 3, 2, 4, 5 };

	// Map which gives relative Icon to use on a block which can be placed on any side.
	public static final byte[][] ICON_ROTATION_MAP = new byte[6][];

	static {
		ICON_ROTATION_MAP[0] = new byte[] { 0, 1, 2, 3, 4, 5 };
		ICON_ROTATION_MAP[1] = new byte[] { 1, 0, 2, 3, 4, 5 };
		ICON_ROTATION_MAP[2] = new byte[] { 3, 2, 0, 1, 4, 5 };
		ICON_ROTATION_MAP[3] = new byte[] { 3, 2, 1, 0, 5, 4 };
		ICON_ROTATION_MAP[4] = new byte[] { 3, 2, 5, 4, 0, 1 };
		ICON_ROTATION_MAP[5] = new byte[] { 3, 2, 4, 5, 1, 0 };
	}

	public static final class RotationType {

		public static final int PREVENT = -1;
		public static final int FOUR_WAY = 1;
		public static final int SIX_WAY = 2;
		public static final int RAIL = 3;
		public static final int PUMPKIN = 4;
		public static final int STAIRS = 5;
		public static final int REDSTONE = 6;
		public static final int LOG = 7;
		public static final int SLAB = 8;
		public static final int CHEST = 9;
		public static final int LEVER = 10;
		public static final int SIGN = 11;

		private RotationType() {

		}
	}

	static { // TODO: review which of these can be removed in favor of the vanilla handler
		rotateType[Block.getIdFromBlock(Blocks.BED)] = RotationType.PREVENT;

		rotateType[Block.getIdFromBlock(Blocks.STONE_SLAB)] = RotationType.SLAB;
		rotateType[Block.getIdFromBlock(Blocks.WOODEN_SLAB)] = RotationType.SLAB;

		rotateType[Block.getIdFromBlock(Blocks.RAIL)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.GOLDEN_RAIL)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.DETECTOR_RAIL)] = RotationType.RAIL;
		rotateType[Block.getIdFromBlock(Blocks.ACTIVATOR_RAIL)] = RotationType.RAIL;

		rotateType[Block.getIdFromBlock(Blocks.PUMPKIN)] = RotationType.PUMPKIN;
		rotateType[Block.getIdFromBlock(Blocks.LIT_PUMPKIN)] = RotationType.PUMPKIN;

		rotateType[Block.getIdFromBlock(Blocks.FURNACE)] = RotationType.FOUR_WAY;
		rotateType[Block.getIdFromBlock(Blocks.LIT_FURNACE)] = RotationType.FOUR_WAY;
		rotateType[Block.getIdFromBlock(Blocks.ENDER_CHEST)] = RotationType.FOUR_WAY;

		rotateType[Block.getIdFromBlock(Blocks.TRAPPED_CHEST)] = RotationType.CHEST;
		rotateType[Block.getIdFromBlock(Blocks.CHEST)] = RotationType.CHEST;

		rotateType[Block.getIdFromBlock(Blocks.DISPENSER)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.STICKY_PISTON)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.PISTON)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.HOPPER)] = RotationType.SIX_WAY;
		rotateType[Block.getIdFromBlock(Blocks.DROPPER)] = RotationType.SIX_WAY;

		rotateType[Block.getIdFromBlock(Blocks.UNPOWERED_REPEATER)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.UNPOWERED_COMPARATOR)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.POWERED_REPEATER)] = RotationType.REDSTONE;
		rotateType[Block.getIdFromBlock(Blocks.POWERED_COMPARATOR)] = RotationType.REDSTONE;

		rotateType[Block.getIdFromBlock(Blocks.LEVER)] = RotationType.LEVER;

		rotateType[Block.getIdFromBlock(Blocks.STANDING_SIGN)] = RotationType.SIGN;

		rotateType[Block.getIdFromBlock(Blocks.OAK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.STONE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.BRICK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.STONE_BRICK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.NETHER_BRICK_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.SANDSTONE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.SPRUCE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.BIRCH_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.JUNGLE_STAIRS)] = RotationType.STAIRS;
		rotateType[Block.getIdFromBlock(Blocks.QUARTZ_STAIRS)] = RotationType.STAIRS;
	}

	public static int getMicroBlockAngle(int side, float hitX, float hitY, float hitZ) {

		int direction = side ^ 1;
		float degreeCenter = 0.32f / 2;

		float x = 0, y = 0;
		switch (side >> 1) {
			case 0:
				x = hitX;
				y = hitZ;
				break;
			case 1:
				x = hitX;
				y = hitY;
				break;
			case 2:
				x = hitY;
				y = hitZ;
				break;
		}
		x -= .5f;
		y -= .5f;

		if (x * x + y * y > degreeCenter * degreeCenter) {

			int a = (int) ((Math.atan2(x, y) + Math.PI) * 4 / Math.PI);
			a = ++a & 7;
			switch (a >> 1) {
				case 0:
				case 4:
					direction = 2;
					break;
				case 1:
					direction = 4;
					break;
				case 2:
					direction = 3;
					break;
				case 3:
					direction = 5;
					break;
			}
		}
		return direction;
	}

	public static EnumFacing getMicroBlockAngle(EnumFacing side, float hitX, float hitY, float hitZ) {

		return EnumFacing.VALUES[getMicroBlockAngle(side.ordinal(), hitX, hitY, hitZ)];
	}

	public static int getHighestY(World world, int x, int z) {

		return world.getChunkFromBlockCoords(new BlockPos(x, 0, z)).getTopFilledSegment() + 16;
	}

	public static int getSurfaceBlockY(World world, int x, int z) {

		int y = world.getChunkFromBlockCoords(new BlockPos(x, 0, z)).getTopFilledSegment() + 16;

		BlockPos pos;
		IBlockState state;
		Block block;
		do {
			if (--y < 0) {
				break;
			}
			pos = new BlockPos(x, y, z);
			state = world.getBlockState(pos);
			block = state.getBlock();
		}
		while (block.isAir(state, world, pos) || block.isReplaceable(world, pos) || block.isLeaves(state, world, pos) || block.isFoliage(world, pos) || block.canBeReplacedByLeaves(state, world, pos));
		return y;
	}

	public static int getTopBlockY(World world, int x, int z) {

		int y = world.getChunkFromBlockCoords(new BlockPos(x, 0, z)).getTopFilledSegment() + 16;

		BlockPos pos;
		IBlockState state;
		Block block;
		do {
			if (--y < 0) {
				break;
			}
			pos = new BlockPos(x, y, z);
			state = world.getBlockState(pos);
			block = state.getBlock();
		} while (block.isAir(state, world, pos));
		return y;
	}

	//TODO RayTracer
	@Deprecated
	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player, double distance, boolean fluid) {

		Vec3d posVec = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3d lookVec = player.getLook(1);
		lookVec = posVec.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
		return player.worldObj.rayTraceBlocks(posVec, lookVec, fluid);
	}

	@Deprecated
	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player, double distance) {

		return getCurrentMovingObjectPosition(player, distance, false);
	}

	@Deprecated
	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player, boolean fluid) {

		return getCurrentMovingObjectPosition(player, player.capabilities.isCreativeMode ? 5.0F : 4.5F, fluid);
	}

	@Deprecated
	public static RayTraceResult getCurrentMovingObjectPosition(EntityPlayer player) {

		return getCurrentMovingObjectPosition(player, player.capabilities.isCreativeMode ? 5.0F : 4.5F, false);
	}

	public static EnumFacing getCurrentMousedOverSide(EntityPlayer player) {

		RayTraceResult mouseOver = getCurrentMovingObjectPosition(player);
		return mouseOver == null ? EnumFacing.DOWN : mouseOver.sideHit;
	}

	public static int determineXZPlaceFacing(EntityLivingBase living) {

		int quadrant = MathHelper.floor_double(living.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		switch (quadrant) {
			case 0:
				return 2;
			case 1:
				return 5;
			case 2:
				return 3;
			case 3:
				return 4;
		}
		return 3;
	}

	public static boolean isEqual(Block blockA, Block blockB) {

		if (blockA == blockB) {
			return true;
		}
		if (blockA == null | blockB == null) {
			return false;
		}
		return blockA.equals(blockB) || blockA.isAssociatedBlock(blockB);
	}

	/* UNSAFE Tile Entity Retrieval */
	// public static TileEntity getAdjacentTileEntityUnsafe(World world, int x, int y, int z, ForgeDirection dir) {
	//
	// if (world == null) {
	// return null;
	// }
	// Chunk chunk = world.getChunkFromBlockCoords(x + dir.offsetX, z + dir.offsetZ);
	// return chunk == null ? null : chunk.getChunkBlockTileEntityUnsafe((x + dir.offsetX) & 0xF, y + dir.offsetY, (z + dir.offsetZ) & 0xF);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(World world, int x, int y, int z, int side) {
	//
	// return world == null ? null : getAdjacentTileEntityUnsafe(world, x, y, z, ForgeDirection.values()[side]);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(TileEntity refTile, ForgeDirection dir) {
	//
	// return refTile == null ? null : getAdjacentTileEntityUnsafe(refTile.worldObj, refTile.xCoord, refTile.yCoord, refTile.zCoord, dir);
	// }
	//
	// public static TileEntity getAdjacentTileEntityUnsafe(TileEntity refTile, int side) {
	//
	// return refTile == null ? null : getAdjacentTileEntityUnsafe(refTile.worldObj, refTile.xCoord, refTile.yCoord, refTile.zCoord,
	// ForgeDirection.values()[side]);
	// }

	public static IBlockState getAdjacentBlock(World world, BlockPos pos, EnumFacing dir) {

		pos = pos.offset(dir);
		return world == null || !world.isBlockLoaded(pos) ? Blocks.AIR.getDefaultState() : world.getBlockState(pos);
	}

	public static IBlockState getAdjacentBlock(World world, BlockPos pos, int side) {

		return world == null ? Blocks.AIR.getDefaultState() : getAdjacentBlock(world, pos, EnumFacing.VALUES[side]);
	}

	/* Safe Tile Entity Retrieval */
	public static TileEntity getAdjacentTileEntity(World world, BlockPos pos, EnumFacing dir) {

		pos = pos.offset(dir);
		return world == null || !world.isBlockLoaded(pos) ? null : world.getTileEntity(pos);
	}

	public static TileEntity getAdjacentTileEntity(World world, BlockPos pos, int side) {

		return world == null ? null : getAdjacentTileEntity(world, pos, EnumFacing.VALUES[side]);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity refTile, EnumFacing dir) {

		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorld(), refTile.getPos(), dir);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity refTile, int side) {

		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorld(), refTile.getPos(), EnumFacing.VALUES[side]);
	}

	public static int determineAdjacentSide(TileEntity refTile, BlockPos pos) {

		return pos.getY() > refTile.getPos().getY() ? 1 : pos.getY() < refTile.getPos().getY() ? 0 : pos.getZ() > refTile.getPos().getZ() ? 3 : pos.getZ() < refTile.getPos().getZ() ? 2 : pos.getX() > refTile.getPos().getX() ? 5 : 4;
	}

	/* COORDINATE TRANSFORM */
	public static int[] getAdjacentCoordinatesForSide(RayTraceResult trace) {

		BlockPos pos = trace.getBlockPos();
		return getAdjacentCoordinatesForSide(pos.getX(), pos.getY(), pos.getZ(), trace.sideHit.ordinal());
	}

	public static int[] getAdjacentCoordinatesForSide(int x, int y, int z, int side) {

		return new int[] { x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2] };
	}

	public static AxisAlignedBB getAdjacentAABBForSide(RayTraceResult trace) {

		BlockPos pos = trace.getBlockPos();
		return getAdjacentAABBForSide(pos.getX(), pos.getY(), pos.getZ(), trace.sideHit.ordinal());
	}

	public static AxisAlignedBB getAdjacentAABBForSide(BlockPos pos, EnumFacing side) {

		return getAdjacentAABBForSide(pos, side.ordinal());
	}

	public static AxisAlignedBB getAdjacentAABBForSide(BlockPos pos, int side) {

		return getAdjacentAABBForSide(pos.getX(), pos.getY(), pos.getZ(), side);
	}

	public static AxisAlignedBB getAdjacentAABBForSide(int x, int y, int z, int side) {

		return new AxisAlignedBB(x + SIDE_COORD_MOD[side][0], y + SIDE_COORD_MOD[side][1], z + SIDE_COORD_MOD[side][2], x + SIDE_COORD_AABB[side][0], y + SIDE_COORD_AABB[side][1], z + SIDE_COORD_AABB[side][2]);
	}

	public static int getLeftSide(int side) {

		return SIDE_LEFT[side];
	}

	public static int getRightSide(int side) {

		return SIDE_RIGHT[side];
	}

	public static int getOppositeSide(int side) {

		return SIDE_OPPOSITE[side];
	}

	public static int getAboveSide(int side) {

		return SIDE_ABOVE[side];
	}

	public static int getBelowSide(int side) {

		return SIDE_BELOW[side];
	}

	/* BLOCK ROTATION */
	public static boolean canRotate(Block block) {

		return Block.getIdFromBlock(block) < MAX_ID && rotateType[Block.getIdFromBlock(block)] != 0;
	}

	public static IBlockState rotateVanillaBlock(World world, IBlockState state, BlockPos pos) {

		int bId = Block.getIdFromBlock(state.getBlock()), bMeta = state.getBlock().getMetaFromState(state);
		Block block = state.getBlock();
		switch (rotateType[bId]) {
			case RotationType.FOUR_WAY:
				return block.getStateFromMeta(SIDE_LEFT[bMeta]);
			case RotationType.SIX_WAY:
				if (bMeta < 6) {
					return block.getStateFromMeta(++bMeta % 6);
				}
				return block.getStateFromMeta(bMeta);
			case RotationType.RAIL:
				if (bMeta < 2) {
					return block.getStateFromMeta(++bMeta % 2);
				}
				return block.getStateFromMeta(bMeta);
			case RotationType.PUMPKIN:
				return block.getStateFromMeta(++bMeta % 4);
			case RotationType.STAIRS:
				return block.getStateFromMeta(++bMeta % 8);
			case RotationType.REDSTONE:
				int upper = bMeta & 0xC;
				int lower = bMeta & 0x3;
				return block.getStateFromMeta(upper + ++lower % 4);
			case RotationType.LOG:
				return block.getStateFromMeta((bMeta + 4) % 12);
			case RotationType.SLAB:
				return block.getStateFromMeta((bMeta + 8) % 16);
			case RotationType.CHEST:
				BlockPos offsetPos;
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					offsetPos = pos.offset(facing);
					if (isEqual(world.getBlockState(offsetPos).getBlock(), state.getBlock())) {
						world.setBlockState(offsetPos, state.getBlock().getStateFromMeta(SIDE_OPPOSITE[bMeta]), 1);
						return block.getStateFromMeta(SIDE_OPPOSITE[bMeta]);
					}
				}
				return block.getStateFromMeta(SIDE_LEFT[bMeta]);
			case RotationType.LEVER:
				int shift = 0;
				if (bMeta > 7) {
					bMeta -= 8;
					shift = 8;
				}
				if (bMeta == 5) {
					return block.getStateFromMeta(6 + shift);
				} else if (bMeta == 6) {
					return block.getStateFromMeta(5 + shift);
				} else if (bMeta == 7) {
					return block.getStateFromMeta(0 + shift);
				} else if (bMeta == 0) {
					return block.getStateFromMeta(7 + shift);
				}
				return block.getStateFromMeta(bMeta + shift);
			case RotationType.SIGN:
				return block.getStateFromMeta(++bMeta % 16);
			case RotationType.PREVENT:
			default:
				return block.getStateFromMeta(bMeta);
		}
	}

	public static IBlockState rotateVanillaBlockAlt(World world, IBlockState state, BlockPos pos) {

		int bId = Block.getIdFromBlock(state.getBlock()), bMeta = state.getBlock().getMetaFromState(state);
		Block block = state.getBlock();

		switch (rotateType[bId]) {
			case RotationType.FOUR_WAY:
				return block.getStateFromMeta(SIDE_RIGHT[bMeta]);
			case RotationType.SIX_WAY:
				if (bMeta < 6) {
					return block.getStateFromMeta((bMeta + 5) % 6);
				}
				return block.getStateFromMeta(bMeta);
			case RotationType.RAIL:
				if (bMeta < 2) {
					return block.getStateFromMeta(++bMeta % 2);
				}
				return block.getStateFromMeta(bMeta);
			case RotationType.PUMPKIN:
				return block.getStateFromMeta((bMeta + 3) % 4);
			case RotationType.STAIRS:
				return block.getStateFromMeta((bMeta + 7) % 8);
			case RotationType.REDSTONE:
				int upper = bMeta & 0xC;
				int lower = bMeta & 0x3;
				return block.getStateFromMeta(upper + (lower + 3) % 4);
			case RotationType.LOG:
				return block.getStateFromMeta((bMeta + 8) % 12);
			case RotationType.SLAB:
				return block.getStateFromMeta((bMeta + 8) % 16);
			case RotationType.CHEST:
				BlockPos offsetPos;
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					offsetPos = pos.offset(facing);
					if (isEqual(world.getBlockState(offsetPos).getBlock(), block)) {
						world.setBlockState(pos, block.getStateFromMeta(SIDE_OPPOSITE[bMeta]), 1);
						return block.getStateFromMeta(SIDE_OPPOSITE[bMeta]);
					}
				}
				return block.getStateFromMeta(SIDE_RIGHT[bMeta]);
			case RotationType.LEVER:
				int shift = 0;
				if (bMeta > 7) {
					bMeta -= 8;
					shift = 8;
				}
				if (bMeta == 5) {
					return block.getStateFromMeta(6 + shift);
				} else if (bMeta == 6) {
					return block.getStateFromMeta(5 + shift);
				} else if (bMeta == 7) {
					return block.getStateFromMeta(0 + shift);
				} else if (bMeta == 0) {
					return block.getStateFromMeta(7 + shift);
				}
			case RotationType.SIGN:
				return block.getStateFromMeta(++bMeta % 16);
			case RotationType.PREVENT:
			default:
				return block.getStateFromMeta(bMeta);
		}
	}

	public static List<ItemStack> breakBlock(World worldObj, BlockPos pos, IBlockState state, int fortune, boolean doBreak, boolean silkTouch) {

		return breakBlock(worldObj, null, pos, state, fortune, doBreak, silkTouch);
	}

	public static List<ItemStack> breakBlock(World worldObj, EntityPlayer player, BlockPos pos, IBlockState state, int fortune, boolean doBreak, boolean silkTouch) {

		if (state.getBlockHardness(worldObj, pos) == -1) {
			return new LinkedList<>();
		}
		List<ItemStack> stacks = null;
		if (silkTouch && state.getBlock().canSilkHarvest(worldObj, pos, state, player)) {
			stacks = new LinkedList<>();
			stacks.add(createStackedBlock(state));
		} else {
			stacks = state.getBlock().getDrops(worldObj, pos, state, fortune);
		}
		if (!doBreak) {
			return stacks;
		}
		//worldObj.playAuxSFXAtEntity(player, 2001, x, y, z, Block.getIdFromBlock(state) + (meta << 12));
		worldObj.playEvent(2001, pos, Block.getStateId(state));
		worldObj.setBlockToAir(pos);

		List<EntityItem> result = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3));
		for (EntityItem entity : result) {
			if (entity.isDead || entity.getEntityItem().stackSize <= 0) {
				continue;
			}
			stacks.add(entity.getEntityItem());
			entity.worldObj.removeEntity(entity);
		}
		return stacks;
	}

	public static ItemStack createStackedBlock(IBlockState state) {

		Item item = Item.getItemFromBlock(state.getBlock());
		if (item.getHasSubtypes()) {
			return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
		}
		return new ItemStack(item, 1, 0);
	}

}
