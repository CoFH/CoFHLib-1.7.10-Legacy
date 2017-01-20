package cofh.api.tileentity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Implement this interface on Tile Entities which can have access restrictions.
 *
 * @author King Lemming
 */
public interface ISecurable {

	/**
	 * Enum for Access Modes - TeamOnly allows Team access, FriendsOnly is Friends Only, Private is Owner only.
	 *
	 * @author King Lemming
	 */
	enum AccessMode {
		PUBLIC, TEAM, FRIENDS, PRIVATE;

		public boolean isPublic() {

			return this == PUBLIC;
		}

		public boolean isTeamOnly() {

			return this == TEAM;
		}

		public boolean isFriendsOnly() {

			return this == FRIENDS;
		}

		public boolean isPrivate() {

			return this == PRIVATE;
		}

		public static AccessMode stepForward(AccessMode curAccess) {

			return curAccess == PUBLIC ? TEAM : curAccess == TEAM ? FRIENDS : curAccess == PRIVATE ? PUBLIC : PRIVATE;
		}

		public static AccessMode stepBackward(AccessMode curAccess) {

			return curAccess == PUBLIC ? PRIVATE : curAccess == PRIVATE ? FRIENDS : curAccess == FRIENDS ? TEAM : PUBLIC;
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
