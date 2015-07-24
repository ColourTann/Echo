package echo.screen.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import echo.Main;
import echo.utilities.TextRegion;

public class FairyHelp extends TextRegion{
	static float hiddenY;
	static float pokingY;
	static float normalY;
	public FairyHelp() {
		super("The fairies offer their assistance", Gdx.graphics.getWidth()/2, 0, 300);
		normalY=Gdx.graphics.getHeight()-getHeight();
		hiddenY=Gdx.graphics.getHeight()+getHeight();
		pokingY=hiddenY-10;
		setY(Gdx.graphics.getHeight()-getHeight());
		TextRegion accept = new TextRegion("Accept", 0, -getHeight(), 150);
		TextRegion decline = new TextRegion("Decline", getWidth()/2, -getHeight(), 150);
		addActor(accept);
		addActor(decline);
		decline.setClickAction(new Runnable() {
			@Override
			public void run() {
				decline();
			}
		});
		accept.setClickAction(new Runnable() {
			@Override
			public void run() {
				accept();
			}
		});
		
		addListener(new InputListener() {
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
				if(Gdx.input.isButtonPressed(0))return;
				if(accepted)return;
			
					showFairyHelp();
			
			}
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
				if(toActor!=null)return;
				if(accepted)return;
			
					hideFairyHelp();
				
			}
		});
	
	
	}

	boolean accepted;
	boolean declined;
	public void accept(){
		GameScreen.scoreKeeper.addHelp();
		accepted=true;
		declined=false;
		GameScreen.get().currentMap.requestHelp();
		hideFairyHelp();
	}
	
	public void reset(){
		declined=false;
		accepted=false;
		hideFairyHelp();
	}
	
	public void decline(){
		declined=true;
		hideFairyHelp();
	}
	
	public void showFairyHelp(){
		if(accepted)return;
		toFront();
		addAction(Actions.moveTo(getX(), normalY, .3f, Interpolation.pow2Out));
	}

	public void hideFairyHelp(){
		float tY=hiddenY;
		if(!accepted)tY=pokingY;
		addAction(Actions.moveTo(getX(), tY, .3f, Interpolation.pow2Out));
	}

}
