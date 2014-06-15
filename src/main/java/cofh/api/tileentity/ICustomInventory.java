package cofh.api.tileentity;

import net.minecraft.item.ItemStack;

public interface ICustomInventory {

	ItemStack[] getInventorySlots(int inventoryIndex);

	int getSlotStackLimit(int slotIndex);

}
