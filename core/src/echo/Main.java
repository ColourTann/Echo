package echo;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import echo.screen.AdvancedScreen;
import echo.screen.GameScreen;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.Draw.BlendType;


public class Main extends Game {
	public static final float version = 0.3f;
	public static final float frameSpeed = 1/60f;
	public static final int scale=1;
	public static int tilesAcross=25;
	public static int tilesDown=40;
	public static final int width=scale*tilesAcross*Tile.tileWidth;
	public static final int height=scale*tilesDown*Tile.tileHeight;

	public static TextureAtlas atlas;


	public static Main self;
	
	public static double ticks;

	

	
	@Override
	public void create () {
		self=this;
		atlas= new TextureAtlas(Gdx.files.internal("atlas_image.atlas"));
		
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
				getScreen().keyDown(keycode);
				return true;
			}
		});
		redoScale();
		Map.setupMapParser();
		setScreen(GameScreen.get());
		
	}

	



	private void redoScale() {
		Gdx.graphics.setDisplayMode((int)(Main.width*scale), (int) (Main.height*scale), false);
	}


	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Main.frameSpeed);
		getScreen().render(Main.frameSpeed);
	}


	public void update(float delta){
		ticks+=Gdx.graphics.getDeltaTime();
		getScreen().update(delta);
		Entity.update(delta);

	}
	
	public AdvancedScreen getScreen(){
		return (AdvancedScreen) super.getScreen();
	}



	
}
