package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot that will only accept potion ingredients
 */
public class SlotPotionIngredient extends Slot
{
	public SlotPotionIngredient(IInventory inv, int index, int x, int y)
	{
		super(inv, index, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return stack != null && stack.getItem().isPotionIngredient(stack);
	}
}
