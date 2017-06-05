package comp557.a5;

/**
 * Abstract class for an intersectable surface 
 */
public abstract class Intersectable	// All objects (Sphere, mesh, box, plane etc.) should be extended from this base class
{
	
	/** Material for this intersectable surface
	 */
	public Material material;	// Most basic one is diffuse color
	
	public int id = -1;
	
	/** 
	 * Default constructor, creates the default material for the surface
	 */
	public Intersectable()
	{
		this.material = new Material();
	}
	
	/**
	 * Test for intersection between a ray and this surface. This is an abstract
	 *   method and must be overridden for each surface type.
	 * @param ray
	 * @param result
	 */
    public abstract void intersect(Ray ray, IntersectResult result, boolean isShadow);	// Most basic one is ray-sphere intersection
    
}
