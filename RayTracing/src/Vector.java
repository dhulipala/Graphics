

public class Vector {

    public float x, y, z;

    // constructor
    public Vector(float x, float y, float z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public float length() {
	return (float) Math.sqrt(x * x + y * y + z * z);
    }

    // Addition method
    public Vector add(Vector v) {
	return new Vector(x + v.x, y + v.y, z + v.z);
    }

    // Subtrtaction method
    public Vector subtract(Vector v) {
	return new Vector(x - v.x, y - v.y, z - v.z);

    }

    // Multiply vector and a float
    public Vector multiplyFloat(float f) {
	return new Vector(x * f, y * f, z * f);

    }

    // Multiply two vectors
    public Vector multiplyVector(Vector v) {
	return new Vector(x * v.x, y * v.y, z * v.z);

    }

    // dot product method
    public float dotProduct(Vector v) {
	return (v.x * x + v.y * y + v.z * z);
    }

    // Normalize method
    public Vector normalize() {
	float length = length();
	if (length != 0) {
	    return this.multiplyFloat(1 / length);
	} else {
	    return this.multiplyFloat(1 / Float.MIN_VALUE);
	}
    }

    public Vector round() {
	return new Vector(roundf(x), roundf(y), roundf(z));
    }

    private float roundf(float f) {
	if (f > 1) {
	    return 1;
	}
	if (f < 0) {
	    return 0;
	}
	return f;
    }

    @Override
    public String toString() {
	return "[x=" + x + ", y=" + y + ", z=" + z + "]";
    }

}
