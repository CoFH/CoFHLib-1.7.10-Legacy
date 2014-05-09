package cofh.gui.slot;

import cofh.util.EnergyHelper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Slot which only accepts Energy (Redstone Flux) Containers as valid.
 * 
 * @author King Lemming
 * 
 */
public class SlotEnergy extends Slot {

	public SlotEnergy(IInventory inventory, int x, int y, int z) {

		super(inventory, x, y, z);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return EnergyHelper.isEnergyContainerItem(stack);
	}

}
