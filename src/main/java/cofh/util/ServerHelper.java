package cofh.util;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.world.World;

/**
 * Contains various helper functions to assist with determining Server/Client status.
 * 
 * @author King Lemming
 * 
 */
public final class ServerHelper {

	private ServerHelper() {

	}

	public static final boolean isClientWorld(World world) {

		return world.isRemote;
	}

	public static final boolean isServerWorld(World world) {

		return !world.isRemote;
	}

	public static final boolean isSinglePlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
	}

	public static final boolean isMultiPlayerServer() {

		return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
	}

}
