package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

/**
 * Slot that will only accept itemstacks when the IInventory returns true from isItemValidForSlot and
 * canInsertItem (from side 6 (unknown)) when alos an ISidedInventory
 */
public class SlotAcceptInsertable extends SlotAcceptValid
{
	protected ISidedInventory _inv;
	public SlotAcceptInsertable(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
		if (par1iInventory instanceof ISidedInventory)
			_inv = (ISidedInventory)par1iInventory;
		else
			_inv = null;
	}

    @Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
    	boolean valid = super.isItemValid(par1ItemStack);
        return valid && _inv != null ? _inv.canInsertItem(slotNumber, par1ItemStack, 6) : valid;
    }
}
