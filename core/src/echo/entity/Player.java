package echo.entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import echo.Main;
import echo.map.Tile;
import echo.utilities.Colours;
import echo.utilities.Draw;

public class Player extends Entity{
	/*bytes*/
	static final byte byteLeft =	 	1;
	static final byte byteRight =	 	1<<1;
	static final byte upByte =		 	1<<2;
	static final byte byteJumpPressed= 	1<<3;
	/*sounds*/
	static final Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("sfx/jump.wav"));
	static final Sound dead = Gdx.audio.newSound(Gdx.files.internal("sfx/dead.wav"));
	static final Sound win = Gdx.audio.newSound(Gdx.files.internal("sfx/win.wav"));
	/*constants*/
	static final float animSpd=.04f;
	static final float deathDelay=.7f;
	public static final int extraHeight=Tile.visibleHeight/3;
	
//	static final int rectWidth=Tile.tileWidth, rectHeight=(int) (Tile.visibleHeight+extraHeight);

	static final float gravity =5400;
	static final float jumpStrength=840f, maxJumpTime=.18f;
	static final float horizontalAccel=3000;
	static final float drag = .002f;
	static final float groundTimerNiceness=.06f;
	static final float stepsPerSound=Tile.tileWidth;
	/*graphical*/
	static TextureRegion[] run;
	static {
		TextureRegion sheetRun = Main.atlas.findRegion("player/run");
		run = sheetRun.split(sheetRun.getRegionWidth()/8, sheetRun.getRegionHeight())[0];
	}
	 
	 
	static TextureRegion idle = Main.atlas.findRegion("player/idle");
	static final int rectWidth=run[0].getRegionWidth()/2, rectHeight=(int) (run[0].getRegionHeight()/1.4f);
	public static final int extraWidth=rectWidth/2;
	static final Color playerCol = Colours.make(107, 165, 214);
	static final Color playerReplayCol = new Color(.3f,.5f,1,1);
	/*input stuff*/
	int inputIndex=0;
	byte currentByte;
	ArrayList<Byte> inputs = new ArrayList<Byte>();
	/*positioning*/
	int startX,startY;
	Rectangle collider = new Rectangle(0, 0, rectWidth, rectHeight);
	
	static final int feetWidth=(int) (rectWidth*.1f);
	static final int feetHeight=1;
	Rectangle feet = new Rectangle(0, 0, feetWidth, feetHeight);
	float stepper=0; 
	float prevX, prevY;
	float dx, dy;
	float jumpKindness; 
	float airTime=0;
	boolean jumping; //used to check if player has hit ceiling or floor since last jump//
	boolean onGround;
	boolean overridePositioning; //should override positioning so can be pinged back to start//
	int facingSide=1;
	/*state flags*/
	public boolean replay; //if the player has died or won and is now a replayer//
	public boolean replaying; //if currently replaying self//
	boolean active; //if should be moving/accepting input/
	public boolean victory; //if this player reached the goal//
	int age; //how many plays ago//
	public float multiplier=1; //determines alpha and volume for sound effects//
	int id=0;
	int fn;
	Entity finalCollision;
	public Player(int x, int y) {
		startX=x*Tile.tileWidth; startY=y*Tile.tileHeight;
		reset();
		updateSprite();
		id=(int) (Math.random()*100);
	}

	public void act(float delta){
		super.act(delta);
		
		if(!active)return;
		if(replay&&!replaying)return;
//		System.out.println("player: "+fn++);
		/* if replaying, take input from list, otherwise record input */

		if(replay){
			if(inputIndex==inputs.size()){
				collideWith(finalCollision);
				return;
			}
			currentByte=inputs.get(inputIndex);
			inputIndex++;
		}
		else {
			recordInput();
		}
		doInput(currentByte);
		doGravity();
		doDrag();
		move();
		checkCollisions();
		updatePreviousPosition();
		updateSprite();
		admin();
	}

	private void updateFootCollider(){
		feet.x=collider.x+collider.width/2-feet.width/2;
		feet.y=collider.y-1;
	}
	
	private void admin() {
		float mult=dy==0?1:.4f;
		
		
			frameTicker+=Math.abs(dx)*Main.frameSpeed*animSpd*mult;
		
		frameTicker%=run.length;
		jumpKindness-=Main.frameSpeed;
		if(onGround){
			float stepAmount = Math.abs(dx);
			stepAmount=Math.min(stepAmount, 250);
			stepper+=Math.abs(stepAmount)*Main.frameSpeed;
		}
		else {
			airTime+=Main.frameSpeed;
			stepper=0;
		}
	}

	private void updateSprite() {
		if(overridePositioning) return;
		prevX=collider.x; prevY=collider.y;
		setPosition(collider.x, collider.y);
	}

	private void updatePreviousPosition() {
		prevX=collider.x; prevY=collider.y;
	}

	private void doDrag() {
		dx*=Math.pow(drag, Main.frameSpeed);
	}

	public void keyDown(int keyCode){
		if(replay||!active)return;
		if(keyCode==Keys.UP) currentByte|=byteJumpPressed;
	}

	private void recordInput(){
		if(Gdx.input.isKeyPressed(Keys.LEFT)) currentByte|=byteLeft;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) currentByte|=byteRight;
		if(Gdx.input.isKeyPressed(Keys.UP)) currentByte|=upByte;
	}

	private void doInput(byte input) {		
		float lr=0;
		if((input&byteLeft)>0){
			faceSide(-1);
			lr-=1;
		}
		if((input&byteRight)>0){
			faceSide(1);
			lr+=1;
		}
		if((input&byteJumpPressed)>0) jump();
		if((input&upByte)>0){
			if(jumping){
				if(airTime<maxJumpTime){
					dy=jumpStrength;
				}
			}
		}
		else{
			jumping=false;
		}
		dx+=lr*horizontalAccel*Main.frameSpeed;
		if(!replay)inputs.add(currentByte);
		currentByte=0;
	}

	private void faceSide(int side) {
		facingSide=side;
	}

	private void jump() {
		if(jumpKindness<0||!active)return;
		jumpSound.play(multiplier);
		dy = jumpStrength;
		airTime=0;
		jumping=true;
	}

	private void checkCollisions() {
		onGround=false;

		//first tiles//	
		for(Tile t: Main.self.currentMap.tiles){
			if(t.collider.overlaps(collider)){
				t.collide(this);
				/*vertical collisions*/
				if(!t.checkIfInner(0, 1)){ //bot//
					if(prevY>=t.collider.y+t.collider.height){
						collider.y=t.collider.y+t.collider.height;
						touchGround(t);
					}
				}
				if(!t.checkIfInner(0, -1)){ //top//
					if(prevY+collider.height<=t.collider.y){
						collider.y=t.collider.y-collider.height;
						touchCeiling(t);
					}
				}
				//check to see if still colliding before checking horizontal collisions//
				if(t.collider.overlaps(collider)){
					if(!t.checkIfInner(1, 0)){
						if(prevX>=t.collider.x+t.collider.width){
							collider.x=t.collider.x+t.collider.width;
							touchWall(t);

						}
					}
					if(!t.checkIfInner(-1, 0)){
						if(prevX+collider.width<=t.collider.x){
							collider.x=t.collider.x-collider.width;
							touchWall(t);
						}
					}
				}
			}
		}	
		//then entities//
		if(replay) return;
		for(Entity e:Main.self.currentMap.entities){
			if(e.collider.overlaps(collider)) collideWith(e);
		}
	}
	
	public void collideWith(Entity e){
		CollisionResult cr =e.collideWithPlayer(this);
		if(cr==null)return;
		switch(cr){
		case Death:
			finalCollision=e;
			die();
			break;
		case Glory:
			finalCollision=e;
			win();
			break;
		default:
			break;
		}
	}

	private void touchWall(Tile t) {
		dx=0;
	}

	private void touchCeiling(Tile t) {
		dy=0;
		jumping=false;
	}

	private void touchGround(Tile underneath) {
		dy=0;
		onGround=true;
		jumping=false;
		jumpKindness=groundTimerNiceness;
		updateFootCollider();
		Tile t = getTileUnderFeet();
		if(t==null){
			t=underneath;
		}
		if(airTime>0){
			t.land(this);
		}
		airTime=0;
		if(stepper>stepsPerSound){
			t.step(this);
			stepper=0;
		}
	}

	private Tile getTileUnderFeet() {
		for(Tile t:Main.self.currentMap.tiles){
			if(t.collider.overlaps(feet))return t;
		}
		return null;
	}

	private void move() {
		if(Math.abs(dx)<=10)dx=0;
		changePosition(dx*Main.frameSpeed, dy*Main.frameSpeed);
	}

	private void changePosition(float x, float y) {
		collider.x+=x;
		collider.y+=y;
	}

	private void doGravity(){
		dy-=gravity*Main.frameSpeed;
	}
	static int jumpFrameThreshold=400;
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		if(age>0) Colours.setBatchColour(batch, playerReplayCol, multiplier*getColor().a);
		
		TextureRegion toDraw=run[(int) frameTicker];
		//if(dx==0) toDraw=idle;
		
		if(dy!=0){
			
			if(dy>jumpFrameThreshold) toDraw=run[5];
			else if(dy<-jumpFrameThreshold) toDraw=run[7];
			else toDraw = run[6];
			
		}
		
		
		toDraw.flip(toDraw.isFlipX()==(facingSide==1), false);
		Draw.draw(batch, toDraw, getX()-extraWidth, getY()-extraHeight);
		
		//draw collider//
