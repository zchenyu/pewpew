
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

import org.pushingpixels.substance.api.skin.*;
import net.miginfocom.swing.*;
import com.jme3.system.*;

public class PewPew extends JFrame {
    private static final long serialVersionUID = 1L;
    
	static final int WIDTH = 720;
	static final int HEIGHT = 480;
	static final int GRID = 16;
	static final int PNL_SIZE = 160;
	
	static PewApp app;
	
	Container c;
	JmeCanvasContext ctx;
	JPanel pnlRight;
	DrawPanel[] pnlViews;

	JPanel pnlOptions;
	static JTextField txtColor;
	JButton btnClear, btnSphere, btnChair, btnMario, btnLink;
	static JCheckBox chkColor, chkBloom;
	
	public PewPew() {
		super( "PewPew" );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		c = getContentPane();
		c.setLayout( new MigLayout() );
		
		pnlRight = new JPanel( new MigLayout() );
		pnlViews = new DrawPanel[6];
		pnlRight.add( new JLabel( "Top" ) );
		pnlRight.add( new JLabel( "Bottom" ), "wrap" );
		pnlRight.add( pnlViews[0] = new DrawPanel( PNL_SIZE, PNL_SIZE, GRID, GRID ) );
		pnlRight.add( pnlViews[1] = new DrawPanel( PNL_SIZE, PNL_SIZE, GRID, GRID ), "wrap" );
		pnlRight.add( new JLabel( "Left" ) );
		pnlRight.add( new JLabel( "Right" ), "wrap" );
		pnlRight.add( pnlViews[2] = new DrawPanel( PNL_SIZE, PNL_SIZE, GRID, GRID ) );
		pnlRight.add( pnlViews[3] = new DrawPanel( PNL_SIZE, PNL_SIZE, GRID, GRID ), "wrap" );
		pnlRight.add( new JLabel( "Front" ) );
		pnlRight.add( new JLabel( "Back" ), "wrap" );
		pnlRight.add( pnlViews[4] = new DrawPanel( PNL_SIZE, PNL_SIZE, GRID, GRID ) );
		pnlRight.add( pnlViews[5] = new DrawPanel( PNL_SIZE, PNL_SIZE, GRID, GRID ), "wrap" );
		pnlRight.add( pnlOptions = new JPanel( new MigLayout() ), "span 2" );
		pnlOptions.add( new JLabel( "Color" ) );
		pnlOptions.add( txtColor = new JTextField( "AAAAAA", 10 ), "wrap" );
		pnlOptions.add( new JLabel( "Demos" ) );
		pnlOptions.add( btnClear = new JButton( "Clear" ), "split 5" );
		pnlOptions.add( btnSphere = new JButton( "Sphere" ) );
		pnlOptions.add( btnChair = new JButton( "Chair" ) );
		pnlOptions.add( btnMario = new JButton( "Mario" ) );
		pnlOptions.add( btnLink = new JButton( "Link" ), "wrap" );
		pnlOptions.add( new JLabel( "Options" ) );
		pnlOptions.add( chkColor = new JCheckBox( "Color" ), "split 2" );
		pnlOptions.add( chkBloom = new JCheckBox( "Bloom Filter" ) );
		
		AppSettings settings = new AppSettings( true );
		settings.setWidth( WIDTH );
		settings.setHeight( HEIGHT );
		app = new PewApp( pnlViews[0].grid, pnlViews[1].grid, pnlViews[2].grid, pnlViews[3].grid, pnlViews[4].grid, pnlViews[5].grid );
		app.setSettings( settings );
		app.createCanvas();
		ctx = (JmeCanvasContext)app.getContext();
		ctx.setSystemListener( app );
		ctx.getCanvas().setPreferredSize( new Dimension( WIDTH, HEIGHT ) );
		
		c.add( ctx.getCanvas(), "dock west" );
		c.add( pnlRight, "dock east" );
		
		pack();
		setVisible( true );
		
		btnClear.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				setGrid( "clear.png" );
			}
		});
		
		btnSphere.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				setGrid( "sphere.png" );
			}
		});
		
		btnChair.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				setGrid( "chair.png" );
			}
		});
		
		btnMario.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				setGrid( "mario.png" );
			}
		});
		
		btnLink.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				setGrid( "link.png" );
			}
		});
		
		chkColor.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent arg0 ) {
				app.changed = true;
				app.gainFocus();
			}
		});
		
		chkBloom.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent arg0 ) {
				app.changed = true;
				app.gainFocus();
			}
		});
	}
	
	private void setGrid( String s ) {
		try {
	        BufferedImage img = ImageIO.read( new File( s ) );
	        
	        for( int i = 0; i < 16; i++ ) {
	        	for( int j = 0; j < 16; j++ ) {
	        		pnlViews[0].grid[i][j] = img.getRGB( i, j ) & 0x00FFFFFF;
	        		pnlViews[1].grid[i][j] = img.getRGB( 16+i, j ) & 0x00FFFFFF;
	        		pnlViews[2].grid[i][j] = img.getRGB( i, 16+j ) & 0x00FFFFFF;
	        		pnlViews[3].grid[i][j] = img.getRGB( 16+i, 16+j ) & 0x00FFFFFF;
	        		pnlViews[4].grid[i][j] = img.getRGB( i, 32+j ) & 0x00FFFFFF;
	        		pnlViews[5].grid[i][j] = img.getRGB( 16+i, 32+j ) & 0x00FFFFFF;
	        	}
	        }
	        
	        for( int i = 0; i < pnlViews.length; i++ ) {
	        	pnlViews[i].repaint();
	        }
	        
	        app.changed = true;
	        app.gainFocus();
        } catch( IOException e ) {
	        e.printStackTrace();
        }
	}
	
	public static void main( String[] args ) {
		EventQueue.invokeLater( new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel( new SubstanceGraphiteGlassLookAndFeel() );
				} catch( UnsupportedLookAndFeelException e ) {
					e.printStackTrace();
				}

				new PewPew();
			}
		});
	}
}
