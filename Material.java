package comp557.a5;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;

/**
 * A class defining the material properties of a surface, 
 * such as colour and specularity. 
 */
public class Material
{
	
	/** Static member to access all the materials */
	public static Map<String,Material> materials = new HashMap<String,Material>();
	
	/** Material name */
    public String name = "";
    
    /** Diffuse colour, defaults to white */
    public Color4f diffuse = new Color4f(1,1,1,1);
    
    /** Specular colour, default to black (no specular highlight) */
    public Color4f specular = new Color4f(0,0,0,0);
    
    /** Specular hardness, or exponent, default to a reasonable value */ 
    public float hardness = 64;
    
    public BufferedImage img = null;	// For texture mapping
    
    /**
     * Default constructor
     */
    public Material() {
    	// do nothing
    }
    
}
