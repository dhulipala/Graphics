

import java.io.File;
import java.io.FileOutputStream;

public class RayTrace {

    // materials
    Material my_materials[] = {
	    new Material(new Vector(0.6f, 0.8f, 0.6f), 0.2f, 1.0f, 0.0f),
	    new Material(new Vector(0.6f, 0.6f, 0.8f), 0.4f, 0.8f, 0.7f),
	    new Material(new Vector(1.0f, 1.0f, 1.0f), 0.2f, 0.5f, 0.4f) };

    // spheres
    Sphere my_spheres[] = {
	    new Sphere(new Vector(-2.5f, 0.0f, -10.0f), 1f, my_materials[0]),
	    new Sphere(new Vector(1.5f, 0.0f, -12.0f), 2.5f, my_materials[1]) };

    // mirrors
    Mirror my_mirrors[] = { new Mirror(new Vector(0.0f, -2f, 0.0f), new Vector(
	    0.0f, 1.0f, 0.0f), my_materials[2]) };

    // lights
    Light my_lights[] = {
	    new Light(new Vector(-5.0f, 5.0f, -8.0f), new Vector(1.0f, 1.0f,
		    1.0f), 30f),
	    new Light(new Vector(5.0f, 5.0f, -5.0f), new Vector(1.0f, 0.0f,
		    0.0f), 20f),
            new Light(new Vector(5.0f, 5.0f, -8.0f), new Vector(0.2f, 1.0f,
			    0.3f), 40f)};
    
    // background color
    Vector bgcolor = new Vector(0.0f, 0.0f, 0.0f);
    Vector ambient = new Vector(0.7f, 0.2f, 0.2f);
    
    // view plane;
    Vector vp_v = new Vector(1.0f, 0.0f, 0.0f);
    Vector vp_u = new Vector(0.0f, 1.0f, 0.0f);
    Vector vp_w = new Vector(0.0f, 0.0f, -1.0f);

    // focal length
    float vp_d = 1.0f;

    int width = 500;
    int height = 500;
   
    Vector viewpoint = new Vector(0.0f, 1.0f, 5.0f);

