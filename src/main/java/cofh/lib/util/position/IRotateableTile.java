package cofh.lib.util.position;

import net.minecraft.util.EnumFacing;

public interface IRotateableTile {

	public boolean canRotate();

	public boolean canRotate(EnumFacing axis);

	public void rotate(EnumFacing axis);

	public void rotateDirectlyTo(int facing);

	public EnumFacing getDirectionFacing();

}
