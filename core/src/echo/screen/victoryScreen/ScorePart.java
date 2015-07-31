package echo.screen.victoryScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Region;
import com.badlogic.gdx.scenes.scene2d.Group;

import echo.screen.gameScreen.ScoreKeeper;
import echo.utilities.ButtonBorder;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.TimeStuff;


public class ScorePart extends Group{
	TextureRegion region;
	float value;

	String text;
	public enum PartType{Time, Death, Help};
	PartType type;
	float picX=30, picY, textX=80, textY=80;
	
	public ScorePart(float value, TextureRegion region, PartType type) {
		this.type=type;
		this.region=region;
		this.value=value;

		
		switch(type){
		case Death:
			text = TimeStuff.pad((int)value,2)+" * 2 = "+value*2;
			break;
		case Help:
			text = TimeStuff.pad((int)value,2)+" * 10 = "+value*10;
			break;
		case Time:
			text= TimeStuff.timeString(value);
			break;
		default:
			break;
		}
		Font.largeLayout.setText(Font.largeFont, text);
		setSize(Font.largeLayout.width+105, 80);
		picY=getHeight()/2-region.getRegionHeight()/2*2;
	}
	
	public float getValue(){
		switch(type){
		case Death:
			return value*2;
		case Help:
			return value*10;
		case Time:
			return value;
		default:
			break;
		
		}
		return 0;
	}
	
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), false);
		Draw.drawScaled(batch, region, getX()+picX, getY()+picY, 2, 2);
		Font.largeFont.draw(batch, text+"", getX()+textX, getY()+picY+region.getRegionHeight()/2+Font.largeFont.getCapHeight()*1);
		super.draw(batch, parentAlpha);
	}
}
