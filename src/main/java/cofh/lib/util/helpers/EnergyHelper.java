package cofh.lib.util.helpers;

import cofh.api.energy.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * This class contains helper functions related to Redstone Flux, the basis of the CoFH Energy System.
 *
 * @author King Lemming
 */
public class EnergyHelper {

    public static final int RF_PER_MJ = 10; // Official Ratio of RF to MJ (BuildCraft)
    public static final int RF_PER_EU = 4; // Official Ratio of RF to EU (IndustrialCraft)

    private EnergyHelper() {

    }

    /* NBT TAG HELPER */
    public static void addEnergyInformation(ItemStack stack, List<String> list) {

        if (stack.getItem() instanceof IEnergyContainerItem) {
            list.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(stack.getTagCompound().getInteger("Energy")) + " / " + StringHelper.getScaledNumber(((IEnergyContainerItem) stack.getItem()).getMaxEnergyStored(stack)) + " RF");
        }
    }

    /* IEnergyContainer Interaction */
    public static int extractEnergyFromContainer(ItemStack container, int maxExtract, boolean simulate) {

        return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).extractEnergy(container, maxExtract, simulate) : 0;
    }

    public static int insertEnergyIntoContainer(ItemStack container, int maxReceive, boolean simulate) {

        return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).receiveEnergy(container, maxReceive, simulate) : 0;
    }

    public static int extractEnergyFromHeldContainer(EntityPlayer player, int maxExtract, boolean simulate) {

        ItemStack container = player.getHeldItemMainhand();

        return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).extractEnergy(container, maxExtract, simulate) : 0;
    }

    public static int insertEnergyIntoHeldContainer(EntityPlayer player, int maxReceive, boolean simulate) {

        ItemStack container = player.getHeldItemMainhand();

        return isEnergyContainerItem(container) ? ((IEnergyContainerItem) container.getItem()).receiveEnergy(container, maxReceive, simulate) : 0;
    }

    public static boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

        return isEnergyContainerItem(player.getHeldItemMainhand());
    }

    public static boolean isEnergyContainerItem(ItemStack container) {

        return container != null && container.getItem() instanceof IEnergyContainerItem;
    }

    public static ItemStack setDefaultEnergyTag(ItemStack container, int energy) {

        if (!container.hasTagCompound()) {
            container.setTagCompound(new NBTTagCompound());
        }
        container.getTagCompound().setInteger("Energy", energy);

        return container;
    }

    /* IEnergyHandler Interaction */
    @Deprecated
    public static int extractEnergyFromAdjacentEnergyHandler(TileEntity tile, int side, int energy, boolean simulate) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return handler instanceof IEnergyProvider ? ((IEnergyProvider) handler).extractEnergy(EnumFacing.VALUES[side ^ 1], energy, simulate) : 0;
    }

    @Deprecated
    public static int insertEnergyIntoAdjacentEnergyHandler(TileEntity tile, int side, int energy, boolean simulate) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return handler instanceof IEnergyReceiver ? ((IEnergyReceiver) handler).receiveEnergy(EnumFacing.VALUES[side ^ 1], energy, simulate) : 0;
    }

    public static int extractEnergyFromAdjacentEnergyProvider(TileEntity tile, int side, int energy, boolean simulate) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return handler instanceof IEnergyProvider ? ((IEnergyProvider) handler).extractEnergy(EnumFacing.VALUES[side ^ 1], energy, simulate) : 0;
    }

    public static int insertEnergyIntoAdjacentEnergyReceiver(TileEntity tile, int side, int energy, boolean simulate) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return handler instanceof IEnergyReceiver ? ((IEnergyReceiver) handler).receiveEnergy(EnumFacing.VALUES[side ^ 1], energy, simulate) : 0;
    }

    @Deprecated
    public static boolean isAdjacentEnergyHandlerFromSide(TileEntity tile, int side) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return isEnergyHandlerFromSide(handler, EnumFacing.VALUES[side ^ 1]);
    }

    @Deprecated
    public static boolean isEnergyHandlerFromSide(TileEntity tile, EnumFacing from) {

        return tile instanceof IEnergyHandler && ((IEnergyHandler) tile).canConnectEnergy(from);
    }

    public static boolean isAdjacentEnergyConnectableFromSide(TileEntity tile, int side) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return isEnergyConnectableFromSide(handler, EnumFacing.VALUES[side ^ 1]);
    }

    public static boolean isEnergyConnectableFromSide(TileEntity tile, EnumFacing from) {

        return tile instanceof IEnergyConnection && ((IEnergyConnection) tile).canConnectEnergy(from);
    }

    public static boolean isAdjacentEnergyReceiverFromSide(TileEntity tile, int side) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return isEnergyReceiverFromSide(handler, EnumFacing.VALUES[side ^ 1]);
    }

    public static boolean isEnergyReceiverFromSide(TileEntity tile, EnumFacing from) {

        return tile instanceof IEnergyReceiver && ((IEnergyReceiver) tile).canConnectEnergy(from);
    }

    public static boolean isAdjacentEnergyProviderFromSide(TileEntity tile, int side) {

        TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

        return isEnergyProviderFromSide(handler, EnumFacing.VALUES[side ^ 1]);
    }

    public static boolean isEnergyProviderFromSide(TileEntity tile, EnumFacing from) {

        return tile instanceof IEnergyProvider && ((IEnergyProvider) tile).canConnectEnergy(from);
    }

}
