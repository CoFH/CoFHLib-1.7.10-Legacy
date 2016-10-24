package cofh.lib.gui.element;

import cofh.lib.gui.GuiBase;
import cofh.lib.gui.GuiProps;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public abstract class ElementButtonBase extends ElementBase {

	public static final ResourceLocation HOVER = new ResourceLocation(GuiProps.PATH_ELEMENTS + "button_hover.png");
	public static final ResourceLocation ENABLED = new ResourceLocation(GuiProps.PATH_ELEMENTS + "button_enabled.png");
	public static final ResourceLocation DISABLED = new ResourceLocation(GuiProps.PATH_ELEMENTS + "button_disabled.png");

	public ElementButtonBase(GuiBase containerScreen, int posX, int posY, int sizeX, int sizeY) {

		super(containerScreen, posX, posY, sizeX, sizeY);
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		playSound(mouseButton);
		switch (mouseButton) {
		case 0:
			onClick();
			break;
		case 1:
			onRightClick();
			break;
		case 2:
			onMiddleClick();
			break;
		}
		return true;
	}

	protected void playSound(int button) {

		if (button == 0) {
			//TODO is UI_BUTTON_CLICK sound ok? (was random.click which now has different variants for different blocks)
			GuiBase.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F);
		}
	}

	public void onClick() {

	}

	public void onRightClick() {

	}

	public void onMiddleClick() {

	}
}
