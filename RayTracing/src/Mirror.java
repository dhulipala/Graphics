

public class Mirror {

    public Vector normal;
    public Vector origin;
    public Material mat;

    public Mirror(Vector origin, Vector normal, Material mat) {
	this.origin = origin;
	this.normal = normal;
	this.mat = mat;
    }
}
