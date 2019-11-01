package com.zff.xpanel.parser.view;

/**
 * 
 * @author 1016tx
 * 
 * 拖动条参数
 *
 *<slider j="20" d="30" x="139" y="314" w="154" h="15" min="0" max="100" decimals="0" unit="decimal" t="newtheme_3" sim="0" l="0">
        <indicator state="0" offsetX="0" offsetY="0" x="0" y="0" w="0" h="0" t="">transports_grey_play_off_20.png</indicator>
      </slider>
 */
public class SliderArgs extends GaugeArgs{

	private Indicator indicator;
	
	public SliderArgs(){
		setType(Type.SEEK_BAR);
	}
	
	public Indicator getIndicator() {
		return indicator;
	}



	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}



	public static class Indicator{
		public String imgPath;
	}
	
}
