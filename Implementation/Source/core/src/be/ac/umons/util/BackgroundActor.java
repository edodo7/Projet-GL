package be.ac.umons.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Actor that draws a background
 */
public class BackgroundActor extends Actor {


	private Texture bg;

	/**
	 * Constructor
	 * @param bg			Texture to be drawn
	 */
	public BackgroundActor(Texture bg) {
		this.bg = bg;
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(bg, getX(), getY(), bg.getWidth(), bg.getHeight());
	}
}
