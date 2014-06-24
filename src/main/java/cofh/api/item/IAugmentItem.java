package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface IAugmentItem {

	public enum AugmentType {
		CONTROL, EFFICIENCY, ENDER, ENERGY, FLEXIBILITY, POTENCY, RANGE, REDSTONE, SPEED
	}

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
