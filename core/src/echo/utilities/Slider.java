package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;


public class Slider extends Actor{
	static float w=250,h=30, gap=2;;
	public static Slider SFX=  new Slider("SFX", .5f, Colours.brown, Colours.lightGrey);
	
	
	private float value;
	private Color backGround, foreGround;
	private boolean dragging;
	private String title;
	public Slider(String title, float base, Color bg, Color fg) {
		this.title=title;
		value=base;
		backGround=bg; foreGround=fg;
		setSize(w, h);
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				dragging=true;
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				dragging=false;
			}
		});
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if(dragging){
			value=((Gdx.input.getX())-getX()-getParent().getX()-gap)/(getWidth()-gap*2);
			value=Math.max(0, Math.min(1, value));
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.setColor(backGround);
		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
		batch.setColor(foreGround);
		Draw.fillRectangle(batch, getX()+gap, getY()+gap, (getWidth()-gap*2)*value, getHeight()-gap*2);
		Font.font.draw(batch, title, getX(), getY()+46, getWidth(), Align.center, false);		
	}
	public float getValue(){
		return value;
	}
}
