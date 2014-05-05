package cofh.util;

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
	public int hashcode;

	public static int getHashCode(Block block, int metadata) {

		return block.hashCode() * 31 + metadata;
	}

	public BlockWrapper(Block block, int metadata) {

		this.block = block;
		this.metadata = metadata;
		this.hashcode = block.hashCode() * 31 + metadata;
	}

	@Override
	public int hashCode() {

		return hashcode;
	}

}
