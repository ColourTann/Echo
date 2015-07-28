package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;


public class Font {
	public static BitmapFont font;
	public static BitmapFont largeFont;
	public static BitmapFont hugeFont;
	public static GlyphLayout largeLayout;

	
	public static void setup(){
		font  = new BitmapFont(Gdx.files.internal("font.fnt"));
		largeFont  = new BitmapFont(Gdx.files.internal("largefont.fnt"));
		hugeFont = new BitmapFont(Gdx.files.internal("hugefont.fnt"));
		largeLayout= new GlyphLayout(largeFont, "");
		font.setUseIntegerPositions(true);
	}
	
}
