package cofh.lib.util;

import net.minecraft.block.Block;

/**
 * Wrapper for a Block/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 * 
 * @author King Lemming
 * 
 */
public final class BlockWrapper {

	public Block block;
	public int metadata;

	public BlockWrapper(Block block, int metadata) {

		this.block = block;
		this.metadata = metadata;
	}

	public BlockWrapper set(Block block, int metadata) {

		if (block != null) {
			this.block = block;
			this.metadata = metadata;
		} else {
			this.block = null;
			this.metadata = 0;
		}
		return this;
	}

	public boolean isEqual(BlockWrapper other) {

		return other != null && block == other.block && metadata == other.metadata;
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof BlockWrapper)) {
			return false;
		}
		return isEqual((BlockWrapper) o);
	}

	@Override
	public int hashCode() {

		return metadata | Block.getIdFromBlock(block) << 16;
	}

}
