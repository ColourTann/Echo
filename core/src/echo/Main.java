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
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMapTile.BlendMode;
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
import echo.utilities.Draw.BlendType;


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

		mask = new Texture(Gdx.files.internal("mask.png"));
		buffer = new FrameBuffer(Format.RGBA8888, width, height, false);
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

	Texture mask;
	public static FrameBuffer buffer;


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
	private void setupBuffer(){

		buffer.bind();
		buffer.begin();
		batch.begin();
		batch.setColor(0,0,0,1);
		Draw.fillRectangle(batch, 0, 0, width, height);
		
		batch.flush();
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_DST_ALPHA);
		Gdx.gl20.glBlendEquation(GL30.GL_MIN);
		batch.flush();
		batch.setColor(0,0,0,1);
		for(Fairy f:fairies){
			if(!f.dead){
				Draw.drawCenteredScaled(batch, mask, f.getX(), height-f.getY(),3,3);
			}
		}

		batch.flush();
		batch.end();
		buffer.end();
		buffer.unbind();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glBlendEquation(GL30.GL_FUNC_ADD);
		batch.flush();

	}

	Texture t;
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Gdx.graphics.getDeltaTime());



//				stage.draw();

		batch.begin();
		Draw.draw(batch, buffer.getColorBufferTexture(), 0, 0);
		Font.font.draw(batch, "FPS: "+Gdx.graphics.getFramesPerSecond(), 1, Gdx.graphics.getHeight());
		batch.end();
	}


	ArrayList<Fairy> fairies = new ArrayList<Fairy>();
	public void update(float delta){
		setupBuffer();
		Entity.update(Main.frameSpeed);
		ticks+=frameSpeed;

		if(ticks>.3f){
			ticks=0;
			Fairy f = new Fairy();
			fairies.add(f);
			f.setStart(60, 60);
			stage.addActor(f);
			f.flyTo(500, 500);
		}

		stage.act(Main.frameSpeed);



	}
}
