package com.jmlb0003.prueba3.modelo.sync.Excepciones;


public class NDPConectionException extends Exception {
    /**
	 * //Serial id para la excepción...
	 */
	private static final long serialVersionUID = -6659430959001738949L;
	private static String mErrorDescription;

	public NDPConectionException(String description) {
        super();//Constructor por defecto de Excepcion
        mErrorDescription = description;
    }
    
    @Override
    public String getMessage(){
        return ("ERROR:" + mErrorDescription);
//        		+ "Ha ocurrido un error al conectar con el servidor.");
    }
}
