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
import com.badlogic.gdx.utils.Align;

import echo.Main;
import echo.map.Tile;
import echo.screen.gameScreen.GameScreen;
import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Slider;
import echo.utilities.Sounds;

public class Player extends Entity{
	/*bytes*/
	static final byte byteLeft =	 	1;
	static final byte byteRight =	 	1<<1;
	static final byte byteLR =	 		byteLeft|byteRight;
	static final byte byteUp =		 	1<<2;
	static final byte byteJumpPressed= 	1<<3;
	static final byte byteR= 	1<<4;
	/*sounds*/
	static Sound deathSound = Sounds.am.get("sfx/dead.wav", Sound.class);
	static Sound winSound = Sounds.am.get("sfx/win.wav", Sound.class);
	static Sound[] jumpSounds = new Sound[3];
	static Sound[] landSounds = new Sound[2];
	static{
		 for(int i=0;i<3;i++) jumpSounds[i]= Sounds.am.get("sfx/jump"+i+".wav", Sound.class);
		 for(int i=0;i<2;i++) landSounds[i]= Sounds.am.get("sfx/land"+i+".wav", Sound.class);
	}
	
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
	static TextureRegion[] death;
	static{
		TextureRegion sheetDeath= Main.atlas.findRegion("player/death");
		death = sheetDeath.split(sheetDeath.getRegionWidth()/4, sheetDeath.getRegionHeight())[0];
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
	public Rectangle collider = new Rectangle(0, 0, rectWidth, rectHeight);

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
	public boolean active; //if should be moving/accepting input/
	public boolean victory; //if this player reached the goal//
	int age; //how many plays ago//
	public float multiplier=1; //determines alpha and volume for sound effects//
	int id=0;
	int fn;
	CollisionHandler finalCollision;
	private static double deathAnimationSpeed=8;
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
		/* if replaying, take input from list, otherwise record input */
		if(replay){
			if(inputIndex==inputs.size()){
				handleCollision(finalCollision);
				return;
			}
			currentByte=inputs.get(inputIndex);
			inputIndex++;
		}
		else {
			recordInput();
		}
		doInput(currentByte, delta);
		doGravity(delta);
		doDrag(delta);
		move(delta);
		checkCollisions();
		updatePreviousPosition();
		updateSprite();
		admin(delta);
	}

	private void updateFootCollider(){
		feet.x=collider.x+collider.width/2-feet.width/2;
		feet.y=collider.y-1;
	}

	private void admin(float delta) {
		float mult=dy==0?1:.4f;
		frameTicker+=Math.abs(dx)*delta*animSpd*mult;
		frameTicker%=run.length;
		jumpKindness-=delta;
		if(onGround){
			float stepAmount = Math.abs(dx);
			stepAmount=Math.min(stepAmount, 250);
			stepper+=Math.abs(stepAmount)*delta;
		}
		else {
			airTime+=delta;
			stepper=0;
		}
		currentByte=0;
	}

	private void updateSprite() {
		if(overridePositioning) return;
		prevX=collider.x; prevY=collider.y;
		setPosition(collider.x, collider.y);
	}

	private void updatePreviousPosition() {
		prevX=collider.x; prevY=collider.y;
	}

	private void doDrag(float delta) {
		dx*=Math.pow(drag, delta);
	}

	public void keyDown(int keyCode){
		if(replay||!active)return;
		if(keyCode==Keys.UP) currentByte|=byteJumpPressed;
	}

