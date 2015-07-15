package echo.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.Main;
import echo.utilities.Draw;

public abstract class Entity extends Actor implements CollisionHandler{
	static float ticker=0;
	public Rectangle collider;
	protected float frameTicker;
	protected int currentFrame;
	protected void setupCollider(){
		collider= new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	public enum CollisionResult{Death, Glory}
	public abstract CollisionResult collideWithPlayer(Player p);
	public abstract void reset();
	public abstract void begin();
	public static void resetTicker(){
		ticker=0;
	}

	public static void update(float delta) {
		ticker+=Main.frameSpeed;
	}
	public void drawCollider(Batch batch){
		batch.setColor(1,1,1,.5f);
		Draw.fillRectangle(batch, collider.x, collider.y, collider.width, collider.height);
	}
	public abstract void drawLights(Batch batch);
	
	private static AtlasRegion mask;
	public static AtlasRegion getMask(){
		if(mask==null)mask = Main.atlas.findRegion("normalmask");
		return mask;
	}
	private static AtlasRegion reverseMask;
	public static AtlasRegion getReverseMask(){
		if(mask==null)mask = Main.atlas.findRegion("mask");
		return mask;
	}
}
