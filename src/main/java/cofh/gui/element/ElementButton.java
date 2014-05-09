package cofh.gui.element;

import cofh.gui.GuiBase;
import cofh.render.RenderHelper;
import cofh.util.StringHelper;

import java.util.List;

public class ElementButton extends ElementBase {

	int sheetX;
	int sheetY;
	int hoverX;
	int hoverY;
	int disabledX = 0;
	int disabledY = 0;
	boolean disabled = false;
	boolean tooltipLocalized = false;
	String tooltip;

	public ElementButton(GuiBase gui, int posX, int posY, String name, int sheetX, int sheetY, int hoverX, int hoverY, int sizeX, int sizeY, String texture) {

		super(gui, posX, posY);
		setName(name);
		setSize(sizeX, sizeY);
		setTexture(texture, texW, texH);
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		this.hoverX = hoverX;
		this.hoverY = hoverY;
	}

	public ElementButton(GuiBase gui, int posX, int posY, String name, int sheetX, int sheetY, int hoverX, int hoverY, int disabledX, int disabledY, int sizeX,
			int sizeY, String texture) {

		super(gui, posX, posY);
		setName(name);
		setSize(sizeX, sizeY);
		setTexture(texture, texW, texH);
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		this.hoverX = hoverX;
		this.hoverY = hoverY;
		this.disabledX = disabledX;
		this.disabledY = disabledY;
	}

	public ElementButton clearToolTip() {

		this.tooltip = null;
		return this;
	}

	public ElementButton setToolTip(String tooltip) {

		this.tooltip = tooltip;
		return this;
	}

	public ElementButton setToolTipLocalized(boolean localized) {

		this.tooltipLocalized = localized;
		return this;
	}

	@Override
	public void draw() {

		RenderHelper.bindTexture(texture);
		if (!disabled) {
			if (intersectsWith(gui.getMouseX(), gui.getMouseY())) {

				drawTexturedModalRect(posX, posY, hoverX, hoverY, sizeX, sizeY);
			} else {
				drawTexturedModalRect(posX, posY, sheetX, sheetY, sizeX, sizeY);
			}
		} else {
			drawTexturedModalRect(posX, posY, disabledX, disabledY, sizeX, sizeY);
		}
	}

	@Override
	public void addTooltip(List<String> list) {

		if (tooltip != null) {
			if (tooltipLocalized) {
				list.add(tooltip);
			} else {
				list.add(StringHelper.localize(tooltip));
			}
		}
	}

	@Override
	public boolean handleMouseClicked(int x, int y, int mouseButton) {

		if (!disabled) {
			gui.handleElementButtonClick(getName(), mouseButton);
			return true;
		}
		return false;
	}

	public void setSheetX(int pos) {

		sheetX = pos;
	}

	public void setSheetY(int pos) {

		sheetY = pos;
	}

	public void setHoverX(int pos) {

		hoverX = pos;
	}

	public void setHoverY(int pos) {

		hoverY = pos;
	}

	public void setActive() {

		disabled = false;
	}

	public void setDisabled() {

		disabled = true;
	}

}
