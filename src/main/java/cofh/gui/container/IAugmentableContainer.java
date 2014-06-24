package cofh.gui.container;

import net.minecraft.inventory.Slot;

/**
 * Implement this interface on Container objects (the backend of a GUI). These are basically passthrough functions which should call back to the Tile Entity.
 * 
 * @author King Lemming
 * 
 */
public interface IAugmentableContainer {

	/**
	 * Returns the Augment slots.
	 */
	Slot[] getAugmentSlots();

}
