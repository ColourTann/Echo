package echo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import echo.entity.Entity;
import echo.map.Map;
import echo.screen.gameScreen.GameScreen;
import echo.screen.gameScreen.Menu;
import echo.screen.introScreen.IntroScreen;
import echo.screen.levelSelect.LevelSelectScreen;
import echo.utilities.Font;
import echo.utilities.TannScreen;
import echo.utilities.TannScreen.TransitionType;


public class Main extends ApplicationAdapter {
	public static final float version = 0.86f;
	public static final float frameSpeed = 1/60f;
	public static int tilesAcross=25;
	public static int tilesDown=40;
	public static TextureAtlas atlas;
	public static Main self;
	public static double ticks;
	public static Stage menuStage;
	private TannScreen prevScreen;
	private TannScreen currentScreen;
	private SpriteBatch extraBatch;
	public static boolean debug=false;

	public static int totalLevels=28;
	@Override
	public void create () {
		extraBatch=new SpriteBatch();
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
		setScreen(screen, null);
	}


	public void setScreen(TannScreen screen, TransitionType type){
		if(screen==currentScreen)return;
		prevScreen=currentScreen;
		currentScreen=screen;
		if(type==null){
			currentScreen.center();
		}
		if(type!=null){
			if(prevScreen!=null)prevScreen.transition(type, .35f, Interpolation.pow3Out, false);
			currentScreen.transition(type, .35f, Interpolation.pow3Out, true);
		}
		screen.listenForInput();
		screen.activate();
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
		Gdx.gl.glClearColor(0, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Main.frameSpeed);
		if(prevScreen!=null)prevScreen.doDraw(Main.frameSpeed);
		currentScreen.doDraw(Main.frameSpeed);
		if(Menu.active){
			menuStage.draw();
		}
		if(debug){
			extraBatch.begin();
			drawFPS(extraBatch);
			extraBatch.end();
		}
	}


	public void update(float delta){
		if(Menu.active){
			menuStage.act();
			return;
		}
		TannScreen previous =prevScreen;
		currentScreen.updateAll(delta);
		if(previous!=null)previous.updateAll(delta);
		ticks+=Gdx.graphics.getDeltaTime();
		Entity.update(delta);
	}

	public void drawFPS(Batch batch){
		Font.font.draw(batch, "fps: "+Gdx.graphics.getFramesPerSecond(), 0, 100);
	}







}
