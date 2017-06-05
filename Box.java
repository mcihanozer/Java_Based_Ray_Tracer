package comp557.a5;

import javax.vecmath.Point3d;

/**
 * A simple box class. A box is defined by it's lower (@see min) and upper (@see max) corner. 
 */
public class Box extends Intersectable {

	public Point3d max;
	public Point3d min;
	
    /**
     * Default constructor. Creates a 2x2x2 box centered at (0,0,0)
     */
    public Box() {
    	super();
    	this.max = new Point3d(1, 1, 1);
    	this.min = new Point3d(-1, -1, -1);
    }	

	@Override
	public void intersect(Ray ray, IntersectResult result, boolean isShadow)
	{
		// Reference: http://people.csail.mit.edu/amy/papers/box-jgt.pdf
		
		
		double tmin, tmax, tymin, tymax, tzmin, tzmax;
		
		double invDirX = 1 / ray.viewDirection.x;
		double invDirY = 1 / ray.viewDirection.y;
		double invDirZ = 1 / ray.viewDirection.z;
		
		if(ray.viewDirection.x >=  1e-9)
		{
			tmin = (min.x - ray.eyePoint.x) * invDirX;
			tmax = (max.x - ray.eyePoint.x) * invDirX;
		}
		else
		{
			tmax = (min.x - ray.eyePoint.x) * invDirX;
			tmin = (max.x - ray.eyePoint.x) * invDirX;
		}
		
		if(ray.viewDirection.y >=  1e-9)
		{
			tymin = (min.y - ray.eyePoint.y) * invDirY;
			tymax = (max.y - ray.eyePoint.y) * invDirY;
		}
		else
		{
			tymax = (min.y - ray.eyePoint.y) * invDirY;
			tymin = (max.y - ray.eyePoint.y) * invDirY;
		}
		
		if( (tmin > tymax) || (tymin > tmax) )
		{
			return;
		}
		
		if(tymin > tmin)
		{
			tmin = tymin;
		}
		
		if(tymax < tmax)
		{
			tmax = tymax;
		}
		
		if(ray.viewDirection.z >=  1e-9)
		{
			tzmin = (min.z - ray.eyePoint.z) * invDirZ;
			tzmax = (max.z - ray.eyePoint.z) * invDirZ;
		}
		else
		{
			tzmax = (min.z - ray.eyePoint.z) * invDirZ;
			tzmin = (max.z - ray.eyePoint.z) * invDirZ;
		}
		
		if( (tmin > tzmax) || (tzmin > tmax) )
		{
			return;
		}
		
		result.isIntersected = true;
		
		if(tzmin > tmin)
		{
			tmin = tzmin;
		}
		
		if(tzmax < tmax)
		{
			tmax = tzmax;
		}
		
		if(tmin >  1e-9)
		{
			result.t = tmin;
		}
		else
		{
			result.t = tmax;
		}
		
		

		result.material = material;

		result.p.x = ray.eyePoint.x + ray.viewDirection.x * result.t;
		result.p.y = ray.eyePoint.y + ray.viewDirection.y * result.t;
		result.p.z = ray.eyePoint.z + ray.viewDirection.z * result.t;
		
		
		if(result.p.x == min.x)
		{
			result.n.x = -1;
			result.n.y = 0;
			result.n.z = 0;
		}
		else if(result.p.x == max.x)
		{
			result.n.x = 1;
			result.n.y = 0;
			result.n.z = 0;
		}
		else if(result.p.y == min.y)
		{
			result.n.x = 0;
			result.n.y = -1;
			result.n.z = 0;
		}
		else if(result.p.y == max.y)
		{
			result.n.x = 0;
			result.n.y = 1;
			result.n.z = 0;
		}
		else if(result.p.z == min.z)
		{
			result.n.x = 0;
			result.n.y = 0;
			result.n.z = -1;
		}
		else if(result.p.z == max.x)
		{
			result.n.x = 0;
			result.n.y = 0;
			result.n.z = 1;
		}
		
		result.id = id;
		
		result.n.normalize();
	}	

}
