package cofh.lib.util.position;

import cofh.lib.util.helpers.BlockHelper;

import java.io.Serializable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 * Standardized implementation for representing and manipulating Chunk Coordinates. Provides standard Java Collection interaction.
 *
 * @author King Lemming
 *
 */
public final class ChunkCoord implements Comparable<ChunkCoord>, Serializable {

	private static final long serialVersionUID = -9154178151445196959L;

	public int chunkX;
	public int chunkZ;

	public ChunkCoord(Chunk chunk) {

		this.chunkX = chunk.xPosition;
		this.chunkZ = chunk.zPosition;
	}

	public ChunkCoord(BlockPos c) {

		this(c.getX() >> 4, c.getZ() >> 4);
	}

	public ChunkCoord(int x, int z) {

		this.chunkX = x;
		this.chunkZ = z;
	}

	public int getCenterX() {

		return (this.chunkX << 4) + 8;
	}

	public int getCenterZ() {

		return (this.chunkZ << 4) + 8;
	}

	public void step(EnumFacing dir) {

		chunkX += dir.getDirectionVec().getX();
		chunkZ += dir.getDirectionVec().getZ();
	}

	public void step(EnumFacing dir, int dist) {

		this.chunkX += dir.getDirectionVec().getX() * dist;
		this.chunkZ += dir.getDirectionVec().getZ() * dist;
	}

	public ChunkCoord copy() {

		return new ChunkCoord(chunkX, chunkZ);
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof ChunkCoord)) {
			return false;
		}
		ChunkCoord other = (ChunkCoord) obj;
		return this.chunkX == other.chunkX && this.chunkZ == other.chunkZ;
	}

	@Override
	public int hashCode() {

		int hash = chunkX;
		hash *= 31 + this.chunkZ;
		return hash;
	}

	@Override
	public String toString() {

		return "[" + this.chunkX + ", " + this.chunkZ + "]";
	}

	/* Comparable */
	@Override
	public int compareTo(ChunkCoord other) {

		return this.chunkX == other.chunkX ? this.chunkZ - other.chunkZ : this.chunkX - other.chunkX;
	}

}
