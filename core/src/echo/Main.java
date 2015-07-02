package echo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.utilities.Draw;

public class Main extends ApplicationAdapter {
	public static float frameSpeed = 1/60f;
	SpriteBatch batch;
	public static TextureAtlas atlas;
	Stage stage;
	OrthographicCamera cam;
	static int width=200;
	static int height=160;
	static float scale=3;
	public static Map currentMap = new Map();
	Player player;
	@Override
	public void create () {
		
		atlas= new TextureAtlas(Gdx.files.internal("atlas_image.atlas"), true);
		
		Gdx.graphics.setDisplayMode((int)(Main.width*scale), (int) (Main.height*scale), false);
		
		cam = new OrthographicCamera();
		
//		cam.zoom=1/(float)scale;
		
		
//		cam.translate(-width/2, -height/2);
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
				player.pushKey(keycode);
				return true;
			}
		});
		
		for(int i=0;i<0;i++){
			stage.addActor(new Square());
		}
		player = new Player();
		stage.addActor(player);
		stage.addActor(currentMap);
		cam.zoom=1/scale;
		cam.setToOrtho(true);
		//cam.translate(-cam.position.x, height/2);
		System.out.println(cam.position.x+":"+cam.position.y);
		cam.update();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Gdx.graphics.getDeltaTime());
		stage.draw();


//		batch.end();
	}
	
	public void update(float delta){
//		Main.frameSpeed=delta;
		stage.act(delta);
	}
}
