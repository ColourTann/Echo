package echo.screen.levelSelect;

import com.badlogic.gdx.Gdx;

import echo.Main;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.TannScreen;

public class LevelSelectScreen extends TannScreen{
	static int levelsAcross=6;
	static int levelsDown=(int) Math.ceil(Main.totalLevels/(float)levelsAcross);
	static int xGap=(Gdx.graphics.getWidth()-(levelsAcross*LevelChoice.scale*Main.tilesAcross))/(levelsAcross+1);
	static int yGap=(Gdx.graphics.getHeight()-(levelsDown*LevelChoice.scale*Main.tilesDown/2))/(levelsDown+1);
	private static TannScreen self;
	public static TannScreen get() {
		if(self==null)self=new LevelSelectScreen();
		return self;
	}
	
	private LevelSelectScreen() {
		init();
		for(int i=0;i<Main.totalLevels;i++){
			LevelChoice lc = new LevelChoice(i+1);
			lc.setPosition(
					xGap+i%levelsAcross*(lc.getWidth()+xGap), 
					Gdx.graphics.getHeight()-lc.getHeight()-(yGap+i/levelsAcross*(yGap+lc.getHeight())));
			stage.addActor(lc);
		}
	}
	
	@Override
	public void keyPressed(int keyCode) {
	}

	@Override
	public void draw(float delta) {
		batch.begin();
		batch.setColor(Colours.darkGrey);
		Draw.fillRectangle(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.end();
		batch.setColor(1,1,1,1);
		stage.draw();
	}

	@Override
	public void update(float delta) {
		stage.act(delta);
	}

	@Override
	public void switchTo() {
	}

	@Override
	public boolean handleEsc() {
		return false;
	}

	

}
