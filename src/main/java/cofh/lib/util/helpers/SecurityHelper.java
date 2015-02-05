package cofh.lib.util.helpers;

import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ISecurable.AccessMode;
import com.mojang.authlib.GameProfile;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;

public class SecurityHelper {

	private SecurityHelper() {

	}

	/* NBT TAG HELPER */
	public static NBTTagCompound setItemStackTagSecure(NBTTagCompound tag, ISecurable tile) {

		if (tile == null) {
			return null;
		}
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		tag.setBoolean("Secure", true);
		tag.setByte("Access", (byte) tile.getAccess().ordinal());
		tag.setString("OwnerUUID", tile.getOwner().toString());
		tag.setString("Owner", tile.getOwnerName());
		return tag;
	}

	/**
	 * Adds Security information to ItemStacks.
	 */
	public static void addOwnerInformation(ItemStack stack, List<String> list) {

		if (SecurityHelper.isSecure(stack)) {
			if (!stack.stackTagCompound.hasKey("Owner")) {
				list.add(StringHelper.localize("info.cofh.owner") + ": " + StringHelper.localize("info.cofh.none"));
			} else {
				list.add(StringHelper.localize("info.cofh.owner") + ": " + stack.stackTagCompound.getString("Owner"));
			}
		}
	}

	public static void addAccessInformation(ItemStack stack, List<String> list) {

		if (SecurityHelper.isSecure(stack)) {
			String accessString = "";
			switch (stack.stackTagCompound.getByte("Access")) {
			case 0:
				accessString = StringHelper.localize("info.cofh.accessPublic");
				break;
			case 1:
				accessString = StringHelper.localize("info.cofh.accessRestricted");
				break;
			case 2:
				accessString = StringHelper.localize("info.cofh.accessPrivate");
				break;
			}
			list.add(StringHelper.localize("info.cofh.access") + ": " + accessString);
		}
	}

	/* ITEM HELPERS */
	public static boolean isSecure(ItemStack stack) {

		return stack.stackTagCompound == null ? false : stack.stackTagCompound.hasKey("Secure");
	}

	public static ItemStack setSecure(ItemStack stack) {

		if (isSecure(stack)) {
			return stack;
		}
		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		stack.stackTagCompound.setBoolean("Secure", true);
		stack.stackTagCompound.setByte("Access", (byte) 0);
		return stack;
	}

	public static boolean setAccess(ItemStack stack, AccessMode access) {

		if (!isSecure(stack)) {
			return false;
		}
		stack.stackTagCompound.setByte("Access", (byte) access.ordinal());
		return true;
	}

	public static AccessMode getAccess(ItemStack stack) {

		return stack.stackTagCompound == null ? AccessMode.PUBLIC : AccessMode.values()[stack.stackTagCompound.getByte("Access")];
	}

	public static boolean setOwner(ItemStack stack, GameProfile name) {

		if (!isSecure(stack)) {
			return false;
		}
		stack.setTagInfo("OwnerUUID", new NBTTagString(name.getId().toString()));
		return true;
	}

	public static UUID getOwner(ItemStack stack) {

		if (stack.stackTagCompound != null) {
			if (stack.stackTagCompound.hasKey("OwnerUUID", 8))
				return UUID.fromString(stack.stackTagCompound.getString("OwnerUUID"));
			if (stack.stackTagCompound.hasKey("Owner", 8))
				return UUID.fromString(PreYggdrasilConverter.func_152719_a(getOwnerName(stack)));
		}
		return UUID.fromString("1ef1a6f0-87bc-4e78-0a0b-c6824eb787ea");
	}

	public static GameProfile getProfile(UUID uuid) {

		GameProfile owner = MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid);
		if (owner == null) {
			GameProfile temp = new GameProfile(uuid, null);
			owner = MinecraftServer.getServer().func_147130_as().fillProfileProperties(temp, true);
			if (owner != temp)
				MinecraftServer.getServer().func_152358_ax().func_152649_a(owner);
		}
		return owner;
	}

	@Deprecated
	public static boolean setOwnerName(ItemStack stack, String name) {

		if (!isSecure(stack)) {
			return false;
		}
		stack.stackTagCompound.setString("Owner", name);
		return true;
	}

	public static String getOwnerName(ItemStack stack) {

		return stack.stackTagCompound == null ? "[None]" : stack.stackTagCompound.getString("Owner");
	}

}
