package cofh.api.tileentity;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Implement this interface on Tile Entities which can have access restrictions.
 *
 * @author King Lemming
 *
 */
public interface ISecurable {

	/**
	 * Enum for Access Modes - Guild allows Guild access, Restricted is Friends Only, Private is Owner only.
	 *
	 * @author King Lemming
	 *
	 */
	public static enum AccessMode {
		PUBLIC, GUILD, RESTRICTED, PRIVATE;

		public boolean isPublic() {

			return this == PUBLIC;
		}

		public boolean isGuild() {

			return this == GUILD;
		}

		public boolean isRestricted() {

			return this == RESTRICTED;
		}

		public boolean isPrivate() {

			return this == PRIVATE;
		}

		public static AccessMode stepForward(AccessMode curAccess) {

			return curAccess == PUBLIC ? GUILD : curAccess == GUILD ? RESTRICTED : curAccess == PRIVATE ? PUBLIC : PRIVATE;
		}

		public static AccessMode stepBackward(AccessMode curAccess) {

			return curAccess == PUBLIC ? PRIVATE : curAccess == PRIVATE ? RESTRICTED : curAccess == RESTRICTED ? GUILD : PUBLIC;
		}
	}

	boolean canPlayerAccess(EntityPlayer player);

	boolean setAccess(AccessMode access);

	boolean setOwnerName(String name);

	boolean setOwner(GameProfile name);

	AccessMode getAccess();

	String getOwnerName();

	GameProfile getOwner();

}
