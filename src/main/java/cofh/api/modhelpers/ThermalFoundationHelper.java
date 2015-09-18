package cofh.api.modhelpers;

import cpw.mods.fml.common.event.FMLInterModComms;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ThermalFoundationHelper {

	private ThermalFoundationHelper() {

	}

	/* Lexicon */
	public static void addBlacklistEntry(ItemStack entry) {

		if (entry == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("entry", new NBTTagCompound());

		entry.writeToNBT(toSend.getCompoundTag("entry"));
		FMLInterModComms.sendMessage("ThermalFoundation", "AddLexiconBlacklistEntry", toSend);
	}

	public static void removeBlacklistEntry(ItemStack entry) {

		if (entry == null) {
			return;
		}
		NBTTagCompound toSend = new NBTTagCompound();

		toSend.setTag("entry", new NBTTagCompound());

		entry.writeToNBT(toSend.getCompoundTag("entry"));
		FMLInterModComms.sendMessage("ThermalFoundation", "RemoveLexiconBlacklistEntry", toSend);
	}

}
