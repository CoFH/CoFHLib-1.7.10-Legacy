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
			inventoryCopy[i] = inventory[i] == null ? null : inventory[i].copy();
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

		if (stack == null) {
			return true;
		}
		int openSlot = -1;
		for (int i = startIndex; i <= endIndex; i++) {
			if (ItemHelper.itemsEqualForCrafting(stack, inventory[i]) && inventory[i].getMaxStackSize() > inventory[i].stackSize) {
				int hold = inventory[i].getMaxStackSize() - inventory[i].stackSize;
				if (hold >= stack.stackSize) {
					inventory[i].stackSize += stack.stackSize;
					stack = null;
					return true;
				} else {
					stack.stackSize -= hold;
					inventory[i].stackSize += hold;
				}
			} else if (inventory[i] == null && openSlot == -1) {
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

		if (stack == null || inventory == null) {
			return null;
		}
		if (inventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) inventory;
			int slots[] = sidedInv.getSlotsForFace(side);

			if (slots == null) {
				return stack;
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side)) {
					ItemStack existingStack = inventory.getStackInSlot(slots[i]);
					if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
						stack = simulateAddToOccupiedInventorySlot(sidedInv, slots[i], stack, existingStack);
					}
				}
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (inventory.getStackInSlot(slots[i]) == null && sidedInv.canInsertItem(slots[i], stack, side)) {
					stack = simulateAddToEmptyInventorySlot(sidedInv, slots[i], stack);
				}
			}
		} else {
			int invSize = inventory.getSizeInventory();
			for (int i = 0; i < invSize && stack != null; i++) {
				ItemStack existingStack = inventory.getStackInSlot(i);
				if (ItemHelper.itemsEqualWithMetadata(stack, existingStack, true)) {
					stack = simulateAddToOccupiedInventorySlot(inventory, i, stack, existingStack);
				}
			}
			for (int i = 0; i < invSize && stack != null; i++) {
				if (inventory.getStackInSlot(i) == null) {
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
		inventory.setInventorySlotContents(slot, ItemHelper.cloneStack(stack, Math.min(stack.stackSize, stackLimit)));
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	@Deprecated
	public static ItemStack addToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		if (stack.stackSize + stackInSlot.stackSize > stackLimit) {
			int stackDiff = stackLimit - stackInSlot.stackSize;
			stackInSlot.stackSize = stackLimit;
			stack.stackSize -= stackDiff;
			inventory.setInventorySlotContents(slot, stackInSlot);
			return stack;
		}
		stackInSlot.stackSize += Math.min(stack.stackSize, stackLimit);
		inventory.setInventorySlotContents(slot, stackInSlot);
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	@Deprecated
	public static ItemStack addToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack, ItemStack existingStack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

		if (existingStack.stackSize >= stackLimit) {
			return stack;
		}
		if (stack.stackSize + existingStack.stackSize > stackLimit) {
			int stackDiff = stackLimit - existingStack.stackSize;
			existingStack.stackSize = stackLimit;
			stack.stackSize -= stackDiff;
			inventory.setInventorySlotContents(slot, existingStack);
			return stack;
		}
		existingStack.stackSize += stack.stackSize;
		inventory.setInventorySlotContents(slot, existingStack);
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToEmptyInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		if (!inventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		ItemStack stackInSlot = inventory.getStackInSlot(slot);

		if (stack.stackSize + stackInSlot.stackSize > stackLimit) {
			stack.stackSize -= stackLimit - stackInSlot.stackSize;
			return stack;
		}
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory inventory, int slot, ItemStack stack, ItemStack existingStack) {

		int stackLimit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

		if (existingStack.stackSize >= stackLimit) {
			return stack;
		}
		if (stack.stackSize + existingStack.stackSize > stackLimit) {
			stack.stackSize -= stackLimit - existingStack.stackSize;
			return stack;
		}
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
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
			while (stack.stackSize > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getStack();

				if (existingStack != null) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.stackSize);

					if (slot.isItemValid(ItemHelper.cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
						int existingSize = existingStack.stackSize + stack.stackSize;

						if (existingSize <= maxStack) {
							stack.stackSize = 0;
							existingStack.stackSize = existingSize;
							slot.putStack(existingStack);
							successful = true;
						} else if (existingStack.stackSize < maxStack) {
							stack.stackSize -= maxStack - existingStack.stackSize;
							existingStack.stackSize = maxStack;
							slot.putStack(existingStack);
							successful = true;
						}
					}
				}

				i += iterOrder;
			}
		}

		if (stack.stackSize > 0) {
			i = !r ? start : length - 1;

			while (stack.stackSize > 0 && (!r && i < length || r && i >= start)) {
				slot = slots.get(i);
				existingStack = slot.getStack();

				if (existingStack == null) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.stackSize);

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

		if (stack == null) {
			return null;
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

}
