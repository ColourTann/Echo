package echo.screen.levelSelect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import echo.Main;
import echo.screen.gameScreen.GameScreen;
import echo.utilities.Draw;
import echo.utilities.TannScreen.TransitionType;

public class LevelChoice extends Actor{
	
	TextureRegion region;
	static int scale=5;
	boolean moused;
	public LevelChoice(final int levelNum) {
		String levelString = levelNum+"";
		if(levelString.length()<2)levelString="0"+levelString;
		levelString="map/"+levelString+".png";
		Texture levelPic;
		levelPic=new Texture(Gdx.files.internal(levelString));
		region = new TextureRegion(levelPic, 0, 1, levelPic.getWidth(), levelPic.getHeight()-1);
		setSize(region.getRegionWidth()*scale, region.getRegionHeight()*scale);
		addListener(new InputListener(){
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Main.self.setScreen(GameScreen.get(), TransitionType.SlideRight);
				GameScreen.get().changeMap(levelNum,true);
				return false;
			}
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {moused=true;}
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {moused=false;}
		});
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(1,1,1,1);
		Draw.drawScaled(batch, region, getX(), getY(), scale, scale);
		if(moused){
			batch.setColor(1,1,1,.2f);
			Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
		}
		super.draw(batch, parentAlpha);
	}

}
