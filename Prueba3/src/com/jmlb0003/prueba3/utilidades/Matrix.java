package com.jmlb0003.prueba3.utilidades;


/**
 * Clase de apoyo para las operaciones con matrices en los cálculos de posiciones y rotaciones
 * @author Jose
 */
public class Matrix {
    private static final Matrix tmp = new Matrix();

    private volatile float a1=0f, a2=0f, a3=0f;
    private volatile float b1=0f, b2=0f, b3=0f;
    private volatile float c1=0f, c2=0f, c3=0f;
    
    /*
     * Constructor por defecto. Los elementos de la matriz se identifican con números en las 
     * columnas y con letras en las filas.
     */
    public Matrix() { }

    public synchronized float getA1() {
        return a1;
    }
    public synchronized void setA1(float toA1) {
        a1 = toA1;
    }

    public synchronized float getA2() {
        return a2;
    }
    public synchronized void setA2(float toA2) {
        a2 = toA2;
    }

    public synchronized float getA3() {
        return a3;
    }
    public synchronized void setA3(float toA3) {
        a3 = toA3;
    }

    public synchronized float getB1() {
        return b1;
    }
    public synchronized void setB1(float toB1) {
        b1 = toB1;
    }

    public synchronized float getB2() {
        return b2;
    }
    public synchronized void setB2(float toB2) {
        b2 = toB2;
    }

    public synchronized float getB3() {
        return b3;
    }
    public synchronized void setB3(float toB3) {
        b3 = toB3;
    }

    public synchronized float getC1() {
        return c1;
    }
    public synchronized void setC1(float toC1) {
        c1 = toC1;
    }

    public synchronized float getC2() {
        return c2;
    }
    public synchronized void setC2(float toC2) {
        c2 = toC2;
    }

    public synchronized float getC3() {
        return c3;
    }
    public synchronized void setC3(float toC3) {
        c3 = toC3;
    }

    /**
     * Devuelve los elementos de la matriz en un array.
     * @param array Variable donde se almacenan los valores de la matriz.
     */
    public synchronized void get(float[] array) {
        if (array == null || array.length != 9) {
        	throw new IllegalArgumentException("get() array must be non-NULL and size of 9");
        }
        
        array[0] = a1;
        array[1] = a2;
        array[2] = a3;

        array[3] = b1;
        array[4] = b2;
        array[5] = b3;

        array[6] = c1;
        array[7] = c2;
        array[8] = c3;
    }

    
    /**
     * Método para indicar unos nuevos valores para los elementos de la matriz.
     * @param m Variable que contiene los valores que se le van a asignar la matriz.
     */
    public void set(Matrix m) {
        if (m == null) {
        	throw new NullPointerException();
        }

        set(m.a1,m. a2, m.a3, m.b1, m.b2, m.b3, m.c1, m.c2, m.c3);
    }
    
    
    /**
     * Método para indicar unos nuevos valores para los elementos de la matriz.
     * @param a1 Contiene el valor para ese elemento de la matriz.
     * @param a2 Contiene el valor para ese elemento de la matriz.
     * @param a3 Contiene el valor para ese elemento de la matriz.
     * @param b1 Contiene el valor para ese elemento de la matriz.
     * @param b2 Contiene el valor para ese elemento de la matriz.
     * @param b3 Contiene el valor para ese elemento de la matriz.
     * @param c1 Contiene el valor para ese elemento de la matriz.
     * @param c2 Contiene el valor para ese elemento de la matriz.
     * @param c3 Contiene el valor para ese elemento de la matriz.
     */
    public synchronized void set(float toA1, float toA2, float toA3, 
    							float toB1, float toB2, float toB3, 
    							float toC1, float toC2, float toC3) {
        a1 = toA1;
        a2 = toA2;
        a3 = toA3;

        b1 = toB1;
        b2 = toB2;
        b3 = toB3;

        c1 = toC1;
        c2 = toC2;
        c3 = toC3;
    }

