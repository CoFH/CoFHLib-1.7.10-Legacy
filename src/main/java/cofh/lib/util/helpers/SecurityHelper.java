package cofh.lib.util.helpers;

import cofh.api.tileentity.ISecurable;
import cofh.api.tileentity.ISecurable.AccessMode;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SecurityHelper {

    public static final GameProfile UNKNOWN_GAME_PROFILE = new GameProfile(UUID.fromString("1ef1a6f0-87bc-4e78-0a0b-c6824eb787ea"), "[None]");
    private static boolean setup = false;

    public static void setup() {

        if (setup) {
            return;
        }
        //Hack packet registration.
//        Map<EnumPacketDirection, BiMap<Integer, Class<?>>> dirMap;//TODO CCL Reflection pipe.
//        dirMap = ReflectionHelper.getPrivateValue(EnumConnectionState.class, null, "field_179247_h", "directionMaps");
//        dirMap.get(EnumPacketDirection.CLIENTBOUND).put(-26, Login.S__PacketSendUUID.class);

        //Hack class > state map.
        Map<Class<?>, EnumConnectionState> data;//TODO CCL Reflection pipe.
        data = ReflectionHelper.getPrivateValue(EnumConnectionState.class, null, "field_150761_f", "STATES_BY_CLASS");
        data.put(Login.S__PacketSendUUID.class, EnumConnectionState.PLAY);
        MinecraftForge.EVENT_BUS.register(new Login.S__PacketSendUUID());
        setup = true;
    }

    static {
        setup();
    }

    private SecurityHelper() {

    }

    public static boolean isDefaultUUID(UUID uuid) {

        return uuid == null || (uuid.version() == 4 && uuid.variant() == 0);
    }

    public static UUID getID(EntityPlayer player) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null && server.isServerRunning()) {
            return player.getGameProfile().getId();
        }
        return getClientId(player);
    }

    private static UUID cachedId;

    private static UUID getClientId(EntityPlayer player) {

        if (player != Minecraft.getMinecraft().thePlayer) {
            return player.getGameProfile().getId();
        }
        if (cachedId == null) {
            cachedId = Minecraft.getMinecraft().thePlayer.getGameProfile().getId();
        }
        return cachedId;
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
        tag.setString("OwnerUUID", tile.getOwner().getId().toString());
        tag.setString("Owner", tile.getOwner().getName());
        return tag;
    }

    /**
     * Adds Security information to ItemStacks.
     */
    public static void addOwnerInformation(ItemStack stack, List<String> list) {

        if (SecurityHelper.isSecure(stack)) {
            boolean hasUUID = stack.getTagCompound().hasKey("OwnerUUID");
            if (!stack.getTagCompound().hasKey("Owner") && !hasUUID) {
                list.add(StringHelper.localize("info.cofh.owner") + ": " + StringHelper.localize("info.cofh.none"));
            } else {
                if (hasUUID && stack.getTagCompound().hasKey("Owner")) {
                    list.add(StringHelper.localize("info.cofh.owner") + ": " + stack.getTagCompound().getString("Owner") + " \u0378");
                } else {
                    list.add(StringHelper.localize("info.cofh.owner") + ": " + StringHelper.localize("info.cofh.anotherplayer"));
                }
            }
        }
    }

    public static void addAccessInformation(ItemStack stack, List<String> list) {

        if (SecurityHelper.isSecure(stack)) {
            String accessString = "";
            switch (stack.getTagCompound().getByte("Access")) {
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

        return stack.hasTagCompound() && stack.getTagCompound().hasKey("Secure");
    }

    public static ItemStack setSecure(ItemStack stack) {

        if (isSecure(stack)) {
            return stack;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("Secure", true);
        stack.getTagCompound().setByte("Access", (byte) 0);
        return stack;
    }

    public static ItemStack removeSecure(ItemStack stack) {

        if (!isSecure(stack)) {
            return stack;
        }
        stack.getTagCompound().removeTag("Secure");
        stack.getTagCompound().removeTag("Access");
        stack.getTagCompound().removeTag("OwnerUUID");
        stack.getTagCompound().removeTag("Owner");

        if (stack.getTagCompound().hasNoTags()) {
            stack.setTagCompound(null);
        }
        return stack;
    }

    public static boolean setAccess(ItemStack stack, AccessMode access) {

        if (!isSecure(stack)) {
            return false;
        }
        stack.getTagCompound().setByte("Access", (byte) access.ordinal());
        return true;
    }

    public static AccessMode getAccess(ItemStack stack) {

        return !stack.hasTagCompound() ? AccessMode.PUBLIC : AccessMode.values()[stack.getTagCompound().getByte("Access")];
    }

    public static boolean setOwner(ItemStack stack, GameProfile name) {

        if (!isSecure(stack)) {
            return false;
        }
        stack.setTagInfo("OwnerUUID", new NBTTagString(name.getId().toString()));
        stack.setTagInfo("Owner", new NBTTagString(name.getName()));
        return true;
    }

    public static GameProfile getOwner(ItemStack stack) {

        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();

            String uuid = nbt.getString("OwnerUUID");
            String name = nbt.getString("Owner");
            if (!Strings.isNullOrEmpty(uuid)) {
                return new GameProfile(UUID.fromString(uuid), name);
            } else if (!Strings.isNullOrEmpty(name)) {
                return new GameProfile(UUID.fromString(PreYggdrasilConverter.convertMobOwnerIfNeeded(FMLCommonHandler.instance().getMinecraftServerInstance(), name)), name);
            }
        }
        return UNKNOWN_GAME_PROFILE;
    }

    public static GameProfile getProfile(UUID uuid, String name) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        GameProfile owner = server.getPlayerProfileCache().getProfileByUUID(uuid);
        if (owner == null) {
            GameProfile temp = new GameProfile(uuid, name);
            owner = server.getMinecraftSessionService().fillProfileProperties(temp, true);
            if (owner != temp) {
                server.getPlayerProfileCache().addEntry(owner);
            }
        }
        return owner;
    }

    public static String getOwnerName(ItemStack stack) {

        NBTTagCompound nbt = stack.getTagCompound();
        boolean hasUUID;
        if (nbt == null || (!(hasUUID = nbt.hasKey("OwnerUUID")) && !nbt.hasKey("Owner"))) {
            return "[None]";
        }
        return hasUUID ? stack.getTagCompound().getString("Owner") : StringHelper.localize("info.cofh.anotherplayer");
    }

    // this class is to avoid an illegal access error from FML's event handler
    private static class Login {

        public static class S__PacketSendUUID implements Packet {

            @SubscribeEvent
            public void login(PlayerLoggedInEvent evt) {

                ((EntityPlayerMP) evt.player).connection.sendPacket(new S__PacketSendUUID(evt.player));
            }

            private UUID id;

            public S__PacketSendUUID() {

            }

            public S__PacketSendUUID(EntityPlayer player) {

                id = player.getGameProfile().getId();
            }

            @Override
            public void readPacketData(PacketBuffer buffer) throws IOException {

                id = new UUID(buffer.readLong(), buffer.readLong());
            }

            @Override
            public void writePacketData(PacketBuffer buffer) throws IOException {

                buffer.writeLong(id.getMostSignificantBits());
                buffer.writeLong(id.getLeastSignificantBits());
            }

            @Override
            public void processPacket(INetHandler p_148833_1_) {

                cachedId = id;
            }

        }

    }

}
