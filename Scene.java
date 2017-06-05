package comp557.a5;

import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple scene loader based on XML file format.
 */
public class Scene {
    
    /**
     * Flat array of surfaces
     */
    public SceneNode root;
	
	//public Intersectable root;
	
	/**
	 * All scene lights.
	 */
	public Map<String,Light> lights;

    /** 
     * Contains information about how to render the scene.
     */
    public Render render;
    
    /** 
     * The ambient colour.
     */
    public Color3f ambient;

    /** 
     * Default constructor.
     */
    public Scene()
    {
		this.root = null;
    	this.render = new Render();
    	this.ambient = new Color3f();
    	this.lights = new HashMap<String,Light>();
    }
    
    /**
     * renders the scene
     */
    public void render(boolean showPanel)
    {
 
        Camera cam = render.camera; 
        int w = cam.imageSize.width;
        int h = cam.imageSize.height;
        
        render.init(w, h, showPanel);
        
        for ( int i = 0; i < h && !render.isDone(); i++ )
        {
            for ( int j = 0; j < w && !render.isDone(); j++ )
            {
            	
                // TODO: Objective 1: generate a ray (use the generateRay method)
            	double[] offset = new double[2];
            	
            	Color3f pixelColor = new Color3f();
            	
            	//render.samples = 1;
            	
            	// Anti-aliasing rays: Stochastic (Random) Supersampling approach is used
            	for(int si = 0; si < render.samples - 1; si++)
            	{
            	
	            	offset[0] = Math.random();
	            	offset[1] = Math.random();
	            	
	            	sampleRay(cam, render.bgcolor, offset, i, j, pixelColor);	// Samples a ray by given offset, trace it, and add result to "pixelColor"
            	}
            	
            	//render.isJittered = true;
            	
            	// Shoot the main ray
            	if(render.isJittered) 	// Check whether jittering is set
            	{
            		// Jitter the offset
            		offset[0] = Math.random();
                	offset[1] = Math.random();
            	}
            	else
            	{
            		// Shoot the ray to the center
            		offset[0] = 0.5;
                	offset[1] = 0.5;
            	}
            	
            	sampleRay(cam, render.bgcolor, offset, i, j, pixelColor);	// Samples a ray by given offset, trace it, and add result to "pixelColor"t
            	
            	double invSampleCount = 1 / (double)render.samples;
            	
            	pixelColor.x *= invSampleCount;
            	pixelColor.y *= invSampleCount;
            	pixelColor.z *= invSampleCount;
            	
            	int r = (int)(255*pixelColor.x);
                int g = (int)(255*pixelColor.y);
                int b = (int)(255*pixelColor.z);
                int a = 255;
                int argb = (a<<24 | r<<16 | g<<8 | b);    
                
                // update the render image
                render.setPixel(j, i, argb);
            }
        }
        
        // save the final render image
        render.save();
        
        // wait for render viewer to close
        render.waitDone();
        
    }
    
    // Samples a ray by given offset, trace it, and add result to "pixelColor"
    private Color3f sampleRay(final Camera cam, final Color3f bgColor, final double[] offset, final int i, final int j, Color3f pixelColor)
    {
    	Ray newRay = new Ray();
    	generateRay(i, j, offset, cam, newRay);
    	
        // TODO: Objective 2: test for intersection with scene surfaces
    	
    	IntersectResult result = new IntersectResult();
    	root.intersect(newRay, result, false);
    	
    	Color3f c = new Color3f();	// Default color value is backgorund color
    	if(result.isIntersected)
    	{
    		c = shade(newRay, result, cam.from, 1);
    		
    	}
    	else
    	{
    		c.add(bgColor);
    	}
    	
    	pixelColor.x += c.x;
    	pixelColor.y += c.y;
    	pixelColor.z += c.z;
    	
    	return pixelColor;
    }
    
