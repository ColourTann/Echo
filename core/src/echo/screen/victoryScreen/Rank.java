package echo.screen.victoryScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import echo.utilities.Colours;
import echo.utilities.Draw;
import echo.utilities.Font;
import echo.utilities.TimeStuff;

public class Rank extends Actor{
	
	public enum RankName{GM(0, 180), M(180, 210), S(210, 300), A(300, 360),B(360, 480),C(480, 600), D(600, 0);
	int lower, upper;
	String desc="";
	RankName(int lower, int upper){
		this.lower=lower;
		this.upper=upper;
		if(lower==0){
			desc+="<"+TimeStuff.timeString(upper);
			return;
		}
		if(upper==0){
			desc=TimeStuff.timeString(lower)+"+";
			return;
		}
		desc+=TimeStuff.timeString(lower)+" - "+TimeStuff.timeString(upper);
	}
	}
	RankName r;
	public Rank(RankName r) {
		this.r=r;
		addAction(Actions.fadeOut(.3f));
		GlyphLayout gl =Font.largeLayout;
		gl.setText(Font.hugeFont, r.toString());
		setSize(gl.width+50, 150);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		
		batch.setColor(Colours.darkBlueGreen);
		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight());
		batch.setColor(Colours.light);
		Draw.drawRectangle(batch, getX(), getY(), getWidth(), getHeight(), 10);
		
		GlyphLayout gl =Font.largeLayout;
		gl.setText(Font.hugeFont, r.toString());
		
		Font.hugeFont.setColor(Colours.light);
		Font.hugeFont.draw(batch, r.toString(), getX()+(getWidth()-gl.width)/2, getY()+getHeight()/2+gl.height/2);
		Font.font.setColor(Colours.light);
		Font.font.draw(batch, r.desc, getX(), getY()-10, getWidth(), Align.center, false);
		
		batch.setColor(getColor());
		Draw.fillRectangle(batch, getX(), getY(), getWidth(), getHeight()+10);
		
		
		
		batch.setColor(1,1,1,1);
		super.draw(batch, parentAlpha);
	}

}
