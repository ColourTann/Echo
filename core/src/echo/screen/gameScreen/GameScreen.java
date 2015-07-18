package echo.screen.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.Main;
import echo.map.Map;
import echo.map.Map.MapState;
import echo.utilities.TextRegion;

public class GameScreen implements Screen{
	public static TextRegion topPanel;
	private static GameScreen self;
	public static GameScreen get(){
		if(self==null){
			self=new GameScreen();
			self.create();
		}
		return self;
	}
	
	public static int level=7;
	SpriteBatch stageBatch;
	Stage stage;
	OrthographicCamera cam;
	public Map currentMap;
	Group fairyHelp;
	private GameScreen() {
	}
	
	private void create() {
		cam = new OrthographicCamera();
		Viewport v = new ScreenViewport(cam);
		stage = new Stage(v);
		stageBatch=(SpriteBatch) stage.getBatch();
		
		
		topPanel=new TextRegion("", 0, 0, 170);
		setupFairyHelp();
		changeMap(level);
		currentMap.finishedZooming();
		stage.addActor(currentMap);
		
		stage.addActor(topPanel);
		setState(MapState.Waiting);
		Gdx.input.setInputProcessor(stage);
		stage.addListener(new InputListener(){
			  @Override
		         public boolean keyDown(InputEvent event, int keyCode){
				  switch(keyCode){
				  case Keys.H:
					  showFairyHelp();
					  break;
				  }
		           currentMap.keyDown(keyCode);
		            return true;
		         }
			
		});
		
	}
	
	private void setupFairyHelp(){
		fairyHelp=new Group();
		fairyHelp = new TextRegion("The fairies offer their assistance", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()-fairyHelp.getHeight(), 300);
		TextRegion accept = new TextRegion("Accept", 0, -fairyHelp.getHeight(), 150);
		TextRegion decline = new TextRegion("Decline", 150, -fairyHelp.getHeight(), 150);
		fairyHelp.addActor(accept);
		fairyHelp.addActor(decline);
		decline.setClickAction(new Runnable() {
			@Override
			public void run() {
				hideFairyHelp();
			}
		});
		accept.setClickAction(new Runnable() {
			@Override
			public void run() {
				hideFairyHelp();
				currentMap.requestHelp();
			}
		});
	}
	
	
	
	public void showFairyHelp(){
		fairyHelp.addAction(Actions.moveTo(fairyHelp.getX(), Gdx.graphics.getHeight()-fairyHelp.getHeight(), .3f, Interpolation.pow2Out));
		stage.addActor(fairyHelp);
	}
	
	public void hideFairyHelp(){
		fairyHelp.addAction(Actions.moveTo(fairyHelp.getX(), Gdx.graphics.getHeight()+fairyHelp.getHeight(), .3f, Interpolation.pow2Out));
	}
	
	public void setPanelText(String text){
		topPanel.setText(text);
		topPanel.clipToTopLeft();
	}

	
	public void setState(MapState state){
		switch(state){
		case Playing:
		case Waiting:
			switch(level){
				case 0: setPanelText("Version "+Main.version+"\nLevel 1\nTurn your sound up!\nKeep moving right"); break;
				case 1: setPanelText("Version "+Main.version+"\nLevel 1\nTurn your sound up!\nPress up to jump"); break;
				default: setPanelText("Level "+(level+1)); break;
			}
			
			
			break;
		case Replaying:
			setPanelText("Level "+(level+1)+"\nReplaying\nSpace to retry");
			break;
		case Victory:
			setPanelText("Level "+(level+1)+"\nYou win!\nSpace to continue");
			break;
		default:
			break;
		}
	}
	
	public void changeMap(int mapNum){
		if(currentMap!=null)currentMap.remove();
		currentMap=new Map(mapNum);
		currentMap.addDetails();
		stage.addActor(currentMap);
		topPanel.toFront();
	}

	public void nextLevel() {
		changeMap((++level)%13);
		setState(MapState.Waiting);
	}

	static Interpolation zoomTerp;
	static Interpolation zoomInTerp = new Interpolation.PowOut(3);
	static Interpolation zoomOutTerp = new Interpolation.PowOut(3);
	boolean zooming;
	boolean in;
	float startCamX, targetCamX, startCamY, targetCamY, startCamZoom, targetCamZoom, camTicker;
	public void tickCam(float delta){
		if(!zooming)return;

		camTicker+=delta;
		if(camTicker>1){
			zooming=false;
			camTicker=1;
			if(in){
				nextLevel();
				zoomOut(currentMap.portal.getX(Align.center), currentMap.portal.getY(Align.center));
			}
			else{
				currentMap.finishedZooming();
			}
		}

		float factor = zoomTerp.apply(camTicker);

		cam.position.x=startCamX+(targetCamX-startCamX)*factor;
		cam.position.y=startCamY+(targetCamY-startCamY)*factor;
		cam.zoom=startCamZoom+(targetCamZoom-startCamZoom)*factor;
		cam.update();
	}

	public void zoomInto(float x, float y) {
		startCamX=cam.position.x; startCamY=cam.position.y; startCamZoom=cam.zoom;
		targetCamX=x; targetCamY=y;
		targetCamZoom=.01f;
		camTicker=0;
		zooming=true;
		zoomTerp=zoomInTerp;
		in=true;
	}

	public void zoomOut(float fromX, float fromY){
		startCamX=fromX; startCamY=fromY; startCamZoom=cam.zoom;
		targetCamX=Gdx.graphics.getWidth()/2; targetCamY=Gdx.graphics.getHeight()/2;
		targetCamZoom=1f;
		camTicker=0;
		zooming=true;
		zoomTerp=zoomOutTerp;
		in=false;
	}

	@Override
	public void show() {
	}

	public void update(float delta){
		tickCam(delta);
		stage.act(delta);
	}

	@Override
	public void render(float delta) {
		update(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}


	

	public static void setup() {
		self=new GameScreen();
	}





}
