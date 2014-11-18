package com.jmlb0003.prueba3.modelo;

import java.util.Map;



/**
 * Detalles de un Punto de Interés.
 * Son los campos que contienen la informacion del PI. En lugar de tener una variable de 
 * instancia por cada uno de los campos, lo que da lugar a un codigo mas acoplado, se tiene 
 * un mapa, lo que permite añadir nuevos campos con mayor facilidad.
 * @author Jose
 *
 */
public class DetallesPI {
	
	/* - Atributos --------------------------------------------------------- */
    public static final String DETALLESPI_ID_POI = "ID";
    public static final String DETALLESPI_USER_ID = "id_usuario";
    public static final String DETALLESPI_NAME = "nombre_poi";
    public static final String DETALLESPI_LATITUDE = "latitud";
    public static final String DETALLESPI_LONGITUDE = "longitud";
    public static final String DETALLESPI_ALTITUDE = "altitud";
    public static final String DETALLESPI_COLOR = "color";
    public static final String DETALLESPI_DISTANCE = "distancia";
    public static final String DETALLESPI_IMAGE = "imagen";
    public static final String DETALLESPI_ICON = "icono";
    public static final String DETALLESPI_SELECTED_ICON = "icono_seleccionado";    
    public static final String DETALLESPI_DESCRIPTION = "descripcion";
    public static final String DETALLESPI_WEBSITE = "sitio_web";
    public static final String DETALLESPI_PRICE = "precio";
    public static final String DETALLESPI_OPEN_HOURS = "horario_apertura";
    public static final String DETALLESPI_CLOSE_HOURS = "horario_cierre";
    public static final String DETALLESPI_MAX_AGE = "edad_maxima";
    public static final String DETALLESPI_MIN_AGE = "edad_minima";
    
	/**
     * Contiene los detalles de un PI incluyendo pares: <nombre_del_campo, valor_del_campo>:<br>
     * <tt>"ID"</tt>: ID del PI'/'<br>
     * <tt>"id_usuario"</tt>: ID del usuario autor del PI'/'<br>
     * <tt>"color"</tt>: Color del tipo de PI<br>
     * <tt>"distancia"</tt>: Distancia desde el usuario hasta el PI<br>
     * <tt>"imagen"</tt>: Imagen del PI<br>
     * <tt>"descripcion"</tt>: Descripcion del PI<br>
     * <tt>"sitio_web"</tt>: Dirección del sitio web del PI si existe<br>
     * <tt>"precio"</tt>: Precio del PI si existe<br>
     * <tt>"horario_apertura"</tt>: Horario de apertura del PI si existe '00:00'<br>
     * <tt>"horario_cierre"</tt>: Horario de cierre del PI si existe '00:00'<br>
     * <tt>"edad_maxima"</tt>: Edad máxima de acceso al PI si existe<br>
     * <tt>"edad_minima"</tt>: Edad mínima de acceso al PI si existe<br>
     */
    private Map<String,Object> mDetalles;

    /* - Metodos ----------------------------------------------------------- */

    /**
     * Constructor de la clase
     * @param detalles Mapa con los valores del nuevo conjunto de atributos
     */
    public DetallesPI(Map<String,Object> detalles) {
        
    	mDetalles = detalles;
    }
    
    
    public Map<String,Object> obtenerDetalles() {
        return mDetalles;
    }

    public void setDetalles(Map<String,Object> detalles) {
        mDetalles = detalles;
    }
    
    
    /**
     * Consultor.
     * @param nombre Nombre del campo del PI que se quiere consultar.
     * @return el valor del campo del libro cuyo nombre se pasa como argumento.
     */
    public Object getDetalle(String nombre) {

        return mDetalles.get(nombre);
    }
    
    
    /**
     * Método para actualizar el texto que contiene la distancia del PI al usuario.
     * @param txt Cadena de texto que contiene la distancia y la unidad métrica (puede ser 
     * distancia en metros o en kilómetros)
     */
    public void actualizaDistancia(String txt) {
    	mDetalles.put(DETALLESPI_DISTANCE, txt);
    }
    
    
    /**
     * Comparador.
     * @param criteriosBusqueda Criterios de busqueda para comparar.
     * @return <tt>true</tt> si los detalles del libro coinciden con los criterios
     * de busqueda especificados y <tt>false</tt> en caso contrario.
     */
    public boolean coincide(DetallesPI criteriosBusqueda) {

    	//TODO: El coincide del DetallesPI está sin implementar
        /**
         * Si es una busqueda de la interfaz, mira primero si el filtro (prestado)
         * es verdadero y despues por ISBN, titulo y autor
         * si no, mira todos los campos.

        if (criteriosBusqueda.obtenerDetalles().containsKey("texto")) {

            //Si prestado es verdadero se compara, si no se pasa
            if ( criteriosBusqueda.obtenerDetalles().containsKey("prestado") &&
                    criteriosBusqueda.obtenerDetalle("prestado").equals(true) ) {

                if ( !criteriosBusqueda.obtenerDetalle("prestado").equals(_detalles.get("prestado")) ) {

                    return false;
                }
            }


            if ( criteriosBusqueda.obtenerDetalle("texto") != null ) {

                //Si no coincide el texto con nada, devuelve false
                if ( !(_detalles.get("isbn").toString().contains(criteriosBusqueda.obtenerDetalle("texto").toString())) ) {
                    if ( !(_detalles.get("titulo").toString().contains(criteriosBusqueda.obtenerDetalle("texto").toString())) ) {
                        if ( !(_detalles.get("autor").toString().contains(criteriosBusqueda.obtenerDetalle("texto").toString())) ) {

                            return false;
                        }
                    }
                }
            }
        } else {    //Si es una comparacion de libros

            Iterator<String> itClaves = criteriosBusqueda.obtenerDetalles().keySet().iterator();

            while (itClaves.hasNext()) {
                String sigClave = itClaves.next();

                //Si criterios contiene un dato, se compara con el que hay en detalles
                //Si en detalles hay un dato nulo que tiene un valor en
                //criterios no coinciden los dos objetos
                if (criteriosBusqueda.obtenerDetalle(sigClave) != null &&
                        _detalles.get(sigClave) != null) {
                    if ( !(_detalles.get(sigClave).toString().contains(criteriosBusqueda.obtenerDetalle(sigClave).toString())) ) {

                        return false;
                    }
                } else {
                    if (criteriosBusqueda.obtenerDetalle(sigClave) != null &&
                            _detalles.get(sigClave) == null) {
                        return false;
                    }
                }
            }
        }

         */
        //Si todos los criterios de busqueda coinciden se devuelve true.
        return true;

    }
}
