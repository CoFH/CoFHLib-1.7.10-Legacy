package cofh.lib.gui.container;

import cofh.api.item.IInventoryContainerItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InventoryContainerItemWrapper implements IInventory {

	private final ContainerInventoryItem container;
	private final ItemStack inventory;
	private final IInventoryContainerItem inventoryItem;

	public InventoryContainerItemWrapper(ContainerInventoryItem gui, ItemStack stack) {

		container = gui;
		inventory = stack;
		inventoryItem = (IInventoryContainerItem) stack.getItem();
	}

	public ItemStack getContainerStack() {

		return inventory;
	}

	@Override
	public int getSizeInventory() {

		return inventoryItem.getSizeInventory(inventory);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		if (inventory.stackTagCompound.getCompoundTag("Slot" + slot) == null || inventory.stackTagCompound.getCompoundTag("Slot" + slot).hasNoTags()) {
			return null;
		}
		return ItemStack.loadItemStackFromNBT(inventory.stackTagCompound.getCompoundTag("Slot" + slot));
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (inventory.stackTagCompound.getCompoundTag("Slot" + slot) == null || inventory.stackTagCompound.getCompoundTag("Slot" + slot).hasNoTags()) {
			return null;
		}
		ItemStack stack = ItemStack.loadItemStackFromNBT(inventory.stackTagCompound.getCompoundTag("Slot" + slot));
		ItemStack retStack = stack.splitStack(amount);
		if (stack.stackSize <= 0) {
			inventory.stackTagCompound.setTag("Slot" + slot, new NBTTagCompound());
		} else {
			NBTTagCompound itemTag = new NBTTagCompound();
			stack.writeToNBT(itemTag);
			inventory.stackTagCompound.setTag("Slot" + slot, itemTag);
		}
		return retStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		if (stack == null) {
			inventory.stackTagCompound.setTag("Slot" + slot, new NBTTagCompound());
		} else {
			NBTTagCompound itemTag = new NBTTagCompound();
			stack.writeToNBT(itemTag);
			inventory.stackTagCompound.setTag("Slot" + slot, itemTag);
		}
	}

	@Override
	public String getInventoryName() {

		return inventory.getDisplayName();
	}

	@Override
	public boolean hasCustomInventoryName() {

		return true;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public void markDirty() {

		container.onSlotChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		if (stack == null) {
			return false;
		}
		if (stack.getItem() instanceof IInventoryContainerItem) {
			return ((IInventoryContainerItem) stack.getItem()).getSizeInventory(stack) <= 0;
		}
		return true;
	}

}
