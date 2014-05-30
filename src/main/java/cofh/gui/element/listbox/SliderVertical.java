package cofh.gui.element.listbox;

import static cofh.gui.element.ElementButtonManaged.*;

import cofh.gui.GuiBase;
import cofh.gui.GuiColor;
import cofh.gui.element.ElementBase;

import org.lwjgl.opengl.GL11;

public abstract class SliderVertical extends ElementBase {

	private int _value;
	private int _valueMax;

	private boolean _isDragging;

	public int borderColor = new GuiColor(120, 120, 120, 255).getColor();
	public int backgroundColor = new GuiColor(0, 0, 0, 255).getColor();

	protected SliderVertical(GuiBase containerScreen, int x, int y, int width, int height, int maxValue) {

		super(containerScreen, x, y, width, height);
		_valueMax = maxValue;
	}

	public void setValue(int value) {

		if (value != _value && value >= 0 && value <= _valueMax) {
			_value = value;
			onValueChanged(_value);
		}
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

		drawModalRect(posX - 1, posY - 1, posX + sizeX + 1, posY + sizeY + 1, borderColor);
		drawModalRect(posX, posY, posX + sizeX, posY + sizeY, backgroundColor);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

		int sliderWidth = sizeX;
		int sliderHeight = 8;
		int sliderX = posX;
		int sliderY = posY + (sizeY - sliderHeight) * _value / _valueMax;

		if (!enabled) {
			gui.bindTexture(DISABLED);
		} else if (intersectsWith(mouseX, mouseY)) {
			gui.bindTexture(HOVER);
		} else {
			gui.bindTexture(ENABLED);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(sliderX, sliderY, 0, 0, sliderWidth / 2, sliderHeight / 2);
		drawTexturedModalRect(sliderX, sliderY + sliderHeight / 2, 0, 256 - sliderHeight / 2, sliderWidth / 2, sliderHeight / 2);
		drawTexturedModalRect(sliderX + sliderWidth / 2, sliderY, 256 - sliderWidth / 2, 0, sliderWidth / 2, sliderHeight / 2);
		drawTexturedModalRect(sliderX + sliderWidth / 2, sliderY + sliderHeight / 2, 256 - sizeX / 2, 256 - sliderHeight / 2, sliderWidth / 2,
				sliderHeight / 2);
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {

		_isDragging = true;
		return true;
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {

		_isDragging = false;
	}

	@Override
	public void update(int mouseX, int mouseY) {

		if (_isDragging) {
			setValue(_valueMax * (mouseY - posY) / sizeY);
		}
	}

	public abstract void onValueChanged(int value);
}
