package echo.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.Main;
import echo.utilities.Draw;

public abstract class Entity extends Actor{
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
}
