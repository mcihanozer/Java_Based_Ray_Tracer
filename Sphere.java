package comp557.a5;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * A simple sphere class.
 */
public class Sphere extends Intersectable
{
    
	/**
	 * Radius of the sphere.
	 */
	public double radius;
    
	/**
	 * Location of the sphere center.
	 */
	public Point3d center;
	
	public BBox Bbox = new BBox();
    
    /**
     * Default constructor. Creates a unit sphere centered at (0,0,0)
     */
    public Sphere()
    {
    	super();	// this.material = new Material(); -> A totally diffuse white sphere
    	this.radius = 1.0;
    	this.center = new Point3d(0,0,0);
    	
    	setBbox();
    }
    
    /**
     * Creates a sphere with the request radius and center. 
     * 
     * @param radius
     * @param center
     * @param material
     */
    public Sphere(double radius, Point3d center, Material material)
    {
    	this.radius = radius;
    	this.center = center;
    	this.material = material;
    	
    	setBbox();
    }
    
    public void setBbox()
    {
    	Bbox.min.x = center.x-radius;
    	Bbox.min.y = center.y-radius;
    	Bbox.min.z = center.z-radius;
    	
    	Bbox.max.x = center.x+radius;
    	Bbox.max.y = center.y+radius;
    	Bbox.max.z = center.z+radius;
    }
    
    // It will be useful for Monte Carlo Path Tracer
    public Vector3d getNormalAt(final Point3d p)
    {
    	/*
			Method for calculating the normal of the sphere at a given point.
			
			Referenced from SIGGRAPH:
			Link: https://www.siggraph.org/education/materials/HyperGraph/raytrace/rtinter1.htm
    	 */
    	
    	double invRad = 1 / radius;
    	
    	Vector3d n = new Vector3d(	(p.x - center.x) * invRad,	// X
    								(p.y - center.y) * invRad,	// Y
    								(p.z - center.z) * invRad	// Z
    							);
    	
    	n.normalize();
    	
    	return n;
    }
    
    @Override
    public void intersect( Ray ray, IntersectResult result, boolean isShadow)
    {
    	IntersectResult bboxResult = new IntersectResult();
    	Bbox.intersect(ray, bboxResult, isShadow);
    	
    	if(bboxResult.isIntersected)
    	{
    		sphereIntersect(ray, result, isShadow);
    	}
    }
    
    private void sphereIntersect( Ray ray, IntersectResult result, boolean isShadow)	// Calculate Ray - Sphere intersection
    {
    	/*
    		This method uses algebraic solution for calculating ray-sphere intersection.
    		
    		Referenced from SIGGRAPH:
    		Link: https://www.siggraph.org/education/materials/HyperGraph/raytrace/rtinter1.htm
    		
    	*/

    	// Check for the intersection. If there is an intersection set:
    	// * Surface normal "n" (Normal at intersection point)
    	// * Intersection point "p"
    	// * "material" of the sphere
    	// * "t" parameter
    	
    	// Calculate A = Ray.d.x^2 + Ray.d.y^2 + Ray.z^2
    	//double a = (ray.viewDirection.x * ray.viewDirection.x) + (ray.viewDirection.y * ray.viewDirection.y) + (ray.viewDirection.z * ray.viewDirection.z);
    	
    	// Calculate B = 2 * (Xd * (X0 - Xc) + Yd * (Y0 - Yc) + Zd * (Z0 - Zc)),
    	// where Xd, Yd, Zd are direction of the ray, X0, Y0, Z0 are origin of the ray, and Xc, Yc, Zc are center of the sphere
    	
    	double b = 	2 * ( ray.viewDirection.x * (ray.eyePoint.x - center.x)
    					+ ray.viewDirection.y * (ray.eyePoint.y - center.y)
    					+ ray.viewDirection.z * (ray.eyePoint.z - center.z) );
    	
    	// Calculate C = (X0 - Xc)^2 + (Y0 - Yc)^2 + (Z0 - Zc)^2 - Sr^2, where Sr is radius of the sphere
    	
    	double c = 	  (ray.eyePoint.x - center.x) * (ray.eyePoint.x - center.x)
    				+ (ray.eyePoint.y - center.y) * (ray.eyePoint.y - center.y)
    				+ (ray.eyePoint.z - center.z) * (ray.eyePoint.z - center.z)
    				- radius * radius;
    	
    	// Calculate discriminant
    	// a || c = 0 -> discriminant = b^2
    	// else, disciriminant = b^2 - 4ac
    	// 1e-9 is EPSILON
    	
    	double discri;
    	
    	if(c <= 1e-9)
    	{
    		discri = b * b;
    	}
    	else
    	{
    		discri = (b * b) - (4*c);;
    	}
    	
    	// Check for intersection
    	
    	if(discri >  1e-9)	// There is an intersection
    	{
    		// Calculate t, and set related/needed data to IntersectResult
    		
    		// Calculate and set t
    		double sqrtDis = Math.sqrt(discri);
    		double t0 = (-b - sqrtDis) / 2;
    		
    		if(t0 >= 0)	//if t0 >= 0, there is no need to calculate t1. Because t0 will be the smallest root
    		{
    			result.t = t0;
    		}
    		else
    		{
    			result.t = ( -b + sqrtDis ) / 2;
    		}
    		
    		result.isIntersected = true;
    		
    		if(isShadow)
    		{
    			return;
    		}

    		// Set intersection point (Referenced from the same SIGGRAPH tutorial)
    		result.p.x = ray.eyePoint.x + ray.viewDirection.x * result.t;
    		result.p.y = ray.eyePoint.y + ray.viewDirection.y * result.t;
    		result.p.z = ray.eyePoint.z + ray.viewDirection.z * result.t;
    		
    		
    		// Set normal at intersection point
    		result.n = getNormalAt(result.p);
    		
    		// Set UV if material has an image
    		if(this.material.img != null)
    		{
    			//Calculate UV Mapping values

    			/*
    				UV Mapping calculation formulae are written with referenced Wikipedia Page
    				Link:	http://en.wikipedia.org/wiki/UV_mapping
    			*/

    			//Calculate Vector d, which is unit vector from intersection point to the sphere's origin

    			//Calculate Vector d
    			Vector3d d = new Vector3d(	result.p.x - center.x,
    										result.p.y - center.y,
    										result.p.z - center.z
    									);
    			d.normalize();	// Normalize Vector d

    			//Calculate U value
    			result.u = 0.5 + (Math.atan2(d.z, d.x) / (2 * Math.PI));

    			//Calculte V value
    			result.v = 0.5 - (Math.asin(d.y) / Math.PI);
    		}
    		
        	// Set material of the sphere
    		result.material = this.material;
    		
    		result.id = id;
    		
    		
    	}
    	
    }
    
}
