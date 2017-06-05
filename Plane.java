package comp557.a5;

import javax.vecmath.Vector3d;

/**
 * Class for a plane at y=0.
 * 
 * This surface can have two materials.  If both are defined, a 1x1 tile checker 
 * board pattern should be generated on the plane using the two materials.
 */
public class Plane extends Intersectable {
    
	/** The second material, if non-null is used to produce a checker board pattern. */
	Material material2;
	
	/** The plane normal is the y direction */
	public static final Vector3d n = new Vector3d(0, 1, 0);
    
    /**
     * Default constructor
     */
    public Plane() {
    	super();
    }

        
    @Override
    public void intersect( Ray ray, IntersectResult result, boolean isShadow)
    {
    	result.isIntersected = false;
    	
    
        /*
         	Ray - Plane intersection:
         	
         	https://www.siggraph.org/education/materials/HyperGraph/raytrace/rayplane_intersection.htm
         */
    	
    	// Step 1. Check whether the ray is parallel to the plane or not
    	// Vd = Pn dot Rd, where Pn is plane normal, Rd is ray direction
    	// If Vd > 0 there is an intersection.
    	
    	
    	double vd = n.dot(ray.viewDirection);
    	
    	if(Math.abs(vd) > 1e-9)	// There is an intersection
    	{
    		// Step 2. Compute V0 = -(Pn dot RO + D) for checking whether the ray
    		// hits the plane behind origin or not, where Pn is plane normal, D
    		// is plane position, RO is ray origin.
    		
    		// Since n.dot() does not support Point3d...
    		Vector3d center = new Vector3d(0, 0, 0);	// Center is origin
    		center.sub(ray.eyePoint);
    		
    		double t = ( center.dot(n) ) / vd;
    		
    		if(t > 1e-9)
    		{
    			// Calculate intersection point
    			
    			result.isIntersected = true;
    			result.t = t;
    			
    			if(isShadow)
        		{
        			return;
        		}
    			
    			


    			result.n.x = n.x;
    			result.n.y = n.y;
    			result.n.z = n.z;
    			
    			
    			
    			result.p.x = ray.eyePoint.x + ray.viewDirection.x * t;
    			result.p.y = ray.eyePoint.y + ray.viewDirection.y * t;
    			result.p.z = ray.eyePoint.z + ray.viewDirection.z * t;
    			
    			result.id = id;
    			
    			// Set color
    			int a = ((int)Math.abs(result.p.x)) %2;
    			int b = ((int)Math.abs(result.p.z)) %2;
    			
       			if (result.p.x < 0)
    				a+=1;
 
       			if (result.p.z < 0)
    				b+=1;
    			  			
    			int c = ((a&1) ^ (b&1));
    			
    			if(material == null && material2 == null)
    			{
    				result.material = new Material();
    				
    				result.material.diffuse.x = c;
    				result.material.diffuse.y = c;
    				result.material.diffuse.z = c;
    				result.material.diffuse.w = 1;
    			}
    			else if(material == null)
    			{
    				result.material = material2;
    			}
    			else if(material2 == null)
    			{
    				result.material = material;
    			}
    			else
    			{
        			if(c == 1)
        			{
        				result.material = material2;
        			}
        			else
        			{
        				result.material = material;
        			}
    			}				
    		}
    		
    	}
    	
    }
    
}