//		batch.setColor(1,1,1,.5f);
//		Draw.fillRectangle(batch, feet.x, feet.y, feet.width, feet.height);
	}


	public void die() {
		int missedInputs = inputs.size()-inputIndex;
		if(replay && missedInputs>0)System.out.println(hashCode()+": "+missedInputs);
		dead.play(multiplier);
		endLife();
	}

	public void win() {
		victory=true;
		Main.self.currentMap.levelComplete();
		endLife();
	}

	public void endLife(){
		if(!replay) {
			Main.self.currentMap.lightsOn();
		}
		replay=true;
		active=false;
		addAction(Actions.delay(age>0?0:deathDelay, Actions.run(new Runnable() {
			@Override
			public void run() {
				moveBack();
			}
		} )));
	}	

	/*
	 * int startX,startY;
	Rectangle collider = new Rectangle(0, 0, rectWidth, rectHeight);
	float stepper=0; 
	float prevX, prevY;
	float dx, dy;
	float jumpKindness; 
	float airTime=0;
	boolean jumping; //used to check if player has hit ceiling or floor since last jump//
	boolean onGround;
	boolean overridePositioning; //should override positioning so can be pinged back to start//

	public boolean replay; //if the player has died or won and is now a replayer//
	public boolean replaying; //if currently replaying self//
	boolean active; //if should be moving/accepting input/
	public boolean victory; //if this player reached the goal//
	int age; //how many plays ago//
	public float multiplier=1; //determines alpha and volume for sound effects//
	 */
	


	public void activate() {
		active=true;
	}

	public void age(){
		age++;
		multiplier*=.8f;
		multiplier=Math.max(.3f, multiplier);
	}

	public void moveBack() {
		overridePositioning=true;
		SequenceAction sa = new SequenceAction();
		if(age>0){
			sa.addAction(Actions.fadeOut(.3f));
		}
		else{
			sa.addAction(Actions.moveTo(startX, startY, .3f, Interpolation.pow2Out));	
		}
		sa.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				reset();
				Main.self.currentMap.finishedMovingBack();
			}
		}));
		addAction(sa);
	}

	public void startReplay(){
		reset();
		getColor().a=1;
		replaying=true;
		replay=true;		active=true;
	}

	@Override
	public CollisionResult collideWithPlayer(Player p) {
		return null;
	}

	@Override
	public void reset() {
		fn=0;
		stepper=0;
		dx=0;dy=0;
		jumpKindness=0;
		airTime=0;
		jumping=false;
		onGround=false;
		overridePositioning=false;
		replaying=false;
		facingSide=1;
		
		currentFrame=0;
		frameTicker=0;
		
		collider.x=startX;
		collider.y=startY;
		
		updateSprite();
		updatePreviousPosition();
		inputIndex=0;
		clearActions();	
	}

	@Override
	public void begin() {
	}
}
