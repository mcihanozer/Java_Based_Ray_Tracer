package comp557.a5;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4d;

import comp557.a5.IntersectResult;
import comp557.a5.Intersectable;
import comp557.a5.Ray;

/**
 * The scene is constructed from a hierarchy of nodes, where each node
 * contains a transform, a material definition, some amount of geometry, 
 * and some number of children nodes.  Each node has a unique name so that
 * it can be instanced elsewhere in the hierarchy (provided it does not 
 * make loops. 
 * 
 * Note that if the material (inherited from Intersectable) for a scene 
 * node is non-null, it should override the material of any child.
 * 
 */
public class SceneNode extends Intersectable {
	
	/**
	 * Static map for accessing scene nodes by name, to perform instancing.
	 */
	public static Map<String,SceneNode> nodeMap = new HashMap<String,SceneNode>();
	
    public String name;
   
    /** Matrix transform for this node. */
    public Matrix4d M;
    
    /** Inverse matrix transform for this node. */
    public Matrix4d Minv;
    
    /** Child nodes. */
    public List<Intersectable> children;
    
    /**
     * Default constructor.
     * Note that all nodes must have a unique name, so that they can used as an instance later on.
     */
    public SceneNode() {
    	super();
    	this.name = "";
    	this.M = new Matrix4d();
    	this.Minv = new Matrix4d();
    	this.children = new LinkedList<Intersectable>();
    }
    
    private IntersectResult tmpResult = new IntersectResult();
    
    private Ray tmpRay = new Ray();
    
    @Override
    public void intersect(Ray ray, IntersectResult result, boolean isShadow)	// For a node you need to call this method
    														// It makes the transformation for the ray, and intersect with all children
    														// TODO Part of the hierarchy can be applied here
    {
    	tmpRay.eyePoint.set(ray.eyePoint);
    	tmpRay.viewDirection.set(ray.viewDirection);
    	
    	Minv.transform(tmpRay.eyePoint);
    	Minv.transform(tmpRay.viewDirection);
    	
    	tmpResult.t = Double.POSITIVE_INFINITY;
    	tmpResult.n.set(0, 0, 1);
    	tmpResult.isIntersected = false;
    	tmpResult.id = -1;
    	tmpResult.u = Double.POSITIVE_INFINITY;
    	tmpResult.v = Double.POSITIVE_INFINITY;
    	
        for ( Intersectable s : children )
        {
            s.intersect( tmpRay, tmpResult, isShadow); 
            
            if(tmpResult.isIntersected)
            {
            	if ( tmpResult.t > 1e-9  )
                {
            		if(isShadow)	// TODO: Objective 5: finish this method and use it in your lighting computation
            		{
            			result.isIntersected = tmpResult.isIntersected;
            			result.id = tmpResult.id;
            			return;
            		}
            		
            		if(tmpResult.t < result.t)
            		{
	                	M.transform(tmpResult.n);
	                	M.transform(tmpResult.p);
	                	
	                	result.n.set(tmpResult.n);
	                	result.p.set(tmpResult.p); 
	                	result.t = tmpResult.t;
	                	result.isIntersected = tmpResult.isIntersected;
	                	result.id = tmpResult.id;
	                	result.u = tmpResult.u;
	                	result.v = tmpResult.v;
	                	
	                	result.material = (this.material == null) ? tmpResult.material : this.material;
            		}
                }
            }

        }
  
    }
    
}
