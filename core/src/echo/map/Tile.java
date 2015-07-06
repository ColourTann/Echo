package echo.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.Main;
import echo.entity.Player;
import echo.map.Map.TerrainType;
import echo.utilities.Colours;
import echo.utilities.Draw;

public class Tile extends Actor{
	static Sound rockStep = Gdx.audio.newSound(Gdx.files.internal("sfx/basefoot.wav"));
	static Sound rockLand = Gdx.audio.newSound(Gdx.files.internal("sfx/baseland.wav"));
	static Sound snowStep = Gdx.audio.newSound(Gdx.files.internal("sfx/snowfoot.wav"));
	static Sound snowLand = Gdx.audio.newSound(Gdx.files.internal("sfx/snowland.wav"));
	static Sound metalStep = Gdx.audio.newSound(Gdx.files.internal("sfx/metalfoot.wav"));
	static Sound metalLand = Gdx.audio.newSound(Gdx.files.internal("sfx/metalland.wav"));
	static Sound grassStep = Gdx.audio.newSound(Gdx.files.internal("sfx/grassfoot.wav"));
	static Sound grassLand = Gdx.audio.newSound(Gdx.files.internal("sfx/grassland.wav"));

	static TextureRegion[][] tilesTextures = TextureRegion.split(new Texture(Gdx.files.internal("map/tiles.png")), 32, 32);
	static final Color goal = Colours.make(212,240,58);
	public static final int tileWidth=32;
	public static final int tileHeight=16;
	public static final int visibleHeight=32;
	public Rectangle collider;
	public int x, y;
	public TerrainType type;
	boolean collidable;
	boolean background;
	public Tile(int x, int y, TerrainType type) {
		collider=new Rectangle(x*tileWidth,y*tileHeight,tileWidth,visibleHeight);
		this.x=x;this.y=y;
		this.type=type;
		switch(type){
		case background:
		case goal:
		case player:
			break;
		case base:
		case grass:
		case metal:
		case snow:
		case rock:
			collidable=true;
		default:
			break;
		}
	}

	public Tile(int x, int y, int width, int height) {
		this.background=true;
		collider=new Rectangle(x,y,width,height);
		type=TerrainType.background;
	}
	
	public boolean checkIfInner(int dx, int dy){ //dx and dy represent the side the player is colliding. 1,0 is the right side//
		if(background)return false;
		int checkX = x+dx;
		int checkY = y+dy;
		
		//out of bounds
		if(checkY<0||checkX<0)return false; 
		if(checkX>=Main.tilesAcross||checkY>=Main.tilesDown) return false;
		
		//no tile there
		if(Main.self.currentMap.tilesArray[checkX][checkY]==null) return false;
		
		//only an inner edge if the tile in that direction is collidable
		return Main.self.currentMap.tilesArray[checkX][checkY].collidable;
	}
	
	public void step(Player p){
		switch(type){
		case background:
			break;
		case rock:
			rockStep.play(p.multiplier);
			break;
		case goal:
			break;
		case player:
			break;
		case snow:
			snowStep.play(p.multiplier);
			break;
		case base:
			break;
		case grass:
			grassStep.play(p.multiplier);
			break;
		case metal:
			metalStep.play(p.multiplier);
			break;
		case water:
			break;
		default:
			break;

		}
	}

	public void draw(Batch batch, float parentAlpha){
		TextureRegion tr =null;
		switch(type){
		case background:
			batch.setColor(Color.WHITE);
			Draw.fillRectangle(batch, collider.x, collider.y, collider.width, collider.height);
			break;
		case base:
			tr=tilesTextures[0][1];
			break;
		case rock:
			tr=tilesTextures[0][2];
			break;
		case grass:
			tr=tilesTextures[0][3];
			break;
		case snow:
			tr=tilesTextures[0][4];
			break;
		case metal:
			tr=tilesTextures[0][5];
			break;
		case water:
			tr=tilesTextures[0][6];
			break;
		case goal:
			Colours.setBatchColour(batch, goal, (float) (Math.sin(Main.ticks*4))/3+.5f);
			break;
		case player:
			break;
		default:
			break;
		}
		if(tr!=null) {
			batch.setColor(Color.WHITE);
			Draw.draw(batch, tr, collider.x, collider.y);
		}
		else {
			Draw.fillRectangle(batch, collider.x, collider.y, collider.width, Tile.visibleHeight);
		}
	}
	
	public void collide(Player p){
		switch(type){
		case background:
			break;
		case base:
			break;
		case goal: 
			break;
		case player:
			break;
		case snow:
			break;
		default:
			break;
		}
	}

	public void land(Player p) {
		switch(type){
		case background:
			p.die();
			break;
		case rock:
			rockLand.play(p.multiplier);
			break;
		case goal:
			break;
		case player:
			break;
		case snow:
			snowLand.play(p.multiplier);
			break;
		case base:
			break;
		case grass:
			grassLand.play(p.multiplier);
			break;
		case metal:
			metalLand.play(p.multiplier);
			break;
		case water:
			break;
		default:
			break;
	
		}
	}
}
