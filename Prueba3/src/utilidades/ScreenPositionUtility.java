package utilidades;


/**
 * Esta clase se utiliza para poder dibujar las líneas del radar en el modo de Realidad Aumentada
 * @author Jose
 *
 */
public class ScreenPositionUtility {
    private float x = 0f;
    private float y = 0f;

	public ScreenPositionUtility() {
        set(0, 0);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	
	/**
	 * Rota las coordenadas X e Y un ángulo t
	 * @param t Ángulo que se va a aplicar a las coordenadas
	 */
    public void rotate(double t) {
        float xp = (float) Math.cos(t) * x - (float) Math.sin(t) * y;
        float yp = (float) Math.sin(t) * x + (float) Math.cos(t) * y;

        x = xp;
        y = yp;
    }

    
    /**
     * Suma los valores X e Y a las coordenadas X e Y respectivamente
     * @param x Valor a sumar a la coordenada X
     * @param y Valor a sumar a la coordenada Y
     */
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public String toString() {
        return "x="+x+" y="+y;
    }
}
