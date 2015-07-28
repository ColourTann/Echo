package echo.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import echo.Main;
import echo.map.Tile;
import echo.utilities.Draw;
import echo.utilities.Noise;
import echo.utilities.Slider;
import echo.utilities.Sounds;

public class Bee extends Entity{
	public enum Direction{RIGHT, DOWN}
	
	static float stayTime=1;
	static float moveTime=1.5f;
	static float moveAmount=Tile.tileWidth*9;
	static float animationSpeed=20;
	static float noiseFreq=1;
	static float noiseAmp=30;
	static TextureRegion[] animation;
	static{
		TextureRegion tr= Main.atlas.findRegion("entity/bee");
		animation = tr.split(tr.getRegionWidth()/2, tr.getRegionHeight())[0];
	}
	
	int startX, startY;
	float xWiggle, yWiggle;
	float xNoiseOffset, yNoiseOffset;
	Direction dir;
	boolean moved;
	boolean prime;
	boolean beeSound;
	public Bee(int x, int y, Direction dir, boolean prime, boolean beeSound) {
		
		this.prime=prime&&beeSound;
		this.dir=dir;
		this.startX=x*Tile.tileWidth+Tile.tileWidth/2; this.startY=y*Tile.tileHeight-Player.extraHeight+Tile.tileHeight/2;
		setSize(animation[0].getRegionWidth(), animation[0].getRegionHeight());
		setupCollider();
		this.xNoiseOffset=(float) (Math.random()*1000); 
		this.yNoiseOffset=(float) (Math.random()*1000);
		reset();
	}
	
	@Override
	public void reset() {
		setPosition(startX,  startY);
		moved=false;
		currentFrame=0;
		frameTicker=0;
		clearActions();

	}

	

	
	private void arrive() {
		SequenceAction sa = new SequenceAction();
		sa.addAction(Actions.delay(stayTime));
		sa.addAction(Actions.run(new Runnable() {
			
			@Override
			public void run() {
				if(!prime) return;
				addAction(Actions.delay(.4f, Actions.run( new Runnable() {
					public void run() {
						Sounds.beeMove[moved?1:0].play(Slider.SFX.getValue());
					}
				})));
				
			}
		}));
		float toMove=moved?-moveAmount:moveAmount;
		
		sa.addAction(Actions.moveBy(dir==Direction.RIGHT?toMove:0, dir==Direction.DOWN?toMove:0, moveTime, new Interpolation.Swing(1)));
		sa.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				
				moved=!moved;
				arrive();
			}			
		}));
		addAction(sa);
	}
	
	@Override
	public CollisionResult collideWithPlayer(Player p) {
		if(!p.active)return null;
		return CollisionResult.Death;
	}
	
	public void act(float delta){
		super.act(delta);
//		if(prime)System.out.println("bee: "+tick++);
		frameTicker+=Main.frameSpeed*animationSpeed;
		currentFrame=(int) (frameTicker%animation.length);
		xWiggle=(float)(Noise.noise(Entity.ticker*noiseFreq+xNoiseOffset)*noiseAmp);
		yWiggle=(float)(Noise.noise(Entity.ticker*noiseFreq+yNoiseOffset)*noiseAmp);
		collider.x=getX()-getWidth()/2+xWiggle;
		collider.y=getY()-getHeight()/2+yWiggle;
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		Draw.drawCentered(batch, animation[currentFrame], getX()+xWiggle, getY()+yWiggle);
	}

	@Override
	public void begin() {
		arrive();
	}

	@Override
	public void drawLights(Batch batch) {
	}

	@Override
	public boolean checkCollision(Player p) {
		return false;
	}

	@Override
	public CollisionResult handCollision(Player p) {
		return CollisionResult.Death;
	}
}
