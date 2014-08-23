package cofh.lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

/**
 * This class essentially allows for ores to be generated in clusters, with Features randomly choosing one or more blocks from a weighted list.
 * 
 * @author King Lemming
 * 
 */
public final class WeightedRandomBlock extends WeightedRandom.Item {

	public final Block block;
	public final int metadata;

	public WeightedRandomBlock(ItemStack ore) {

		this(ore, 100);
	}

	public WeightedRandomBlock(ItemStack ore, int weight) {

		this(Block.getBlockFromItem(ore.getItem()), ore.getItemDamage(), weight);
	}

	public WeightedRandomBlock(Block ore) {

		this(ore, 0, 100); // some blocks do not have associated items
	}

	public WeightedRandomBlock(Block ore, int metadata) {

		this(ore, metadata, 100);
	}

	public WeightedRandomBlock(Block ore, int metadata, int weight) {

		super(weight);
		this.block = ore;
		this.metadata = metadata;
	}

}
