package cofh.lib.util;

import cofh.lib.util.helpers.ItemHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Wrapper for an Item/Metadata combination post 1.7. Quick and dirty, allows for Integer-based Hashes without collisions.
 * 
 * @author King Lemming
 * 
 */
public final class ItemWrapper {

	public Item item;
	public int metadata;

	public static ItemWrapper fromItemStack(ItemStack stack) {

		return new ItemWrapper(stack);
	}

	public ItemWrapper(Item item, int metadata) {

		this.item = item;
		this.metadata = metadata;
	}

	public ItemWrapper(ItemStack stack) {

		this.item = stack.getItem();
		this.metadata = ItemHelper.getItemDamage(stack);
	}

	public ItemWrapper set(ItemStack stack) {

		if (stack != null) {
			this.item = stack.getItem();
			this.metadata = ItemHelper.getItemDamage(stack);
		} else {
			this.item = null;
			this.metadata = 0;
		}
		return this;
	}

	public boolean isEqual(ItemWrapper other) {

		return other != null && item == other.item && metadata == other.metadata;
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof ItemWrapper)) {
			return false;
		}
		return isEqual((ItemWrapper) o);
	}

	@Override
	public int hashCode() {

		return metadata | Item.getIdFromItem(item) << 16;
	}

}