    /**
     * Convierte la matriz en la matriz Identidad (toda a cero excepto la diagonal principal).
     */
    public void toIdentity() {
        set(1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    
    /**
     * Calcula la matriz adjunta de esta matriz.
     */
    public synchronized void adj() {
        float a11 = a1;
        float a12 = a2;
        float a13 = a3;

        float a21 = b1;
        float a22 = b2;
        float a23 = b3;

        float a31 = c1;
        float a32 = c2;
        float a33 = c3;

        a1 = det2x2(a22, a23, a32, a33);
        a2 = det2x2(a13, a12, a33, a32);
        a3 = det2x2(a12, a13, a22, a23);

        b1 = det2x2(a23, a21, a33, a31);
        b2 = det2x2(a11, a13, a31, a33);
        b3 = det2x2(a13, a11, a23, a21);

        c1 = det2x2(a21, a22, a31, a32);
        c2 = det2x2(a12, a11, a32, a31);
        c3 = det2x2(a11, a12, a21, a22);
    }

    
    /**
     * Calcula la matriz inversa de esta matriz.
     */
    public void invert() {
        float det = this.det();

        adj();
        mult(1 / det);
    }

    
    /**
     * Calcula la matriz transpuesta de esta matriz.
     */
    public synchronized void transpose() {    
        float a11 = a1;
        float a12 = a2;
        float a13 = a3;

        float a21 = b1;
        float a22 = b2;
        float a23 = b3;

        float a31 = c1;
        float a32 = c2;
        float a33 = c3;

        b1 = a12;
        a2 = a21;
        b3 = a32;
        c2 = a23;
        c1 = a13;
        a3 = a31;

        a1 = a11;
        b2 = a22;
        c3 = a33;
    }

    
    /**
     * Calcula el determinante de una matriz de dimension 2x2
     * @param a Contiene el valor para el elemento de la matriz 11 (fila arriba, columna izquierda).
     * @param b Contiene el valor para el elemento de la matriz 12 (fila arriba, columna derecha).
     * @param c Contiene el valor para el elemento de la matriz 21 (fila abajo, columna izquierda).
     * @param d Contiene el valor para el elemento de la matriz 22 (fila abajo, columna derecha).
     * @return Valor del resultado del cálculo del determinante
     */
    private float det2x2(float a, float b, float c, float d) {
        return (a * d) - (b * c);
    }

    
    /**
     * Calcula el determinante de la matriz de dimension 3x3
     * @return Valor del resultado del cálculo del determinante
     */
    public synchronized float det() {
        return (a1*b2*c3) - (a1*b3*c2) - (a2*b1*c3) + (a2*b3*c1) + (a3*b1*c2) - (a3*b2*c1);
    }

    
    /**
     * Multiplica la matriz por un valor
     * @param c Valor por el que se multiplicará la matriz
     */
    public synchronized void mult(float c) {
        a1 = a1 * c;
        a2 = a2 * c;
        a3 = a3 * c;

        b1 = b1 * c;
        b2 = b2 * c;
        b3 = b3 * c;

        c1 = c1 * c;
        c2 = c2 * c;
        c3 = c3 * c;
    }

    
    /**
     * Calcula el producto de una matriz por otra matriz.
     * @param n Es la otra matriz por la que se multiplicará esta matriz.
     */
    public synchronized void prod(Matrix n) {
        if (n == null) {
        	throw new NullPointerException();
        }

        tmp.set(this);
        a1 = (tmp.a1 * n.a1) + (tmp.a2 * n.b1) + (tmp.a3 * n.c1);
        a2 = (tmp.a1 * n.a2) + (tmp.a2 * n.b2) + (tmp.a3 * n.c2);
        a3 = (tmp.a1 * n.a3) + (tmp.a2 * n.b3) + (tmp.a3 * n.c3);

        b1 = (tmp.b1 * n.a1) + (tmp.b2 * n.b1) + (tmp.b3 * n.c1);
        b2 = (tmp.b1 * n.a2) + (tmp.b2 * n.b2) + (tmp.b3 * n.c2);
        b3 = (tmp.b1 * n.a3) + (tmp.b2 * n.b3) + (tmp.b3 * n.c3);

        c1 = (tmp.c1 * n.a1) + (tmp.c2 * n.b1) + (tmp.c3 * n.c1);
        c2 = (tmp.c1 * n.a2) + (tmp.c2 * n.b2) + (tmp.c3 * n.c2);
        c3 = (tmp.c1 * n.a3) + (tmp.c2 * n.b3) + (tmp.c3 * n.c3);
    }

    @Override
    public synchronized String toString() {
        return "(" + a1 + "," + a2 + "," + a3 + ")"+
               " (" + b1 + "," + b2 + "," + b3 + ")"+
               " (" + c1 + "," + c2 + "," + c3 + ")";
    }
}