package echo.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.Main;

public abstract class Entity extends Actor{
	static float ticker=0;
	public Rectangle collider;
	protected float frameTicker;
	protected int currentFrame;
	protected void setupCollider(){
		collider= new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	
	public abstract void collideWithPlayer(Player p);
	public abstract void reset();
	public static void resetTicker(){
		ticker=0;
	}

	public static void update(float delta) {
		ticker+=Main.frameSpeed;
	}
}
