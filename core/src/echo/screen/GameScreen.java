package echo.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.Main;
import echo.map.Map;
import echo.map.Map.MapState;
import echo.utilities.TextRegion;

public class GameScreen implements AdvancedScreen{
	public static TextRegion topPanel;
	private static GameScreen self;
	public static GameScreen get(){
		if(self==null){
			self=new GameScreen();
			self.create();
		}
		return self;
	}
	
	
	
	



	public static int level=0;
	SpriteBatch stageBatch;
	Stage stage;
	OrthographicCamera cam;
	public Map currentMap;
	private GameScreen() {
	}
	
	private void create() {
		cam = new OrthographicCamera();
		Viewport v = new ScreenViewport(cam);
		stage = new Stage(v);
		stageBatch=(SpriteBatch) stage.getBatch();
		cam.zoom=1/Main.scale;
		cam.update();
		
		topPanel=new TextRegion(0, 0, 170, "");
		
		changeMap(level);
		currentMap.finishedZooming();
		stage.addActor(currentMap);
		
		stage.addActor(topPanel);
		setState(MapState.Waiting);
	}
	
	public void setPanelText(String text){
		topPanel.setText(text);
		topPanel.clipToTopLeft();
	}

	
	public void setState(MapState state){
		switch(state){
		case Playing:
		case Waiting:
			if(level==0)setPanelText("Level 1\nTurn your sound up!\nArrow keys to move");
			else setPanelText("Level "+(level+1));
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
		targetCamX=Main.width/2; targetCamY=Main.height/2;
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


	@Override
	public void keyDown(int keyCode) {
		currentMap.keyDown(keyCode);
	}


	public static void setup() {
		self=new GameScreen();
	}



}
