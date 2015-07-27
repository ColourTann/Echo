package echo.screen.gameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
	public static ScoreKeeper scoreKeeper;
	public static GameScreen get(){
		if(self==null){
			self=new GameScreen();
			self.create();
		}
		return self;
	}

	public Map currentMap;
	public FairyHelp fairyHelp;
	private GameScreen() {
	}
 
	private void create() {
		init();
		topPanel=new TextRegion("", 0, 0, 170);
		setupFairyHelp();
		changeMap(1, true);
		stage.addActor(currentMap);
		stage.addActor(topPanel);
		stage.addListener(new InputListener(){
			@Override
			public boolean keyDown(InputEvent event, int keyCode){
				
				currentMap.keyDown(keyCode);
				
				return false;
			}	
		});
		scoreKeeper=new ScoreKeeper();
		stage.addActor(scoreKeeper);
		stage.addActor(fairyHelp);
	}

	

	private void setupFairyHelp(){
		fairyHelp = new FairyHelp();
	}

	public void setPanelText(String text){
		topPanel.setText(text);
		topPanel.clipTo(Align.topLeft);
	}

	private MapState currentState;
	public void setState(MapState state){
		currentState=state;
		switch(state){
		case Playing:
		case Waiting:
			switch(currentMap.level){
			case 1: setPanelText("Version "+Main.version+"\nLevel 1\nTurn your sound up!\nKeep moving right"); break;
			case 2: setPanelText("Version "+Main.version+"\nLevel 1\nTurn your sound up!\nPress up to jump"); break;
			default: setPanelText("Level "+(currentMap.level)); break;
			}


			break;
		case Replaying:
			setPanelText("Level "+(currentMap.level)+"\nReplaying\nSpace to retry");
			break;
		case Victory:
			setPanelText("Level "+(currentMap.level)+"\nYou win!\nSpace to continue");
			break;
		default:
			break;
		}
	}

	public MapState getState(){
		return currentState;
	}

	public void changeMap(int mapNum, boolean instant){
		if(currentMap!=null)currentMap.remove();
		if(mapNum>Main.totalLevels) mapNum=1;
		Fairy.setBrightness(Fairy.noHelp);
		currentMap=new Map(mapNum);
		currentMap.addDetails();
		stage.addActor(currentMap);
		topPanel.toFront();
		if(instant){
			currentMap.finishedZooming();
			setState(MapState.Waiting);
		}
		fairyHelp.hideFairyHelp();
	}

	public void nextLevel(boolean instant) {
		changeMap((++currentMap.level), instant);
		scoreKeeper.toFront();
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
				nextLevel(false);
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
		if(getState()==MapState.Playing)GameScreen.scoreKeeper.addTimer(delta);
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
		switch(keyCode){
		case Keys.NUMPAD_0:
			nextLevel(true);
			break;
		}
	}

	@Override
	public void activate() {
		Fairy.setBrightness(Fairy.noHelp);
	}

	@Override
	public boolean handleEsc() {
		return false;
	}

	@Override
	public void deactivate() {
	}


}
