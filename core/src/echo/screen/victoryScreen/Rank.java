package echo.screen.victoryScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Font;

public class Rank extends Actor{
	
	String title;
	public enum RankName{D, C, B, A, S, M, GM};
	public Rank(RankName r) {
		this.title=r.toString();
		addAction(Actions.fadeOut(.3f));
		GlyphLayout gl =Font.largeLayout;
		gl.setText(Font.hugeFont, title);
		setSize(gl.width+50, 150);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		
		batch.setColor(Colours.darkBlueGreen);
		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
		batch.setColor(Colours.light);
		Draw.drawRectangle(batch, getX(), getY(), getWidth(), getHeight(), 10);
		
		GlyphLayout gl =Font.largeLayout;
		gl.setText(Font.hugeFont, title);
		
		Font.hugeFont.setColor(Colours.light);
		Font.hugeFont.draw(batch, title, getX()+(getWidth()-gl.width)/2, getY()+getHeight()/2+gl.height/2);
		
		batch.setColor(getColor());
		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
		
		batch.setColor(1,1,1,1);
		super.draw(batch, parentAlpha);
	}

}
