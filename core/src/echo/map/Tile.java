package echo.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.Main;
import echo.map.Map.TerrainType;
import echo.utilities.Colours;
import echo.utilities.Draw;

public class Tile extends Actor{
	static Sound base = Gdx.audio.newSound(Gdx.files.internal("sfx/basefoot.wav"));
	static Sound baseLand = Gdx.audio.newSound(Gdx.files.internal("sfx/baseland.wav"));
	static Sound snow = Gdx.audio.newSound(Gdx.files.internal("sfx/snowfoot.wav"));
	static Sound snowLand = Gdx.audio.newSound(Gdx.files.internal("sfx/snowland.wav"));

	public static final int tileSize=5;
	public Rectangle collider;
	public int x, y;
	public TerrainType type;
	boolean collidable;
	public Tile(int x, int y, TerrainType type) {
		collider=new Rectangle(x*tileSize,y*tileSize,tileSize,tileSize);
		this.x=x;this.y=y;
		this.type=type;
		switch(type){
		case background:
			break;
		
		case goal:
			break;
		case player:
			break;
		case base:
		case snow:
			collidable=true;
			break;
		default:
			break;
		
		}
	}

	public Tile(int x, int y, int width, int height) {
		collider=new Rectangle(x,y,width,height);
		type=TerrainType.background;
	}
	
	public boolean check(int dx, int dy){
		int nx = x+dx;
		int ny = y+dy;
		if(ny<0||nx<0)return false;
		if(nx>=Main.width/tileSize||ny>=Main.height/tileSize)return false;
		if(Main.currentMap.tilesArray[nx][ny]==null)	return false;
		return Main.currentMap.tilesArray[nx][ny].collidable;
	}
	
	public void step(){
		switch(type){
		case background:
			break;
		case base:
			base.play();
			break;
		case goal:
			break;
		case player:
			break;
		case snow:
			snow.play();
			break;
		default:
			break;
		}
	}
	
	public void draw(Batch batch, float parentAlpha){
		switch(type){
		case background:
			break;
		case base:
			batch.setColor(Colours.darkRed);	
			break;
		case goal:
			batch.setColor(Colours.yellow);
			break;
		case player:
			break;
		case snow:
			batch.setColor(Colours.light);
			break;
		default:
			break;
		
		}
		Draw.fillRectangle(batch, collider.x, collider.y, collider.width, collider.height);
	}
	
	public void collide(){
		switch(type){
		case background:
			break;
		case base:
			break;
		case goal:
			Main.currentMap.currentPlayer.win();
			break;
		case player:
			break;
		case snow:
			break;
		default:
			break;
		
		}
	}

	public void land() {
		switch(type){
		case background:
			Main.currentMap.currentPlayer.die();
			break;
		case base:
			baseLand.play();
			break;
		case goal:
			break;
		case player:
			break;
		case snow:
			snowLand.play();
			break;
		default:
			break;
		}
	}
	
}
