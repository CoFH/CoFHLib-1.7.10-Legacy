package cofh.util;

import cofh.api.item.IEmpowerableItem;
import cofh.util.oredict.OreDictionaryProxy;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Contains various helper functions to assist with {@link Item} and {@link ItemStack} manipulation and interaction.
 * 
 * @author King Lemming
 * 
 */
public final class ItemHelper {

	public static final String BLOCK = "block";
	public static final String ORE = "ore";
	public static final String DUST = "dust";
	public static final String INGOT = "ingot";
	public static final String NUGGET = "nugget";
	public static final String LOG = "log";

	public static OreDictionaryProxy oreProxy = new OreDictionaryProxy();

	private ItemHelper() {

	}

	public static ItemStack cloneStack(Item item, int stackSize) {

		if (item == null) {
			return null;
		}
		ItemStack stack = new ItemStack(item, stackSize);

		return stack;
	}

	public static ItemStack cloneStack(ItemStack stack, int stackSize) {

		if (stack == null) {
			return null;
		}
		ItemStack retStack = stack.copy();
		retStack.stackSize = stackSize;

		return retStack;
	}

	public static ItemStack copyTag(ItemStack container, ItemStack other) {

		if (other != null && other.stackTagCompound != null) {
			container.stackTagCompound = (NBTTagCompound) other.stackTagCompound.copy();
		}
		return container;
	}

