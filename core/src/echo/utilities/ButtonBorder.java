package echo.utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;

import echo.Main;

public class ButtonBorder{
	static TextureRegion corner = Main.atlas.findRegion("interface/corner");
	static TextureRegion edge = Main.atlas.findRegion("interface/edge");
	static TextureRegion frill = Main.atlas.findRegion("interface/frill");
	static final Color bgCol = Colours.make(35, 45, 50); 
	static final Color highlightgCol = Colours.make(135, 145, 150);
	static final float gap =3;
	
	public static void drawBorder(Batch batch, float x, float y, float width, float height, boolean highlight){
		batch.setColor(highlight?highlightgCol:bgCol);
		Draw.fillRectangle(batch, x+gap, y+gap, width-gap*2, height-gap*2);
		
		batch.setColor(1,1,1,1);
		Draw.drawScaled(batch, edge, x, y, width, 1); //bottom//
		Draw.drawRotatedScaled(batch, edge, x+width, y, height, 1, (float)Math.PI/2); //right//
		Draw.drawRotatedScaled(batch, edge, x+width, y+height, width, 1, (float)Math.PI); //top//
		Draw.drawRotatedScaled(batch, edge, x, y+height, height, 1, (float)Math.PI*3/2); //left//
		
		Draw.drawScaled(batch, frill, x+width/2f-frill.getRegionWidth()/2f, y, 1, 1); //bottom//
		Draw.drawRotatedScaled(batch, frill, x+width, y+height/2f-frill.getRegionWidth()/2f, 1, 1, (float)Math.PI/2); //right//
		Draw.drawRotatedScaled(batch, frill, x+width/2f+frill.getRegionWidth()/2f, y+height, 1, 1, (float)Math.PI); //top//
		Draw.drawRotatedScaled(batch, frill, x, y+height/2+frill.getRegionWidth()/2, 1, 1, (float)Math.PI*3/2); //left//
		
		Draw.draw(batch, corner, x, y);
		corner.flip(false, true);
		Draw.draw(batch, corner, x, y+height-corner.getRegionHeight());
		corner.flip(true, false);
		Draw.draw(batch, corner, x+width-corner.getRegionWidth(), y+height-corner.getRegionHeight());
		corner.flip(false, true);
		Draw.draw(batch, corner, x+width-corner.getRegionWidth(), y);
		corner.flip(true, false);		
	}
}