    private Color3f shade(final Ray ray, final IntersectResult result, final Point3d camPos, int depth)
    {
    	Color3f pixelColor = new Color3f();
    	
    	if(depth > 5)
    	{
    		return pixelColor;
    	}
    	
    	// Ambient
    	pixelColor.x += result.material.diffuse.x * ambient.x;
    	pixelColor.y += result.material.diffuse.y * ambient.y;
    	pixelColor.z += result.material.diffuse.z * ambient.z;
    	
    	for(Light currentLight : lights.values())	// For each color
    	{
    		// Algorithm
    		// 1. Check for visibility
    		// 2. If visible, calculate diffuse and Blinn-Phong
    		
    		
    		
    		// Step 1. Generate shadow ray
    		Vector3d lightDir = new Vector3d(	currentLight.from.x - result.p.x,
    											currentLight.from.y - result.p.y,
    											currentLight.from.z - result.p.z
    										);
    		lightDir.normalize();
    		
    		Point3d shadowRayP = new Point3d(	result.p.x + (1e-4f * result.n.x),
    											result.p.y + (1e-4f * result.n.y),
    											result.p.z + (1e-4f * result.n.z)
    										);
    		
    		Ray shadowRay = new Ray(shadowRayP, lightDir);
    		
    		IntersectResult shadowRayResult = new IntersectResult();
    		
    		shadowRayResult.id = result.id;
    		
        	root.intersect(shadowRay, shadowRayResult, true);	// TODO: Objective 5: finish this method and use it in your lighting computation
        	
        	if(!shadowRayResult.isIntersected)	// Not in the shadow, do shading
        	{
        		// Diffuse
        		//lightDir.scale(-1);
        		double theta = Math.max(result.n.dot(lightDir), 0.0);
        		
        		if(result.material.img == null)
        		{
        			// Java is the best language for making simple taks harder! Wow! I LOVE Java! Best language ever!
            		Color3f diffuseC = new Color3f( (float)(currentLight.color.x * result.material.diffuse.x * theta * currentLight.power),
            										(float)(currentLight.color.y * result.material.diffuse.y * theta * currentLight.power),
            										(float)(currentLight.color.z * result.material.diffuse.z * theta * currentLight.power)
            									 );
            		
            		// Add diffuse effect
            		pixelColor.x += diffuseC.x;
                	pixelColor.y += diffuseC.y;
                	pixelColor.z += diffuseC.z;
                	
                	// Blinn-Phong
                	// Get camere vector = Pcamera - Pintersection
                	Vector3d cameraV = new Vector3d(	camPos.x - result.p.x,
                										camPos.y - result.p.y,
                										camPos.z - result.p.z
                									);
                	cameraV.normalize();
                	
                	Vector3d h = new Vector3d(	cameraV.x + lightDir.x,
                								cameraV.y + lightDir.y,
                								cameraV.z + lightDir.z
                							);
                	h.normalize();
                	
                	double sTheta = Math.pow(( Math.max(result.n.dot(h), 0.0) ), result.material.hardness);
                	
                	Color3f specularC = new Color3f(	(float)(currentLight.color.x * result.material.specular.x * sTheta * currentLight.power),
    													(float)(currentLight.color.y * result.material.specular.y * sTheta * currentLight.power),
    													(float)(currentLight.color.z * result.material.specular.z * sTheta * currentLight.power)
                									);
                	
                	// Add specular effect
                	pixelColor.x += specularC.x;
                	pixelColor.y += specularC.y;
                	pixelColor.z += specularC.z;
                	
        		}
        		else
        		{
        			// TODO Code repeat
        			
        			// Read image texel color and shade it
        			
        			int x = (int)Math.floor(result.u * result.material.img.getWidth());
        			int y = (int)Math.floor(result.v* result.material.img.getHeight());
        			
        			int pix = result.material.img.getRGB(x,y);
        			
        			int r = (pix >> 16) & 0xFF;
        			int g = (pix >> 8) & 0xFF;
        			int b = pix & 0xFF;
        			
        			float converter = 1 / (float)255;
        			
        			float red = r * converter;
        			float green = b * converter; 
        			float blue = g * converter;
                	
            		Color3f diffuseC = new Color3f( (float)(currentLight.color.x * red * theta * currentLight.power),
            										(float)(currentLight.color.y * green * theta * currentLight.power),
            										(float)(currentLight.color.z * blue * theta * currentLight.power)
            									 );
                	
            		//Color3f diffuseC = new Color3f( red, green, blue );
            		
            		// Add diffuse effect
            		pixelColor.x += diffuseC.x;
                	pixelColor.y += diffuseC.y;
                	pixelColor.z += diffuseC.z;
                	
                	// Blinn-Phong
                	// Get camere vector = Pcamera - Pintersection
                	Vector3d cameraV = new Vector3d(	camPos.x - result.p.x,
                										camPos.y - result.p.y,
                										camPos.z - result.p.z
                									);
                	
                	cameraV.normalize();
                	
                	Vector3d h = new Vector3d(	cameraV.x + lightDir.x,
                								cameraV.y + lightDir.y,
                								cameraV.z + lightDir.z
                							);
                	h.normalize();
                	
                	double sTheta = Math.pow(( Math.max(result.n.dot(h), 0.0) ), result.material.hardness);
                	
                	Color3f specularC = new Color3f(	(float)(currentLight.color.x * result.material.specular.x * sTheta * currentLight.power),
    													(float)(currentLight.color.y * result.material.specular.y * sTheta * currentLight.power),
    													(float)(currentLight.color.z * result.material.specular.z * sTheta * currentLight.power)
                									);
                	
                	// Add specular effect
                	pixelColor.x += specularC.x;
                	pixelColor.y += specularC.y;
                	pixelColor.z += specularC.z;
        		}
        		
        	}
        	
        	
    	}
    	
    	if(pixelColor.x > 1){	pixelColor.x = 1;	}
    	if(pixelColor.y > 1){	pixelColor.y = 1;	}
    	if(pixelColor.z > 1){	pixelColor.z = 1;	}
    	
    	return pixelColor;
    }
    
