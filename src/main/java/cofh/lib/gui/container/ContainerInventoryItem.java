package cofh.lib.gui.container;

import cofh.lib.util.helpers.ItemHelper;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
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
	public ItemStack slotClick(int slotIndex, int p_75144_2_, int p_75144_3_, EntityPlayer player) {

		if (p_75144_3_ == 2 && p_75144_2_ == containerIndex) {
			return null;
		}
		ItemStack stack = null;
		InventoryPlayer inventoryPlayer = player.inventory;
		int i1;
		ItemStack itemstack3;

		if (p_75144_3_ == 5) {
			int l = this.field_94536_g;
			this.field_94536_g = func_94532_c(p_75144_2_);

			if ((l != 1 || this.field_94536_g != 2) && l != this.field_94536_g) {
				this.func_94533_d();
			} else if (inventoryPlayer.getItemStack() == null) {
				this.func_94533_d();
			} else if (this.field_94536_g == 0) {
				this.field_94535_f = func_94529_b(p_75144_2_);

				if (func_94528_d(this.field_94535_f)) {
					this.field_94536_g = 1;
					this.field_94537_h.clear();
				} else {
					this.func_94533_d();
				}
			} else if (this.field_94536_g == 1) {
				Slot slot = (Slot) this.inventorySlots.get(slotIndex);

				if (slot != null && func_94527_a(slot, inventoryPlayer.getItemStack(), true) && slot.isItemValid(inventoryPlayer.getItemStack())
						&& inventoryPlayer.getItemStack().stackSize > this.field_94537_h.size() && this.canDragIntoSlot(slot)) {
					this.field_94537_h.add(slot);
				}
			} else if (this.field_94536_g == 2) {
				if (!this.field_94537_h.isEmpty()) {
					itemstack3 = inventoryPlayer.getItemStack().copy();
					i1 = inventoryPlayer.getItemStack().stackSize;
					Iterator<Slot> iterator = this.field_94537_h.iterator();

					while (iterator.hasNext()) {
						Slot slot1 = iterator.next();

						if (slot1 != null && func_94527_a(slot1, inventoryPlayer.getItemStack(), true) && slot1.isItemValid(inventoryPlayer.getItemStack())
								&& inventoryPlayer.getItemStack().stackSize >= this.field_94537_h.size() && this.canDragIntoSlot(slot1)) {
							ItemStack itemstack1 = itemstack3.copy();
							int j1 = slot1.getHasStack() ? slot1.getStack().stackSize : 0;
							func_94525_a(this.field_94537_h, this.field_94535_f, itemstack1, j1);

							if (itemstack1.stackSize > itemstack1.getMaxStackSize()) {
								itemstack1.stackSize = itemstack1.getMaxStackSize();
							}
							if (itemstack1.stackSize > slot1.getSlotStackLimit()) {
								itemstack1.stackSize = slot1.getSlotStackLimit();
							}
							i1 -= itemstack1.stackSize - j1;
							slot1.putStack(itemstack1);
						}
					}

					itemstack3.stackSize = i1;

					if (itemstack3.stackSize <= 0) {
						itemstack3 = null;
					}
					inventoryPlayer.setItemStack(itemstack3);
				}
				this.func_94533_d();
			} else {
				this.func_94533_d();
			}
		} else if (this.field_94536_g != 0) {
			this.func_94533_d();
		} else {
			Slot slot2;
			int l1;
			ItemStack itemstack5;

			if ((p_75144_3_ == 0 || p_75144_3_ == 1) && (p_75144_2_ == 0 || p_75144_2_ == 1)) {
				if (slotIndex == -999) {
					if (inventoryPlayer.getItemStack() != null && slotIndex == -999) {
						if (p_75144_2_ == 0) {
							player.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack(), true);
							inventoryPlayer.setItemStack((ItemStack) null);
						}

						if (p_75144_2_ == 1) {
							player.dropPlayerItemWithRandomChoice(inventoryPlayer.getItemStack().splitStack(1), true);

							if (inventoryPlayer.getItemStack().stackSize == 0) {
								inventoryPlayer.setItemStack((ItemStack) null);
							}
						}
					}
				} else if (p_75144_3_ == 1) {
					if (slotIndex < 0) {
						return null;
					}
					slot2 = (Slot) this.inventorySlots.get(slotIndex);

					if (slot2 != null && slot2.canTakeStack(player)) {
						itemstack3 = this.transferStackInSlot(player, slotIndex);

						if (itemstack3 != null) {
							Item item = itemstack3.getItem();
							stack = itemstack3.copy();

							if (slot2.getStack() != null && slot2.getStack().getItem() == item) {
								this.retrySlotClick(slotIndex, p_75144_2_, true, player);
							}
						}
					}
				} else {
					if (slotIndex < 0) {
						return null;
					}
					slot2 = (Slot) this.inventorySlots.get(slotIndex);

					if (slot2 != null) {
						itemstack3 = slot2.getStack();
						ItemStack itemstack4 = inventoryPlayer.getItemStack();

						if (itemstack3 != null) {
							stack = itemstack3.copy();
						}
						if (itemstack3 == null) {
							if (itemstack4 != null && slot2.isItemValid(itemstack4)) {
								l1 = p_75144_2_ == 0 ? itemstack4.stackSize : 1;

								if (l1 > slot2.getSlotStackLimit()) {
									l1 = slot2.getSlotStackLimit();
								}
								if (itemstack4.stackSize >= l1) {
									slot2.putStack(itemstack4.splitStack(l1));
								}
								if (itemstack4.stackSize == 0) {
									inventoryPlayer.setItemStack((ItemStack) null);
								}
							}
						} else if (slot2.canTakeStack(player)) {
							if (itemstack4 == null) {
								l1 = p_75144_2_ == 0 ? itemstack3.stackSize : (itemstack3.stackSize + 1) / 2;
								itemstack5 = slot2.decrStackSize(l1);
								inventoryPlayer.setItemStack(itemstack5);

								if (itemstack3.stackSize == 0) {
									slot2.putStack((ItemStack) null);
								}
								slot2.onPickupFromSlot(player, inventoryPlayer.getItemStack());
							} else if (slot2.isItemValid(itemstack4)) {
								if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getItemDamage() == itemstack4.getItemDamage()
										&& ItemStack.areItemStackTagsEqual(itemstack3, itemstack4)) {
									l1 = p_75144_2_ == 0 ? itemstack4.stackSize : 1;

									if (l1 > slot2.getSlotStackLimit() - itemstack3.stackSize) {
										l1 = slot2.getSlotStackLimit() - itemstack3.stackSize;
									}
									if (l1 > itemstack4.getMaxStackSize() - itemstack3.stackSize) {
										l1 = itemstack4.getMaxStackSize() - itemstack3.stackSize;
									}
									itemstack4.splitStack(l1);

									if (itemstack4.stackSize == 0) {
										inventoryPlayer.setItemStack((ItemStack) null);
									}
									itemstack3.stackSize += l1;
									slot2.putStack(itemstack3);
								} else if (itemstack4.stackSize <= slot2.getSlotStackLimit()) {
									slot2.putStack(itemstack4);
									inventoryPlayer.setItemStack(itemstack3);
								}
							} else if (itemstack3.getItem() == itemstack4.getItem() && itemstack4.getMaxStackSize() > 1
									&& (!itemstack3.getHasSubtypes() || itemstack3.getItemDamage() == itemstack4.getItemDamage())
									&& ItemStack.areItemStackTagsEqual(itemstack3, itemstack4)) {
								l1 = itemstack3.stackSize;

								if (l1 > 0 && l1 + itemstack4.stackSize <= itemstack4.getMaxStackSize()) {
									itemstack4.stackSize += l1;
									itemstack3 = slot2.decrStackSize(l1);

									if (itemstack3.stackSize == 0) {
										slot2.putStack((ItemStack) null);
									}
									slot2.onPickupFromSlot(player, inventoryPlayer.getItemStack());
								}
							}
						}
						slot2.onSlotChanged();
					}
				}
			} else if (p_75144_3_ == 2 && p_75144_2_ >= 0 && p_75144_2_ < 9) {
				slot2 = (Slot) this.inventorySlots.get(slotIndex);

				if (slot2.canTakeStack(player)) {
					itemstack3 = inventoryPlayer.getStackInSlot(p_75144_2_);
					boolean flag = itemstack3 == null || slot2.inventory == inventoryPlayer && slot2.isItemValid(itemstack3);
					l1 = -1;

					if (!flag) {
						l1 = inventoryPlayer.getFirstEmptyStack();
						flag |= l1 > -1;
					}
					if (slot2.getHasStack() && flag) {
						itemstack5 = slot2.getStack();
						inventoryPlayer.setInventorySlotContents(p_75144_2_, itemstack5.copy());

						if ((slot2.inventory != inventoryPlayer || !slot2.isItemValid(itemstack3)) && itemstack3 != null) {
							if (l1 > -1) {
								inventoryPlayer.addItemStackToInventory(itemstack3);
								slot2.decrStackSize(itemstack5.stackSize);
								slot2.putStack((ItemStack) null);
								slot2.onPickupFromSlot(player, itemstack5);
							}
						} else {
							slot2.decrStackSize(itemstack5.stackSize);
							slot2.putStack(itemstack3);
							slot2.onPickupFromSlot(player, itemstack5);
						}
					} else if (!slot2.getHasStack() && itemstack3 != null && slot2.isItemValid(itemstack3)) {
						inventoryPlayer.setInventorySlotContents(p_75144_2_, (ItemStack) null);
						slot2.putStack(itemstack3);
					}
				}
			} else if (p_75144_3_ == 3 && player.capabilities.isCreativeMode && inventoryPlayer.getItemStack() == null && slotIndex >= 0) {
				slot2 = (Slot) this.inventorySlots.get(slotIndex);

				if (slot2 != null && slot2.getHasStack()) {
					itemstack3 = slot2.getStack().copy();
					itemstack3.stackSize = itemstack3.getMaxStackSize();
					inventoryPlayer.setItemStack(itemstack3);
				}
			} else if (p_75144_3_ == 4 && inventoryPlayer.getItemStack() == null && slotIndex >= 0) {
				slot2 = (Slot) this.inventorySlots.get(slotIndex);

				if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player)) {
					itemstack3 = slot2.decrStackSize(p_75144_2_ == 0 ? 1 : slot2.getStack().stackSize);
					slot2.onPickupFromSlot(player, itemstack3);
					player.dropPlayerItemWithRandomChoice(itemstack3, true);
				}
			} else if (p_75144_3_ == 6 && slotIndex >= 0) {
				slot2 = (Slot) this.inventorySlots.get(slotIndex);
				itemstack3 = inventoryPlayer.getItemStack();

				if (itemstack3 != null && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(player))) {
					i1 = p_75144_2_ == 0 ? 0 : this.inventorySlots.size() - 1;
					l1 = p_75144_2_ == 0 ? 1 : -1;

					for (int i2 = 0; i2 < 2; ++i2) {
						for (int j2 = i1; j2 >= 0 && j2 < this.inventorySlots.size() && itemstack3.stackSize < itemstack3.getMaxStackSize(); j2 += l1) {
							Slot slot3 = (Slot) this.inventorySlots.get(j2);

							if (slot3.getHasStack() && func_94527_a(slot3, itemstack3, true) && slot3.canTakeStack(player)
									&& this.func_94530_a(itemstack3, slot3) && (i2 != 0 || slot3.getStack().stackSize != slot3.getStack().getMaxStackSize())) {
								int k1 = Math.min(itemstack3.getMaxStackSize() - itemstack3.stackSize, slot3.getStack().stackSize);
								ItemStack itemstack2 = slot3.decrStackSize(k1);
								itemstack3.stackSize += k1;

								if (itemstack2.stackSize <= 0) {
									slot3.putStack((ItemStack) null);
								}
								slot3.onPickupFromSlot(player, itemstack2);
							}
						}
					}
				}
				this.detectAndSendChanges();
			}
		}
		return stack;
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
						slot.putStack(stackInSlot);
						slot.onSlotChanged();
						slotFound = true;
					} else if (stackInSlot.stackSize < slotLimit) {
						stack.stackSize -= slotLimit - stackInSlot.stackSize;
						stackInSlot.stackSize = slotLimit;
						slot.putStack(stackInSlot);
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
