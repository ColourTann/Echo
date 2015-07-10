package echo.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import echo.Main;
import echo.entity.Bee;
import echo.entity.Entity;
import echo.entity.Fairy;
import echo.entity.Portal;
import echo.entity.Player;
import echo.entity.Bee.Direction;
import echo.entity.Spike;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Font;

public class Map extends Group{
	public enum TerrainType{background, player, goal, base, snow, stone, grass, metal, water, beeRight, beeDown, spike;
	Sound[] foot = new Sound[2];
	TerrainType(){
		for(int i=0;i<2;i++){
			FileHandle f = Gdx.files.internal("sfx/"+this+"foot"+i+".wav");
			System.out.println(f);
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
	int level;
	public Player currentPlayer;
	public boolean ready=true;
	public Map(int levelNum) {		
		this.level=levelNum;
		loadMap(levelNum+"");
		setupBorders();
		makePlayer();
		setColor(Colours.yesIReallyWantToUseColourWithAlpha(Colours.arachGround, 0));
		addFairy();
	}

	private void addFairy() {
		Fairy f = Fairy.get();
		f.setStart(10, 10);
		addEntity(f);
		f.flyTo(700, 700);
	}

	private void loadMap(String string) {
		boolean beeSound=true;
		Pixmap p = Draw.getPix("map/"+string);
		for(int y=Main.tilesDown/2;y>0;y--){
			for(int x=0;x<Main.tilesAcross;x++){
				TerrainType target=mapKey.get(p.getPixel(x, y));
				int location = (p.getHeight()-y)*2-1;
				switch(target){
				case background:
					break;
				case goal:
					addEntity(new Portal(x, location));
					break;
				case base:
				case stone:
				case grass:
				case snow:
				case metal:
				case water:
					//add extra base tile below//
					addTile(x, location-1, TerrainType.base);
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
				case spike:
					addEntity(new Spike(x, location));
					break;
				default:
					break;
				}
			}	
		}


	}

	public void act(float delta){
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
				Main.self.changeMap((++Main.level)%13);
			}
			if(replaying){
				resetLevel();
			}
			break;
		}
		currentPlayer.keyDown(keyCode);
	}

	boolean replaying;
	private void resetLevel(){
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
		Tile bot =new Tile(-offset, -depth, Main.width+offset*2, depth);
		Tile top =new Tile(-offset, Main.height, Main.width+offset*2, depth);
		Tile left = new Tile(-depth, -offset, depth, Main.height+offset*2);
		Tile right =new Tile(Main.width, -offset, depth, Main.height+offset*2); 
		tiles.add(bot); 
		tiles.add(top); 
		tiles.add(left); 
		tiles.add(right); 

		addActor(bot);
		addActor(top);
		addActor(left);
		addActor(right);

	}


	public void draw(Batch batch, float parentAlpha){
		//first background//
		batch.setColor(Colours.arachGround);
		Draw.fillRectangle(batch, 0, 0, Main.width, Main.height);
		//then actors//
		super.draw(batch, parentAlpha);
		//then darkness//
		batch.setColor(getColor());
		Draw.fillRectangle(batch, 0, 0, Main.width, Main.height);
		//then the goal (it's currently drawing twice....)//
		for(Entity e:entities){
			if(e instanceof Portal){
				e.draw(batch, parentAlpha);
			}
		}
		//bad tutorial code//

		String s="";
		if(level==0) s= "Version "+Main.version+"\nTurn your sound up!\nArrow keys to move\n";
		if(replaying)s="Replaying\nPress space to retry";
		if(victory) s="Congratulations! Press space to continue";
		Font.font.draw(batch, s, 10, Main.height-10);

	}



	public void lightsOn() {
		clearActions();
		addAction(Actions.alpha(0, .3f));
	}

	public void lightsOff() {
		clearActions();
		addAction(Actions.alpha(1, .15f));
	}

	public void showAllReplays(){
		replaying=true;
		resetEntities();
		for(Player p:deadPlayers){
			deadReplay(p);
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
		for(Player p:deadPlayers) if(p.replaying) return false;
		return true;
	}
	public void finishedMovingBack() {
		if(finishedReplaying()){
			showAllReplays();
		}
	}

	static HashMap<Integer, TerrainType> mapKey= new HashMap<Integer, Map.TerrainType>();
	public static void setupMapParser() {
		Pixmap p = Draw.getPix("map/0");
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
		victory=true;
	}

}