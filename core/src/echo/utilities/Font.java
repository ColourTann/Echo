package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;


public class Font {
	public static BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
	public static BitmapFont largeFont = new BitmapFont(Gdx.files.internal("largefont.fnt"));
	public static BitmapFont hugeFont = new BitmapFont(Gdx.files.internal("hugefont.fnt"));
	public static GlyphLayout largeLayout = new GlyphLayout(largeFont, "");
	static{
		
		font.setUseIntegerPositions(true);
	}
	
}
