package echo.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;

import echo.Main;

public class TextRegion extends Group{
	final static int gap=15;
	final static Color textCol = Colours.make(204, 240, 255);
	String text;
	float wrapWidth;
	GlyphLayout layout = new GlyphLayout();
	Runnable clickAction;
	public TextRegion(String text, float x, float y, float width) {
		setWidth(width);
		setPosition(x, y);
		wrapWidth=width-gap*2;
		setText(text);
	}
	
	public void setClickAction(final Runnable r){
		clickAction=r;
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				r.run();
				return true;
			}
		});
	}
	
	public void setText(String text){
		this.text=text;
		layout.setText(Font.font, text, Color.BLACK, wrapWidth, Align.bottomLeft, true);
		setHeight(layout.height+gap*2);
	}
	
	public void clipToTopLeft(){
		setPosition(10, Gdx.graphics.getHeight()-getHeight()-10);
	}
	
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		batch.setColor(getColor());
		ButtonBorder.drawBorder(batch, getX(), getY(), getWidth(), getHeight());
		Font.font.setColor(textCol.r, textCol.g, textCol.b, getColor().a);
		Font.font.draw(batch, text, getX()+gap, getY()+getHeight()-gap, wrapWidth, Align.center, true);
	}	
}
