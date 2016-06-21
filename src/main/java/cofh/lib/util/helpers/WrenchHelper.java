package cofh.lib.util.helpers;

import cofh.api.item.IToolHammer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;

// TODO: Update for 1.8.9 APIs.
public final class WrenchHelper {

	private WrenchHelper() {

	}

	public static boolean isHoldingUsableWrench(EntityPlayer player, BlockPos pos) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolHammer) {
			return ((IToolHammer) equipped).isUsable(player.getCurrentEquippedItem(), player, pos);
		} else if (bcWrenchExists) {
			// return canHandleBCWrench(equipped, player, pos);
		}
		return false;
	}

	public static void usedWrench(EntityPlayer player, BlockPos pos) {

		Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
		if (equipped instanceof IToolHammer) {
			((IToolHammer) equipped).toolUsed(player.getCurrentEquippedItem(), player, pos);
		} else if (bcWrenchExists) {
			// bcWrenchUsed(equipped, player, pos);
		}
	}

	/* HELPERS */
	private static boolean bcWrenchExists = false;

	static {
		try {
			Class.forName("buildcraft.api.tools.IToolWrench");
			bcWrenchExists = true;
		} catch (Throwable t) {
			// pokemon!
		}
	}

	//	private static boolean canHandleBCWrench(Item item, EntityPlayer player, BlockPos pos) {
	//
	//		return item instanceof IToolWrench && ((IToolWrench) item).canWrench(player, pos);
	//	}
	//
	//	private static void bcWrenchUsed(Item item, EntityPlayer player, BlockPos pos) {
	//
	//		if (item instanceof IToolWrench) {
	//			((IToolWrench) item).wrenchUsed(player, pos);
	//		}
	//	}
}
