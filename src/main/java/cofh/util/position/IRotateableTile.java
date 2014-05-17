package cofh.util.position;

import net.minecraftforge.common.util.ForgeDirection;

public interface IRotateableTile {

	public boolean canRotate(ForgeDirection axis);

	public void rotate(ForgeDirection axis);

	public ForgeDirection getDirectionFacing();
}
