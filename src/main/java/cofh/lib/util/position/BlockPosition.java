package cofh.lib.util.position;

import cofh.lib.util.helpers.BlockHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BlockPosition implements Comparable<BlockPosition>, Serializable {

	private static final long serialVersionUID = 8671402745765780610L;

	public int x;
	public int y;
	public int z;
	public EnumFacing orientation;

	public BlockPosition(int x, int y, int z) {

		this.x = x;
		this.y = y;
		this.z = z;
		orientation = null;
	}

	public BlockPosition(BlockPos pos) {

		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		orientation = null;
	}

	public BlockPosition(int x, int y, int z, EnumFacing orientation) {

		this.x = x;
		this.y = y;
		this.z = z;
		this.orientation = orientation;
	}

	public BlockPosition(BlockPos pos, EnumFacing orientation) {

		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.orientation = orientation;
	}

	public BlockPosition(BlockPosition p) {

		x = p.x;
		y = p.y;
		z = p.z;
		orientation = p.orientation;
	}

	public BlockPosition(NBTTagCompound tag) {

		x = tag.getInteger("bp_i");
		y = tag.getInteger("bp_j");
		z = tag.getInteger("bp_k");

		if (!tag.hasKey("bp_dir")) {
			orientation = null;
		} else {
			orientation = EnumFacing.VALUES[(tag.getByte("bp_dir"))];
		}
	}

	public BlockPosition(TileEntity tile) {

		x = tile.getPos().getX();
		y = tile.getPos().getY();
		z = tile.getPos().getZ();
		if (tile instanceof IRotateableTile) {
			orientation = ((IRotateableTile) tile).getDirectionFacing();
		} else {
			orientation = null;
		}
	}

	public static <T extends TileEntity & IRotateableTile> BlockPosition fromRotateableTile(T te) {

		return new BlockPosition(te);
	}

	public BlockPos pos() {

		return new BlockPos(x, y, z);
	}

	public BlockPosition copy() {

		return new BlockPosition(x, y, z, orientation);
	}

	public BlockPosition copy(EnumFacing orientation) {

		return new BlockPosition(x, y, z, orientation);
	}

	public BlockPosition setOrientation(EnumFacing o) {

		orientation = o;
		return this;
	}

	public BlockPosition step(int dir) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		x += d[0];
		y += d[1];
		z += d[2];
		return this;
	}

	public BlockPosition step(int dir, int dist) {

		int[] d = BlockHelper.SIDE_COORD_MOD[dir];
		x += d[0] * dist;
		y += d[1] * dist;
		z += d[2] * dist;
		return this;
	}

	public BlockPosition step(EnumFacing dir) {

		BlockPos pos = new BlockPos(x, y, z).offset(dir);
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		return this;
	}

	public BlockPosition step(EnumFacing dir, int dist) {

		BlockPos pos = new BlockPos(x, y, z).offset(dir, dist);
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		return this;
	}

	public BlockPosition moveForwards(int step) {

		switch (orientation) {
			case UP:
				y = y + step;
				break;
			case DOWN:
				y = y - step;
				break;
			case SOUTH:
				z = z + step;
				break;
			case NORTH:
				z = z - step;
				break;
			case EAST:
				x = x + step;
				break;
			case WEST:
				x = x - step;
				break;
			default:
		}
		return this;
	}

	public BlockPosition moveBackwards(int step) {

		return moveForwards(-step);
	}

	public BlockPosition moveRight(int step) {

		switch (orientation) {
			case UP:
			case SOUTH:
				x = x - step;
				break;
			case DOWN:
			case NORTH:
				x = x + step;
				break;
			case EAST:
				z = z + step;
				break;
			case WEST:
				z = z - step;
				break;
			default:
				break;
		}
		return this;
	}

	public BlockPosition moveLeft(int step) {

		return moveRight(-step);
	}

	public BlockPosition moveUp(int step) {

		switch (orientation) {
			case EAST:
			case WEST:
			case NORTH:
			case SOUTH:
				y = y + step;
				break;
			case UP:
				z = z - step;
				break;
			case DOWN:
				z = z + step;
			default:
				break;
		}
		return this;
	}

	public BlockPosition moveDown(int step) {

		return moveUp(-step);
	}

	public void writeToNBT(NBTTagCompound tag) {

		tag.setInteger("bp_i", x);
		tag.setInteger("bp_j", y);
		tag.setInteger("bp_k", z);
		tag.setByte("bp_dir", (byte) orientation.ordinal());
	}

	@Override
	public String toString() {

		if (orientation == null) {
			return "{" + x + ", " + y + ", " + z + "}";
		}
		return "{" + x + ", " + y + ", " + z + ";" + orientation.toString() + "}";
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof BlockPosition)) {
			return false;
		}
		BlockPosition bp = (BlockPosition) obj;
		return bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}

	// so compiler will optimize
	public boolean equals(BlockPosition bp) {

		return bp != null && bp.x == x & bp.y == y & bp.z == z & bp.orientation == orientation;
	}

	@Override
	public int hashCode() {

		return (x & 0xFFF) | (y & 0xFF << 8) | (z & 0xFFF << 12);
	}

	public BlockPosition min(BlockPosition p) {

		return new BlockPosition(p.x > x ? x : p.x, p.y > y ? y : p.y, p.z > z ? z : p.z);
	}

	public BlockPosition max(BlockPosition p) {

		return new BlockPosition(p.x < x ? x : p.x, p.y < y ? y : p.y, p.z < z ? z : p.z);
	}

	public List<BlockPosition> getAdjacent(boolean includeVertical) {

		List<BlockPosition> a = new ArrayList<BlockPosition>(4 + (includeVertical ? 2 : 0));
		a.add(copy(EnumFacing.EAST).moveForwards(1));
		a.add(copy(EnumFacing.WEST).moveForwards(1));
		a.add(copy(EnumFacing.SOUTH).moveForwards(1));
		a.add(copy(EnumFacing.NORTH).moveForwards(1));
		if (includeVertical) {
			a.add(copy(EnumFacing.UP).moveForwards(1));
			a.add(copy(EnumFacing.DOWN).moveForwards(1));
		}
		return a;
	}

	public boolean blockExists(World world) {

		return world.isBlockLoaded(new BlockPos(x, y, z));
	}

	public TileEntity getTileEntity(World world) {

		return world.getTileEntity(new BlockPos(x, y, z));
	}

	public Block getBlock(World world) {

		return getBlockState(world).getBlock();
	}

	public IBlockState getBlockState(World world) {

		return world.getBlockState(new BlockPos(x, y, z));
	}

	@SuppressWarnings ("unchecked")
	public <T> T getTileEntity(World world, Class<T> targetClass) {

		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static EnumFacing getDirection(int xS, int yS, int zS, int x, int y, int z) {

		int dir = 0;
		if (y < yS) {
			dir |= 1;
		} else if (y != yS) {
			dir |= 2;
		}
		if (z < zS) {
			dir |= 4;
		} else if (z != zS) {
			dir |= 8;
		}
		if (x < xS) {
			dir |= 16;
		} else if (x != xS) {
			dir |= 32;
		}
		switch (dir) {
			case 2:
				return EnumFacing.UP;
			case 1:
				return EnumFacing.DOWN;
			case 4:
				return EnumFacing.WEST;
			case 8:
				return EnumFacing.EAST;
			case 16:
				return EnumFacing.NORTH;
			case 32:
				return EnumFacing.SOUTH;
			default:
				return null;
		}
	}

	public static TileEntity getTileEntityRaw(World world, BlockPos pos) {

		if (!world.isBlockLoaded(pos)) {
			return null;
		}
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		TileEntity tileentity = chunk.chunkTileEntityMap.get(pos);
		return tileentity == null || tileentity.isInvalid() ? null : tileentity;
	}

	@SuppressWarnings ("unchecked")
	public static <T> T getTileEntityRaw(World world, BlockPos pos, Class<T> targetClass) {

		TileEntity te = getTileEntityRaw(world, pos);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	public static boolean blockExists(TileEntity start, EnumFacing dir) {

		BlockPos pos = start.getPos().offset(dir);
		return start.getWorld().isBlockLoaded(pos);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, EnumFacing dir) {

		BlockPos pos = start.getPos().offset(dir);
		return getTileEntityRaw(start.getWorld(), pos);
	}

	@SuppressWarnings ("unchecked")
	public static <T> T getAdjacentTileEntity(TileEntity start, EnumFacing direction, Class<T> targetClass) {

		TileEntity te = getAdjacentTileEntity(start, direction);
		if (targetClass.isInstance(te)) {
			return (T) te;
		} else {
			return null;
		}
	}

	/* Comparable */
	@Override
	public int compareTo(BlockPosition other) {

		return this.x == other.x ? this.y == other.y ? this.z - other.z : this.y - other.y : this.x - other.x;
	}

}
