package be.ac.umons.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Class to be used as background for a table
 */
public class BackgroundDrawable implements Drawable {

	private Texture text;

	/**
	 * Constructor
	 * @param t 				Texture to be drawn
	 */
	public BackgroundDrawable(Texture t) {
		text = t;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		batch.draw(text, x, y, width, height);
	}

	@Override
	public float getLeftWidth() {
		return 0;
	}

	@Override
	public void setLeftWidth(float leftWidth) {

	}

	@Override
	public float getRightWidth() {
		return 0;
	}

	@Override
	public void setRightWidth(float rightWidth) {

	}

	@Override
	public float getTopHeight() {
		return 0;
	}

	@Override
	public void setTopHeight(float topHeight) {

	}

	@Override
	public float getBottomHeight() {
		return 0;
	}

	@Override
	public void setBottomHeight(float bottomHeight) {

	}

	@Override
	public float getMinWidth() {
		return 0;
	}

	@Override
	public void setMinWidth(float minWidth) {

	}

	@Override
	public float getMinHeight() {
		return 0;
	}

	@Override
	public void setMinHeight(float minHeight) {

	}
}
