package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface IAugmentItem {

	/**
	 * Get the augmentation level for a given Augment and Augment Type.
	 *
	 * @param stack ItemStack representing the Augment.
	 * @param type  String containing the Augment type name.
	 * @return The Augment level of the stack for the requested type - 0 if it does not affect that attribute.
	 */
	int getAugmentLevel(ItemStack stack, String type);

	/**
	 * Get the Augment Types for a given Augment.
	 *
	 * @param stack ItemStack representing the Augment.
	 * @return Augment Type of the stack.
	 */
	String getAugmentType(ItemStack stack);

}
