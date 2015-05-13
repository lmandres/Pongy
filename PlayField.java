package pongy;

public class PlayField {

	int pFieldWidth, pFieldHeight, originX, originY;

	public PlayField() {
	}

	public void setWidth( int w ) {
		pFieldWidth = w;
	}

	public void setHeight( int h ) {
		pFieldHeight = h;
	}

	public void setOriginX( int x ) {
		originX = x;
	}

	public void setOriginY( int y ) {
		originY = y;
	}

	public int getWidth() {
		return pFieldWidth;
	}

	public int getHeight() {
		return pFieldHeight;
	}

	public int getOriginX() {
		return originX;
	}

	public int getOriginY() {
		return originY;
	}

	public int getTopBound() {
		return originY;
	}

	public int getBottomBound() {
		return originY+pFieldHeight;
	}

	public int getLeftBound() {
		return originX;
	}

	public int getRightBound() {
		return originX+pFieldWidth;
	}
}
