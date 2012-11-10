import com.jme3.app.*;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.*;
import com.jme3.material.*;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue.*;
import com.jme3.scene.*;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.*;
import com.jme3.shadow.*;
import com.jme3.effect.*;

public class PewApp extends SimpleApplication {
	boolean changed;
	int[][] top, bottom, left, right, front, back;
	
	Geometry[][][] grid;
	Geometry[][] topbox, bottombox, leftbox, rightbox, frontbox, backbox;
	DirectionalLight dirLight;
	SpotLight spotLight;
	SpotLightShadowRenderer slsr;
	FilterPostProcessor fpp;
	
	boolean animation;
	boolean aTop, aLeft, aFront;
	float aTimer;
	
	public PewApp( int[][] top, int[][] bottom, int[][] left, int[][] right, int[][] front, int[][] back ) {
		changed = true;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.front = front;
		this.back = back;
		this.animation = false;
	}
	
	public void simpleInitApp() {
		flyCam.setDragToRotate( true );
		flyCam.setMoveSpeed( 20f );
		
		grid = new Geometry[PewPew.GRID][PewPew.GRID][PewPew.GRID];
		Material mat = makeMaterial( 0xFFFFFF );
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				for( int k = 0; k < PewPew.GRID; k++ ) {
					Box box = new Box( new Vector3f( i, j, k ), 0.5f, 0.5f, 0.5f );
					grid[i][j][k] = new Geometry( "Box", box );
					grid[i][j][k].setMaterial( mat );
			        grid[i][j][k].setShadowMode( ShadowMode.CastAndReceive );
				}
			}
		}
		
		dirLight = new DirectionalLight();
		dirLight.setColor( ColorRGBA.Gray );
		dirLight.setDirection( new Vector3f( 1f, 1f, 1f ) );
		
        spotLight = new SpotLight();
        spotLight.setSpotRange( 100f );
		spotLight.setSpotInnerAngle( 15f * FastMath.DEG_TO_RAD );
		spotLight.setSpotOuterAngle( 50f * FastMath.DEG_TO_RAD );
		spotLight.setColor( ColorRGBA.White );
		spotLight.setPosition( cam.getLocation() );
		spotLight.setDirection( cam.getDirection() );
        
		rootNode.addLight( dirLight );
		rootNode.addLight( spotLight );
		
		slsr = new SpotLightShadowRenderer( assetManager, 256 );
		slsr.setLight( spotLight );
		viewPort.addProcessor( slsr );
		
		fpp = new FilterPostProcessor( assetManager );
		BloomFilter bloom = new BloomFilter();
		fpp.addFilter( bloom );
		
		cam.setLocation( new Vector3f( 50, 10, 30 ) );
		cam.lookAt( new Vector3f( PewPew.GRID/2f, PewPew.GRID/2f, PewPew.GRID/2f ), Vector3f.UNIT_Y );

		topbox = new Geometry[PewPew.GRID][PewPew.GRID];
		bottombox = new Geometry[PewPew.GRID][PewPew.GRID];
		leftbox = new Geometry[PewPew.GRID][PewPew.GRID];
		rightbox = new Geometry[PewPew.GRID][PewPew.GRID];
		frontbox = new Geometry[PewPew.GRID][PewPew.GRID];
		backbox = new Geometry[PewPew.GRID][PewPew.GRID];
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				Box box = new Box( new Vector3f( i, 56, j ), 0.5f, 0.5f, 0.5f );
				Geometry geom = new Geometry( "Box", box );
				rootNode.attachChild( geom );
				topbox[i][j] = geom;
				
				box = new Box( new Vector3f( i, -40, j ), 0.5f, 0.5f, 0.5f );
				geom = new Geometry( "Box", box );
				rootNode.attachChild( geom );
				bottombox[i][j] = geom;

				box = new Box( new Vector3f( -40, i, j ), 0.5f, 0.5f, 0.5f );
				geom = new Geometry( "Box", box );
				rootNode.attachChild( geom );
				leftbox[i][j] = geom;

				box = new Box( new Vector3f( 56f, i, j ), 0.5f, 0.5f, 0.5f );
				geom = new Geometry( "Box", box );
				rootNode.attachChild( geom );
				rightbox[i][j] = geom;
				
				box = new Box( new Vector3f( i, j, 56f ), 0.5f, 0.5f, 0.5f );
				geom = new Geometry( "Box", box );
				rootNode.attachChild( geom );
				frontbox[i][j] = geom;
				
				box = new Box( new Vector3f( i, j, -50f ), 0.5f, 0.5f, 0.5f );
				geom = new Geometry( "Box", box );
				rootNode.attachChild( geom );
				backbox[i][j] = geom;
			}
		}
		
		setSides();
	}
	
	public void simpleUpdate( float tpf ) {
		spotLight.setPosition( cam.getLocation() );
		spotLight.setDirection( cam.getDirection() );
		
		if( !changed ) {
			return;
		}
		
		if( PewPew.chkBloom.isSelected() && !viewPort.getProcessors().contains( fpp ) ) {
			viewPort.addProcessor( fpp );
		} else {
			viewPort.removeProcessor( fpp );
		}
		
		rootNode.detachAllChildren();
		setSides();
		
		if( animation ) {
			animation( tpf );
			return;
		}
		
		boolean[][][] arr = new boolean[PewPew.GRID][PewPew.GRID][PewPew.GRID];
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				for( int k = 0; k < PewPew.GRID; k++ ) {
					if( !culled( i, j, k ) ) {
						rootNode.attachChild( grid[i][j][k] );
						arr[i][j][k] = true;
					}
				}
			}
		}
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				int mink = 0, maxk = PewPew.GRID-1;
				
				while( mink != PewPew.GRID && !arr[i][j][mink++] );
				while( maxk != -1 && !arr[i][j][maxk--] );
				
				if( mink != 0 ) {
					grid[i][j][mink-1].setMaterial( makeMaterial( back[PewPew.GRID-i-1][PewPew.GRID-j-1] ) );
				}
				
				if( maxk != PewPew.GRID-1 ) {
					grid[i][j][maxk+1].setMaterial( makeMaterial( front[i][PewPew.GRID-j-1] ) );
				}
			}
		}
		
		for( int j = 0; j < PewPew.GRID; j++ ) {
			for( int k = 0; k < PewPew.GRID; k++ ) {
				int mini = 0, maxi = PewPew.GRID-1;
				
				while( mini != PewPew.GRID && !arr[mini++][j][k] );
				while( maxi != -1 && !arr[maxi--][j][k] );
				
				if( mini != 0 ) {
					grid[mini-1][j][k].setMaterial( makeMaterial( left[k][PewPew.GRID-j-1] ) );
				}
				
				if( maxi != PewPew.GRID-1 ) {
					grid[maxi+1][j][k].setMaterial( makeMaterial( right[PewPew.GRID-k-1][PewPew.GRID-j-1] ) );
				}
			}
		}
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int k = 0; k < PewPew.GRID; k++ ) {
				int minj = 0, maxj = PewPew.GRID-1;
				
				while( minj != PewPew.GRID && !arr[i][minj++][k] );
				while( maxj != -1 && !arr[i][maxj--][k] );
				
				if( minj != 0 ) {
					grid[i][minj-1][k].setMaterial( makeMaterial( bottom[i][PewPew.GRID-k-1] ) );
				}
				
				if( maxj != PewPew.GRID-1 ) {
					grid[i][maxj+1][k].setMaterial( makeMaterial( top[i][k] ) );
				}
			}
		}
		
		changed = false;
	}
	
	Geometry[][] topbomb, leftbomb, frontbomb;
	ParticleEmitter[][][] pe;
	
	public void startAnimation() {
		animation = true;
		aTimer = 0f;
		aTop = false;
		aLeft = false;
		aFront = false;
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				for( int k = 0; k < PewPew.GRID; k++ ) {
					pe[i][j][k] = new ParticleEmitter("Emitter",  com.jme3.effect.ParticleMesh.Type.Triangle, 30);
				}
			}
		}
		
		/*
		topbomb = new Geometry[PewPew.GRID][PewPew.GRID];
		leftbomb = new Geometry[PewPew.GRID][PewPew.GRID];
		frontbomb = new Geometry[PewPew.GRID][PewPew.GRID];
		
		Material mat = makeUnshaded( 0xFFFFFF );
		
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				Sphere sphere = new Sphere( 10, 10, 0.4f, true, false );
				topbomb[i][j] = new Geometry( "sphere", sphere );
				topbomb[i][j].setMaterial( mat );
				
				sphere = new Sphere( 10, 10, 0.4f, true, false );
				leftbomb[i][j] = new Geometry( "sphere", sphere );
				leftbomb[i][j].setMaterial( mat );
				
				sphere = new Sphere( 10, 10, 0.4f, true, false );
				frontbomb[i][j] = new Geometry( "sphere", sphere );
				frontbomb[i][j].setMaterial( mat );
			}
		}
		
		*/
	}
	
	private void animation( float tpf ) {
		aTimer += tpf;
		
		if( !aTop ) {
			
		}
		
		if( !aLeft ) {
			
		}
		
		if( !aFront ) {
			
		}
	}
	
	private void setSides() {
		for( int i = 0; i < PewPew.GRID; i++ ) {
			for( int j = 0; j < PewPew.GRID; j++ ) {
				if( top[i][j] != 0xFFFFFF ) {
					rootNode.attachChild( topbox[i][j] );
					topbox[i][j].setMaterial( makeUnshaded( top[i][j] ) );
				}
				
				if( bottom[i][PewPew.GRID-j-1] != 0xFFFFFF ) {
					rootNode.attachChild( bottombox[i][j] );
					bottombox[i][j].setMaterial( makeUnshaded( bottom[i][PewPew.GRID-j-1] ) );
				}
				
				if( left[j][PewPew.GRID-i-1] != 0xFFFFFF ) {
					rootNode.attachChild( leftbox[i][j] );
					leftbox[i][j].setMaterial( makeUnshaded( left[j][PewPew.GRID-i-1] ) );
				}
				
				if( right[PewPew.GRID-j-1][PewPew.GRID-i-1] != 0xFFFFFF ) {
					rootNode.attachChild( rightbox[i][j] );
					rightbox[i][j].setMaterial( makeUnshaded( right[PewPew.GRID-j-1][PewPew.GRID-i-1] ) );
				}
				
				if( front[i][PewPew.GRID-j-1] != 0xFFFFFF ) {
					rootNode.attachChild( frontbox[i][j] );
					frontbox[i][j].setMaterial( makeUnshaded( front[i][PewPew.GRID-j-1] ) );
				}
				
				if( back[PewPew.GRID-i-1][PewPew.GRID-j-1] != 0xFFFFFF ) {
					rootNode.attachChild( backbox[i][j] );
					backbox[i][j].setMaterial( makeUnshaded( back[PewPew.GRID-i-1][PewPew.GRID-j-1] ) );
				}
			}
		}
	}
	
	private ColorRGBA getColorRGBA( int rgb ) {
		return new ColorRGBA( ((rgb>>16)&0xFF)/255f, ((rgb>>8)&0xFF)/255f, ((rgb>>0)&0xFF)/255f, 1f );
	}
	
	private Material makeMaterial( int rgb ) {
		if( PewPew.chkColor.isSelected() ) {
			Material mat = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
			mat.setBoolean( "UseMaterialColors", true ); 
			ColorRGBA color = new ColorRGBA( ((rgb>>16)&0xFF)/255f, ((rgb>>8)&0xFF)/255f, ((rgb>>0)&0xFF)/255f, 1f );
			mat.setColor( "Diffuse", color );
			mat.setColor( "Ambient", color );
			mat.setColor( "Specular", color );
			return mat;
		} else {
			Material mat = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );
			float color = 0.5f+(float)( 0.2*Math.random() );
			mat.setColor( "Color", new ColorRGBA( color, color, color, 1f ) );
			return mat;
		}
	}
	
	private Material makeUnshaded( int rgb )  {
		Material mat = new Material( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );
		mat.setColor( "Color", new ColorRGBA( ((rgb>>16)&0xFF)/255f, ((rgb>>8)&0xFF)/255f, ((rgb>>0)&0xFF)/255f, 1f ) );
		return mat;
	}
	
	private boolean culled( int i, int j, int k ) {
		if( top[i][k] == 0xFFFFFF || left[k][PewPew.GRID-j-1] == 0xFFFFFF || front[i][PewPew.GRID-j-1] == 0xFFFFFF ) {
			return true;
		}

		return false;
	}
}
