package cofh.api.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Implement this interface on subclasses of Item to change how the item works in Thermal Dynamics Fluiducts filter slots
 */
public interface ISpecialFilterFluid {
    /**
     * Called to find out if the given fluidstack should be matched by the given filter
     *
     * @param filter
     *            ItemStack representing the filter.
     * @param fluidstack
     *            Fluidstack representing the fluid to match.
     * @return True if the filter should match the fluidstack. False if the default matching should be used.
     */
    public boolean matchesFluid(ItemStack filter, FluidStack fluidstack);
}