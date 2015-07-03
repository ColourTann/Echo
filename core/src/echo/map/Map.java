package echo.map;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import echo.Main;
import echo.Player;
import echo.utilities.Colours;
import echo.utilities.Draw;

public class Map extends Group{
	public Tile[][] tilesArray= new Tile[Main.width/Tile.tileSize][Main.height/Tile.tileSize];
	public ArrayList<Tile> tiles= new ArrayList<Tile>();
	ArrayList<Player> deadPlayers= new ArrayList<Player>();
	String mapString;
	int playerStartX, playerStartY;
	public Map(String string) {
		setupBorders();
		this.mapString=string;
		loadMap(string);
		restart();
		setColor(0,0,0,0);


	}

	private void loadMap(String string) {
		Pixmap p = Draw.getPix("map/"+string);

		for(int x=0;x<p.getWidth();x++){
			for(int y=0;y<p.getHeight();y++){



				TerrainType target=mapKey.get(p.getPixel(x, y));


				switch(target){
				case background:

					break;
				case base:
				case snow:
				case goal:
					addTile(x, y, target);
					break;
				case player:
					playerStartX=x*Tile.tileSize;
					playerStartY=y*Tile.tileSize;
					break;
				default:
					break;

				}
			}	
		}


	}



	public void keyDown(int keyCode){
		if(keyCode==Keys.SPACE){
			if(ready)begin();
		}	
		currentPlayer.keyDown(keyCode);
	}



	private void begin() {
		ready=false;
		currentPlayer.activate();
		lightsOff();
	}



	public Player currentPlayer;
	private void makePlayer() {
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
		batch.setColor(Colours.dark);
		Draw.fillRectangle(batch, 0, 0, Main.width, Main.height);
		super.draw(batch, parentAlpha);

		batch.setColor(getColor());
		Draw.fillRectangle(batch, 0, 0, Main.width, Main.height);
	}
	public enum TerrainType{background, player, goal, base, snow}

	static HashMap<Integer, TerrainType> mapKey= new HashMap<Integer, Map.TerrainType>();
	public static void setupMap() {

		Pixmap p = Draw.getPix("map/key");

		mapKey.put(p.getPixel(0, 0), TerrainType.background);
		mapKey.put(p.getPixel(1, 0), TerrainType.player);
		mapKey.put(p.getPixel(2, 0), TerrainType.goal);
		mapKey.put(p.getPixel(3, 0), TerrainType.base);
		mapKey.put(p.getPixel(4, 0), TerrainType.snow);

	}


	public void restart(){
		if(currentPlayer!=null) deadPlayers.add(currentPlayer);
		makePlayer();
	}

	public void lightsOn() {
		addAction(Actions.alpha(0, .3f));
	}

	public void lightsOff() {
		addAction(Actions.alpha(.9f, .1f));
	}

	boolean ready=true;
	public void beginRestarting() {
		lightsOn();	
		SequenceAction sa = new SequenceAction();

		sa.addAction(Actions.delay(.7f, Actions.run(new Runnable() {
			@Override
			public void run() {
				currentPlayer.moveBack();
			}
		})));
		
		sa.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				ready=true;
				currentPlayer.startReplay();
			}
		}));
		addAction(sa);
	}
}