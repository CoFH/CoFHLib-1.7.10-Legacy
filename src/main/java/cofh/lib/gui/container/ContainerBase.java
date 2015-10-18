package cofh.lib.gui.container;

import cofh.lib.gui.slot.SlotFalseCopy;
import cofh.lib.util.helpers.InventoryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerBase extends Container {

	public ContainerBase() {

	}

	protected abstract int getPlayerInventoryVerticalOffset();

	protected int getPlayerInventoryHorizontalOffset() {

		return 8;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {

		int yOff = getPlayerInventoryVerticalOffset();
		int xOff = getPlayerInventoryHorizontalOffset();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, xOff + j * 18, yOff + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, xOff + i * 18, yOff + 58));
		}
	}

	protected abstract int getSizeInventory();

	protected boolean supportsShiftClick(int slotIndex) {

		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

		if (!supportsShiftClick(slotIndex)) {
			return null;
		}

		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		int invBase = getSizeInventory();
		int invFull = inventorySlots.size();

		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();

			if (slotIndex < invBase) {
				if (!mergeItemStack(stackInSlot, invBase, invFull, false)) {
					return null;
				}
			} else if (!mergeItemStack(stackInSlot, 0, invBase, true)) {
				return null;
			}
			if (stackInSlot.stackSize <= 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.putStack(stackInSlot);
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}

	@Override
	public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {

		Slot slot = slotId < 0 ? null : (Slot) this.inventorySlots.get(slotId);
		if (slot instanceof SlotFalseCopy) {
			if (mouseButton == 2) {
				slot.putStack(null);
			} else {
				slot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
			}
			return player.inventory.getItemStack();
		}
		return super.slotClick(slotId, mouseButton, modifier, player);
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotMin, int slotMax, boolean ascending) {

		return InventoryHelper.mergeItemStack(this.inventorySlots, stack, slotMin, slotMax, ascending);
	}

}
