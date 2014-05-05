package cofh.gui.element;

import cofh.gui.GuiBase;
import cofh.render.RenderHelper;

/**
 * Basic element which can render an arbitrary texture.
 * 
 * @author King Lemming
 * 
 */
public class ElementSimple extends ElementBase {

	int texU = 0;
	int texV = 0;

	public ElementSimple(GuiBase gui, int posX, int posY) {

		super(gui, posX, posY);
	}

	public ElementSimple setTextureOffsets(int u, int v) {

		texU = u;
		texV = v;
		return this;
	}

	@Override
	public void draw() {

		if (!visible) {
			return;
		}
		RenderHelper.bindTexture(texture);
		drawTexturedModalRect(posX, posY, texU, texV, sizeX, sizeY);
	}

}
