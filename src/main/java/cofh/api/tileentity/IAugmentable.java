package cofh.api.tileentity;

import cofh.api.item.IAugmentItem;
import net.minecraft.item.ItemStack;

/**
 * Implemented on objects which support Augments - these are modular and removable items which provide boosts or alterations to functionality.
 *
 * Effects of this are determined by the object itself and should be checked vs the Augment Type denoted in {@link IAugmentItem}.
 *
 * @author King Lemming
 */
public interface IAugmentable {

	/**
	 * Attempt to install a specific augment in the (Tile) Entity.
	 *
	 * Returns TRUE if augment was installed properly.
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
