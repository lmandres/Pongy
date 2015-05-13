package pongy;

import java.awt.Rectangle;
import java.awt.Shape;

public class Paddle {

	Rectangle paddle;
	PlayField pField;

	int paddleWidth, paddleHeight;
	int xCoord, yCoord;

	public Paddle() {
		paddle = new Rectangle();
	}

	public void setX( int x ) {
		xCoord = x;
	}

	public void setY( int y ) {
		yCoord = y;
	}

	public void setWidth( int w ) {
		paddleWidth = w;
	}

	public void setHeight( int h ) {
		paddleHeight = h;
	}

	public int getX() {
		return xCoord;
	}

	public int getY() {
		return yCoord;
	}

	public int getWidth() {
		return paddleWidth;
	}

	public int getHeight() {
		return paddleHeight;
	}

	public void setField( PlayField f ) {
		pField = f;
	}

	public Shape getPaddle() {

		if ( xCoord < pField.getLeftBound() ) {
			xCoord = pField.getLeftBound();
		} else if ( xCoord >= pField.getRightBound()-paddleWidth ) {
			xCoord = pField.getRightBound()-paddleWidth-1;
		}

		paddle.setBounds( xCoord, yCoord, paddleWidth, paddleHeight );
		return ( Shape )paddle;
	}	
}