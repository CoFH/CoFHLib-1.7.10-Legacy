package cofh.lib.util.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import java.util.List;

/**
 * This class contains helper functions related to Inventories and Inventory manipulation.
 *
 * @author King Lemming
 */
public class InventoryHelper {

	private InventoryHelper() {

	}

	/**
	 * Copy an entire inventory. Best to avoid doing this often.
	 */
	public static ItemStack[] cloneInventory(ItemStack[] inventory) {

		ItemStack[] inventoryCopy = new ItemStack[inventory.length];
		for (int i = 0; i < inventory.length; i++) {
			inventoryCopy[i] = inventory[i].isEmpty() ? ItemStack.EMPTY : inventory[i].copy();
		}
		return inventoryCopy;
	}

	/**
	 * Add an ItemStack to an inventory. Return true if the entire stack was added.
	 *
	 * @param inventory  The inventory.
	 * @param stack      ItemStack to add.
	 * @param startIndex First slot to attempt to add into. Does not loop around fully.
	 * @param endIndex   Final slot to attempt to add into. Should be at most length - 1
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex, int endIndex) {

		if (stack.isEmpty()) {
			return true;
		}
		int openSlot = -1;
		for (int i = startIndex; i <= endIndex; i++) {
			if (ItemHelper.itemsEqualForCrafting(stack, inventory[i]) && inventory[i].getMaxStackSize() > inventory[i].getCount()) {
				int hold = inventory[i].getMaxStackSize() - inventory[i].getCount();
				if (hold >= stack.getCount()) {
					inventory[i].grow(stack.getCount());
					stack = ItemStack.EMPTY;
					return true;
				} else {
					stack.shrink(hold);
					inventory[i].grow(hold);
				}
			} else if (inventory[i].isEmpty() && openSlot == -1) {
				openSlot = i;
			}
		}
		if (openSlot > -1) {
			inventory[openSlot] = stack;
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Shortcut method for above, assumes ending slot is length - 1
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex) {

		return addItemStackToInventory(inventory, stack, startIndex, inventory.length - 1);
	}

	/**
	 * Shortcut method for above, assumes starting slot is 0.
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack) {

		return addItemStackToInventory(inventory, stack, 0);
	}

	public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate) {

		return insertStackIntoInventory(handler, stack, simulate, false);
	}

	public static ItemStack insertStackIntoInventory(IItemHandler handler, ItemStack stack, boolean simulate, boolean forceEmptySlot) {

		return forceEmptySlot ? ItemHandlerHelper.insertItem(handler, stack, simulate) : ItemHandlerHelper.insertItemStacked(handler, stack, simulate);
	}

	@Deprecated
	public static ItemStack simulateInsertItemStackIntoInventory(IInventory inventory, ItemStack stack, EnumFacing side) {

		if (stack.isEmpty() || inventory == null) {
			return ItemStack.EMPTY;
		}
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int slots[] = sidedInv.getSlotsForFace(side);

			if (slots == null) {
				return stack;
			}
			for (int i = 0; i < slots.length && !stack.isEmpty(); i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side)) {
					ItemStack existingStack = inventory.getStackInSlot(slots[i]);
					if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
						stack = simulateAddToOccupiedInventorySlot(sidedInv, slots[i], stack, existingStack);
					}
				}
			}
			for (int i = 0; i < slots.length && !stack.isEmpty(); i++) {
				if (inventory.getStackInSlot(slots[i]).isEmpty() && sidedInv.canInsertItem(slots[i], stack, side)) {
					stack = simulateAddToEmptyInventorySlot(sidedInv, slots[i], stack);
				}
			}
		} else {
			int invSize = inventory.getSizeInventory();
			for (int i = 0; i < invSize && !stack.isEmpty(); i++) {
				ItemStack existingStack = inventory.getStackInSlot(i);
				if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
					stack = simulateAddToOccupiedInventorySlot(inventory, i, stack, existingStack);
				}
			}
			for (int i = 0; i < invSize && !stack.isEmpty(); i++) {
				if (inventory.getStackInSlot(i).isEmpty()) {
					stack = simulateAddToEmptyInventorySlot(inventory, i, stack);
				}
			}
		}
		return stack;
	}

	/* Slot Interaction */
	@Deprecated
	public static ItemStack addToEmptyInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		if (!inventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = inventory.getInventoryStackLimit();
		inventory.setInventorySlotContents(slot, ItemHelper.cloneStack(stack, Math.min(stack.getCount(), stackLimit)));
		return stackLimit >= stack.getCount() ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - stackLimit);
	}

	@Deprecated
	public static ItemStack addToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		if (stack.getCount() + stackInSlot.getCount() > stackLimit) {
			int stackDiff = stackLimit - stackInSlot.getCount();
			stackInSlot.setCount(stackLimit);
			stack.shrink(stackDiff);
			inventory.setInventorySlotContents(slot, stackInSlot);
			return stack;
		}
		stackInSlot.grow(Math.min(stack.getCount(), stackLimit));
		inventory.setInventorySlotContents(slot, stackInSlot);
		return stackLimit >= stack.getCount() ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - stackLimit);
	}

	@Deprecated
	public static ItemStack addToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack, ItemStack existingStack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

		if (existingStack.getCount() >= stackLimit) {
			return stack;
		}
		if (stack.getCount() + existingStack.getCount() > stackLimit) {
			int stackDiff = stackLimit - existingStack.getCount();
			existingStack.setCount(stackLimit);
			stack.shrink(stackDiff);
			inventory.setInventorySlotContents(slot, existingStack);
			return stack;
		}
		existingStack.grow(stack.getCount());
		inventory.setInventorySlotContents(slot, existingStack);
		return stackLimit >= stack.getCount() ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - stackLimit);
	}

	public static ItemStack simulateAddToEmptyInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		if (!inventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		return stackLimit >= stack.getCount() ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		if (stack.getCount() + stackInSlot.getCount() > stackLimit) {
			stack.shrink(stackLimit - stackInSlot.getCount());
			return stack;
		}
		return stackLimit >= stack.getCount() ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack, ItemStack existingStack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

		if (existingStack.getCount() >= stackLimit) {
			return stack;
		}
		if (stack.getCount() + existingStack.getCount() > stackLimit) {
			stack.shrink(stackLimit - existingStack.getCount());
			return stack;
		}
		return stackLimit >= stack.getCount() ? ItemStack.EMPTY : stack.splitStack(stack.getCount() - stackLimit);
	}

	public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int start, int length, boolean reverse) {

		return mergeItemStack(slots, stack, start, length, reverse, true);
	}

	public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int start, int length, boolean r, boolean limit) {

		boolean successful = false;
		int i = !r ? start : length - 1;
		int iterOrder = !r ? 1 : -1;

		Slot slot;
		ItemStack existingStack;

		if (stack.isStackable()) {
			while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getStack();

				if (!existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());

					if (slot.isItemValid(ItemHelper.cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
						int existingSize = existingStack.getCount() + stack.getCount();

						if (existingSize <= maxStack) {
							stack.setCount(0);
							existingStack.setCount(existingSize);
							slot.putStack(existingStack);
							successful = true;
						} else if (existingStack.getCount() < maxStack) {
							stack.shrink(maxStack - existingStack.getCount());
							existingStack.setCount(maxStack);
							slot.putStack(existingStack);
							successful = true;
						}
					}
				}

				i += iterOrder;
			}
		}

		if (stack.getCount() > 0) {
			i = !r ? start : length - 1;

			while (stack.getCount() > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getStack();

				if (existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());

					if (slot.isItemValid(ItemHelper.cloneStack(stack, rmv))) {
						existingStack = stack.splitStack(rmv);
						slot.putStack(existingStack);
						successful = true;
					}
				}

				i += iterOrder;
			}
		}

		return successful;
	}

	/* HELPERS */
	public static ItemStack addToInsertion(TileEntity tile, EnumFacing side, ItemStack stack) {

		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (hasItemHandlerCap(tile, side.getOpposite())) {
			stack = insertStackIntoInventory(getItemHandlerCap(tile, side.getOpposite()), stack, false);
		}
		return stack;
	}

	public static boolean hasItemHandlerCap(TileEntity tileEntity, EnumFacing face) {

		return tileEntity != null && (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face) || tileEntity instanceof ISidedInventory || tileEntity instanceof IInventory);
	}

	public static IItemHandler getItemHandlerCap(TileEntity tileEntity, EnumFacing face) {

		if (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)) {
			return tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
		} else if (tileEntity instanceof ISidedInventory && face != null) {
			return new SidedInvWrapper(((ISidedInventory) tileEntity), face);
		} else if (tileEntity instanceof IInventory) {
			return new InvWrapper(((IInventory) tileEntity));
		}
		return new EmptyHandler();
	}

	public static boolean isEmpty(ItemStack[] inventory) {
		for (ItemStack stack : inventory) {
			if (!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

}
