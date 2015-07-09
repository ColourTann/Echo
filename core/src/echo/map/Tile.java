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

	
	
	public static TextureRegion[] tilesTextures = Main.atlas.findRegion("map/tiles").split(32, 32)[0];
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
		case stone:
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
		Sound s=type.foot[(int) (Math.random()*2)];
		if(s==null) return;
		s.play(p.multiplier, getPitch(), 0);
	}
	
	static final float variance = .1f;
	float getPitch(){
		return (float) (Math.random()*variance*2+(1-variance));
	}
	

	public void draw(Batch batch, float parentAlpha){
		TextureRegion tr =null;
		switch(type){
		case background:
			batch.setColor(Color.WHITE);
			Draw.fillRectangle(batch, collider.x, collider.y, collider.width, collider.height);
			break;
		case base:
			tr=tilesTextures[1];
			break;
		case stone:
			tr=tilesTextures[2];
			break;
		case grass:
			tr=tilesTextures[3];
			break;
		case snow:
			tr=tilesTextures[4];
			break;
		case metal:
			tr=tilesTextures[5];
			break;
		case water:
			tr=tilesTextures[6];
			break;
		case goal:
			
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
		default:
			break;
		}
		Sound s=type.foot[(int) (Math.random()*2)];
		if(s==null) return;
		s.play(p.multiplier, getPitch(), 0);
	}
}
