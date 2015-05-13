package pongy;

import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Pongy extends JComponent implements MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6570991309443685041L;
	
	JFrame		frame;
	Thread		gameThread;

	Paddle		p1Paddle, p2Paddle;
	Ball		ball;
	PlayField	pField;

	Line2D.Double	pFieldTop;

	int			wins, losses, p2Level;
	int			hitCounter, yLineBound;
	boolean		playGame;

	public Pongy() {
		
		frame = new JFrame( "Pongy" );

		JMenu gameMenu = new JMenu( "Game" );
		gameMenu.setMnemonic( KeyEvent.VK_G );

		JMenuItem newGameMenuItem = new JMenuItem( "New Game" );
		newGameMenuItem.setMnemonic( KeyEvent.VK_N );
		newGameMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, Event.CTRL_MASK ) );
		newGameMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				initGame();
			}
		} );
		gameMenu.add( newGameMenuItem );
		
		JMenuItem exitMenuItem = new JMenuItem( "Exit" );
		exitMenuItem.setMnemonic( KeyEvent.VK_X );
		exitMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, Event.CTRL_MASK ) );
		exitMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		} );
		gameMenu.add( exitMenuItem );

		addMouseMotionListener( this );

		JMenuBar menuBar = new JMenuBar();

		menuBar.add( gameMenu );

		frame.setJMenuBar( menuBar );

		frame.getContentPane().add( this );
		frame.setSize( 400, 600 );
		frame.setResizable( false );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );

		playGame = false;
		yLineBound = 20;
		wins = 0;
		losses = 0;
	}

	protected void initGame() {

		int p1Width = 60;
		int p2Width = 60;

		int p1Height = 10;
		int p2Height = 10;

		int p1Distance = 30;
		int p2Distance = 30;

		int bRadius = 6;

		wins = 0;
		losses = 0;
		p2Level = 6;

		p1Paddle = new Paddle();
		p2Paddle = new Paddle();
		ball = new Ball();
		pField = new PlayField();

		pField.setOriginX( 0 );
		pField.setOriginY( yLineBound );
		pField.setWidth( getSize().width );
		pField.setHeight( getSize().height-yLineBound );

		p1Paddle.setX( (pField.getWidth()/2)+(pField.getOriginX())-(p1Width/2) );
		p1Paddle.setY( pField.getBottomBound()-p1Height-p1Distance );
		p1Paddle.setWidth( p1Width );
		p1Paddle.setHeight( p1Height );
		p1Paddle.setField( pField );

		p2Paddle.setX( (pField.getWidth()/2)+(pField.getOriginX())-(p2Width/2) );
		p2Paddle.setY( pField.getTopBound()+p2Distance );
		p2Paddle.setWidth( p2Width );
		p2Paddle.setHeight( p2Height );
		p2Paddle.setField( pField );

		ball.setX( (pField.getWidth()/2)+(pField.getOriginX()) );
		ball.setY( (pField.getHeight()/2)+(pField.getOriginY()) );
		ball.setRadius( bRadius );
		ball.setField( pField );

		startGame();
	}

	protected void startGame() {

		startPaddles();
		startBall();
		
		playGame = true;

		gameThread = new Thread( 
			new Runnable() {
				public void run() {
					runGame();
				}
			}
		);
		gameThread.start();
	}

	protected void startPaddles() {

		int pFieldXCoord;

		pFieldXCoord = (pField.getWidth()/2)+pField.getOriginX();
		p2Paddle.setX( pFieldXCoord-(p2Paddle.getWidth()/2) );
	}

	protected void startBall() {

		int pFieldXCoord;
		int pFieldYCoord;

		double bVelocity = 1.5;

		pFieldXCoord = (pField.getWidth()/2)+pField.getOriginX();
		pFieldYCoord = (pField.getHeight()/2)+pField.getOriginY();

		ball.setX( pFieldXCoord );
		ball.setY( pFieldYCoord );
		ball.setVelocity( bVelocity );

		if ( ((wins+losses) % 2) == 1 ) {
			ball.setAngle( 90 );
		} else {
			ball.setAngle( 270 );
		}
	}

	protected void moveP1Paddle( int x ) {
		if ( p1Paddle != null ) {
			p1Paddle.setX( x-(p1Paddle.getWidth()/2) );
		}
	}

	protected void endGame( boolean won ) {

		playGame = false;

		if ( won ) {
			wins++;
		} else {
			losses++;
		}

		repaint();

		if ( wins == 7 || losses == 7 ) {
			gameOver( wins, losses );
		} else {
			JOptionPane.showMessageDialog( frame, "Press OK to start." );
			startGame();
		}
	}

	protected void gameOver( int wins, int losses ) {

		String gameMessage;

		if ( wins > losses ) {
			gameMessage = "You win!\nScore: ";
		} else {
			gameMessage = "You lose.\nScore: ";
		}

		gameMessage += Integer.toString( wins ) +" - ";
		gameMessage += Integer.toString( losses );

		JOptionPane.showMessageDialog( frame, gameMessage );
	}

	protected void runGame() {

		Shape ballShape;
		int ballX, ballRadius;

		int p1PaddleX, p1PaddleY, p1PaddleWidth;
		int p2PaddleX, p2PaddleY, p2PaddleWidth, p2PaddleHeight;

		ballRadius = ball.getRadius();

		p1PaddleWidth = p1Paddle.getWidth();

		p2PaddleWidth = p2Paddle.getWidth();
		p2PaddleHeight = p2Paddle.getHeight();

		// I declared the hitCounter variable outside of the function
		// because its values kept messing up when declared inside.
		hitCounter = 0;

		try {
			while ( playGame ) {

				ballShape = ball.getBall();
				ballX = ball.getX();

				p1PaddleX = p1Paddle.getX();
				p1PaddleY = p1Paddle.getY();

				p2PaddleX = p2Paddle.getX();
				p2PaddleY = p2Paddle.getY();

				if ( ballX < (p2PaddleX+(p2PaddleWidth/2)) && (p2PaddleX+(p2PaddleWidth/2))-ballX > p2Level ) {
					p2Paddle.setX( p2PaddleX-p2Level );
				} else if ( ballX > (p2PaddleX+(p2PaddleWidth/2)) && ballX-(p2PaddleX+(p2PaddleWidth/2)) > p2Level) {
					p2Paddle.setX( p2PaddleX+p2Level );
				} else {
					p2Paddle.setX( ballX-(p2PaddleWidth/2) );
				}

				if ( ballShape.intersects( p1PaddleX, p1PaddleY, p1PaddleWidth, 1 ) ) {

					ball.setAngle( ( int )Math.round( (150-(( double )(ballX-p1PaddleX)/p1PaddleWidth)*120) ) );

					hitCounter++;
					ball.setY( p1PaddleY-ballRadius );
				}

				if ( ballShape.intersects( p2PaddleX, p2PaddleY+p2PaddleHeight, p2PaddleWidth, 1 ) ) {

					ball.setAngle( ( int )Math.round( (210+(( double )(ballX-p2PaddleX)/p2PaddleWidth)*60+(Math.random()*60)) ) );

					hitCounter++;
					ball.setY( p2PaddleY+p2PaddleHeight+ballRadius );
				}

				if ( hitCounter >= 5 ) {
					ball.setVelocity( ball.getVelocity()+0.5 );
					hitCounter = 0;
				}

				if ( ball.getY()-ballRadius < pField.getTopBound() ) {
					endGame( true );
				} else if ( ball.getY()+ballRadius > pField.getBottomBound() ) {
					endGame( false );
				}
				
				repaint();
				Thread.sleep( 25 );
			}
			
		} catch ( InterruptedException ie ) {
		}
	}

	public void paint( Graphics g ) {

		Graphics2D g2 = ( Graphics2D )g;
		String scoreString;
		Shape p1PaddleShape, p2PaddleShape, ballShape;

		scoreString = "Score: ";
		scoreString += Integer.toString( wins );
		scoreString += " - ";
		scoreString += Integer.toString( losses );

		if ( pFieldTop == null ) {
			pFieldTop = new Line2D.Double( 0, yLineBound, getSize().width, yLineBound );
		}

		if ( playGame ) {

			p1PaddleShape = p1Paddle.getPaddle();
			p2PaddleShape = p2Paddle.getPaddle();
			ballShape = ball.getBall();

			g2.setPaint( Color.blue );

			g2.draw( p1PaddleShape );
			g2.fill( p1PaddleShape );

			g2.draw( p2PaddleShape );
			g2.fill( p2PaddleShape );

			g2.draw( ballShape );
			g2.fill( ballShape );
		}

		g2.setPaint( Color.black );
		g2.draw( pFieldTop );
		g2.drawString( scoreString, getSize().width-90, yLineBound-5 );
	}

	public static void main( String args[] ) {
		new Pongy();
	}

	public void mouseMoved( MouseEvent e ) {
		moveP1Paddle( e.getX() );
	}

	public void mouseDragged( MouseEvent e ) {
		moveP1Paddle( e.getX() );
	}
}