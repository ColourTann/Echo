package echo.screen.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;
import echo.Main;
import echo.entity.Fairy;
import echo.map.Map;
import echo.map.Map.MapState;
import echo.utilities.TannScreen;
import echo.utilities.TextRegion;

public class GameScreen extends TannScreen{
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
	public Map currentMap;
	public FairyHelp fairyHelp;
	private GameScreen() {
	}

	private void create() {
		init();
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
				
				currentMap.keyDown(keyCode);
				
				return false;
			}	
		});
	}

	

	private void setupFairyHelp(){
		fairyHelp = new FairyHelp();
	}

	public void setPanelText(String text){
		topPanel.setText(text);
		topPanel.clipToTopLeft();
	}

	private MapState currentState;
	public void setState(MapState state){
		currentState=state;
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

	public MapState getState(){
		return currentState;
	}

	public void changeMap(int mapNum){
		if(currentMap!=null)currentMap.remove();
		Fairy.setBrightness(Fairy.noHelp);
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
				fairyHelp.reset();
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

	
	public void update(float delta){
		if(Menu.active) return;
		tickCam(delta);
		stage.act(delta);
	}

	@Override
	public void draw(float delta) {
		stage.draw();
	}





	public static void setup() {
		self=new GameScreen();
	}

	@Override
	public void keyPressed(int keyCode) {
	}





}
