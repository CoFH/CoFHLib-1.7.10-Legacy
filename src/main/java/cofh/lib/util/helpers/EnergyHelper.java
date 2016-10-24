package cofh.lib.util.helpers;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * This class contains helper functions related to Redstone Flux, the basis of the CoFH Energy System.
 *
 * @author King Lemming
 *
 */
public class EnergyHelper {

	private EnergyHelper() {

	}

	/* IEnergyContainer Interaction */
	public static int extractEnergyFromContainer(ItemStack container, int maxExtract, boolean simulate) {

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).extractEnergy(container, maxExtract, simulate) : 0;
	}

	public static int insertEnergyIntoContainer(ItemStack container, int maxReceive, boolean simulate) {

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).receiveEnergy(container, maxReceive, simulate) : 0;
	}

	public static int extractEnergyFromHeldContainer(EntityPlayer player, int maxExtract, boolean simulate) {

		//TODO add support for off hand (probably just use main if something held there otherwise try offhand)
		ItemStack container = player.getHeldItemMainhand();

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).extractEnergy(container, maxExtract, simulate) : 0;
	}

	public static int insertEnergyIntoHeldContainer(EntityPlayer player, int maxReceive, boolean simulate) {

		//TODO add support for off hand
		ItemStack container = player.getHeldItemMainhand();

		return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).receiveEnergy(container, maxReceive, simulate) : 0;
	}

	public static boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

		//TODO add support for off hand
		return isEnergyContainerItem(player.getHeldItemMainhand());
	}

	public static boolean isEnergyContainerItem(ItemStack container) {

		return container != null && container.getItem() instanceof IEnergyContainerItem;
	}

	public static int getEnergyStoredFromContainerItem(ItemStack container) {

		return ((IEnergyContainerItem) container.getItem()).getEnergyStored(container);
	}

	/* NBT TAG HELPER */
	public static ItemStack setDefaultEnergyTag(ItemStack container, int energy) {

		if (!container.hasTagCompound()) {
			container.setTagCompound(new NBTTagCompound());
		}
		container.getTagCompound().setInteger("Energy", energy);

		return container;
	}

	/* TOOLTIP HELPER */
	public static void addEnergyInformation(ItemStack stack, List<String> list) {

		if (stack.getItem() instanceof IEnergyContainerItem) {
			list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(stack.getTagCompound().getInteger("Energy")) + " / "
					+ StringHelper.getScaledNumber(((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack)) + " RF");
		}
	}

	/* IEnergyProvider/Receiver Interaction */
	public static int extractEnergyFromAdjacentEnergyProvider(TileEntity tile, EnumFacing side, int energy, boolean simulate) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IEnergyProvider ? ((IEnergyProvider) handler).extractEnergy(side.getOpposite(), energy, simulate) : 0;
	}

	public static int insertEnergyIntoAdjacentEnergyReceiver(TileEntity tile, EnumFacing side, int energy, boolean simulate) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IEnergyReceiver ? ((IEnergyReceiver) handler).receiveEnergy(side.getOpposite(), energy, simulate) : 0;
	}

	/* ADJACENT CHECKS - "FROM" */
	public static boolean isAdjacentEnergyConnectionFromSide(TileEntity tile, EnumFacing from) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, from);

		return isEnergyConnectionOnSide(handler, from.getOpposite());
	}

	public static boolean isAdjacentEnergyProviderFromSide(TileEntity tile, EnumFacing from) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, from);

		return isEnergyProviderOnSide(handler, from.getOpposite());
	}

	public static boolean isAdjacentEnergyReceiverFromSide(TileEntity tile, EnumFacing from) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, from);

		return isEnergyReceiverOnSide(handler, from.getOpposite());
	}

	/* TILE CHECKS - "SIDE" */
	public static boolean isEnergyConnectionOnSide(TileEntity tile, EnumFacing side) {

		return tile instanceof IEnergyConnection ? ((IEnergyConnection) tile).canConnectEnergy(side) : false;
	}

	public static boolean isEnergyProviderOnSide(TileEntity tile, EnumFacing side) {

		return tile instanceof IEnergyProvider ? ((IEnergyProvider) tile).canConnectEnergy(side) : false;
	}

	public static boolean isEnergyReceiverOnSide(TileEntity tile, EnumFacing side) {

		return tile instanceof IEnergyReceiver ? ((IEnergyReceiver) tile).canConnectEnergy(side) : false;
	}

}
