package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;

import net.minecraft.util.IIcon;

public class ElementIcon extends ElementBase {

	IIcon icon;
	int spriteSheet;

	public ElementIcon(GuiBase gui, int posX, int posY, IIcon icon, int spriteSheet) {

		super(gui, posX, posY);
		this.icon = icon;
		this.spriteSheet = spriteSheet;
	}

	public ElementIcon(GuiBase gui, int posX, int posY, IIcon icon) {

		super(gui, posX, posY);
		this.icon = icon;
		this.spriteSheet = 0;
	}

	public ElementIcon setIcon(IIcon icon) {

		this.icon = icon;
		return this;
	}

	public ElementIcon setSpriteSheet(int spriteSheet) {

		this.spriteSheet = spriteSheet;
		return this;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		if (icon != null) {
			gui.drawIcon(icon, posX, posY, spriteSheet);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		return;
	}

}
