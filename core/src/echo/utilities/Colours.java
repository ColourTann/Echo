package echo.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Colours {

	// DB16 Palette//
	public static final Color dark = make(20, 12, 28);
	public static final Color darkRed = make(68, 36, 52);
	public static final Color darkBlue = make(48, 52, 109);
	public static final Color darkGrey = make(78, 74, 78);
	public static final Color brown = make(133, 76, 48);
	public static final Color green = make(52, 101, 36);
	public static final Color red = make(208, 70, 72);
	public static final Color grey = make(117, 113, 97);
	public static final Color blue = make(89, 125, 206);
	public static final Color orange = make(210, 125, 44);
	public static final Color lightGrey = make(133, 149, 161);
	public static final Color lightGreen = make(109, 170, 44);
	public static final Color pink = make(210, 170, 153);
	public static final Color lightBlue = make(109, 194, 202);
	public static final Color yellow = make(218, 212, 94);
	public static final Color light = make(222, 238, 214);

	public static Color[] colourList = new Color[] { dark, darkRed, darkBlue,
			darkGrey, brown, green, red, grey, blue, orange, lightGrey,
			lightGreen, pink, lightBlue, yellow, light };

	public static Color randomColor() {
		return colourList[(int) (Math.random() * colourList.length)];
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

	private static Color make(int r, int g, int b) {
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