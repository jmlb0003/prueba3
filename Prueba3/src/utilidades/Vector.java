package utilidades;


/**
 * Clase de apoyo para las operaciones con vectores en los cálculos de posiciones y rotaciones
 * @author Jose
 */
public class Vector {
    private final float[] matrixArray = new float[9];
    
	private volatile float x = 0f;
    private volatile float y = 0f;
	private volatile float z = 0f;

	public Vector() {
		this(0, 0, 0);
	}

	public Vector(float x, float y, float z) {
	    set(x, y, z);
	}

	public synchronized float getX() {
        return x;
    }
    public synchronized void setX(float toX) {
        x = toX;
    }

    public synchronized float getY() {
        return y;
    }
    
    public synchronized void setY(float toY) {
        y = toY;
    }

    public synchronized float getZ() {
        return z;
    }
    
    public synchronized void setZ(float toZ) {
        z = toZ;
    }

    
    /**
     * Devuelve los elementos del vector en un array.
     * @param array Variable donde se almacenan los valores.
     */
    public synchronized void get(float[] array) {
        if (array == null || array.length != 3) {
        	throw new IllegalArgumentException("get() array must be non-NULL and size of 3");
        }
        
        array[0] = x;
        array[1] = y;
        array[2] = z;
    }

    
    /**
     * Método para indicar unos nuevos valores para los elementos del vector.
     * @param v Variable que contiene los valores que se le van a asignar al vector.
     */
    public void set(Vector v) {
        if (v == null) {
        	return;
        }
        
        set(v.x, v.y, v.z);
    }

    
    /**
     * Método para indicar unos nuevos valores para los elementos del vector.
     * @param array Variable que contiene los valores que se le van a asignar al vector.
     */
    public void set(float[] array) {
        if (array == null || array.length != 3) {
        	throw new IllegalArgumentException("get() array must be non-NULL and size of 3");
        }
        
        set(array[0], array[1], array[2]);
    }

    
    /**
     * Método para indicar unos nuevos valores para los elementos del vector.
     * @param toX Contiene el valor x del vector
     * @param toY Contiene el valor y del vector
     * @param toZ Contiene el valor z del vector
     */
	public synchronized void set(float toX, float toY, float toZ) {
		x = toX;
		y = toY;
		z = toZ;
	}
	
	@Override
	public synchronized boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		Vector v = (Vector) obj;
		
		return (v.x == x && v.y == y && v.z == z);
	}
	
	
	/**
	 * Método para sumar un vector a este vector
	 * @param sumToX Contiene el valor x del vector a sumar
	 * @param sumToY Contiene el valor x del vector a sumar
	 * @param sumToZ Contiene el valor x del vector a sumar
	 */
	public synchronized void add(float sumToX, float sumToY, float sumToZ) {
		x += sumToX;
		y += sumToY;
		z += sumToZ;
	}

	
	/**
	 * Método para sumar un vector a este vector
	 * @param v Contiene el vector a sumar
	 */
	public void add(Vector v) {
		if (v == null) {
			return;
		}
		
		add(v.x, v.y, v.z);
	}

	
	/**
	 * Método para restar un vector a este vector
	 * @param v Contiene el vector a restar
	 */
	public void sub(Vector v) {
		if (v == null) {
			return;
		}
		
		add(-v.x, -v.y, -v.z);
	}

	
	/**
	 * Método para multiplicar por un número escalar a este vector
	 * @param s Contiene el número escalar por el que se va a multiplicar el vector
	 */
	public synchronized void mult(float s) {
	    x *= s;
	    y *= s;
	    z *= s;
	}

	
	/**
	 * Método para dividir por un número escalar a este vector
	 * @param s Contiene el número escalar por el que se va a dividir
	 */
	public synchronized void divide(float s) {
	    x /= s;
	    y /= s;
	    z /= s;
	}

	
	/**
	 * Calcula la longitud del vector
	 * @return Valor de la longitud del vector
	 */
	public synchronized float length() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	
	/**
	 * Calcula el vector unitario correspondiente al vector
	 */
	public void norm() {
		divide(length());
	}

	
	/**
	 * Calcula el producto vectorial de dos vectores y lo asigna a este vector
	 * @param u Vector que multiplicará vectorialmente a v
	 * @param v Vector que multiplicará vectorialmente a u
	 */
	public synchronized void cross(Vector u, Vector v) {
		if (v == null || u == null) {
			return;
		}
		
		x = u.y * v.z - u.z * v.y;
		y = u.z * v.x - u.x * v.z;
		z = u.x * v.y - u.y * v.x;
		
	}

	
	/**
	 * Calcula el producto del vector por una matriz
	 * @param m Matriz con la que se multiplica el vector
	 */
	public synchronized void prod(Matrix m) {
		if (m == null) {
			return;
		}

		m.get(matrixArray);
        float xTemp = matrixArray[0] * x + matrixArray[1] * y + matrixArray[2] * z;
        float yTemp = matrixArray[3] * x + matrixArray[4] * y + matrixArray[5] * z;
        float zTemp = matrixArray[6] * x + matrixArray[7] * y + matrixArray[8] * z;

		x = xTemp;
		y = yTemp;
		z = zTemp;
	}

	@Override
	public synchronized String toString() {
		return "x = " + this.x + ", y = " + this.y + ", z = " + this.z;
	}

}
