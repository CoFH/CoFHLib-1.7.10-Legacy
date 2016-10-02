package cofh.api.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;

/**
 * Implement this interface on Tile Entities which can provide information about themselves.
 *
 * @author King Lemming
 *
 */
public interface ITileInfo {

	/**
	 * This function appends information to a list provided to it.
	 *
	 * @param info
	 *            The list that the information should be appended to.
	 * @param world
	 *            Reference to the world.
	 * @param pos
	 *            Coordinates of the block containing the tile.
	 * @param side
	 *            The side of the block that is being queried.
	 * @param player
	 *            Player doing the querying - this can be NULL.
	 * @param debug
	 *            If true, the block should return "debug" information.
	 */
	void getTileInfo(List<ITextComponent> info, IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, boolean debug);

}
