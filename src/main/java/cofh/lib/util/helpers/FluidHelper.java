package cofh.lib.util.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.io.*;

/**
 * Contains various helper functions to assist with {@link Fluid} and Fluid-related manipulation and interaction.
 *
 * @author King Lemming
 */
public class FluidHelper {

	public static final int BUCKET_VOLUME = Fluid.BUCKET_VOLUME;

	public static final Fluid WATER_FLUID = FluidRegistry.WATER;
	public static final Fluid LAVA_FLUID = FluidRegistry.LAVA;

	public static final FluidStack WATER = new FluidStack(WATER_FLUID, BUCKET_VOLUME);
	public static final FluidStack LAVA = new FluidStack(LAVA_FLUID, BUCKET_VOLUME);

	@CapabilityInject (IFluidHandler.class)
	public static final Capability<IFluidHandler> FLUID_HANDLER = null;

	public static final FluidTankInfo[] NULL_TANK_INFO = new FluidTankInfo[] {};

	private FluidHelper() {

	}

	/* IFluidContainer Interaction */
	public static int fillFluidContainerItem(ItemStack container, FluidStack resource, boolean doFill) {

		return isFluidHandler(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).fill(container, resource, doFill) : 0;
	}

	public static FluidStack drainFluidContainerItem(ItemStack container, int maxDrain, boolean doDrain) {

		return isFluidHandler(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).drain(container, maxDrain, doDrain) : null;
	}

	public static FluidStack extractFluidFromHeldContainer(EntityPlayer player, int maxDrain, boolean doDrain) {

		ItemStack container = player.getHeldItemMainhand();

		return isFluidHandler(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).drain(container, maxDrain, doDrain) : null;
	}

	public static int insertFluidIntoHeldContainer(EntityPlayer player, FluidStack resource, boolean doFill) {

		ItemStack container = player.getHeldItemMainhand();

		return isFluidHandler(container) && container.stackSize == 1 ? ((IFluidContainerItem) container.getItem()).fill(container, resource, doFill) : 0;
	}

	public static boolean isPlayerHoldingFluidHandler(EntityPlayer player) {

		return isFluidHandler(player.getHeldItemMainhand());
	}

	public static FluidStack getFluidStackFromContainerItem(ItemStack container) {

		return ((IFluidContainerItem) container.getItem()).getFluid(container);
	}

	/**
	 * Checks if an item has the FluidHandler capability.
	 *
	 * @param stack The ItemStack to check.
	 * @return If the ItemStack has the fluid cap.
	 */
	public static boolean isFluidHandler(@Nullable ItemStack stack) {

		return stack != null && stack.hasCapability(FLUID_HANDLER, null);
	}

	public static boolean isFillableEmptyContainer(ItemStack empty) {

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(empty);
		if (fluidHandler == null) {
			return false;
		}
		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (!properties.canFill()) {
				return false;
			}
			FluidStack contents = properties.getContents();
			if (contents != null && contents.amount > 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDrainableFilledContainer(ItemStack container) {

		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(container);
		if (fluidHandler == null) {
			return false;
		}
		IFluidTankProperties[] tankProperties = fluidHandler.getTankProperties();
		for (IFluidTankProperties properties : tankProperties) {
			if (!properties.canDrain()) {
				return false;
			}
			FluidStack contents = properties.getContents();
			if (contents == null || contents.amount < properties.getCapacity()) {
				return false;
			}
		}
		return true;
	}

	public static ItemStack setDefaultFluidTag(ItemStack container, FluidStack resource) {

		container.setTagCompound(new NBTTagCompound());
		NBTTagCompound fluidTag = resource.writeToNBT(new NBTTagCompound());
		container.getTagCompound().setTag("Fluid", fluidTag);

		return container;
	}

	/* IFluidHandler Interaction */
	public static FluidStack extractFluidFromAdjacentFluidHandler(TileEntity tile, EnumFacing side, int maxDrain, boolean doDrain) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		boolean isHandler = handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
		return isHandler ? handler.getCapability(FLUID_HANDLER, side.getOpposite()).drain(maxDrain, doDrain) : null;
	}

	public static int insertFluidIntoAdjacentFluidHandler(TileEntity tile, EnumFacing side, FluidStack fluid, boolean doFill) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		boolean isHandler = handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
		return isHandler ? handler.getCapability(FLUID_HANDLER, side.getOpposite()).fill(fluid, doFill) : 0;
	}

