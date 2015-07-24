package echo.map;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.Main;
import echo.entity.CollisionHandler;
import echo.entity.Entity.CollisionResult;
import echo.entity.Player;
import echo.map.Map.TerrainType;
import echo.screen.gameScreen.GameScreen;
import echo.utilities.Colours;
import echo.utilities.Draw;

public class Tile extends Actor implements CollisionHandler{




	public static TextureRegion[] tileTextures = Main.atlas.findRegion("map/tiles").split(32, 32)[0];
	public static TextureRegion[] backgrounds = new TextureRegion[4];
	static TextureRegion spikeLeft;
	static TextureRegion spikeMid;
	static TextureRegion spikeRight;
	static TextureRegion spikeBase;
	static TextureRegion[] decals = new TextureRegion[3];
	static{
		TextureRegion[][] firstTwo =tileTextures[0].split(32, 16);
		TextureRegion[][] secondTwo =tileTextures[1].split(32, 16);
		TextureRegion[][] smallDecals = tileTextures[7].split(32, 16);
		backgrounds[0] = firstTwo[0][0];
		backgrounds[1] = firstTwo[1][0];
		backgrounds[2] = secondTwo[0][0];
		backgrounds[3] = secondTwo[1][0];
		spikeLeft=tileTextures[10];
		spikeMid=tileTextures[11];
		spikeRight=tileTextures[12];
		spikeBase=tileTextures[13];
		
		decals[0] = smallDecals[0][0];
		decals[1] = smallDecals[1][0];
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
	boolean spikeRotate;
	float spikeGrace=8;
	public Tile(int x, int y, TerrainType type) {
		setPosition(x*tileWidth,y*tileHeight);
		collider=new Rectangle(x*tileWidth,y*tileHeight,tileWidth,visibleHeight);
		if(type==TerrainType.spike){
			collider=new Rectangle(x*tileWidth+spikeGrace,y*tileHeight+spikeGrace-Player.extraHeight,tileWidth-spikeGrace*2,visibleHeight-spikeGrace*2);
		}
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
		case spike:	setupSpikeTexture(); break;
		default:
			break;
		}
	}
	
	private void setupSpikeTexture() {
		switch(check()){
		case base:
			texture=spikeBase; spikeRotate=true;
			break;
		case hLeft:
			texture=spikeLeft;
			break;
		case hMid:
			texture=spikeMid;
			break;
		case hRight:
			texture=spikeRight;
			break;
		case vBot:
			texture=spikeLeft; spikeRotate=true;
			break;
		case vMid:
			texture=spikeMid; spikeRotate=true;
			break;
		case vTop:
			texture=spikeRight; spikeRotate=true;
			break;
		default:
			break;
		}
	}

	enum SpikeType{vTop, vMid, vBot, hLeft, hMid, hRight, base};
	private SpikeType check(){
		Tile left = getTile(-1, 0);
		Tile right = getTile(1, 0);
		Tile up = getTile(0, -2);
		Tile down = getTile(0, 2);
		
		boolean leftSpike=left!=null&&left.type==TerrainType.spike&&!left.hasSpikeUD();
		boolean rightSpike=right!=null&&right.type==TerrainType.spike&&!right.hasSpikeUD();
		boolean upSpike=up!=null&&up.type==TerrainType.spike;
		boolean downSpike=down!=null&&down.type==TerrainType.spike;
		
		
		if(upSpike&&downSpike)return SpikeType.vMid;
		if(downSpike)return SpikeType.vTop;
		if(upSpike)return SpikeType.vBot;
		if(leftSpike&&rightSpike)return SpikeType.hMid;
		if(leftSpike)return SpikeType.hRight;
		if(rightSpike)return SpikeType.hLeft;
		return SpikeType.base;
	}
	
	private boolean hasSpikeLR(){
		Tile left = getTile(-1, 0);
		Tile right = getTile(1, 0);
		return (left!=null&&left.type==TerrainType.spike)||(right!=null&&right.type==TerrainType.spike);
	}
	
	private boolean hasSpikeUD(){
		Tile up = getTile(0, -2);
		Tile down = getTile(0, 2);
		return (up!=null&&up.type==TerrainType.spike)||(down!=null&&down.type==TerrainType.spike);
	}
	
	Tile getTile(int dx, int dy){
		int newX=x+dx; int newY=y+dy;
		if(newX<0||newX>=Main.tilesAcross||
				newY<0||newY>=Main.tilesDown){
			return null;
		}
		return GameScreen.get().currentMap.tilesArray[newX][newY];
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
						GameScreen.get().currentMap.tilesArray[x+dx][y+dy].decalBlock=true;
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
				Tile t =GameScreen.get().currentMap.tilesArray[newX][newY];
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
		if(GameScreen.get().currentMap.tilesArray[checkX][checkY]==null) return false;

		//only an inner edge if the tile in that direction is collidable
		return GameScreen.get().currentMap.tilesArray[checkX][checkY].collidable;
	}

	public void step(Player p){
		Sound s=type.foot[(int) (Math.random()*2)];
		if(s==null) return;
		s.play(p.getSoundMultiplier(), 1, 0);
	}

	static final float variance = .1f;
	float getPitch(){
		return (float) (Math.random()*variance*2+(1-variance));
	}

	public void draw(Batch batch, float parentAlpha){
		batch.setColor(Color.WHITE);
		float bonusY=((type==TerrainType.spike)?-Player.extraHeight:0);
		if(spikeRotate){
			Draw.drawRotatedScaled(batch, texture, getX()+getWidth(), getY()+bonusY+Tile.tileHeight*2, 1, 1, (float)-Math.PI/2);
		}
		else if(texture!=null){
			Draw.draw(batch, texture, getX(), getY()+bonusY);
		}
	}
	
	public void postDraw(Batch batch){
		batch.setColor(Color.WHITE);
		if(decal!=null){
			Draw.draw(batch, decal, collider.x, collider.y);
		}
	}

	public void land(Player p) {
		switch(type){
		case background:
			p.die();
			return;
		default:
			break;
		}
		Sound s=type.foot[(int) (Math.random()*2)];
		if(s==null) return;
		s.play(p.getSoundMultiplier(), getPitch(), 0);
	}

	@Override
	public boolean checkCollision(Player p) {
		return(collider.overlaps(p.collider));
	}

	@Override
	public CollisionResult handCollision(Player p) {
		switch(type){
		case spike:
			return CollisionResult.Death;
		default:
			return null;
		}
		
	}
}
