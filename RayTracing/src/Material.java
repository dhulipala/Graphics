

public class Material {

    public Vector color;
    public float ka, kd, kr;
  
    public Material(Vector color, float ka, float kd, float kr) {
	this.color = color;
	this.ka = ka;
	this.kd = kd;
	this.kr = kr;
    }

}
