package cofh.gui.element;

import net.minecraftforge.fluids.FluidStack;
import cofh.gui.GuiBase;

public class ElementFluid extends ElementBase {

	public FluidStack fluid;

	public ElementFluid(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
	}

	public ElementFluid setFluid(FluidStack fluid) {

		this.fluid = fluid;
		return this;
	}

	@Override
	public void draw() {

		if (!visible) {
			return;
		}
		gui.drawFluid(posX, posY, fluid, sizeX, sizeY);
	}

}
