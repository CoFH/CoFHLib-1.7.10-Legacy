package cofh.lib.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public abstract class ContainerInventoryItem extends ContainerBase {

	protected final InventoryContainerItemWrapper containerWrapper;
	protected final EntityPlayer player;
	protected final int containerIndex;
	protected boolean valid = true;

	public ContainerInventoryItem(ItemStack stack, InventoryPlayer inventory) {

		player = inventory.player;
		containerIndex = inventory.currentItem;
		containerWrapper = new InventoryContainerItemWrapper(stack);
	}

	@Override
	protected int getSizeInventory() {

		return containerWrapper.getSizeInventory();
	}

	public ItemStack getContainerStack() {

		return containerWrapper.getContainerStack();
	}

	public String getName() {

		return containerWrapper.getName();
	}

	@Override
	public void detectAndSendChanges() {

		ItemStack item = player.inventory.mainInventory[containerIndex];
		if (item == null || item.getItem() != containerWrapper.getContainerItem()) {
			valid = false;
			return;
		}
		super.detectAndSendChanges();
	}

	public void onSlotChanged() {

		ItemStack item = player.inventory.mainInventory[containerIndex];
		if (valid && item != null && item.getItem() == containerWrapper.getContainerItem()) {
			player.inventory.mainInventory[containerIndex] = containerWrapper.getContainerStack();
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {

		onSlotChanged();
		if (containerWrapper.getDirty() && !valid) {
			player.inventory.setItemStack(null);
		}
		return valid;
	}

	@Override
	protected boolean performMerge(int slotIndex, ItemStack stack) {

		int invPlayer = 27;
		int invFull = invPlayer + 9;
		int invTile = invFull + getSizeInventory();

		if (slotIndex < invFull) {
			return mergeItemStack(stack, invFull, invTile, false);
		}
		return mergeItemStack(stack, 0, invFull, true);
	}

	@Override
	public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {

		if (mode == 2 && clickedButton == containerIndex) {
			return null;
		}
		return super.slotClick(slotId, clickedButton, mode, player);
	}

}
