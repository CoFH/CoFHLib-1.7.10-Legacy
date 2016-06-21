package cofh.lib.util.helpers;

import cofh.api.tileentity.IItemDuct;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public final class TransferHelper {

	private TransferHelper() {

	}

	public static int addToInsertion(Object insertion, ItemStack stack, EnumFacing from) {

		if (!(InventoryHelper.isInsertion(insertion))) {
			return stack.stackSize;
		}
		stack = InventoryHelper.addToInsertion(insertion, stack, from);

		return stack == null ? 0 : stack.stackSize;
	}

	public static boolean isAccessibleInput(TileEntity tile, EnumFacing side) {

		if (tile instanceof ISidedInventory && ((ISidedInventory) tile).getSlotsForFace(side.getOpposite()).length <= 0) {
			return false;
		}
		if (tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0) {
			return true;
		}
		return false;
	}

	public static boolean isAccessibleOutput(TileEntity tile, EnumFacing side) {

		if (tile instanceof ISidedInventory && ((ISidedInventory) tile).getSlotsForFace(side.getOpposite()).length <= 0) {
			return false;
		}
		if (tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0) {
			return true;
		}
		if (tile instanceof IItemDuct) {
			return true;
		}
		return false;
	}

}
