package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot which players can only remove items from.
 * 
 * @author King Lemming
 * 
 */
public class SlotOutput extends Slot {

	public SlotOutput(IInventory inventory, int x, int y, int z) {

		super(inventory, x, y, z);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return false;
	}

}
