package cofh.util;

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
	public int hashcode;

	public static int getHashCode(Item item, int metadata) {

		return (31 + item.hashCode()) * 31 + metadata;
	}

	public static int getHashCode(ItemStack stack) {

		return (31 + stack.getItem().hashCode()) * 31 + ItemHelper.getItemDamage(stack);
	}

	public ItemWrapper(Item item, int metadata) {

		this.item = item;
		this.metadata = metadata;
		this.hashcode = (31 + item.hashCode()) * 31 + metadata;
	}

	public ItemWrapper(ItemStack stack) {

		this.item = stack.getItem();
		this.metadata = ItemHelper.getItemDamage(stack);
		this.hashcode = (31 + item.hashCode()) * 31 + metadata;
	}

	@Override
	public int hashCode() {

		return hashcode;
	}

}
