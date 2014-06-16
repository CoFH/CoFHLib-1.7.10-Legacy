package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot that will only accept itemstacks when the IInventory returns true from isItemValidForSlot 
 */
public class SlotAcceptValid extends Slot
{
	public SlotAcceptValid(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
	}

    @Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack != null && this.inventory.isItemValidForSlot(this.slotNumber, par1ItemStack);
    }
}
