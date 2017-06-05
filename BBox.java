package comp557.a5;

import javax.vecmath.Point3d;

public class BBox
{
	public Point3d max;
	public Point3d min;
	
	public BBox()
	{
    	this.max = new Point3d(1, 1, 1);
    	this.min = new Point3d(-1, -1, -1);
    }	

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

	}	

}
