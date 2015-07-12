package echo;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.entity.Entity;
import echo.entity.Fairy;
import echo.entity.Player;
import echo.map.Map;
import echo.map.Tile;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.Draw.BlendType;


public class Main extends ApplicationAdapter {
	public static final float version = 0.3f;
	public static final float frameSpeed = 1/60f;
	static final int scale=1;
	public static int tilesAcross=25;
	public static int tilesDown=40;
	public static final int width=scale*tilesAcross*Tile.tileWidth;
	public static final int height=scale*tilesDown*Tile.tileHeight;

	public static TextureAtlas atlas;
	SpriteBatch stageBatch;
	Stage stage;
	OrthographicCamera cam;

	public static Main self;
	public static Map currentMap;
	public static double ticks;
	public static int level=0;
	@Override
	public void create () {
		self=this;
		atlas= new TextureAtlas(Gdx.files.internal("atlas_image.atlas"));
		cam = new OrthographicCamera();
		Viewport v = new ScreenViewport(cam);
		stage = new Stage(v);
		stageBatch=(SpriteBatch) stage.getBatch();
		Gdx.input.setInputProcessor(new InputProcessor() {
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				switch(keycode){
				//				case Keys.PLUS:
				//					scale++;
				//					redoScale();
				//					break;
				//
				//				case Keys.MINUS:
				//					scale--;
				//					redoScale();
				//					break;
				}
				currentMap.keyDown(keycode);
				return true;
			}
		});
		redoScale();
		Map.setupMapParser();
		changeMap(level);
		currentMap.finishedZooming();
		
		

		//		setupBuffer();
		//		Pixmap p = new Pixmap(64, 64, Format.RGBA8888);
		//		for(int x=0;x<64;x++){
		//			for(int y=0;y<64;y++){
		//				float dx = 32-x;
		//				float dy = 32-y;
		//				float distance = (float) Math.sqrt(dx*dx+dy*dy);
		//				float ratio = 1-(distance/32);
		//				p.setColor(new Color(1,1,1,ratio));
		//				p.drawPixel(x, y);
		//			}
		//		}
		//		
		//		PixmapIO.writePNG(new FileHandle("normalmask.png"), p);
		
	}

	



	private void redoScale() {
		Gdx.graphics.setDisplayMode((int)(Main.width*scale), (int) (Main.height*scale), false);
		cam.zoom=1/scale;
		cam.setToOrtho(false);
		cam.update();
	}

	public void changeMap(int mapNum){
		if(currentMap!=null)currentMap.remove();
		currentMap=new Map(mapNum);
		currentMap.addDetails();
		stage.addActor(currentMap);
	}
	

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Gdx.graphics.getDeltaTime());


		stage.draw();

//		stageBatch.begin();
//		stageBatch.setColor(1,1,1,1);
//		
//		Font.font.draw(stageBatch, "FPS: "+Gdx.graphics.getFramesPerSecond(), 1, Gdx.graphics.getHeight());
//		stageBatch.end();
	}

	


	public void update(float delta){
		tickCam(delta);
		Entity.update(Main.frameSpeed);
		stage.act(Main.frameSpeed);
	}



	float startCamX, targetCamX, startCamY, targetCamY, startCamZoom, targetCamZoom, camTicker;
	boolean zooming;
	
	static Interpolation zoomInTerp = new Interpolation.PowOut(3);
	static Interpolation zoomOutTerp = new Interpolation.PowOut(3);
	
	
	static Interpolation zoomTerp;
	boolean in;
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
	
	
	public void tickCam(float delta){
		if(!zooming)return;
		
		camTicker+=delta;
		if(camTicker>1){
			zooming=false;
			camTicker=1;
			if(in){
				nextLevel();
				zoomOut(Main.currentMap.portal.getX(Align.center), Main.currentMap.portal.getY(Align.center));
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





	public static void nextLevel() {
		Main.self.changeMap((++Main.level)%13);
	}
}
