package echo.screen.gameScreen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.screen.levelSelect.LevelSelectScreen;
import echo.utilities.ButtonBorder;
import echo.utilities.Draw;
import echo.utilities.InputBlocker;
import echo.utilities.Slider;
import echo.utilities.TextRegion;
import echo.utilities.TannScreen.TransitionType;

public class Menu extends Group{
	static float w=400, h=340;
	public static boolean active;
	private static Menu self;
	public static Menu get(){
		if(self==null)self=new Menu();
		return self;
	}
	
	private Menu() {
		setSize(w, h);
		setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Align.center);
		addActor(InputBlocker.get());
		
		float levelGap= 50;
		float stateButtonWidth=130;
		
				
		TextRegion startSpeedrun = new TextRegion("Start Speedrun",stateButtonWidth);
		startSpeedrun.makeMouseable();
		startSpeedrun.setClickAction(new Runnable() {
			@Override
			public void run() {
				Main.self.setScreen(GameScreen.get(), TransitionType.SlideRight);
				GameScreen.get().changeMap(1, true);
				GameScreen.scoreKeeper.activate();
				Main.self.toggleMenu();
			}
		});
		startSpeedrun.setPosition(levelGap, getHeight()-startSpeedrun.getHeight()-27);
		
		TextRegion levelSelect = new TextRegion("Level Select",stateButtonWidth);
		levelSelect.setClickAction(new Runnable() {
			@Override
			public void run() {
				Main.self.setScreen(LevelSelectScreen.get(), TransitionType.SlideLeft);
				Main.self.toggleMenu();
				GameScreen.scoreKeeper.deactivate();
			}
		});
		levelSelect.setPosition(getWidth()-levelGap-startSpeedrun.getWidth(), getHeight()-levelSelect.getHeight()-27);
		levelSelect.makeMouseable();
		addActor(levelSelect);
		
		addActor(startSpeedrun);
		
		Slider.SFX.setPosition(getWidth()/2, getHeight()-Slider.SFX.getHeight()-120, Align.center);
		addActor(Slider.SFX);
		
		Slider.music.setPosition(getWidth()/2, getHeight()-Slider.SFX.getHeight()-70, Align.center);
		Slider.music.addSlideAction(new Runnable() {
			@Override
			public void run() {
				Main.self.updateMusicVolume();
			}
		});
		addActor(Slider.music);
		
		
		int gap =50;
		int creditsOffset=30;
		for(int i=0;i<3;i++){
			String s = "";
			switch(i){
			case 0: s="Art by Arachne @agnesheyer"; break;
			case 1: s="Sound by Topher Pirkl @PhantomFreq";break;
			case 2: s="Code and concept by Tann @3CGames";break;
			}
			TextRegion t = new TextRegion(s, 0,0,300);
			switch(i){
			case 0: s="https://twitter.com/agnesheyer"; break;
			case 1: s="https://twitter.com/PhantomFreq";break;
			case 2: s="https://twitter.com/3CGames";break;
			}
			final String uri=s;
			t.setClickAction(new Runnable() {
				@Override
				public void run() {
					Gdx.net.openURI(uri);
				}
			});
			t.setPosition(getWidth()/2, getHeight()/2-creditsOffset-gap*i, Align.center);
			t.makeMouseable();
			addActor(t);
		}		

		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(0,0,0,.6f);
		Draw.fillRectangle(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), false);
		batch.setColor(1,1,1,1);
		super.draw(batch, parentAlpha);
	}
	
	

	public void activate() {
		Gdx.input.setInputProcessor(Main.menuStage);
		active=true;
	}
	public void deactivate(){
		active=false;
	}
}
