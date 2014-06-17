package cofh.gui.element.listbox;

import cofh.gui.GuiBase;
import cofh.gui.element.ElementSlider;

public abstract class SliderHorizontal extends ElementSlider {

	protected SliderHorizontal(GuiBase containerScreen, int x, int y, int width, int height, int maxValue) {

		super(containerScreen, x, y, width, height, maxValue);
		setSliderSize(9, height);
	}

	@Override
	public int getSliderX() {

		return (_valueMax == 0 ? 0 : (sizeX - _sliderWidth - 1) * _value / _valueMax);
	}

	@Override
	public void dragSlider(int v, int y) {

		v += Math.round(_sliderWidth * (v / (float) sizeX - 0.5f));
		setValue(_valueMax * v / sizeX);
	}
}
