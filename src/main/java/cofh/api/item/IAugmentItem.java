package cofh.api.item;

import cofh.api.core.IAugmentable;

import net.minecraft.item.ItemStack;

public interface IAugmentItem {

	public enum AugmentType {
		CONTROL, EFFICIENCY, ENDER, ENERGY, FLEXIBILITY, POTENCY, RADIUS, REDSTONE, SPEED
	}

	/**
	 * Check to see if a given Augment can be installed in a given Tile Entity.
	 * 
	 * @param stack
	 *            ItemStack representing the Augment.
	 * @param tile
	 *            Augmentable Tile receiving the ItemStack.
	 * @return TRUE if the requisite conditions are met.
	 */
	boolean canInstall(ItemStack stack, IAugmentable tile);

	/**
	 * Get the augmentation level for a given Augment.
	 * 
	 * @param stack
	 *            ItemStack representing the Augment.
	 * @param type
	 *            Augment type.
	 * @return The Augment level of the stack for the requested type - 0 if it does not affect that attribute.
	 */
	int getAugmentLevel(ItemStack stack, AugmentType type);

}
