package echo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.utilities.TextRegion;

public class FPSWarning extends Group{
	private static FPSWarning self;
	public static FPSWarning get() {
		if(self==null)self=new FPSWarning();
		return self;
	}
	
	public FPSWarning() {
		TextRegion mainRegion = new TextRegion("Seems like the game is running slowly, probably because of something something web. Closing other tabs might help?", 300);
		TextRegion download = new TextRegion("Get desktop version", 200);
		mainRegion.setY(download.getHeight());
		download.setPosition(0, 0);
		TextRegion ignore = new TextRegion("Ignore", 100);
		ignore.setPosition(download.getWidth(), 0);
		addActor(mainRegion);
		addActor(download);
		addActor(ignore);
		setSize(mainRegion.getWidth(), mainRegion.getHeight()+download.getHeight());
		setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Align.center);
		
		download.makeMouseable();
		ignore.makeMouseable();
		
		ignore.setClickAction(new Runnable() {
			@Override
			public void run() {
				Main.ignoreFPSWarnings();
				remove();
			}
		});
		
		download.setClickAction(new Runnable() {
			@Override
			public void run() {
				Gdx.net.openURI("http://threechoicegames.com/games/echo/echo.jar");
			}
		});
		
		
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if(Gdx.graphics.getFramesPerSecond()>55){
			remove();
		}
	}
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

}
