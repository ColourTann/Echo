package echo;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.entity.Entity;
import echo.entity.Fairy;
import echo.entity.Player;
import echo.map.Map;
import echo.map.Tile;
import echo.utilities.Draw;
import echo.utilities.Font;


public class Main extends ApplicationAdapter {
	public static final float version = 0.2f;
	public static final float frameSpeed = 1/60f;
	static final int scale=1;
	public static int tilesAcross=25;
	public static int tilesDown=40;
	public static final int width=scale*tilesAcross*Tile.tileWidth;
	public static final int height=scale*tilesDown*Tile.tileHeight;

	public static TextureAtlas atlas;
	SpriteBatch batch;
	Stage stage;
	OrthographicCamera cam;

	public static Main self;
	public Map currentMap;
	public static double ticks;
	public static int level=0;
	@Override
	public void create () {
		t= new Texture(Gdx.files.internal("grass.jpg"));
		self=this;
		atlas= new TextureAtlas(Gdx.files.internal("atlas_image.atlas"));
		cam = new OrthographicCamera();
		Viewport v = new ScreenViewport(cam);
		stage = new Stage(v);
		batch=(SpriteBatch) stage.getBatch();
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
		stage.addActor(currentMap);
	}
Texture t;
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Gdx.graphics.getDeltaTime());
		
		
		
		stage.draw();
		
		batch.begin();
		Font.font.draw(batch, "FPS: "+Gdx.graphics.getFramesPerSecond(), 1, Gdx.graphics.getHeight());
		batch.end();
	}

	
	public void update(float delta){
	
		Entity.update(Main.frameSpeed);
		ticks+=frameSpeed;
		
		if(ticks>.3f){
			ticks=0;
			Fairy f = new Fairy();
			
			f.setStart(60, 60);
			stage.addActor(f);
			f.flyTo(500, 500);
		}
		
		stage.act(Main.frameSpeed);
		
		
		
	}
}
