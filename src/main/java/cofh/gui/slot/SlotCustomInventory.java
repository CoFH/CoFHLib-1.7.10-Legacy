package cofh.gui.slot;

import cofh.api.core.ICustomInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCustomInventory extends Slot {

	ICustomInventory myTile;
	int inventoryIndex = 0;
	boolean canTake = true;

	public SlotCustomInventory(ICustomInventory tile, int invIndex, IInventory inventory, int slotIndex, int x, int y, boolean lootable) {

		super(inventory, slotIndex, x, y);
		myTile = tile;
		inventoryIndex = invIndex;
		canTake = lootable;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return super.isItemValid(stack);
	}

	@Override
	public ItemStack getStack() {

		return myTile.getInventorySlots(inventoryIndex)[getSlotIndex()];
	}

	@Override
	public void putStack(ItemStack stack) {

		myTile.getInventorySlots(inventoryIndex)[getSlotIndex()] = stack;
		onSlotChanged();
	}

	@Override
	public void onSlotChanged() {

		myTile.onSlotUpdate();
	}

	@Override
	public int getSlotStackLimit() {

		return myTile.getSlotStackLimit(getSlotIndex());
	}

	@Override
	public ItemStack decrStackSize(int amount) {

		if (myTile.getInventorySlots(inventoryIndex)[getSlotIndex()] == null) {
			return null;
		}
		if (myTile.getInventorySlots(inventoryIndex)[getSlotIndex()].stackSize <= amount) {
			amount = myTile.getInventorySlots(inventoryIndex)[getSlotIndex()].stackSize;
		}
		ItemStack stack = myTile.getInventorySlots(inventoryIndex)[getSlotIndex()].splitStack(amount);

		if (myTile.getInventorySlots(inventoryIndex)[getSlotIndex()].stackSize <= 0) {
			myTile.getInventorySlots(inventoryIndex)[getSlotIndex()] = null;
		}
		return stack;
	}

	@Override
	public boolean isSlotInInventory(IInventory inventory, int slot) {

		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {

		return canTake;
	}

}