    /**
     * Generate a ray through pixel (i,j).
     * 
     * @param i The pixel row.
     * @param j The pixel column.
     * @param offset The offset from the center of the pixel, in the range [-0.5,+0.5] for each coordinate. 
     * @param cam The camera.
     * @param ray Contains the generated ray.
     */
	public static void generateRay(final int i, final int j, final double[] offset, final Camera cam, Ray ray)
	{
		// Calculate scalars
		double aspectRatio = cam.imageSize.getWidth() / cam.imageSize.getHeight();
		double fov = Math.tan(Math.toRadians(cam.fovy / 2));
		
		double t = fov;
		double b = -fov;
		double l = aspectRatio * b;
		double r = aspectRatio * t;
		
		double scalarU = l + ( (r - l) * (j + offset[0]) / cam.imageSize.getWidth() );	// j
		double scalarV = b + ( (t - b) * (i + offset[1]) / cam.imageSize.getHeight() );	// i
		
		// Basis
		Vector3d w = new Vector3d(	cam.from.x - cam.to.x,
									cam.from.y - cam.to.y,
									cam.from.z - cam.to.z
									);
		w.normalize();
		
		cam.up.normalize();
		Vector3d u = new Vector3d();
		u.cross(cam.up, w);
		u.normalize();
		
		Vector3d v = new Vector3d();
		v.cross(u, w);	// Should be v.cross(w,u) but, it's reverse..
		v.normalize();
		
		Vector3d rayDir = new Vector3d(	(scalarU * u.x) + (scalarV * v.x) - w.x,
										(scalarU * u.y) + (scalarV * v.y) - w.y,
										(scalarU * u.z) + (scalarV * v.z) - w.z
										);
		rayDir.normalize();
		
		ray.set(cam.from, rayDir);

//		double aspectRatio = cam.imageSize.getWidth() / cam.imageSize.getHeight();
//		
//		double height	= Math.tan( Math.toRadians(cam.fovy * 0.5)) * 2;
//		double width 	= height * aspectRatio;
//		
//		double l = -width * 0.5;
//		double r = width * 0.5;
//		double t = height * 0.5;
//		double b = -height * 0.5;
//		
//		double deltaX = (r - l) / cam.imageSize.getWidth();
//		double deltaY = (t - b) / cam.imageSize.getHeight();
//		
//		double xPos = l + deltaX * (j + 0.5);
//		double yPos = b + deltaY * (i + 0.5);
//		
//		Vector3d w = new Vector3d(	cam.from.x - cam.to.x,
//									cam.from.y - cam.to.y,
//									cam.from.z - cam.to.z
//								);
//		w.normalize();
//		
//		cam.up.normalize();
//		Vector3d u = new Vector3d();
//		u.cross(cam.up, w);
//		
//		Vector3d v = new Vector3d();
//		v.cross(u, w);	// Should be v.cross(w,u) but, it's reverse...
//		v.normalize();
//		
//		Vector3d rayDir = new Vector3d( (xPos * u.x) + (yPos * v.x) - w.x,
//										(xPos * u.y) + (yPos * v.y) - w.y,
//										(xPos * u.z) + (yPos * v.z) - w.z
//										);
//		rayDir.normalize();
//		
//		ray.set(cam.from, rayDir);
		
		
		// Perspective ray generation
		
//		double width = cam.imageSize.getWidth();
//		double height = cam.imageSize.getHeight();
//		
//		double aspectRatio = width / height;
//		
//		double fovyRad = Math.toRadians(cam.fovy / 2);
//		double fov = Math.tan(fovyRad);
//		
//		// Converts the point from texture to NDC coordinates
//		double s = (j - (width / 2.0)) / (width / 2.0) * aspectRatio * fov;
//		double t = ((height / 2.0) - i) / (height / 2.0) * fov;
//		
//		
////		double s = ( (j - halfWidth) / halfWidth ) * aspectRatio * fov;
////		double t = ( (halfHeight - i) / halfHeight ) * fov;
//		
//		// Converts the point points to World coordinates
//		
//		// Generate Camera basis and camera vector
//		
//		// Basis
//		Vector3d w = new Vector3d( (cam.from.x - cam.to.x), (cam.from.y - cam.to.y), (cam.from.z - cam.to.z) );	// Because vecmath has no operator- for Point3d,
//																												// and Point3d -> Vector3d conversation...
//		w.normalize();
//		
//
//
//		cam.up.normalize();	// For being sure
//		Vector3d u = new Vector3d(); //(cam.up);
//		u.cross(cam.up, w);
//		u.normalize();
//
//		Vector3d v = new Vector3d();
//		v.cross(w, u);
//		v.normalize();
//
//		// Matrix ( C = [ u, v, w, e], 4t row is 0 0 0 1
//		Matrix4d C = new Matrix4d();
//		
//		// Set u
//		C.m00 = u.x;
//		C.m10 = u.y;
//		C.m20 = u.z;
//		C.m30 = 0;
//		
//		// Set vp
//		C.m01 = v.x;
//		C.m11 = v.y;
//		C.m21 = v.z;
//		C.m31 = 0;
//		
//		// Set w
//		C.m02 = w.x;
//		C.m12 = w.y;
//		C.m22 = w.z;
//		C.m32 = 0;
//		
//		// Set e
//		C.m03 = cam.from.x;
//		C.m13 = cam.from.y;
//		C.m23 = cam.from.z;
//		C.m33 = 1;
//		
//		Point3d newP = new Point3d(s, t, -1);
//		
//		C.transform(newP);
//		
//		// Generate ray direction
//		Vector3d rayDir = new Vector3d(	newP.x - cam.from.x,
//										newP.y - cam.from.y,
//										-1
//										);	
//		rayDir.normalize();
//		
//		ray.set(cam.from, rayDir);
	} 
}
