package cofh.api.item;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface on subclasses of Item to change how the item works in Thermal Dynamics Itemducts filter slots
 */
public interface ISpecialFilterItem {
    /**
     * Called to find out if the given itemstack should be matched by the given filter
     *
     * @param filter
     *            ItemStack representing the filter.
     * @param itemstack
     *            ItemStack representing the fluid to match.
     * @return True if the filter should match. False if the default matching should be used.
     */
    public boolean matchesItem(ItemStack filter, ItemStack itemstack);
}
