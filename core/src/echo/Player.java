package echo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.utilities.Draw;

public class Player extends Actor{
	static int rectWidth=5, rectHeight=10;
	static float gravity =900;
	static float jumpStrength=140f, maxJumpTime=.18f;
	static float horizontalAccel=500;
	static float drag = .005f;

	Rectangle playerRect = new Rectangle(0, 50, rectWidth, rectHeight);
	float prevX=playerRect.x, prevY=playerRect.y;
	float dy, dx;
	boolean jumping, onGround;
	float jumpTime=0;
	public Player() {

	}



	public void act(float delta){
		super.act(delta);
		checkInput();
		doGravity();
		doDrag();
		updatePosition();
		onGround=false;
		checkCollisions();
		updatePreviousPosition();
	}

	private void updatePreviousPosition() {
		prevX=playerRect.x; prevY=playerRect.y;
	}



	private void doDrag() {
		dx*=Math.pow(drag, Main.frameSpeed);
	}



	public void pushKey(int key){
		if(key==Keys.UP){
			jump();
		}
	}
	
	private void checkInput() {


		
		if(Gdx.input.isKeyPressed(Keys.UP)){
			if(jumping){
				dy=-jumpStrength;
				jumpTime+=Main.frameSpeed;
				if(jumpTime>maxJumpTime){
					jumping=false;
				}
			}
		}
		else{
			jumping=false;
		}
		float lr=0;

		if(Gdx.input.isKeyPressed(Keys.LEFT)) lr-=1;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) lr+=1;
		dx+=lr*horizontalAccel*Main.frameSpeed;
	}

	private void jump() {
		if(!onGround)return;
		dy = -jumpStrength;
		jumpTime=0;
		jumping=true;
	}



	private void checkCollisions() {
		for(Rectangle collisionRect:Main.currentMap.colliders){

			if(collisionRect.overlaps(playerRect)){	
				if(prevY>=collisionRect.y+collisionRect.height){
					playerRect.y=collisionRect.y+collisionRect.height;
					dy=0;
				}
				if(prevY+playerRect.height<=collisionRect.y){
					playerRect.y=collisionRect.y-playerRect.height;
					onGround=true;
					dy=0;
				}
				//check to see if still colliding before checking horizontal collisions//
				if(collisionRect.overlaps(playerRect)){
					if(prevX>=collisionRect.y+collisionRect.width){
						playerRect.x=collisionRect.x+collisionRect.width;
						dx=0;
					}
					if(prevX+playerRect.width<=collisionRect.x){
						playerRect.x=collisionRect.x-playerRect.width;
						dx=0;
					}

				}

				//				float goUp=collisionRect.y-playerRect.y-playerRect.height;
				//				float goDown=collisionRect.y+collisionRect.height-playerRect.y;
				//				float yDiffToClosest =(Math.abs(goUp)<Math.abs(goDown))?goUp:goDown;
				//				
				//				float goLeft = collisionRect.x-playerRect.x-playerRect.width;
				//				float goRight=(collisionRect.x-playerRect.x+collisionRect.width);
				//				float xDiffToClosest =(Math.abs(goLeft)<Math.abs(goRight))?goLeft:goRight;
				//				
				//				if(Math.abs(xDiffToClosest)<.1f&&Math.abs(yDiffToClosest)<.1f){
				//					return;
				//				}
				//				
				//				if(Math.abs(xDiffToClosest)<Math.abs(yDiffToClosest)){
				//					dx=0;
				//					playerRect.x+=xDiffToClosest;
				//				}
				//				else{
				//					dy=0;
				//					if(goUp==yDiffToClosest) onGround=true;
				//					playerRect.y+=yDiffToClosest;
				//				}
			}
		}
	}



	private void updatePosition() {
		changePosition(dx*Main.frameSpeed, dy*Main.frameSpeed);
	}



	private void changePosition(float x, float y) {
		playerRect.x+=x;
		playerRect.y+=y;
	}



	private void doGravity(){
		dy+=gravity*Main.frameSpeed;
	}

	public void draw(Batch batch, float parentAlpha){
		Draw.fillRectangle(batch, playerRect.x, playerRect.y, playerRect.width, playerRect.height);
	}

}
