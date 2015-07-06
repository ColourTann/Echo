package echo.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.map.Tile;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Noise;

public class Bee extends Entity{
	public enum Direction{RIGHT, DOWN}
	static float stayTime=1;
	static float moveTime=1.5f;
	static float moveAmount=Tile.tileWidth*10;
	static float animationSpeed=20;
	static float noiseFreq=1;
	static float noiseAmp=30;
	static TextureRegion[] animation;
	static{
		Texture t= new Texture(Gdx.files.internal("entity/bee.png"));
		animation = TextureRegion.split(t, t.getWidth()/2, t.getHeight())[0];
	}
	
	int startX, startY;
	float xWiggle, yWiggle;
	float xNoiseOffset, yNoiseOffset;
	Direction dir;
	boolean moved;
	
	public Bee(int x, int y, Direction dir) {
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
		xWiggle=0;
		yWiggle=0;
		moved=false;
		currentFrame=0;
		frameTicker=0;
		clearActions();
		arrive();
	}
	
	private void arrive() {
		SequenceAction sa = new SequenceAction();
		sa.addAction(Actions.delay(stayTime));
		sa.addAction(Actions.moveBy(moved?-moveAmount:moveAmount, 0, moveTime, new Interpolation.Swing(1)));
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
	public void collideWithPlayer(Player p) {
		p.die();
	}
	
	public void act(float delta){
		super.act(delta);
		frameTicker+=Main.frameSpeed*animationSpeed;
		currentFrame=(int) (frameTicker%animation.length);
		xWiggle=(float)(Noise.noise(Entity.ticker*noiseFreq+xNoiseOffset)*noiseAmp);
		yWiggle=(float)(Noise.noise(Entity.ticker*noiseFreq+yNoiseOffset)*noiseAmp);
		collider.x=getX()-getWidth()/2+xWiggle;
		collider.y=getY()-getHeight()/2+yWiggle;
	}
	
	public void draw(Batch batch, float parentAlpha){
		Draw.drawCentered(batch, animation[currentFrame], getX()+xWiggle, getY()+yWiggle);
	}
}
