package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot which players can only remove items from.
 */
public class SlotRemoveOnly extends Slot
{
	public SlotRemoveOnly(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}
}
