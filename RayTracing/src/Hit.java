

public class Hit {
    
    public float t;
    public Sphere hit_sphere;
    public Mirror hit_mirror;
    
    public Hit(float t, Sphere hit_sphere, Mirror hit_mirror) {
	this.t = t;
	this.hit_sphere = hit_sphere;
	this.hit_mirror = hit_mirror;
    }    
}
