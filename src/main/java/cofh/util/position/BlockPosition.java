package cofh.util.position;

import cofh.util.BlockHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPosition implements Comparable<BlockPosition>, Serializable {

	private static final long serialVersionUID = 8671402745765780610L;

	public int x;
	public int y;
	public int z;
	public ForgeDirection orientation;

	public BlockPosition(int x, int y, int z) {

		this.x = x;
		this.y = y;
		this.z = z;
		orientation = ForgeDirection.UNKNOWN;
	}

	public BlockPosition(int x, int y, int z, ForgeDirection corientation) {

		this.x = x;
		this.y = y;
		this.z = z;
		orientation = corientation;
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
			orientation = ForgeDirection.UNKNOWN;
		} else {
			orientation = ForgeDirection.getOrientation(tag.getByte("bp_dir"));
		}
	}

	public BlockPosition(TileEntity tile) {

		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		if (tile instanceof IRotateableTile) {
			orientation = ((IRotateableTile) tile).getDirectionFacing();
		} else {
			orientation = ForgeDirection.UNKNOWN;
		}
	}

	public static BlockPosition fromFactoryTile(IRotateableTile te) {

		return new BlockPosition((TileEntity) te);
	}

	public BlockPosition copy() {

		return new BlockPosition(x, y, z, orientation);
	}

	public void step(int dir) {

		x += BlockHelper.SIDE_COORD_MOD[dir][0];
		y += BlockHelper.SIDE_COORD_MOD[dir][1];
		z += BlockHelper.SIDE_COORD_MOD[dir][2];
	}

	public void step(int dir, int dist) {

		x += BlockHelper.SIDE_COORD_MOD[dir][0] * dist;
		y += BlockHelper.SIDE_COORD_MOD[dir][1] * dist;
		z += BlockHelper.SIDE_COORD_MOD[dir][2] * dist;
	}

	public void step(ForgeDirection dir) {

		x += dir.offsetX;
		y += dir.offsetY;
		z += dir.offsetZ;
	}

	public void step(ForgeDirection dir, int dist) {

		x += dir.offsetX * dist;
		y += dir.offsetY * dist;
		z += dir.offsetZ * dist;
	}

	public void moveForwards(int step) {

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
	}

	public void moveBackwards(int step) {

		moveForwards(-step);
	}

	public void moveRight(int step) {

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
	}

	public void moveLeft(int step) {

		moveRight(-step);
	}

	public void moveUp(int step) {

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

	}

	public void moveDown(int step) {

		moveUp(-step);
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
		return bp.x == x && bp.y == y && bp.z == z && bp.orientation == orientation;
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
		a.add(new BlockPosition(x + 1, y, z, ForgeDirection.EAST));
		a.add(new BlockPosition(x - 1, y, z, ForgeDirection.WEST));
		a.add(new BlockPosition(x, y, z + 1, ForgeDirection.SOUTH));
		a.add(new BlockPosition(x, y, z - 1, ForgeDirection.NORTH));
		if (includeVertical) {
			a.add(new BlockPosition(x, y + 1, z, ForgeDirection.UP));
			a.add(new BlockPosition(x, y - 1, z, ForgeDirection.DOWN));
		}
		return a;
	}

	public TileEntity getTileEntity(World world) {

		return world.getTileEntity(x, y, z);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection direction) {

		BlockPosition p = new BlockPosition(start);
		p.orientation = direction;
		p.moveForwards(1);
		return start.getWorldObj().getTileEntity(p.x, p.y, p.z);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity start, ForgeDirection direction, Class<? extends TileEntity> targetClass) {

		TileEntity te = getAdjacentTileEntity(start, direction);
		if (targetClass.isAssignableFrom(te.getClass())) {
			return te;
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