	public static int insertFluidIntoAdjacentFluidHandler(World world, BlockPos pos, EnumFacing side, FluidStack fluid, boolean doFill) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(world, pos, side);
		boolean isHandler = handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
		return isHandler ? handler.getCapability(FLUID_HANDLER, side.getOpposite()).fill(fluid, doFill) : 0;
	}

	public static boolean isAdjacentFluidHandler(TileEntity tile, EnumFacing side) {

		TileEntity handler = BlockHelper.getAdjacentTileEntity(tile, side);
		return handler != null && handler.hasCapability(FLUID_HANDLER, side.getOpposite());
	}

	/**
	 * Checks if the tile has the fluid capability on a specific face.
	 *
	 * @param tile The tile to check.
	 * @param face The face of the block to check.
	 * @return If the face has the cap.
	 */
	public static boolean isFluidHandler(TileEntity tile, EnumFacing face) {

		return tile != null && tile.hasCapability(FLUID_HANDLER, face);
	}

	/**
	 * Checks if the tile has the fluid capability on the "General" face.
	 *
	 * @param tile The tile to check.
	 * @return If the tile has the cap.
	 */
	public static boolean isFluidHandler(TileEntity tile) {

		return tile != null && tile.hasCapability(FLUID_HANDLER, null);
	}

	/**
	 * Attempts to drain the item to an IFluidHandler.
	 * TODO This is a bouncer for pre 1.11, 1.11 has immutable stacks, so this needs to change a little bit, here so we don't need to change 40 classes.
	 *
	 * @param stack   The stack to drain from.
	 * @param handler The IFluidHandler to fill.
	 * @param player  The player using the item.
	 * @param hand    The hand the player is holding the item in.
	 * @return If the interaction was successful.
	 */
	public static boolean drainItemToHandler(ItemStack stack, IFluidHandler handler, EntityPlayer player, EnumHand hand) {

		if (stack == null || handler == null || player == null) {
			return false;
		}
		IItemHandler playerInv = new InvWrapper(player.inventory);
		return FluidUtil.tryEmptyContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player);
	}

	/**
	 * Attempts to fill the item from an IFluidHandler.
	 * TODO This is a bouncer for pre 1.11, 1.11 has immutable stacks, so this needs to change a little bit, here so we don't need to change 40 classes.
	 *
	 * @param stack   The stack to fill.
	 * @param handler The IFluidHandler to drain from.
	 * @param player  The player using the item.
	 * @param hand    The hand the player is holding the item in.
	 * @return If the interaction was successful.
	 */
	public static boolean fillItemFromHandler(ItemStack stack, IFluidHandler handler, EntityPlayer player, EnumHand hand) {

		if (stack == null || handler == null || player == null) {
			return false;
		}
		IItemHandler playerInv = new InvWrapper(player.inventory);
		return FluidUtil.tryFillContainerAndStow(stack, handler, playerInv, Integer.MAX_VALUE, player);
	}

	/**
	 * Attempts to interact the item with an IFluidHandler.
	 * Interaction will always try and fill the item first, if this fails it will attempt to drain the item.
	 * TODO This is a bouncer for pre 1.11, 1.11 has immutable stacks, so this needs to change a little bit, here so we don't need to change 40 classes.
	 *
	 * @param stack   The stack to interact with.
	 * @param handler The Handler to fill / drain.
	 * @param player  The player using the item.
	 * @param hand    The hand the player is holding the item in.
	 * @return If any interaction with the handler was successful.
	 */
	public static boolean interactWithHandler(ItemStack stack, IFluidHandler handler, EntityPlayer player, EnumHand hand) {

		return fillItemFromHandler(stack, handler, player, hand) || drainItemToHandler(stack, handler, player, hand);
	}

	/* Fluid Container Registry Interaction */
	@Deprecated
	public static boolean fillContainerFromHandler(World world, net.minecraftforge.fluids.IFluidHandler handler, EntityPlayer player, FluidStack tankFluid) {

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
						((EntityPlayerMP) player).sendContainerToPlayer(player.openContainer);
					}
				}
			}
			handler.drain(null, fluid.amount, true);
			return true;
		}
		return false;
	}

	@Deprecated
	public static boolean fillHandlerWithContainer(World world, net.minecraftforge.fluids.IFluidHandler handler, EntityPlayer player) {

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
							((EntityPlayerMP) player).sendContainerToPlayer(player.openContainer);
						}
					}
				}
				handler.fill(null, fluid, true);
				return true;
			}
		}
		return false;
	}

	/* PACKETS */
	public static void writeFluidStackToPacket(FluidStack fluid, DataOutput data) throws IOException {

		if (!isValidFluidStack(fluid)) {
			data.writeShort(-1);
		} else {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(fluid.writeToNBT(new NBTTagCompound()), byteStream);
			byte[] abyte = byteStream.toByteArray();
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
			ByteArrayInputStream byteStream = new ByteArrayInputStream(abyte);
			return FluidStack.loadFluidStackFromNBT(CompressedStreamTools.readCompressed(byteStream));
		}
	}

	/* HELPERS */
	public static boolean isValidFluidStack(FluidStack fluid) {

		return fluid != null && FluidRegistry.getFluidName(fluid) != null;
	}

	public static int getFluidLuminosity(FluidStack fluid) {

		return fluid == null ? 0 : getFluidLuminosity(fluid.getFluid());
	}

	public static int getFluidLuminosity(Fluid fluid) {

		return fluid == null ? 0 : fluid.getLuminosity();
	}

	public static FluidStack getFluidFromWorld(World world, BlockPos pos, boolean doDrain) {

		IBlockState state = world.getBlockState(pos);
		Block bId = state.getBlock();
		int bMeta = bId.getMetaFromState(state);

		if (Block.isEqualTo(bId, Blocks.WATER)) {
			if (bMeta == 0) {
				return WATER.copy();
			} else {
				return null;
			}
		} else if (Block.isEqualTo(bId, Blocks.LAVA) || Block.isEqualTo(bId, Blocks.FLOWING_LAVA)) {
			if (bMeta == 0) {
				return LAVA.copy();
			} else {
				return null;
			}
		} else if (bId instanceof IFluidBlock) {
			IFluidBlock block = (IFluidBlock) bId;
			return block.drain(world, pos, doDrain);
		}
		return null;
	}

	public static FluidStack getFluidFromWorld(World world, BlockPos pos) {

		return getFluidFromWorld(world, pos, false);
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
