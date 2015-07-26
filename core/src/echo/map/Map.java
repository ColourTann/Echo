package echo.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.entity.Bee;
import echo.entity.Entity;
import echo.entity.Fairy;
import echo.entity.Portal;
import echo.entity.Player;
import echo.entity.Bee.Direction;
import echo.screen.gameScreen.GameScreen;
import echo.screen.gameScreen.Menu;
import echo.utilities.ButtonBorder;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Font;

public class Map extends Group{
	public enum MapState{Waiting, Playing, Replaying, Victory};
	
	public enum TerrainType{background, player, goal, base, snow, stone, grass, metal, water, beeRight, beeDown, spike;
	Sound[] foot = new Sound[2];
	TerrainType(){
		for(int i=0;i<2;i++){
			FileHandle f = Gdx.files.internal("sfx/"+this+"foot"+i+".wav");
			if(!f.exists()) break;
			foot[i]=Gdx.audio.newSound(f);
		}
	}
	}
	public static final float deathDelay=.7f;
	public Tile[][] tilesArray= new Tile[Main.tilesAcross][Main.tilesDown];
	public ArrayList<Tile> tiles= new ArrayList<Tile>();
	ArrayList<Player> deadPlayers= new ArrayList<Player>();
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	String mapString;
	int playerStartX, playerStartY;
	public int level;
	public Player currentPlayer;
	public boolean ready=true;
	public Portal portal;
	float fairyTicks;
	public boolean helpRequested;
	public Map(int levelNum) {		
		this.level=levelNum;
		loadMap(levelNum+"");
		setupBorders();
		makePlayer();
		setColor(Colours.yesIReallyWantToUseColourWithAlpha(Colours.darkBlueGreen, 0));
	}

	public void addFairy() {
		float tx = portal.getX()+portal.getWidth()/2;
		float ty = portal.getY()+portal.getHeight()/2;
		float sx, sy;

		float amountOffscreen=300;
		double rand = Math.random();

		float offScreenX= tx>Gdx.graphics.getWidth()/2?-amountOffscreen:Gdx.graphics.getWidth()+amountOffscreen;
		float offScreenY= ty>Gdx.graphics.getHeight()/2?-amountOffscreen:Gdx.graphics.getHeight()+amountOffscreen;



		if(Math.random()<.3){
			sx=offScreenX;
			sy = (float) (Math.random()/2*Gdx.graphics.getHeight()/2)+Gdx.graphics.getHeight()/4;
		}

		else if( rand < .6){
			sy=offScreenY;
			sx = (float) (Math.random()*Gdx.graphics.getWidth()/2)+Gdx.graphics.getWidth()/4;
		}		
		else{
			sx=offScreenX;
			sy=offScreenY;
		}
		Fairy f = new Fairy();
		f.setStart(sx, sy);
		f.flyTo(tx, ty);

		addEntity(f);

	}

	private void loadMap(String string) {
		boolean beeSound=true;
		if(string.length()==1)string=0+string;
		Pixmap p = Draw.getPix("map/"+string);
		for(int y=Main.tilesDown/2;y>0;y--){
			for(int x=0;x<Main.tilesAcross;x++){
				TerrainType target=mapKey.get(p.getPixel(x, y));
				int location = (p.getHeight()-y)*2-1;
				switch(target){
				case background:
					break;
				case goal:
					portal = new Portal(x, location);
					addEntity(portal);
					break;
				case base:
				case stone:
				case grass:
				case snow:
				case metal:
				case water:
					addTile(x, location-1, TerrainType.base);
					//add extra base tile below and FALL THROUGH//
				case spike:
					addTile(x, location, target);
					break;
				case player:
					playerStartX=x;
					playerStartY=location;
					break;
				case beeRight:
					addSwarm(x, location, Direction.RIGHT, beeSound);
					beeSound=false;
					break;
				case beeDown:
					addSwarm(x, location, Direction.DOWN, beeSound);
					beeSound=false;
					break;

				default:
					break;
				}
			}	
		}




	}

	public void addDetails(){
		for(Tile t:tiles){
			t.setupTexture();
		}
	}