	private void recordInput(){
		if(Gdx.input.isKeyPressed(Keys.LEFT)) currentByte|=byteLeft;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) currentByte|=byteRight;
		if(Gdx.input.isKeyPressed(Keys.UP)) currentByte|=byteUp;
		if(Gdx.input.isKeyPressed(Keys.R)) currentByte|=byteR;
	}

	private void doInput(byte input, float delta) {		

		if((input&byteR)>0){
			die();
			return;
		}

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
		if((input&byteUp)>0){
			if(jumping){
				if(airTime<maxJumpTime){
					dy=jumpStrength;
				}
			}
		}
		else{
			jumping=false;
		}
		dx+=lr*horizontalAccel*delta;
		if(!replay)inputs.add(currentByte);
	}

	private void faceSide(int side) {
		facingSide=side;
	}

	private void jump() {
		if(jumpKindness<0||!active)return;
		jumpSounds[(int) (Math.random()*jumpSounds.length)].play(getSoundMultiplier());
		dy = jumpStrength;
		airTime=0;
		jumping=true;
	}

	public float getSoundMultiplier() {
		return multiplier*multiplier*Slider.SFX.getValue();
	}

	private void checkCollisions() {
		onGround=false;

		//first tiles//	
		for(Tile t: GameScreen.get().currentMap.tiles){
			if(t.checkCollision(this)){
				handleCollision(t);
				if(!active)return;
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
		for(Entity e:GameScreen.get().currentMap.entities){
			if(e.collider.overlaps(collider)) handleCollision(e);
		}
	}

	public void handleCollision(CollisionHandler e){
		if(e==null){
			die();
			return;
		}
		CollisionResult cr =e.handCollision(this);
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
//		if(Math.abs(dx)>100)wallSound[(int) (Math.random()*wallSound.length)].play(getSoundMultiplier());
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
			if(active) landSounds[(int) (Math.random()*landSounds.length)].play(getSoundMultiplier());
		}
		airTime=0;
		if(stepper>stepsPerSound){
			t.step(this);
			stepper=0;
		}
	}

	private Tile getTileUnderFeet() {
		for(Tile t:GameScreen.get().currentMap.tiles){
			if(t.collider.overlaps(feet))return t;
		}
		return null;
	}

	private void move(float delta) {
		if(Math.abs(dx)<=25){
			if((currentByte&byteLR)==0){
				dx=0;	
			}
		}
		changePosition(dx*delta, dy*delta);
	}

	private void changePosition(float x, float y) {
		collider.x+=x;
		collider.y+=y;
	}

	private void doGravity(float delta){
		dy-=gravity*delta;
	}
	static int jumpFrameThreshold=400;
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(1,1,1,1);
		if(replay)batch.setColor(playerReplayCol);
		if(age>0) Colours.setBatchColour(batch, playerReplayCol, multiplier*getColor().a);
		TextureRegion toDraw;
		if(dx==0&&dy==0) toDraw=idle;
		else if(dy!=0){
			if(dy>jumpFrameThreshold) toDraw=run[5];
			else if(dy<-jumpFrameThreshold) toDraw=run[7];
			else toDraw = run[6];	
		}
		else toDraw=run[(int) frameTicker];
		
		if(deathStart>=0){
			int deathFrame= (int) ((Main.ticks-deathStart)*deathAnimationSpeed);
			deathFrame=Math.min(3, deathFrame);
			toDraw=death[deathFrame];
		}
		
		toDraw.flip(toDraw.isFlipX()==(facingSide==1), false);
		Draw.drawCenteredRotatedScaled(batch, toDraw, (int)(getX()-extraWidth+run[0].getRegionWidth()/2), (int)(getY()-extraHeight+run[0].getRegionHeight()/2), getScaleX(), getScaleY(), getRotation());
	}

	double deathStart=-1;
	public void die() {
//		deathStart=Main.ticks;
		if(active&&!replay) GameScreen.get().currentMap.levelFailed();
		deathSound.play(getSoundMultiplier());
		endLife();
	}

	public void win() {
		victory=true;
		winSound.play(getSoundMultiplier());
		GameScreen.get().currentMap.levelComplete();
		endLife();
		addAction(Actions.rotateBy(10, 1, Interpolation.pow2Out));
		addAction(Actions.scaleTo(0f, 0f, 1, Interpolation.pow2Out));
		Portal p = GameScreen.get().currentMap.portal;
		addAction(Actions.moveTo(p.getX(), p.getY(), .2f, Interpolation.pow2Out));
//		setPosition(, Align.center);
		
	}

	public void endLife(){
		if(!replay) {
			GameScreen.get().currentMap.lightsOn();
		}
		active=false;
		addAction(Actions.delay(age>0?0:deathDelay, Actions.run(new Runnable() {
			@Override
			public void run() {
				moveBack();
			}
		} )));
	}	

	public void activate() {
		active=true;
	}

	public void age(){
		age++;
		multiplier*=.85f;
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
				GameScreen.get().currentMap.finishedMovingBack();
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
		deathStart=-1;
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
		setScale(1);
		setRotation(0);
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

	@Override
	public void drawLights(Batch batch) {
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
