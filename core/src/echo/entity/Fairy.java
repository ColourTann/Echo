package echo.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import echo.Main;
import echo.screen.gameScreen.GameScreen;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.Noise;

public class Fairy extends Entity{


	static float animationSpeed=30;
	public static TextureRegion[] animation;
	static{
		TextureRegion tr = Main.atlas.findRegion("entity/fairy");
		animation= tr.split(tr.getRegionWidth()/6, tr.getRegionHeight())[0];
	}



	float startX, startY;
	public void setStart(float x, float y){
		startX=x; startY=y;
		noiseOffset=(float) (Math.random()*1000);
		setPosition(x, y);		
		setupCollider();
	}

	double angle;
	boolean flying;
	static final float speed=.7f;
	float dx, dy;
	float targetX, targetY;
	public void flyTo(float x, float y){
		dx=x-getX(); dy=y-getY();
		this.targetX=x; this.targetY=y;
		distancePart=0;
		flying=true;
	}


	public float getDistance(){
		float dx = targetX-getX();
		float dy = targetY-getY();
		return (float) Math.sqrt(dx*dx+dy*dy);
	}


	float distancePart;
	public void act(float delta){
		super.act(delta);
		if(fading)return;
		distancePart+=delta*speed;

		float baseX=startX+dx*distancePart;
		float baseY=startY+dy*distancePart;
		float baseAmp=300;
		float amp=baseAmp-distancePart*baseAmp;

		float xVariance = (float) Noise.noise(ticker+noiseOffset)*amp;
		float yVariance = (float) Noise.noise(ticker+(noiseOffset*2))*amp;

		setPosition(baseX+xVariance, baseY+yVariance);
		if(distancePart>=1){
			fade();
		}


	}

	boolean fading;
	private void fade() {
		fading=true;
		addAction(Actions.sequence(Actions.fadeOut(.2f), Actions.run(new Runnable() {
			
			@Override
			public void run() {
				kill();
			}
		})));
	}


	protected void kill() {
		GameScreen.get().currentMap.killEntity(this);
	}


	@Override
	public CollisionResult collideWithPlayer(Player p) {
		return null;
	}

	float noiseOffset;
	public boolean dead;
	@Override
	public void reset() {
	
	}

	@Override
	public void begin() {
	}



	public void draw(Batch batch, float parentAlpha){
		batch.setColor(getColor());
		Draw.drawCentered(batch, animation[(int) (ticker*animationSpeed)%animation.length], getX(), getY());
	}


	@Override
	public void drawLights(Batch batch) {
		batch.setColor(getColor());
		float scale = GameScreen.get().currentMap.helpRequested?7:1.5f;
		Draw.drawCenteredScaled(batch, getMask(), getX(), Gdx.graphics.getHeight()-getY(),scale, scale);
	}


	@Override
	public boolean checkCollision(Player p) {
		return false;
	}


	@Override
	public CollisionResult handCollision(Player p) {
		return null;
	}



}
