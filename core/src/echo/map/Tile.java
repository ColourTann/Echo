package echo.map;

import java.util.Arrays;

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



	public static TextureRegion[] tileTextures = Main.atlas.findRegion("map/tiles").split(32, 32)[0];
	public static TextureRegion[] backgrounds = new TextureRegion[4];
	static{
		TextureRegion[][] firstTwo =tileTextures[0].split(32, 16);
		TextureRegion[][] secondTwo =tileTextures[1].split(32, 16);
		backgrounds[0] = firstTwo[0][0];
		backgrounds[1] = firstTwo[1][0];
		backgrounds[2] = secondTwo[0][0];
		backgrounds[3] = secondTwo[1][0];
	}
	public static TextureRegion[] decals = new TextureRegion[3];
	static{
		TextureRegion[][] firstTwo =tileTextures[7].split(32, 16);
		
		decals[0] = firstTwo[0][0];
		decals[1] = firstTwo[1][0];
		decals[2] = Main.atlas.findRegion("map/tiles").split(64, 32)[0][4];
		
	}
	static final Color goal = Colours.make(212,240,58);
	public static final int tileWidth=32;
	public static final int tileHeight=16;
	public static final int visibleHeight=32;
	public Rectangle collider;
	public int x, y;
	public TerrainType type;
	boolean collidable;
	TextureRegion texture;
	TextureRegion decal;
	boolean decalBlock;
	public Tile(int x, int y, TerrainType type) {
		collider=new Rectangle(x*tileWidth,y*tileHeight,tileWidth,visibleHeight);
		this.x=x;this.y=y;
		this.type=type;
		switch(type){
		case background: case goal:	case player:
			break;
		case base: case grass: case metal: case snow: case stone:
			collidable=true;
		default:
			break;
		}
		
	}

	public void setupTexture(){
		switch(type){
		case base:setupBaseTexture();break;
		case stone:texture=tileTextures[2];break;
		case grass:texture=tileTextures[3];break;
		case snow:texture=tileTextures[4];break;
		case metal:texture=tileTextures[5];break;
		case water:	texture=tileTextures[6]; break;
		default:
			break;
		}
	}
	
	private void setupBaseTexture() {
		texture= backgrounds[(int) (Math.random()*backgrounds.length)];
		decal = getDecal();
	}
	
	private TextureRegion getDecal(){
		if(decalBlock)return null;
		double rand = Math.random();
		float boneThreshold = .05f;
		float skullThreshold =	.05f;
		float bigThreshold =.05f;
		if(rand<boneThreshold) {
			decalBlock=true;
			return decals[0];
		}
		rand -= boneThreshold;
		if(rand<skullThreshold){
			decalBlock=true;
			return decals[1];
		}
		rand-= skullThreshold;
		if(rand<bigThreshold){			
			if(hasRoomFor2x2()){
				for(int dx=0;dx<=1;dx++){
					for(int dy=0;dy<=1;dy++){
						Main.currentMap.tilesArray[x+dx][y+dy].decalBlock=true;
					}
				}
				return decals[2];
			}
		}
		return null;
	}

	private boolean hasRoomFor2x2(){
		for(int dx=0;dx<=1;dx++){
			for(int dy=0;dy<=1;dy++){
				if(dx==0&&dy==0)continue;
				int newX=x+dx; int newY=y+dy;
				if(newX<0||newX>=Main.tilesAcross||
						newY<0||newY>=Main.tilesDown)return false;
				Tile t =Main.currentMap.tilesArray[newX][newY];
				if(t==null) return false;
				if(t.type!=TerrainType.base) return false;
				if(t.decalBlock) return false;
			}	
		}
		return true;
	}
	
	public Tile(int x, int y, int width, int height) {
		collider=new Rectangle(x,y,width,height);
		type=TerrainType.background;
	}

	public boolean checkIfInner(int dx, int dy){ //dx and dy represent the side the player is colliding. 1,0 is the right side//
		if(!collidable)return false;
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
		s.play(p.multiplier, 1, 0);
	}

	static final float variance = .1f;
	float getPitch(){
		return (float) (Math.random()*variance*2+(1-variance));
	}


	public void draw(Batch batch, float parentAlpha){
		batch.setColor(Color.WHITE);
		if(texture!=null){
			Draw.draw(batch, texture, collider.x, collider.y);
		}
		
	}
	
	public void postDraw(Batch batch){
		if(decal!=null){
			Draw.draw(batch, decal, collider.x, collider.y);
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
