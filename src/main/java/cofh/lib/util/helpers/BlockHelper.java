package cofh.lib.util.helpers;

import static net.minecraft.util.EnumFacing.*;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public final class BlockHelper {

	private BlockHelper() {

	}

	public static final byte[] SIDE_LEFT = { 4, 5, 5, 4, 2, 3 };
	public static final byte[] SIDE_RIGHT = { 5, 4, 4, 5, 3, 2 };
	public static final byte[] SIDE_OPPOSITE = { 1, 0, 3, 2, 5, 4 };
	public static final byte[] SIDE_ABOVE = { 3, 2, 1, 1, 1, 1 };
	public static final byte[] SIDE_BELOW = { 2, 3, 0, 0, 0, 0 };

	public static final EnumFacing[] ENUM_SIDE_LEFT = { WEST, EAST, EAST, WEST, NORTH, SOUTH };
	public static final EnumFacing[] ENUM_SIDE_RIGHT = { EAST, WEST, WEST, EAST, SOUTH, NORTH };
	public static final EnumFacing[] ENUM_SIDE_OPPOSITE = { UP, DOWN, SOUTH, NORTH, EAST, WEST };
	public static final EnumFacing[] ENUM_SIDE_ABOVE = { SOUTH, NORTH, UP, UP, UP, UP };
	public static final EnumFacing[] ENUM_SIDE_BELOW = { NORTH, SOUTH, DOWN, DOWN, DOWN, DOWN };

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

	public static Block getAdjacentBlock(World world, BlockPos pos, EnumFacing direction) {

		return world == null || !world.isBlockLoaded(pos) ? Blocks.AIR : world.getBlockState(pos.add(direction.getDirectionVec())).getBlock();
	}

	public static Block getAdjacentBlock(TileEntity refTile, EnumFacing direction) {

		return refTile == null ? null : getAdjacentBlock(refTile.getWorld(), refTile.getPos(), direction);
	}

	public static TileEntity getAdjacentTileEntity(World world, BlockPos pos, EnumFacing direction) {

		return world == null || !world.isBlockLoaded(pos) ? null : world.getTileEntity(pos.add(direction.getDirectionVec()));
	}

	public static TileEntity getAdjacentTileEntity(TileEntity refTile, EnumFacing direction) {

		return refTile == null ? null : getAdjacentTileEntity(refTile.getWorld(), refTile.getPos(), direction);
	}

}
