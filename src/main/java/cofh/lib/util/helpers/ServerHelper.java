package cofh.lib.util.helpers;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Contains various helper functions to assist with determining Server/Client status.
 *
 * @author King Lemming
 */
public final class ServerHelper {

    private ServerHelper() {

    }

    public static boolean isClientWorld(World world) {

        return world.isRemote;
    }

    public static boolean isServerWorld(World world) {

        return !world.isRemote;
    }

    public static boolean isSinglePlayerServer() {

        return FMLCommonHandler.instance().getMinecraftServerInstance() != null;
    }

    public static boolean isMultiPlayerServer() {

        return FMLCommonHandler.instance().getMinecraftServerInstance() == null;
    }

    /**
     * This function circumvents a miserable failing.
     */
    @Deprecated//packet has change / moved
    public static void sendItemUsePacket(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ) {

        if (isServerWorld(world)) {
            return;
        }
        NetHandlerPlayClient netClientHandler = (NetHandlerPlayClient) FMLClientHandler.instance().getClientPlayHandler();
        //netClientHandler.sendPacket(new C08PacketPlayerBlockPlacement(x, y, z, hitSide, player.inventory.getCurrentItem(), hitX, hitY, hitZ));
    }

}
