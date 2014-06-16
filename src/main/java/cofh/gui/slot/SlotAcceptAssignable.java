package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Slot that will only accept itemstacks whose items are a subclass of the given class 
 */
public class SlotAcceptAssignable extends Slot
{
	protected Class<? extends Item> clazz;
	
	public SlotAcceptAssignable(IInventory par1iInventory, int par2, int par3, int par4, Class<? extends Item> c)
	{
		super(par1iInventory, par2, par3, par4);
		clazz = c;
	}

    @Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack != null && clazz.isInstance(par1ItemStack.getItem());
    }
}
