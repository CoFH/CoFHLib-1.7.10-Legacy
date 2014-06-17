package cofh.gui.element.listbox;

import cofh.gui.GuiBase;
import cofh.gui.element.ElementSlider;

public abstract class SliderVertical extends ElementSlider {

	protected SliderVertical(GuiBase containerScreen, int x, int y, int width, int height, int maxValue) {

		super(containerScreen, x, y, width, height, maxValue);
		setSliderSize(width, 9);
	}

	@Override
	public int getSliderY() {

		return (_valueMax == 0 ? 0 : (sizeY - _sliderHeight - 1) * _value / _valueMax);
	}

	@Override
	public void dragSlider(int x, int v) {

		v += Math.round(_sliderHeight * (v / (float) sizeY - 0.5f));
		setValue(_valueMax * v / sizeY);
	}
}
