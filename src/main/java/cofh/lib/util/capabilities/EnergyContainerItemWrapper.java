package cofh.lib.util.capabilities;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyContainerItemWrapper implements ICapabilityProvider {

	final ItemStack stack;
	final IEnergyContainerItem container;

	public EnergyContainerItemWrapper(ItemStack stackIn, IEnergyContainerItem containerIn) {

		stack = stackIn;
		container = containerIn;
	}

	/* ICapabilityProvider */
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {

		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, final EnumFacing from) {

		if (!hasCapability(capability, from)) {
			return null;
		}
		return CapabilityEnergy.ENERGY.cast(new net.minecraftforge.energy.IEnergyStorage() {

			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {

				return container.receiveEnergy(stack, maxReceive, simulate);
			}

			@Override
			public int extractEnergy(int maxExtract, boolean simulate) {

				return container.extractEnergy(stack, maxExtract, simulate);
			}

			@Override
			public int getEnergyStored() {

				return container.getEnergyStored(stack);
			}

			@Override
			public int getMaxEnergyStored() {

				return container.getMaxEnergyStored(stack);
			}

			@Override
			public boolean canExtract() {

				return true;
			}

			@Override
			public boolean canReceive() {

				return true;
			}
		});
	}

}
