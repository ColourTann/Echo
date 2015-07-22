package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import echo.Main;
import echo.screen.gameScreen.Menu;

public abstract class TannScreen {
	private boolean setup;
	public Stage stage;
	protected SpriteBatch batch;
	protected OrthographicCamera cam;
	
	protected void init() {
		cam = new OrthographicCamera();
		Viewport v = new ScreenViewport(cam);
		stage = new Stage(v);
		batch=(SpriteBatch) stage.getBatch();
		setup=true;
		stage.addListener(new InputListener(){
			public boolean keyDown(InputEvent event, int keyCode){
				switch(keyCode){
				case Input.Keys.ESCAPE:
					Main.self.toggleMenu();
					break;
				}
				keyPressed(keyCode);
				return false;
			}
		});
	}
	public abstract void keyPressed(int keyCode);
	public void listenForInput(){
		Gdx.input.setInputProcessor(stage);
	}
	
	public abstract void draw(float delta);
	public abstract void update(float delta);
}
