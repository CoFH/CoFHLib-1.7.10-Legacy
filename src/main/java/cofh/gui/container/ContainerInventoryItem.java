package cofh.gui.container;

import cofh.util.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryItem extends Container {

	protected final InventoryContainerItemWrapper containerWrapper;
	protected final EntityPlayer player;
	protected final int containerIndex;

	public ContainerInventoryItem(ItemStack stack, InventoryPlayer inventory) {

		containerWrapper = new InventoryContainerItemWrapper(this, stack);
		player = inventory.player;
		containerIndex = inventory.currentItem;
	}

	public ItemStack getContainerStack() {

		return containerWrapper.getContainerStack();
	}

	public String getInventoryName() {

		return containerWrapper.getInventoryName();
	}

	public void onSlotChanged() {

		player.inventory.mainInventory[containerIndex] = containerWrapper.getContainerStack();
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		int invPlayer = 27;
		int invFull = invPlayer + 9;
		int invTile = invFull + (containerWrapper.getSizeInventory());

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (slotIndex < invFull) {
				if (!this.mergeItemStack(stackInSlot, invFull, invTile, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(stackInSlot, 0, invFull, true)) {
				return null;
			}
			if (stackInSlot.stackSize <= 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
		}
		return stack;
	}

	@Override
	public ItemStack slotClick(int slot, int invIndex, int clickType, EntityPlayer player) {

		return (clickType == 2 && invIndex == containerIndex) ? null : super.slotClick(slot, invIndex, clickType, player);
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotMin, int slotMax, boolean ascending) {

		boolean slotFound = false;
		int k = ascending ? slotMax - 1 : slotMin;

		Slot slot;
		ItemStack stackInSlot;

		if (stack.isStackable()) {
			while (stack.stackSize > 0 && (!ascending && k < slotMax || ascending && k >= slotMin)) {
				slot = (Slot) this.inventorySlots.get(k);
				stackInSlot = slot.getStack();

				if (slot.isItemValid(stack) && ItemHelper.itemsEqualWithMetadata(stack, stackInSlot, true)) {
					int l = stackInSlot.stackSize + stack.stackSize;
					int slotLimit = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());

					if (l <= slotLimit) {
						stack.stackSize = 0;
						stackInSlot.stackSize = l;
						slot.onSlotChanged();
						slotFound = true;
					} else if (stackInSlot.stackSize < slotLimit) {
						stack.stackSize -= slotLimit - stackInSlot.stackSize;
						stackInSlot.stackSize = slotLimit;
						slot.onSlotChanged();
						slotFound = true;
					}
				}
				k += ascending ? -1 : 1;
			}
		}
		if (stack.stackSize > 0) {
			k = ascending ? slotMax - 1 : slotMin;

			while (!ascending && k < slotMax || ascending && k >= slotMin) {
				slot = (Slot) this.inventorySlots.get(k);
				stackInSlot = slot.getStack();

				if (slot.isItemValid(stack) && stackInSlot == null) {
					slot.putStack(ItemHelper.cloneStack(stack, Math.min(stack.stackSize, slot.getSlotStackLimit())));
					slot.onSlotChanged();

					if (slot.getStack() != null) {
						stack.stackSize -= slot.getStack().stackSize;
						slotFound = true;
					}
					break;
				}
				k += ascending ? -1 : 1;
			}
		}
		return slotFound;
	}

}
