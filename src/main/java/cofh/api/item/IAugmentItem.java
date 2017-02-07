package cofh.api.item;

import net.minecraft.item.ItemStack;

public interface IAugmentItem {

	/**
	 * Enum for Augment Types - general level of "power" of an Augment.
	 */
	enum AugmentType {
		BASIC, ADVANCED, CREATIVE
	}

	/**
	 * Get the Augment Type for a given Augment.
	 *
	 * @param stack ItemStack representing the Augment.
	 * @return Augment Type of the stack.
	 */
	AugmentType getAugmentType(ItemStack stack);


	/**
	 * Get the Augment Identifier for a given Augment. This is simply a string with some description of what the Augment does. Individual
	 *
	 * @param stack ItemStack representing the Augment.
	 * @return Augment Type of the stack.
	 */
	String getAugmentIdentifier(ItemStack stack);

}
