package pongy;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class Ball {

	GeneralPath ball;
	PlayField pField;

	int ballRadius;
	double xCoord, yCoord;

	int angle;
	double velocity;

	public Ball() {
		ball = new GeneralPath();
	}

	public int getX() {
		return ( int )xCoord;
	}

	public int getY() {
		return ( int )yCoord;
	}

	public int getRadius() {
		return ballRadius;
	}

	public int getAngle() {
		return angle;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setX( int x ) {
		xCoord = ( double )x;
	}

	public void setY( int y ) {
		yCoord = ( double )y;
	}

	public void setRadius( int r ) {
		ballRadius = r;
	}

	public void setField( PlayField f ) {
		pField = f;
	}

	public void setAngle( int d ) {
		angle = d;
	}

	public void setVelocity( double v ) {
		velocity = v;
	}

	private void calculateCoords() {

		if ( (xCoord-( double )ballRadius) < pField.getLeftBound() ) {
			xCoord = pField.getLeftBound()+( double )ballRadius;
			angle = (540-angle)%360;
		}

		if ( (xCoord+( double )ballRadius) > pField.getRightBound() ) {
			xCoord = pField.getRightBound()-( double )ballRadius;
			angle = (540-angle)%360;
		}

		xCoord += velocity*Math.cos(angle*Math.PI/180);
		yCoord -= velocity*Math.sin(angle*Math.PI/180);
	}

	public Shape getBall() {

		calculateCoords();

		ball.reset();
		ball.moveTo( xCoord+ballRadius*Math.cos( 0 ), yCoord+ballRadius*Math.sin( 0 ) );
		for ( int i = 1; i <= 12; i++ ) {
			ball.lineTo( xCoord+Math.round( ballRadius*Math.cos( Math.PI*i/6 ) ), yCoord+Math.round( ballRadius*Math.sin( Math.PI*i/6 ) ) );
		}
		ball.closePath();

		return ( Shape )ball;
	}
}
