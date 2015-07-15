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
import echo.screen.gameScreen.GameScreen;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.Draw.BlendType;


public class Main extends Game {
	public static final float version = 0.5f;
	public static final float frameSpeed = 1/60f;
	public static int tilesAcross=25;
	public static int tilesDown=40;


	public static TextureAtlas atlas;


	public static Main self;
	
	public static double ticks;

	

	
	@Override
	public void create () {
		self=this;
		atlas= new TextureAtlas(Gdx.files.internal("atlas_image.atlas"));
		Map.setupMapParser();
		setScreen(GameScreen.get());
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
		Entity.update(delta);
	}
	
	



	
}
