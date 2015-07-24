package echo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import echo.entity.Entity;
import echo.map.Map;
import echo.screen.gameScreen.GameScreen;
import echo.screen.gameScreen.Menu;
import echo.screen.introScreen.IntroScreen;
import echo.screen.levelSelect.LevelSelectScreen;
import echo.utilities.TannScreen;


public class Main extends ApplicationAdapter {
	public static final float version = 0.86f;
	public static final float frameSpeed = 1/60f;
	public static int tilesAcross=25;
	public static int tilesDown=40;
	public static TextureAtlas atlas;
	public static Main self;
	public static double ticks;
	public static Stage menuStage;
	private TannScreen currentScreen;
	

	public static int totalLevels=28;
	@Override
	public void create () {
		self=this;
		atlas= new TextureAtlas(Gdx.files.internal("atlas_image.atlas"));
		Map.setupMapParser();
		menuStage=new Stage();
		menuStage.addListener(new InputListener(){
			public boolean keyDown(InputEvent event, int keyCode){
				switch(keyCode){
				case Keys.ESCAPE:
					toggleMenu();
					break;
				}
				return false;
			}
		});
		setScreen(new IntroScreen());
//		setScreen(LevelSelectScreen.get());
	}

	
	public void setScreen(TannScreen screen){
		currentScreen=screen;
		screen.listenForInput();
		screen.switchTo();
	}


	public void toggleMenu(){
		if(Menu.active){
			Menu.get().deactivate();
			currentScreen.listenForInput();
		}
		else{
			Menu.get().activate();
			menuStage.addActor(Menu.get());
		}
	}


	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Main.frameSpeed);
		currentScreen.draw(Main.frameSpeed);
		if(Menu.active){
			menuStage.draw();
		}
	}


	public void update(float delta){
		if(Menu.active){
			menuStage.act();
			return;
		}
		currentScreen.update(delta);
		ticks+=Gdx.graphics.getDeltaTime();
		Entity.update(delta);
	}
	
	
	



	
}
