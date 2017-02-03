package cofh.api.tileentity;

import net.minecraft.item.ItemStack;

/**
 * Implemented on objects which support Augments.
 *
 * @author King Lemming
 */
public interface IAugmentable {

	/**
	 * Attempt to install a specific augment in the (Tile) Entity.
	 */
	boolean installAugment(ItemStack augment);

	/**
	 * Returns an array of the Augment slots for this (Tile) Entity.
	 */
	ItemStack[] getAugmentSlots();

	/**
	 * Returns a status array for the Augmentations installed in the (Tile) Entity.
	 */
	boolean[] getAugmentStatus();

}