	public static NBTTagCompound setItemStackTagName(NBTTagCompound tag, String name) {

		if (name == "") {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		if (!tag.hasKey("display")) {
			tag.setTag("display", new NBTTagCompound());
		}
		tag.getCompoundTag("display").setString("Name", name);

		return tag;
	}

	public static ItemStack readItemStackFromNBT(NBTTagCompound nbt) {

		ItemStack stack = new ItemStack(Item.getItemById(nbt.getShort("id")));
		stack.stackSize = nbt.getInteger("Count");
		stack.setItemDamage(Math.max(0, nbt.getShort("Damage")));

		if (nbt.hasKey("tag", 10)) {
			stack.stackTagCompound = nbt.getCompoundTag("tag");
		}
		return stack;
	}

	public static NBTTagCompound writeItemStackToNBT(ItemStack stack, NBTTagCompound nbt) {

		nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
		nbt.setInteger("Count", stack.stackSize);
		nbt.setShort("Damage", (short) stack.getItemDamage());

		if (stack.stackTagCompound != null) {
			nbt.setTag("tag", stack.stackTagCompound);
		}
		return nbt;
	}

	public static NBTTagCompound writeItemStackToNBT(ItemStack stack, int amount, NBTTagCompound nbt) {

		nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
		nbt.setInteger("Count", amount);
		nbt.setShort("Damage", (short) stack.getItemDamage());

		if (stack.stackTagCompound != null) {
			nbt.setTag("tag", stack.stackTagCompound);
		}
		return nbt;
	}

	public static String getNameFromItemStack(ItemStack stack) {

		if (stack == null || stack.stackTagCompound == null || !stack.stackTagCompound.hasKey("display")) {
			return "";
		}
		return stack.stackTagCompound.getCompoundTag("display").getString("Name");
	}

	public static ItemStack consumeItem(ItemStack stack) {

		Item item = stack.getItem();

		stack.stackSize -= 1;

		if (item.hasContainerItem(stack)) {
			ItemStack ret = item.getContainerItem(stack);

			if (ret == null) {
				return null;
			}
			if (ret.isItemStackDamageable() && ret.getItemDamage() > ret.getMaxDamage()) {
				ret = null;
			}
			return ret;
		}
		return stack.stackSize > 0 ? stack : null;
	}

	/**
	 * This prevents an overridden getDamage() call from messing up metadata acquisition.
	 */
	public static int getItemDamage(ItemStack stack) {

		return Items.diamond.getDamage(stack);
	}

	/**
	 * Gets a vanilla CraftingManager result.
	 */
	public static ItemStack findMatchingRecipe(InventoryCrafting inv, World world) {

		ItemStack[] dmgItems = new ItemStack[2];
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (inv.getStackInSlot(i) != null) {
				if (dmgItems[0] == null) {
					dmgItems[0] = inv.getStackInSlot(i);
				} else {
					dmgItems[1] = inv.getStackInSlot(i);
					break;
				}
			}
		}
		if (dmgItems[0] == null || dmgItems[0].getItem() == null) {
			return null;
		} else if (dmgItems[1] != null && dmgItems[0].getItem() == dmgItems[1].getItem() && dmgItems[0].stackSize == 1 && dmgItems[1].stackSize == 1
				&& dmgItems[0].getItem().isRepairable()) {
			Item theItem = dmgItems[0].getItem();
			int var13 = theItem.getMaxDamage() - dmgItems[0].getItemDamageForDisplay();
			int var8 = theItem.getMaxDamage() - dmgItems[1].getItemDamageForDisplay();
			int var9 = var13 + var8 + theItem.getMaxDamage() * 5 / 100;
			int var10 = Math.max(0, theItem.getMaxDamage() - var9);

			return new ItemStack(dmgItems[0].getItem(), 1, var10);
		} else {
			IRecipe recipe;
			for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); ++i) {
				recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

				if (recipe.matches(inv, world)) {
					return recipe.getCraftingResult(inv);
				}
			}
			return null;
		}
	}

	/* ORE DICTIONARY FUNCTIONS */

	public static ItemStack getOre(String oreName) {

		return oreProxy.getOre(oreName);
	}

	public static String getOreName(ItemStack stack) {

		return oreProxy.getOreName(stack);
	}

	public static boolean isOreIDEqual(ItemStack stack, int oreID) {

		return oreProxy.isOreIDEqual(stack, oreID);
	}

	public static boolean isOreNameEqual(ItemStack stack, String oreName) {

		return oreProxy.isOreNameEqual(stack, oreName);
	}

	public static boolean oreNameExists(String oreName) {

		return oreProxy.oreNameExists(oreName);
	}

	public static boolean hasOreName(ItemStack stack) {

		return !getOreName(stack).equals("Unknown");
	}

	public static boolean isBlock(ItemStack stack) {

		return getOreName(stack).startsWith(BLOCK);
	}

	public static boolean isOre(ItemStack stack) {

		return getOreName(stack).startsWith(ORE);
	}

	public static boolean isDust(ItemStack stack) {

		return getOreName(stack).startsWith(DUST);
	}

	public static boolean isIngot(ItemStack stack) {

		return getOreName(stack).startsWith(INGOT);
	}

	public static boolean isNugget(ItemStack stack) {

		return getOreName(stack).startsWith(NUGGET);
	}

	public static boolean isLog(ItemStack stack) {

		return getOreName(stack).startsWith(LOG);
	}

	/* CRAFTING HELPER FUNCTIONS */
	public static boolean addGearRecipe(ItemStack gear, String ingot) {

		if (!oreNameExists(ingot)) {
			return false;
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(gear, new Object[] { " X ", "XIX", " X ", 'X', ingot, 'I', Items.iron_ingot }));
		return true;
	}

	public static boolean addReverseStorageRecipe(ItemStack nine, String one) {

		if (!oreNameExists(one)) {
			return false;
		}
		GameRegistry.addRecipe(new ShapelessOreRecipe(ItemHelper.cloneStack(nine, 9), new Object[] { one }));
		return true;
	}

	public static boolean addStorageRecipe(ItemStack one, String nine) {

		if (!oreNameExists(nine)) {
			return false;
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(one, new Object[] { "III", "III", "III", 'I', nine }));
		return true;
	}

	public static void registerWithHandlers(String oreName, ItemStack stack) {

		OreDictionary.registerOre(oreName, stack);
		GameRegistry.registerCustomItemStack(oreName, stack);
		FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", stack);
	}

	/* EMPOWERED ITEM HELPERS */
	public static boolean isPlayerHoldingEmpowerableItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IEmpowerableItem;
	}

	public static boolean isPlayerHoldingEmpoweredItem(EntityPlayer player) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		return equipped instanceof IEmpowerableItem && ((IEmpowerableItem) equipped).isEmpowered(player.getCurrentEquippedItem());
	}

	public static boolean toggleHeldEmpowerableItemState(EntityPlayer player) {

		ItemStack equipped = player.getCurrentEquippedItem();
		IEmpowerableItem empowerableItem = (IEmpowerableItem) equipped.getItem();

		return empowerableItem.setEmpoweredState(equipped, !empowerableItem.isEmpowered(equipped));
	}

	/**
	 * Determine if a player is holding a registered Fluid Container.
	 */
	public static final boolean isPlayerHoldingFluidContainer(EntityPlayer player) {

		return FluidContainerRegistry.isContainer(player.getCurrentEquippedItem());
	}

	public static final boolean isPlayerHoldingFluidContainerItem(EntityPlayer player) {

		return FluidHelper.isPlayerHoldingFluidContainerItem(player);
	}

	public static final boolean isPlayerHoldingEnergyContainerItem(EntityPlayer player) {

		return EnergyHelper.isPlayerHoldingEnergyContainerItem(player);
	}

	public static final boolean isPlayerHoldingNothing(EntityPlayer player) {

		return player.getCurrentEquippedItem() == null;
	}

	public static Item getItemFromStack(ItemStack theStack) {

		return theStack == null ? null : theStack.getItem();
	}

	public static boolean areItemsEqual(Item itemA, Item itemB) {

		if (itemA == itemB) {
			return true;
		}
		if (itemA == null | itemB == null) {
			return false;
		}
		return itemA.equals(itemB);
	}

	public static final boolean isPlayerHoldingItem(Class<?> item, EntityPlayer player) {

		return item.isInstance(getItemFromStack(player.getCurrentEquippedItem()));
	}

	/**
	 * Determine if a player is holding an ItemStack of a specific Item type.
	 */
	public static final boolean isPlayerHoldingItem(Item item, EntityPlayer player) {

		return areItemsEqual(item, getItemFromStack(player.getCurrentEquippedItem()));
	}

	/**
	 * Determine if a player is holding an ItemStack with a specific Item ID, Metadata, and NBT.
	 */
	public static final boolean isPlayerHoldingItemStack(ItemStack stack, EntityPlayer player) {

		return itemsEqualWithMetadata(stack, player.getCurrentEquippedItem());
	}

	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB) {

		if (stackA == stackB) {
			return true;
		}
		if (stackA == null | stackB == null) {
			return false;
		}
		return stackA.getItem().equals(stackB.getItem());
	}

	public static boolean itemsEqualWithoutMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		if (stackA == stackB) {
			return true;
		}
		return itemsEqualWithoutMetadata(stackA, stackB) && (!checkNBT || doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound));
	}

	public static boolean itemsEqualWithMetadata(ItemStack stackA, ItemStack stackB) {

		if (stackA == stackB) {
			return true;
		}
		return itemsEqualWithoutMetadata(stackA, stackB) && (stackA.getHasSubtypes() == false || stackA.getItemDamage() == stackB.getItemDamage());
	}

	public static boolean itemsEqualWithMetadata(ItemStack stackA, ItemStack stackB, boolean checkNBT) {

		if (stackA == stackB) {
			return true;
		}
		return itemsEqualWithMetadata(stackA, stackB) && (!checkNBT || doNBTsMatch(stackA.stackTagCompound, stackB.stackTagCompound));
	}

	public static boolean doNBTsMatch(NBTTagCompound nbtA, NBTTagCompound nbtB) {

		if (nbtA == nbtB) {
			return true;
		}
		if (nbtA != null & nbtB != null) {
			return nbtA.equals(nbtB);
		}
		return false;
	}

	public static boolean itemsEqualForCrafting(ItemStack stackA, ItemStack stackB) {

		return itemsEqualWithoutMetadata(stackA, stackB)
				&& (!stackA.getHasSubtypes() || ((stackA.getItemDamage() == OreDictionary.WILDCARD_VALUE || stackB.getItemDamage() == OreDictionary.WILDCARD_VALUE) || stackB
						.getItemDamage() == stackA.getItemDamage()));
	}

	public static boolean craftingEquivalent(ItemStack checked, ItemStack source, String oreDict, ItemStack output) {

		if (itemsEqualForCrafting(checked, source)) {
			return true;
		} else if (output != null && isBlacklist(output)) {
			return false;
		} else if (oreDict == null || oreDict.equals("Unknown")) {
			return false;
		} else {
			return getOreName(checked).equalsIgnoreCase(oreDict);
		}
	}

	public static boolean doOreIDsMatch(ItemStack stackA, ItemStack stackB) {

		int id = oreProxy.getOreID(stackA);
		return id >= 0 && id == oreProxy.getOreID(stackB);
	}

	public static boolean isBlacklist(ItemStack output) {

		Item item = output.getItem();
		return Item.getItemFromBlock(Blocks.birch_stairs) == item || Item.getItemFromBlock(Blocks.jungle_stairs) == item
				|| Item.getItemFromBlock(Blocks.oak_stairs) == item || Item.getItemFromBlock(Blocks.spruce_stairs) == item
				|| Item.getItemFromBlock(Blocks.planks) == item || Item.getItemFromBlock(Blocks.wooden_slab) == item;
	}

	public static String getItemNBTString(ItemStack theItem, String nbtKey, String invalidReturn) {

		return theItem.stackTagCompound != null && theItem.stackTagCompound.hasKey(nbtKey) ? theItem.stackTagCompound.getString(nbtKey) : invalidReturn;
	}

	/**
	 * Adds Inventory information to ItemStacks which themselves hold things. Called in addInformation().
	 */
	public static void addInventoryInformation(ItemStack stack, List<String> list) {

		addInventoryInformation(stack, list, 0, Integer.MAX_VALUE);
	}

	public static void addInventoryInformation(ItemStack stack, List<String> list, int minSlot, int maxSlot) {

		if (stack.stackTagCompound.hasKey("Inventory") && stack.stackTagCompound.getTagList("Inventory", stack.stackTagCompound.getId()).tagCount() > 0) {

			if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
				list.add(StringHelper.shiftForInfo());
			}
			if (!StringHelper.isShiftKeyDown()) {
				return;
			}
			list.add(StringHelper.localize("info.cofh.contents") + ":");
			NBTTagList nbtList = stack.stackTagCompound.getTagList("Inventory", stack.stackTagCompound.getId());
			ItemStack curStack;
			ItemStack curStack2;

			ArrayList<ItemStack> containedItems = new ArrayList<ItemStack>();

			boolean[] visited = new boolean[nbtList.tagCount()];

			for (int i = 0; i < nbtList.tagCount(); i++) {
				NBTTagCompound tag = nbtList.getCompoundTagAt(i);
				int slot = tag.getInteger("Slot");

				if (visited[i] || slot < minSlot || slot > maxSlot) {
					continue;
				}
				visited[i] = true;
				curStack = ItemStack.loadItemStackFromNBT(tag);

				if (curStack == null) {
					continue;
				}
				containedItems.add(curStack);
				for (int j = 0; j < nbtList.tagCount(); j++) {
					NBTTagCompound tag2 = nbtList.getCompoundTagAt(j);
					int slot2 = tag.getInteger("Slot");

					if (visited[j] || slot2 < minSlot || slot2 > maxSlot) {
						continue;
					}
					curStack2 = ItemStack.loadItemStackFromNBT(tag2);

					if (curStack2 == null) {
						continue;
					}
					if (itemsEqualWithMetadata(curStack, curStack2)) {
						curStack.stackSize += curStack2.stackSize;
						visited[j] = true;
					}
				}
			}
			for (ItemStack item : containedItems) {
				int maxStackSize = item.getMaxStackSize();

				if (!StringHelper.displayStackCount || item.stackSize < maxStackSize || maxStackSize == 1) {
					list.add("    " + StringHelper.BRIGHT_GREEN + item.stackSize + " " + StringHelper.getItemName(item));
				} else {
					if (item.stackSize % maxStackSize != 0) {
						list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + "+" + item.stackSize % maxStackSize
								+ " " + StringHelper.getItemName(item));
					} else {
						list.add("    " + StringHelper.BRIGHT_GREEN + maxStackSize + "x" + item.stackSize / maxStackSize + " " + StringHelper.getItemName(item));
					}
				}
			}
		}
	}

}