    public static void main(String[] args) {
	try {
	    RayTrace rt = new RayTrace();
	    Vector[][] img = rt.render();
	    File file = new File("raytrace.ppm");
	    FileOutputStream out = new FileOutputStream(file, false);
	    out.write("P6\n".getBytes());
	    out.write((rt.width + " " + rt.height + "\n").getBytes());
	    out.write("255\n".getBytes());
	    for (int i = 0; i < rt.width; i++) {
		for (int j = 0; j < rt.height; j++) {
		    out.write((byte) (img[i][j].x * 255));
		    out.write((byte) (img[i][j].y * 255));
		    out.write((byte) (img[i][j].z * 255));
		}
	    }
	    out.flush();
	    out.close();
	    System.out.println("Done");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /*
     * The following method computes origination and direction of ray for each pixel
     * */
    private Vector[][] render() {
	Vector[][] img = new Vector[width][height];
	Vector origin, dir;

	for (int x = 0; x < width; x++) {
	    for (int y = 0; y < height; y++) {

		Vector u = vp_u.multiplyFloat((1.0f / width) * (x + .5f) - .5f);
		Vector v = vp_v.multiplyFloat((1.0f / height) * (y + .5f) - .5f);
		Vector w = vp_w.multiplyFloat(vp_d);
		
		origin = viewpoint.add(u).add(v).add(w);
		dir = origin.subtract(viewpoint).normalize();

		Vector color = traceray(origin, dir, 2);
		img[width - x - 1][y] = color;
	    }
	}

	return img;
    }
    
    /*
     * Given an origination point, direction of ray and sphere the following method 
     * returns infinity if the ray does not hit a sphere otherwise a float value
     * representing distance between origin and hit point 
     */
    private float intersect_with_sphere(Vector origin, Vector dir, Sphere sph) {
	Vector p = origin.subtract(sph.center);
	float tm = -1 * p.dotProduct(dir);
	float lmsq = p.dotProduct(p) - tm * tm;
	float delta_t_sq = sph.radius - lmsq;
	if (delta_t_sq >= 0) {
	    float delta_t = (float) Math.sqrt(delta_t_sq);
	    float t0 = tm - delta_t;
	    if (t0 < 0) {
		// sphere is behind origin
		return Float.POSITIVE_INFINITY;
	    }
	    return t0;
	}
	return Float.POSITIVE_INFINITY;
    }
/*
 * Given an origination point, direction of ray and mirror the following method 
 * returns infinity if the ray does not hit a mirror otherwise a float value
 * representing distance between origin and hit point 
 * */
    private float intersect_with_mirror(Vector origin, Vector dir, Mirror pl) {
	float den = dir.dotProduct(pl.normal);
	// If dot is zero, then two lines are parallel
	// MIN_VALUE is close enough to zero
	if (den < Float.MIN_VALUE) {
	    float num = (pl.origin.subtract(origin).dotProduct(pl.normal));
	    float t = num / den;
	    if (t > 0) {
		return t;
	    } else {
		return Float.POSITIVE_INFINITY;
	    }
	}
	return Float.POSITIVE_INFINITY;
    }

/*
 * Given an origination point, direction of ray the method checks if a ray is intersecting with any of the objects or mirrors
 * */
    private Hit intersect_with_all(Vector origin, Vector dir) {
	float lowest_t = Float.POSITIVE_INFINITY;
	Sphere hit_sphere = null;
	Mirror hit_mirror = null;
	for (int i = 0; i < my_spheres.length; i++) {
	    Sphere current_sphere = my_spheres[i];
	    float current_t = intersect_with_sphere(origin, dir, current_sphere);
	    if (current_t < lowest_t) {
		hit_sphere = current_sphere;
		lowest_t = current_t;
	    }
	}
	for (int i = 0; i < my_mirrors.length; i++) {
	    Mirror current_mirror = my_mirrors[i];
	    float current_t = intersect_with_mirror(origin, dir, current_mirror);
	    if (current_t < lowest_t) {
		hit_sphere = null;
		hit_mirror = current_mirror;
		lowest_t = current_t;
	    }
	}
	return new Hit(lowest_t, hit_sphere, hit_mirror);
    }

    /*
     * Given a hit point and normal on an object, the following method computes
     * light ray, direction and gives out the light dimensions at the hit point.
     * */
    public Vector shade(Vector hit_point, Vector normal) {
	Vector total_light = new Vector(0, 0, 0);
	for (int i = 0; i < my_lights.length; ++i) {
	    Light current_light_source = my_lights[i];
	    Vector light_ray = current_light_source.position
		    .subtract(hit_point);
	    Vector light_direction = light_ray.normalize();
	    float dir_intensity = light_direction.dotProduct(normal);
	    if (dir_intensity > 0) {
		// light is shining on this point
		Hit light_ray_hit = intersect_with_all(hit_point,
			light_direction);
		if (light_ray_hit.t == Float.POSITIVE_INFINITY) {
		    // light ray is not blocked - no shadow
		    float light_dist = light_ray.length();
		    float dist_factor = 1 / (light_dist * light_dist);
		    Vector current_light = current_light_source.color
			    .multiplyFloat(dir_intensity * dist_factor
				    * current_light_source.intensity);
		    total_light = total_light.add(current_light);
		}
	    }
	}
	return total_light;
    }

    
/*
 * Given origin and direction the method below checks the kind of object being hit, 
 * and computes the final color for the pixel on the view plane.
 * */
    public Vector traceray(Vector origin, Vector dir, int bounce) {
	Vector fcolor = bgcolor; // start with bgcolor
	if (bounce < 1) {
	    return fcolor;
	}
	Hit ray_hit = intersect_with_all(origin, dir);
	if (ray_hit.t < Float.POSITIVE_INFINITY) {
	    Vector hit_point = origin.add((dir.multiplyFloat(ray_hit.t)));
	    Vector normal;
	    Material hit_material;
	    Vector reflection = new Vector(0f, 0f, 0f);
	    if (ray_hit.hit_sphere != null) {
		// A sphere is hit
		Sphere sphere_hit_spot = ray_hit.hit_sphere;
		hit_material = sphere_hit_spot.material;
		normal = (hit_point.subtract(sphere_hit_spot.center))
			.normalize();
	    } else {
		// A mirror is hit
		Mirror hit_mirror = ray_hit.hit_mirror;
		hit_material = hit_mirror.mat;
		normal = hit_mirror.normal;
		float ch = 2 * dir.dotProduct(normal);
		Vector reflected_ray = dir.subtract(normal.multiplyFloat(ch));
		Vector reflection_direction = reflected_ray.normalize();
		reflection = traceray(hit_point, reflection_direction,
			bounce - 1);
	    }

	    Vector total_light = shade(hit_point, normal);

	    Vector ambient_color = hit_material.color
		    .multiplyFloat(hit_material.ka);
	    Vector diffuse_color = hit_material.color.multiplyVector(
		    total_light).multiplyFloat(hit_material.kd);
	    Vector reflection_color = reflection.multiplyFloat(hit_material.kr);

	    fcolor = fcolor.add(ambient_color);
	    fcolor = fcolor.add(diffuse_color);
	    fcolor = fcolor.add(reflection_color);
	}
	// note that, before returning the color, the computed color may be
	// rounded to [0.0, 1.0].
	return fcolor.round();
    }




}
