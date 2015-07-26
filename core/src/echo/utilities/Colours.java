package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Colours {

	// DB16 Palette//
	static Pixmap p;
	
	static{
		p=Draw.getPix("palette");
	}
	public static final Color light = new Color(p.getPixel(0, 4));
	public static final Color darkBlueGreen = new Color(p.getPixel(3, 1));
	public static final Color mediumBlueGreen = new Color(p.getPixel(1, 1));
	public static final Color darkGreen = new Color(p.getPixel(3, 2));
	public static final Color Green = new Color(p.getPixel(2, 2));
	public static final Color mediumGreen = new Color(p.getPixel(1, 2));
	public static final Color lightGreen = new Color(p.getPixel(0, 2));
	
	public static final Color[] colds4 = new Color[]{palette(3, 4), palette(2, 4), palette(1, 4), palette(0, 4)};
	
	private static Color palette(int x,int y){
		return new Color(p.getPixel(x, y));
	}

	public static void setupPalette(){

	}


	public static Color yesIReallyWantToUseColourWithAlpha(Color c, float alpha) {
		return new Color(c.r, c.g, c.b, alpha);
	}

	public static Color shiftedTowards(Color source, Color target, float amount) {
		if (amount > 1)
			amount = 1;
		if (amount < 0)
			amount = 0;
		float r = source.r + ((target.r - source.r) * amount);
		float g = source.g + (target.g - source.g) * amount;
		float b = source.b + (target.b - source.b) * amount;
		return new Color(r, g, b, 1);
	}

	public static Color multiply(Color source, Color target) {
		return new Color(source.r * target.r, source.g * target.g, source.b
				* target.b, 1);
	}

	public static Color make(int r, int g, int b) {
		return new Color((float) (r / 255f), (float) (g / 255f),
				(float) (b / 255f), 1);
	}

	public static Color monochrome(Color c) {
		float brightness = (c.r + c.g + c.b) / 3;
		return new Color(brightness, brightness, brightness, c.a);
	}

	public static boolean equals(Color a, Color b) {
		return a.a == b.a && a.r == b.r && a.g == b.g && a.b == b.b;
	}

	public static boolean wigglyEquals(Color a, Color aa) {
		float r = Math.abs(a.r - aa.r);
		float g = Math.abs(a.g - aa.g);
		float b = Math.abs(a.b - aa.b);
		float wiggle = .01f;
		return r < wiggle && g < wiggle && b < wiggle;
	}

	public static void setBatchColour(Batch batch, Color c) {
		batch.setColor(c.r, c.g, c.b, c.a);
	}

	public static void setBatchColour(Batch batch, Color c, float a) {
		batch.setColor(c.r, c.g, c.b, a);
	}
}