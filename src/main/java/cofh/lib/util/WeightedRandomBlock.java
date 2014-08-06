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

		super(weight);
		this.block = Block.getBlockFromItem(ore.getItem());
		this.metadata = ore.getItemDamage();
	}

}
