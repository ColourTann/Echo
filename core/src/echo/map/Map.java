package echo.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import echo.Main;
import echo.entity.Player;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Font;

public class Map extends Group{
	public enum TerrainType{background, player, goal, base, snow, rock, grass, metal, water}
	public static final float deathDelay=.7f;
	public Tile[][] tilesArray= new Tile[Main.tilesAcross][Main.tilesDown];
	public ArrayList<Tile> tiles= new ArrayList<Tile>();
	ArrayList<Player> deadPlayers= new ArrayList<Player>();
	String mapString;
	int playerStartX, playerStartY;
	int level;
	public Player currentPlayer;
	public boolean ready=true;
	public Map(int levelNum) {		
		this.level=levelNum;;
		loadMap(levelNum+"");
		setupBorders();
		makePlayer();
		setColor(Colours.yesIReallyWantToUseColourWithAlpha(Colours.arachGround, 0));
	}

	private void loadMap(String string) {
		Pixmap p = Draw.getPix("map/"+string);
		for(int y=Main.tilesDown/2;y>0;y--){
			for(int x=0;x<Main.tilesAcross;x++){
				TerrainType target=mapKey.get(p.getPixel(x, y));
				int location = (y-1)*2;
				switch(target){
				case background:
					break;
				case base:
				case rock:
				case grass:
				case snow:
				case metal:
				case water:
					addTile(x, location+1, TerrainType.base); //add extra base tile below//
				case goal:
					addTile(x, location, target);
					break;
				case player:
					playerStartX=x;
					playerStartY=location-1;
					break;
				default:
					break;
				}
			}	
		}
	}

	public void keyDown(int keyCode){
		switch(keyCode){
		case Keys.HOME:
			lightsOn(); // cheeeats //
			break;
		case Keys.SPACE:
			if(ready){
				if(currentPlayer!=null&&currentPlayer.victory) Main.self.changeMap((++level)%10);
				else begin();
			}
			break;
		}
		currentPlayer.keyDown(keyCode);
	}

	private void begin() {
		ready=false;
		makePlayer();
		currentPlayer.activate();
		for(Player p:deadPlayers){
			p.replaying=false;
			removeActor(p);
		}
		lightsOff();
	}

	private void makePlayer() {
		if(currentPlayer!=null){
			if(!currentPlayer.replay) return; //already has a player (probably only start of the game?//
			deadPlayers.add(currentPlayer);
			currentPlayer.resetStuff();
		}
		for(Player p:deadPlayers) p.age(); 
		removeActor(currentPlayer);
		currentPlayer = new Player(playerStartX, playerStartY);
		addActor(currentPlayer);
	}

	private void addTile(int x, int y, TerrainType target) {
		Tile t =new Tile(x, y, target);
		tiles.add(t);
		tilesArray[x][y]=t;
		addActor(t);
	}

	private void setupBorders() {
		int offset=20, depth=100;
		tiles.add(new Tile(-offset, Main.height, Main.width+offset*2, depth)); //bottom//
		tiles.add(new Tile(-offset, -depth, Main.width+offset*2, depth)); //top//
		tiles.add(new Tile(-depth, -offset, depth, Main.height+offset*2)); //left//
		tiles.add(new Tile(Main.width, -offset, depth, Main.height+offset*2)); //right//
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
		for(Tile t:tiles){
			if(t.type==TerrainType.goal){
				t.draw(batch, parentAlpha);
			}
		}
		//bad tutorial code//
		if(ready){
			String s="";
			if(deadPlayers.size()==0&&!currentPlayer.replay){
				if(level==0) s= "Version "+Main.version+"\nTurn your sound up!\nArrow keys to move\nPress space to start";
				else s="Press space to start";
			}
			else{
				s="Replaying\nPress space to retry";
			}
			if(currentPlayer.victory){
				s="Congratulations! Press space to continue";
			}
			Font.font.draw(batch, s, 50, 50);
		}
	}
	

	static HashMap<Integer, TerrainType> mapKey= new HashMap<Integer, Map.TerrainType>();
	public static void setupMapParser() {
		Pixmap p = Draw.getPix("map/0");
		mapKey.put(p.getPixel(0, 0), TerrainType.background);
		mapKey.put(p.getPixel(1, 0), TerrainType.base);
		mapKey.put(p.getPixel(2, 0), TerrainType.rock);
		mapKey.put(p.getPixel(3, 0), TerrainType.grass);
		mapKey.put(p.getPixel(4, 0), TerrainType.snow);
		mapKey.put(p.getPixel(5, 0), TerrainType.metal);
		mapKey.put(p.getPixel(6, 0), TerrainType.water);
		mapKey.put(p.getPixel(7, 0), TerrainType.player);
		mapKey.put(p.getPixel(8, 0), TerrainType.goal);
	}

	public void lightsOn() {
		clearActions();
		addAction(Actions.alpha(0, .3f));
	}

	public void lightsOff() {
		clearActions();
		addAction(Actions.alpha(1f, 0));
	}

	public void showAllReplays(){
		for(Player p:deadPlayers){
			deadReplay(p);
		}
		currentPlayer.toFront();
		currentPlayer.startReplay();
	}

	private void deadReplay(Player p) {
		addActor(p);
		p.startReplay();
	}

	public boolean finishedReplaying() {
		if(currentPlayer.replaying) return false;
		for(Player p:deadPlayers) if(p.replaying) return false;
		return true;
	}

	public void finishedMovingBack() {
		ready=true;
		if(finishedReplaying()){
			showAllReplays();
		}
	}
}