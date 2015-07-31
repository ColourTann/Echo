package echo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
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
import echo.map.Tile;
import echo.screen.FPSWarning;
import echo.screen.gameScreen.GameScreen;
import echo.screen.gameScreen.Menu;
import echo.screen.introScreen.IntroScreen;
import echo.screen.levelSelect.LevelSelectScreen;
import echo.utilities.Colours;
import echo.utilities.Font;
import echo.utilities.Slider;
import echo.utilities.Sounds;
import echo.utilities.TannScreen;
import echo.utilities.TannScreen.TransitionType;


public class Main extends ApplicationAdapter {
	public static final float version = 1;
	public static float frameSpeed = 1/60f;
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
	public static boolean html=false;
	Stage mainStage;
	public static int totalLevels=25;
	public static Music ambience;

	@Override
	public void create () {
		Sounds.setup();
		extraBatch=new SpriteBatch();
		mainStage=new Stage();
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
		Font.setup();
		Tile.setup();
		ambience = Sounds.am.get("sfx/ambience.ogg", Music.class);
		double current = System.currentTimeMillis();
		while(System.currentTimeMillis()<current+500){
		}
		setScreen(new IntroScreen());

	}

	public void updateMusicVolume(){
		ambience.setVolume(Slider.music.getValue()*.7f);
	}

	public void setScreen(TannScreen screen){
		setScreen(screen, null);
	}

	public static void startMusic(){

		ambience.play();
		ambience.setLooping(true);
		self.updateMusicVolume();
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
		Gdx.gl.glClearColor(Colours.darkBlueGreen.r, Colours.darkBlueGreen.g, Colours.darkBlueGreen.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(Main.frameSpeed);
		if(prevScreen!=null)prevScreen.doDraw(Main.frameSpeed);
		currentScreen.doDraw(Main.frameSpeed);
		if(Menu.active){
			menuStage.draw();
		}
		int fps = Gdx.graphics.getFramesPerSecond();
		if(Main.ticks>10&&fps<50&&!ignoreWarnings&&html){
			showFPSWarning();
		}

		if(debug){
			extraBatch.begin();
			drawFPS(extraBatch);
			extraBatch.end();
		}
		mainStage.draw();
	}

	public void showFPSWarning(){
		currentScreen.stage.addActor(FPSWarning.get());
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
		mainStage.act(delta);
	}

	public void drawFPS(Batch batch){
		Font.font.draw(batch, "fps: "+Gdx.graphics.getFramesPerSecond(), 0, 100);
	}


	static boolean ignoreWarnings;
	public static void ignoreFPSWarnings() {
		ignoreWarnings=true;
	}







}
