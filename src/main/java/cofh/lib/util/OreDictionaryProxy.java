package cofh.lib.util;

import cofh.lib.util.helpers.ItemHelper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Don't instantiate this or call these methods in any way. Use the methods in {@link ItemHelper}.
 *
 * @author King Lemming
 *
 */
public class OreDictionaryProxy {

	public ItemStack getOre(String oreName) {

		if (!oreNameExists(oreName)) {
			return null;
		}
		return ItemHelper.cloneStack(OreDictionary.getOres(oreName).get(0), 1);
	}

	public int getPrimaryOreID(ItemStack stack) {

		int[] oreIDs = OreDictionary.getOreIDs(stack);
		return oreIDs.length > 0 ? oreIDs[0] : -1;
	}

	public String getPrimaryOreName(ItemStack stack) {

		return getOreName(getPrimaryOreID(stack));
	}

	public List<Integer> getOreIDs(ItemStack stack) {

		int[] ids = OreDictionary.getOreIDs(stack);

		ArrayList<Integer> oreIDs = new ArrayList<Integer>();

		for (int i = 0; i < ids.length; i++) {
			oreIDs.add(ids[i]);
		}
		return oreIDs;
	}

	public int getOreID(String oreName) {

		return OreDictionary.getOreID(oreName);
	}

	public List<String> getOreNames(ItemStack stack) {

		int[] oreIDs = OreDictionary.getOreIDs(stack);

		ArrayList<String> oreNames = new ArrayList<String>();

		for (int i = 0; i < oreIDs.length; i++) {
			oreNames.add(OreDictionary.getOreName(oreIDs[i]));
		}
		return oreNames;
	}

	public String getOreName(int oreID) {

		return OreDictionary.getOreName(oreID);
	}

	public boolean isOreIDEqual(ItemStack stack, int oreID) {

		int[] oreIDs = OreDictionary.getOreIDs(stack);

		for (int i = 0; i < oreIDs.length; i++) {
			if (oreID == oreIDs[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isOreNameEqual(ItemStack stack, String oreName) {

		List<String> oreNames = getOreNames(stack);

		for (int i = 0; i < oreNames.size(); i++) {
			if (oreName.equals(oreNames.get(i))) {
				return true;
			}
		}
		return false;
	}

	public boolean oreNameExists(String oreName) {

		return OreDictionary.doesOreNameExist(oreName);
	}

}
