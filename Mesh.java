package comp557.a5;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import comp557.a5.PolygonSoup.Vertex;

public class Mesh extends Intersectable {
	
	/** Static map storing all meshes by name */
	public static Map<String,Mesh> meshMap = new HashMap<String,Mesh>();
	
	/**  Name for this mesh, to allow re-use of a polygon soup across Mesh objects */
	public String name;
	
	/**
	 * The polygon soup.
	 */
	public PolygonSoup soup;
	
	public BBox bbox =  new BBox();

	public Mesh() {
		super();
		this.name = "";
		this.soup = null;
	}

	@Override
	public void intersect(Ray ray, IntersectResult result, boolean isShadow)
	{
		
		// TODO: Objective 7: Bonus: finish this class as a bonus objective
		
		//iterating over values only
		for (Mesh cm : meshMap.values())
		{
			IntersectResult bboxResult = new IntersectResult();
	    	cm.bbox.intersect(ray, bboxResult, isShadow);
	    	
	    	if(bboxResult.isIntersected)
	    	{
	    		for(int vi = 0; vi < cm.soup.faceList.size(); vi ++)
			    {
			    	int[] faces = cm.soup.faceList.get(vi); 
			    	
			    	Vertex v1 = cm.soup.vertexList.get(faces[0]);
			    	Vertex v2 = cm.soup.vertexList.get(faces[1]);
			    	Vertex v3 = cm.soup.vertexList.get(faces[2]);
			    	
			    	intersectTriangle(ray, result, isShadow, v1, v2, v3, cm.material);
			    }
	    	}
	    	
		}	// End of for each Mesh
	}
	
	private void intersectTriangle(	Ray ray, IntersectResult result, boolean isShadow,
									final Vertex v1, final Vertex v2, final Vertex v3, Material mat)
	{
		/*
		  	Fast, Minimum Storage Ray/Triangle Intersection, Moller et al.
		 
		 	Reference: http://www.cs.virginia.edu/~gfx/Courses/2003/ImageSynthesis/papers/Acceleration/Fast%20MinimumStorage%20RayTriangle%20Intersection.pdf
		 */
		
		// Calculate edges of the triangle
		Vector3d edge1 = new Vector3d(	v2.p.x - v1.p.x,
										v2.p.y - v1.p.y,
										v2.p.z - v1.p.z
									);
		
		Vector3d edge2 = new Vector3d(	v3.p.x - v1.p.x,
										v3.p.y - v1.p.y,
										v3.p.z - v1.p.z
									);
		
		// Calculate the determinant (U parameter)
		Vector3d P = new Vector3d();
		P.cross(ray.viewDirection, edge2);
		double det = edge1.dot(P);
		
		if(det > -1e-9 && det < 1e-9)	{	return;	}	// Ray lies in plane of triangle
		
		double invDet = 1 / det;
		
		// Calculate distance from vertex1 to ray origin
		Vector3d T = new Vector3d(	ray.eyePoint.x - v1.p.x,
									ray.eyePoint.y - v1.p.y,
									ray.eyePoint.z - v1.p.z
								);	

		double U = (T.dot(P)) * invDet;	// Calculate U parameter
		
		if(U < 0.f || U > 1.f)	{	return;	}	// Intersection lies outside of the triangle

		// Calculate V parameter
		Vector3d Q = new Vector3d();
		Q.cross(T, edge1);
		
		double V = (ray.viewDirection.dot(Q)) * invDet;

		if(V < 0.f || (U + V) > 1.f){	return;	}	// Intersection lies outside of the triangle

		double t = (edge2.dot(Q)) * invDet;

		if(t > 1e-9)	// Triangle and ray intersects
		{
			result.isIntersected = true;
			result.id = id;
			
			if(isShadow)
			{
				result.t = t;
				return;
			}
			else if(t < result.t)
			{
				result.material = mat;
				
				result.t = t;
				
				result.p.x = ray.eyePoint.x + ray.viewDirection.x * t;
    			result.p.y = ray.eyePoint.y + ray.viewDirection.y * t;
    			result.p.z = ray.eyePoint.z + ray.viewDirection.z * t;

    			result.n.cross(edge1, edge2);
    			result.n.normalize();
			}
		}	
	}

}
