package echo;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import echo.utilities.Draw;

public class Map extends Actor{
	public ArrayList<Rectangle> colliders = new ArrayList<Rectangle>();
	
	public Map() {
		setupBorders();
		
		colliders.add(new Rectangle(50,Main.height-20,50,3));
		colliders.add(new Rectangle(70,Main.height-30,50,3));
		colliders.add(new Rectangle(90,Main.height-40,50,3));
		colliders.add(new Rectangle(110,Main.height-50,50,3));
		colliders.add(new Rectangle(130,Main.height-60,50,3));
		
		colliders.add(new Rectangle(85,Main.height-85,10,3));
		
	}
	private void setupBorders() {
		float offset=20, depth=100;

		colliders.add(new Rectangle(-offset, Main.height, Main.width+offset*2, depth)); //bottom//
		colliders.add(new Rectangle(offset, -depth, Main.width+offset*2, depth)); //top//
		
		colliders.add(new Rectangle(-depth, -offset, depth, Main.height+offset*2)); //left//
		colliders.add(new Rectangle(Main.width, -offset, depth, Main.height+offset*2)); //right//
		
	}
	
	public void draw(Batch batch, float parentAlpha){
		batch.setColor(Color.WHITE);
		for(Rectangle r:colliders){
			Draw.fillRectangle(batch, r.x, r.y, r.width, r.height);
		}
	}
}
