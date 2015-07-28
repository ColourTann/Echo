package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.Main;
import echo.screen.gameScreen.Menu;

public abstract class TannScreen {
	private boolean setup;
	public Stage stage;
	protected SpriteBatch batch;
	protected OrthographicCamera cam;

	protected void init() {
		cam = new OrthographicCamera();
		Viewport v = new ScreenViewport(cam);
		stage = new Stage(v);
		batch=(SpriteBatch) stage.getBatch();
		setup=true;
		stage.addListener(new InputListener(){
			public boolean keyDown(InputEvent event, int keyCode){
				switch(keyCode){
				case Input.Keys.ESCAPE:
					if(!handleEsc()){
						Main.self.toggleMenu();
					}
					break;
				}
				keyPressed(keyCode);
				return false;
			}
		});
	}
	public abstract void keyPressed(int keyCode);
	public void listenForInput(){
		Gdx.input.setInputProcessor(stage);
	}
	public abstract boolean handleEsc();
	public abstract void activate();
	public abstract void deactivate();
	public enum TransitionType{SlideLeft, SlideRight};
	private TransitionType transitionType;
	private float transitionTicks;
	private float maxTransitionTicks;
	float startX, startY, endX, endY;
	Interpolation interp;
	boolean transitionOut;
	boolean finishedTransitioning;
	public void transition(TransitionType type, float time, Interpolation interp, boolean on){
		transitionOut=!on;
		finishedTransitioning=false;
		transitionTicks=0;
		maxTransitionTicks=time;
		this.interp=interp;
		this.transitionType=type;
		if(on){
			switch(type){
			case SlideLeft:
				startX=-Gdx.graphics.getWidth()*.5f;
				startY=cam.position.y;
				break;
			case SlideRight:
				startX=Gdx.graphics.getWidth()*1.5f;
				startY=cam.position.y;
				break;
			default:
				break;
			}
			endX=Gdx.graphics.getWidth()*.5f;
			endY=cam.position.y;
		}
		else{
			startX=cam.position.x; startY=cam.position.y;
			switch(type){
			case SlideLeft:
				endX=Gdx.graphics.getWidth()*1.5f;
				break;
			case SlideRight:
				endX=-Gdx.graphics.getWidth()*.5f;
				break;
			default:
				break;
			}
			endY=cam.position.y;
		}
		cam.position.x=startX;
		cam.position.y=startY;
		cam.update();
	}
	public void updateAll(float delta) {
		if(transitionOut&&finishedTransitioning)return;
		tickTransition(delta);
		update(delta);
	}
	public void tickTransition(float delta){
		if(transitionType==null)return;
		if(transitionTicks==maxTransitionTicks){
			transitionType=null;
			finishedTransitioning=true;
			return;
		}
		transitionTicks+=delta;
		if(transitionTicks>maxTransitionTicks)transitionTicks=maxTransitionTicks;
		float ratio = transitionTicks/maxTransitionTicks;
		float newX=interp.apply(ratio, startX, endX);
		float newY=interp.apply(ratio, startY, endY);
		cam.position.x=(int) newX;
		cam.position.y=(int) newY;
		switch(transitionType){
		case SlideLeft:
			cam.position.x=(int) Math.floor(startX+Gdx.graphics.getWidth()*ratio);
			break;
		case SlideRight:
			cam.position.x=(int) Math.floor(startX-Gdx.graphics.getWidth()*ratio);
			break;
		default:
			break;
		
		}
		
		cam.position.y=startY;
		cam.update();
	}
	public void doDraw(float delta){
		if(transitionOut&&finishedTransitioning)return;
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		draw(delta);
	}
	protected abstract void draw(float delta);
	public abstract void update(float delta);
	public void center() {
		cam.position.x=Gdx.graphics.getWidth()/2;
		cam.update();
	}


}
