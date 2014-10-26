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

	// '0' is null. '-1' is an unmapped item (missing in this World)
	private int getId() {

		return Item.getIdFromItem(item);
	}

	public boolean isEqual(ItemWrapper other) {

		if (other == null) {
			return false;
		}
		if (metadata == other.metadata) {
			if (item == other.item) {
				return true;
			}
			if (item != null && other.item != null) {
				return item.delegate.get() == other.item.delegate.get();
			}
		}
		return false;
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

		return (metadata & 65535) | getId() << 16;
	}

	@Override
	public String toString() {

		StringBuilder b = new StringBuilder(getClass().getName());
		b.append('@').append(System.identityHashCode(this)).append('{');
		b.append("m:").append(metadata).append(", i:").append(item == null ? null : item.getClass().getName());
		b.append('@').append(System.identityHashCode(item)).append(", v:");
		b.append(getId()).append('}');
		return b.toString();
	}

}
