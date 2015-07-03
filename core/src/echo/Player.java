package echo;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import echo.map.Tile;
import echo.utilities.Colours;
import echo.utilities.Draw;

public class Player extends Actor{
	/*bytes*/
	static final byte left =	 0b1;
	static final byte right =	 0b10;
	static final byte up =		 0b100;
	static final byte jump =	 0b1000;
	/*sounds*/
	static final Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
	static final Sound dead = Gdx.audio.newSound(Gdx.files.internal("sfx/dead.wav"));
	static final Sound win = Gdx.audio.newSound(Gdx.files.internal("sfx/win.wav"));
	/*constants*/
	static final float deathDelay=.7f;
	static final int rectWidth=5, rectHeight=10;
	static final float gravity =900;
	static final float jumpStrength=140f, maxJumpTime=.18f;
	static final float horizontalAccel=500;
	static final float drag = .002f;
	static final float groundTimerNiceness=.06f;
	static final float stepsPerSound=8.2f;
	/*fields*/
	Rectangle collider = new Rectangle(0, 0, rectWidth, rectHeight);
	float stepper=0;
	float prevX, prevY;
	float dy, dx;
	boolean jumping, onGround;
	float jumpKindness;
	float airTime=0;
	int startX,startY;
	ArrayList<Byte> inputs = new ArrayList<Byte>();
	byte currentByte; /*00000001 left 
						00000010 right 
						00000100 up
						00001000 jump
	 */
	public boolean replay;
	public Player(int x, int y) {
		collider.x=x;
		collider.y=y;

		startX=x;startY=y;
		updateSprite();
	}


	int inputIndex=0;
	public void act(float delta){
		super.act(delta);
		if(replay){
			if(inputIndex>=inputs.size())return;
			doInput(inputs.get(inputIndex));
			inputIndex++;
		}
		
		else {
			recordInput();
			doInput(currentByte);
		}
		
		if(!active)return;
		jumpKindness-=Main.frameSpeed;
		doGravity();
		doDrag();
		move();
		if(onGround)stepper+=Math.abs(dx)*Main.frameSpeed;
		else {
			airTime+=Main.frameSpeed;
			stepper=0;
		}
		onGround=false;

		checkCollisions();
		updatePreviousPosition();
		updateSprite();

	}

	private void updateSprite() {
		if(overridePositioning) return;
		setPosition(collider.x, collider.y);
	}



	private void updatePreviousPosition() {
		prevX=collider.x; prevY=collider.y;
	}



	private void doDrag() {
		dx*=Math.pow(drag, Main.frameSpeed);
	}



	public void keyDown(int keyCode){
		if(replay)return;
		if(keyCode==Keys.UP) currentByte|=0b1000;
	}

	private void recordInput(){
		if(Gdx.input.isKeyPressed(Keys.LEFT)) currentByte|=left;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) currentByte|=right;
		if(Gdx.input.isKeyPressed(Keys.UP)) currentByte|=up;
	}
	
	private void doInput(byte input) {
		if(!active)return;
		
		float lr=0;
		if((input&left)>0) lr-=1;
		if((input&right)>0) lr+=1;
		if((input&jump)>0) jump();
		if((input&up)>0){
			if(jumping){
				if(airTime<maxJumpTime){
					dy=-jumpStrength;
				}
			}
		}
		else{
			jumping=false;
		}

		dx+=lr*horizontalAccel*Main.frameSpeed;
		inputs.add(currentByte);
		currentByte=0;
	}





	private void jump() {
		if(jumpKindness<0||!active)return;
		jumpSound.play();
		dy = -jumpStrength;
		airTime=0;
		jumping=true;
	}



	private void checkCollisions() {




		for(Tile t: Main.currentMap.tiles){
			if(t.collider.overlaps(collider)){
				t.collide(this);
				if(!t.check(0, 1)){
					if(prevY>=t.collider.y+t.collider.height){
						collider.y=t.collider.y+t.collider.height;
						dy=0;
					}
				}
				if(!t.check(0, -1)){
					if(prevY+collider.height<=t.collider.y){
						collider.y=t.collider.y-collider.height;
						dy=0;
						touchGround(t);
					}
				}
				//check to see if still colliding before checking horizontal collisions//
				if(t.collider.overlaps(collider)){
					if(!t.check(1, 0)){
						if(prevX>=t.collider.x+t.collider.width){
							collider.x=t.collider.x+t.collider.width;
							dx=0;
						}
					}
					if(!t.check(-1, 0)){
						if(prevX+collider.width<=t.collider.x){
							collider.x=t.collider.x-collider.width;
							dx=0;
						}
					}


				}
			}
		}	
	}

	private void touchGround(Tile underneath) {
		onGround=true;
		jumpKindness=groundTimerNiceness;
		if(airTime>0){
			underneath.land(this);
		}
		airTime=0;
		if(stepper>stepsPerSound){
			underneath.step();
			stepper=0;
		}
	}



	private void move() {
		changePosition(dx*Main.frameSpeed, dy*Main.frameSpeed);
	}



	private void changePosition(float x, float y) {
		collider.x+=x;
		collider.y+=y;
	}



	private void doGravity(){
		dy+=gravity*Main.frameSpeed;
	}

	public void draw(Batch batch, float parentAlpha){
		batch.setColor(Colours.blue);
		if(old)Colours.setBatchColour(batch, Colours.blue, .5f);
		Draw.fillRectangle(batch, getX(), getY(), collider.width, collider.height);
	}

	public boolean replaying;
	boolean active;
	public void die() {
		Main.currentMap.lightsOn();
		active=false;
		dead.play();
		addAction(Actions.delay(deathDelay, Actions.run(new Runnable() {
			@Override
			public void run() {
				moveBack();
			}
		} )));
	}



	public void win() {
		Main.currentMap.lightsOn();
		active=false;
		win.play();
		addAction(Actions.delay(deathDelay, Actions.run(new Runnable() {
			@Override
			public void run() {
				moveBack();
			}
		} )));
	}

	private void resetStuff() {
		collider.x=startX; collider.y=startY;
		updateSprite();
		updatePreviousPosition();
		dx=0;dy=0;
		inputIndex=0;
	}



	public void activate() {
		active=true;
	}


	boolean overridePositioning;
	public void moveBack() {
		overridePositioning=true;
		SequenceAction sa = new SequenceAction();
		sa.addAction(Actions.moveTo(startX, startY, .3f, Interpolation.pow2Out));
		sa.addAction(Actions.run(new Runnable() {
			
			@Override
			public void run() {
				overridePositioning=false;
				replaying=false;
				Main.currentMap.finishedMovingBack();
			}
		}));
		addAction(sa);
		collider.x=startX;
		collider.y=startY;
		resetStuff();
	}

	public void startReplay(){
		resetStuff();
		replaying=true;
		replay=true;
		active=true;
	}

	boolean old;
	public void setOld() {
		old=true;
	}

}