	private void lightsIntoBuffer(Batch batch){
		Draw.beginBuffer(batch);
		batch.setColor(0,0,0,1);
		Draw.fillRectangle(batch, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		Gdx.gl20.glBlendEquation(GL30.GL_FUNC_REVERSE_SUBTRACT);
		batch.setColor(0,0,0,1);
		for(Entity e:entities){
			e.drawLights(batch);
		}
		Draw.endBuffer(batch);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl20.glBlendEquation(GL30.GL_FUNC_ADD);
	}

	public void act(float delta){
		if(transitioning)return;
		fairyTicks-=delta;
		if(fairyTicks<=0){
			addFairy();
			fairyTicks=(float) (Math.random()*(helpRequested?.8f:4));
		}
		if(currentPlayer.active&&!replaying){
			
		}
		super.act(delta);
	}

	static int numBees=10;
	private void addSwarm(int x, int y, Direction dir, boolean beeSound) {
		for(int i=0; i<numBees; i++){
			addEntity(new Bee(x, y, dir, i==0, beeSound));
		}
	}

	boolean victory;

	public void keyDown(int keyCode){
		if(Menu.active)return;
		if(transitioning||!finishedZooming) return;
		switch(keyCode){
		case Keys.HOME:
			lightsOn(); // cheeeats //
			break;

		case Keys.UP:
		case Keys.LEFT:
		case Keys.RIGHT:
		case Keys.DOWN:
			if(ready){
				begin();
			}
			break;

		case Keys.SPACE:
			if(victory){
				transition();
			}
			if(replaying){
				resetLevel();
			}
			break;
		}
		currentPlayer.keyDown(keyCode);
	}

	public void requestHelp() {
		Fairy.setBrightness(Fairy.help);
		helpRequested=true;
	}

	boolean transitioning;
	private void transition() {
		GameScreen.get().zoomInto(portal.getX(Align.center), portal.getY(Align.center));
		transitioning=true;
	}



	boolean replaying;
	private void resetLevel(){
		GameScreen.get().setState(MapState.Waiting);
		replaying=false;
		ready=true;
		makePlayer();
		resetEntities();
		currentPlayer.reset();
		for(Player p:deadPlayers){
			p.replaying=false;
			removeActor(p);
		}

	}

	private void beginEntities() {
		for(Entity e :entities){
			e.begin();
		}
	}

	private void begin() {
		GameScreen.get().setState(MapState.Playing);
		ready=false;
		resetEntities();
		currentPlayer.activate();
		beginEntities();
		lightsOff();
	}

	private void resetEntities() {
		//		Entity.resetTicker();
		for(Entity e:entities) e.reset();
	}

	private void makePlayer() {
		if(currentPlayer!=null){
			if(!currentPlayer.replay){
				currentPlayer.reset();
				return; //already has a player (probably only start of the game?//
			}
			deadPlayers.add(currentPlayer);
		}
		for(Player p:deadPlayers)p.age();
		removeActor(currentPlayer);
		currentPlayer = new Player(playerStartX, playerStartY);
		currentPlayer.reset();
		addActor(currentPlayer);
	}

	private void addTile(int x, int y, TerrainType target) {
		Tile t =new Tile(x, y, target);
		tiles.add(t);
		tilesArray[x][y]=t;
		addActor(t);
	}

	private void addEntity(Entity e){
		entities.add(e);
		addActor(e); 
	}

	private void setupBorders() {
		int offset=20, depth=100;
		Tile bot =new Tile(-offset, -depth, Gdx.graphics.getWidth()+offset*2, depth);
		Tile top =new Tile(-offset, Gdx.graphics.getHeight(), Gdx.graphics.getWidth()+offset*2, depth);
		Tile left = new Tile(-depth, -offset, depth, Gdx.graphics.getHeight()+offset*2);
		Tile right =new Tile(Gdx.graphics.getWidth(), -offset, depth, Gdx.graphics.getHeight()+offset*2); 
		tiles.add(bot); 
		tiles.add(top); 
		tiles.add(left); 
		tiles.add(right); 

		addActor(bot);
		addActor(top);
		addActor(left);
		addActor(right);

	}


	static TextureRegion background = Main.atlas.findRegion("map/background");
	public void draw(Batch batch, float parentAlpha){
		//first background//
		batch.setColor(1,1,1,1);
		Draw.draw(batch, background, 0, 0);
		//then actors//
		super.draw(batch, parentAlpha);
		//then tile decals//
		for(Tile t:tiles){
			t.postDraw(batch);
		}
		//then darkness//
		batch.end();
		lightsIntoBuffer(batch);
		batch.begin();
		batch.setColor(getColor());
		Draw.draw(batch, Draw.getBuffer().getColorBufferTexture(), 0, 0);
	}





	public void lightsOn() {
		clearActions();
		addAction(Actions.alpha(0, .3f));
	}

	public void lightsOff() {
		clearActions();
		addAction(Actions.alpha(1, .15f));
	}

	public void showReplays(boolean all){
		GameScreen.get().setState(currentPlayer.victory?MapState.Victory:MapState.Replaying);
		replaying=true;
		resetEntities();
		if(all){
		for(Player p:deadPlayers){
			deadReplay(p);
		}
		}
		beginEntities();
		currentPlayer.toFront();
		currentPlayer.startReplay();
	}

	private void deadReplay(Player p) {
		p.startReplay();
		addActor(p);
		
	}

	public boolean finishedReplaying() {
		if(currentPlayer.replaying) return false;
		if(victory){
			for(Player p:deadPlayers) if(p.replaying) return false;
		}
		return true;
	}
	public void finishedMovingBack() {
		if(finishedReplaying()){
			showReplays(victory);
		}
	}
	


	static HashMap<Integer, TerrainType> mapKey= new HashMap<Integer, Map.TerrainType>();
	public static void setupMapParser() {
		Pixmap p = Draw.getPix("map/01");
		mapKey.put(p.getPixel(0, 0), TerrainType.background);
		mapKey.put(p.getPixel(1, 0), TerrainType.base);
		mapKey.put(p.getPixel(2, 0), TerrainType.stone);
		mapKey.put(p.getPixel(3, 0), TerrainType.grass);
		mapKey.put(p.getPixel(4, 0), TerrainType.snow);
		mapKey.put(p.getPixel(5, 0), TerrainType.metal);
		mapKey.put(p.getPixel(6, 0), TerrainType.water);
		mapKey.put(p.getPixel(7, 0), TerrainType.player);
		mapKey.put(p.getPixel(8, 0), TerrainType.goal);
		mapKey.put(p.getPixel(9, 0), TerrainType.beeRight);
		mapKey.put(p.getPixel(10, 0), TerrainType.beeDown);
		mapKey.put(p.getPixel(11, 0), TerrainType.spike);
	}

	public void levelComplete() {
		GameScreen.get().setState(MapState.Victory);
		victory=true;
	}

	boolean finishedZooming;
	public void finishedZooming() {
		finishedZooming=true;
	}

	public void killEntity(Entity e) {
		entities.remove(e);
		removeActor(e);
	}

	int fails=0;
	public void levelFailed() {
		GameScreen.get().setState(MapState.Replaying);
		GameScreen.scoreKeeper.addDeath();
		fails++;
		if(fails==4)GameScreen.get().fairyHelp.showFairyHelp();
	}

}