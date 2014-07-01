package cofh.api.item;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface IAugmentItem {

	/**
	 * Get the augmentation level for a given Augment and Augment Type.
	 * 
	 * @param stack
	 *            ItemStack representing the Augment.
	 * @param type
	 *            String containing the Augment type name.
	 * @return The Augment level of the stack for the requested type - 0 if it does not affect that attribute.
	 */
	int getAugmentLevel(ItemStack stack, String type);

	/**
	 * Get the Augment Types for a given Augment.
	 * 
	 * @param stack
	 *            ItemStack representing the Augment.
	 * @return List of the Augmentation Types. Should return an empty list if there are none (but this would be really stupid to make). DO NOT RETURN NULL.
	 */
	List<String> getAugmentTypes(ItemStack stack);

}
