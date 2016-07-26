package cofh.lib.util.helpers;

import cofh.lib.util.CompressedStreamUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

/**
 * Contains various helper functions to assist with {@link Fluid} and Fluid-related manipulation and interaction.
 *
 * @author King Lemming
 *
 */
public final class FluidHelper {

	public static final int BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;

	public static final Fluid WATER_FLUID = FluidRegistry.WATER;
	public static final Fluid LAVA_FLUID = FluidRegistry.LAVA;

	public static final FluidStack WATER = new FluidStack(WATER_FLUID, BUCKET_VOLUME);
	public static final FluidStack LAVA = new FluidStack(LAVA_FLUID, BUCKET_VOLUME);

	public static final FluidTankInfo[] NULL_TANK_INFO = new FluidTankInfo[] {};

	private FluidHelper() {

	}

	/* FluidContainerRegistry Interaction */
	public static boolean fillContainerFromHandler(World world, IFluidHandler handler, EntityPlayer player,
			FluidStack tankFluid) {

		ItemStack container = player.getHeldItemMainhand();

		if (FluidContainerRegistry.isEmptyContainer(container)) {
			ItemStack returnStack = FluidContainerRegistry.fillFluidContainer(tankFluid, container);
			FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(returnStack);

			if (fluid == null || returnStack == null) {
				return false;
			}
			if (ServerHelper.isClientWorld(world)) {
				return true;
			}
			if (!player.capabilities.isCreativeMode) {
				if (container.stackSize == 1) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, returnStack);
					container.stackSize--;
					if (container.stackSize <= 0) {
						container = null;
					}
				} else {
					if (ItemHelper.disposePlayerItem(player.getHeldItemMainhand(), returnStack, player, true)) {
						player.openContainer.detectAndSendChanges();
						((EntityPlayerMP) player)
								.updateCraftingInventory(player.openContainer, player.openContainer.getInventory());
					}
				}
			}
			handler.drain(null, fluid.amount, true);
			return true;
		}
		return false;
	}

	public static boolean fillHandlerWithContainer(World world, IFluidHandler handler, EntityPlayer player) {

		ItemStack container = player.getHeldItemMainhand();
		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);

		if (fluid != null) {
			if (handler.fill(null, fluid, false) == fluid.amount || player.capabilities.isCreativeMode) {
				ItemStack returnStack = FluidContainerRegistry.drainFluidContainer(container);
				if (ServerHelper.isClientWorld(world)) {
					return true;
				}
				if (!player.capabilities.isCreativeMode) {
					if (ItemHelper.disposePlayerItem(player.getHeldItemMainhand(), returnStack, player, true)) {
						if (ServerHelper.isServerWorld(world)) {
							player.openContainer.detectAndSendChanges();
							((EntityPlayerMP) player)
									.updateCraftingInventory(player.openContainer, player.openContainer.getInventory());
						}
					}
				}
				handler.fill(null, fluid, true);
				return true;
			}
		}
		return false;
	}

	/* IFluidContainer Interaction */
	public static FluidStack drainFluidContainerItem(ItemStack container, int maxDrain, boolean doDrain) {

		return isFluidContainerItem(container) && container.stackSize == 1 ?
				((IFluidContainerItem) container.getItem()).drain(container, maxDrain, doDrain)
				:
				null;
	}

	public static int fillFluidContainerItem(ItemStack container, FluidStack resource, boolean doFill) {

		return isFluidContainerItem(container) && container.stackSize == 1 ?
				((IFluidContainerItem) container.getItem()).fill(container, resource, doFill) :
				0;
	}

	public static FluidStack extractFluidFromHeldContainer(EntityPlayer player, int maxDrain, boolean doDrain) {

		ItemStack container = player.getHeldItemMainhand();

		return isFluidContainerItem(container) && container.stackSize == 1 ?
				((IFluidContainerItem) container.getItem()).drain(container, maxDrain, doDrain)
				:
				null;
	}

	public static int insertFluidIntoHeldContainer(EntityPlayer player, FluidStack resource, boolean doFill) {

		ItemStack container = player.getHeldItemMainhand();

		return isFluidContainerItem(container) && container.stackSize == 1 ?
				((IFluidContainerItem) container.getItem()).fill(container, resource, doFill) :
				0;
	}

	public static boolean isPlayerHoldingFluidContainerItem(EntityPlayer player) {

		return isFluidContainerItem(player.getHeldItemMainhand());
	}

	public static boolean isFluidContainerItem(ItemStack container) {

		return container != null && container.getItem() instanceof IFluidContainerItem;
	}

	public static FluidStack getFluidStackFromContainerItem(ItemStack container) {

		return ((IFluidContainerItem) container.getItem()).getFluid(container);
	}

	/* NBT TAG HELPER */
	public static ItemStack setDefaultFluidTag(ItemStack container, FluidStack resource) {

		if (!container.hasTagCompound()) {
			container.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());
		container.getTagCompound().setTag("Fluid", fluidTag);

		return container;
	}

	/* TOOLTIP HELPER */
	public static void addFluidInformation(ItemStack stack, List<String> list) {

		if (stack.getItem() instanceof IFluidContainerItem) {
			// TODO: Finish.
		}
	}

	/* IFluidHandler Interaction */
	public static FluidStack drainAdjacentFluidHandler(TileEntity tile, EnumFacing side, FluidStack resource, boolean doDrain) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IFluidHandler ? ((IFluidHandler) handler).drain(side.getOpposite(), resource, doDrain) : null;
	}

	public static FluidStack drainAdjacentFluidHandler(TileEntity tile, EnumFacing side, int maxDrain, boolean doDrain) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IFluidHandler ? ((IFluidHandler) handler).drain(side.getOpposite(), maxDrain, doDrain) : null;
	}

	public static int fillAdjacentFluidHandler(TileEntity tile, EnumFacing side, FluidStack fluid, boolean doFill) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);

		return handler instanceof IFluidHandler ? ((IFluidHandler) handler).fill(side.getOpposite(), fluid, doFill) : 0;
	}

	/* ADJACENT CHECKS - "FROM" */
	public static boolean isAdjacentFluidHandler(TileEntity tile, EnumFacing from) {

		return BlockHelper.getAdjacentTileEntity(tile, from) instanceof IFluidHandler;
	}

	/* TILE CHECKS - "SIDE" */
	public static boolean isFluidHandler(TileEntity tile) {

		return tile instanceof IFluidHandler;
	}

	/* PACKETS */
	public static void writeFluidStackToPacket(FluidStack fluid, DataOutput data) throws IOException {

		if (!isValidFluidStack(fluid)) {
			data.writeShort(-1);
		} else {
			byte[] abyte = CompressedStreamUtils.compress(fluid.writeToNBT(new NBTTagCompound()));
			data.writeShort((short) abyte.length);
			data.write(abyte);
		}
	}

	public static FluidStack readFluidStackFromPacket(DataInput data) throws IOException {

		short length = data.readShort();

		if (length < 0) {
			return null;
		} else {
			byte[] abyte = new byte[length];
			data.readFully(abyte);
			return FluidStack.loadFluidStackFromNBT(CompressedStreamUtils.read(abyte, new NBTSizeTracker(2097152L)));
		}
	}

	/* HELPERS */
	public static boolean isValidFluidStack(FluidStack fluid) {

		return fluid == null ? false : FluidRegistry.getFluidName(fluid) != null;
	}

	public static int getFluidLuminosity(FluidStack fluid) {

		return fluid == null ? 0 : getFluidLuminosity(fluid.getFluid());
	}

	public static int getFluidLuminosity(Fluid fluid) {

		return fluid == null ? 0 : fluid.getLuminosity();
	}

	public static Fluid lookupFluidForBlock(Block block) {

		if (block == Blocks.FLOWING_WATER) {
			return WATER_FLUID;
		}
		if (block == Blocks.FLOWING_LAVA) {
			return LAVA_FLUID;
		}
		return FluidRegistry.lookupFluidForBlock(block);
	}

	public static FluidStack getFluidForFilledItem(ItemStack container) {

		if (container != null && container.getItem() instanceof IFluidContainerItem) {
			return ((IFluidContainerItem) container.getItem()).getFluid(container);
		}
		return FluidContainerRegistry.getFluidForFilledItem(container);
	}

	public static boolean isFluidEqualOrNull(FluidStack resourceA, FluidStack resourceB) {

		return resourceA == null || resourceB == null || resourceA.isFluidEqual(resourceB);
	}

	public static boolean isFluidEqualOrNull(Fluid fluidA, FluidStack resourceB) {

		return fluidA == null || resourceB == null || fluidA == resourceB.getFluid();
	}

	public static boolean isFluidEqualOrNull(Fluid fluidA, Fluid fluidB) {

		return fluidA == null || fluidB == null || fluidA == fluidB;
	}

	public static boolean isFluidEqual(FluidStack resourceA, FluidStack resourceB) {

		return resourceA != null && resourceA.isFluidEqual(resourceB);
	}

	public static boolean isFluidEqual(Fluid fluidA, FluidStack resourceB) {

		return fluidA != null && resourceB != null && fluidA == resourceB.getFluid();
	}

	public static boolean isFluidEqual(Fluid fluidA, Fluid fluidB) {

		return fluidA != null && fluidB != null && fluidA.equals(fluidB);
	}

}
