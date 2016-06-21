package cofh.api.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * This interface is implemented on ItemDucts. Use it to attempt to eject items into an entry point.
 *
 * @author Zeldo Kavira, King Lemming
 *
 */
public interface IItemDuct {

	/**
	 * Insert an ItemStack into the IItemDuct. Will only accept items if there is a valid destination. This returns what is remaining of the original stack - a
	 * null return means that the entire stack was accepted/routed!
	 *
	 * @param stack
	 *            ItemStack to be inserted. The size of this stack corresponds to the maximum amount to insert.
	 * @param from
	 *            Orientation the item is inserted from.
	 * @return An ItemStack representing how much is remaining after the item was inserted into the Duct.
	 */
	public ItemStack insertItem(ItemStack stack, EnumFacing from);

}
