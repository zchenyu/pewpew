
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class DrawPanel extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;
    
	int width, height, cellx, celly;
	int[][] grid;
	
	public DrawPanel( int width, int height, int cellx, int celly ) {
		this.width = width;
		this.height = height;
		this.cellx = cellx;
		this.celly = celly;
		this.grid = new int[cellx][celly];
		
		for( int i = 0; i < cellx; i++ ) {
			for( int j = 0; j < celly; j++ ) {
				grid[i][j] = 0xFFFFFF;
			}
		}
		
		setPreferredSize( new Dimension( width+1, height+1 ) );
		addMouseListener( this );
	}
	
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		Graphics2D g2 = (Graphics2D)g;
		
		for( int i = 0; i < cellx; i++ ) {
			for( int j = 0; j < celly; j++ ) {
				g2.setColor( new Color( grid[i][j], false ) );
				g2.fillRect( width*i/cellx, height*j/celly, width/cellx, height/celly );
			}
		}
		
		g2.setColor( Color.GRAY );
		
		for( int i = 0; i <= cellx; i++ ) {
			g2.drawLine( i*width/cellx, 0, i*width/cellx, height );
		}
		
		for( int i = 0; i <= celly; i++ ) {
			g2.drawLine( 0, i*height/celly, width, i*height/celly );
		}
	}

    public void mouseClicked( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }
	
	public void mouseExited( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
	    int x = cellx*e.getX()/width;
	    int y = celly*e.getY()/height;
	    grid[x][y] = Integer.parseInt( PewPew.txtColor.getText(), 16 );
	    PewPew.app.changed = true;
	    repaint();
    }
}
