package com.jmlb0003.prueba3.vista;

import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jmlb0003.prueba3.utilidades.Compatibility;


/**
 * //TODO:Camera está deprecated...a cambiarla a camera2...
 * https://android.googlesource.com/platform/frameworks/base/+/227b47625d7482b5b47ad0e4c70ce0a246236ade/tests/Camera2Tests/SmartCamera/SimpleCamera/src/androidx/media/filterfw/samples/simplecamera/Camera2Source.java
 * Clase que gestiona la vista que muestra las imágenes capturadas por la cámara del dispositivo.
 * 
 * @author Jose
 *
 */
public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
	    private Camera mCamera;
	    private static final String LOG_TAG = "CameraSurface";
	    
	    /**
	     * Constructor del preview de la cámara para usarlo de forma dinámica.
	     * @param context
	     */
		@SuppressWarnings("deprecation")
		public CameraSurface(Context context) {
			super(context);

			try {
				//Se añade un observador del surface para saber cuando se crea y
				//se destruye
				mHolder = getHolder();
				mHolder.addCallback(this);
				//Esta línea es requerida en versiones anteriores a la 3
				mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			} catch (Exception ex) {

			}
		}
		
		
		/**
	     * Constructor del preview de la cámara insertado en un layout XML.
	     * @param context
	     * @param attrs
	     */
		@SuppressWarnings("deprecation")
		public CameraSurface(Context context, AttributeSet attrs) {
			super(context, attrs);

			try {
				//Se añade un observador del surface para saber cuando se crea y
				//se destruye
				mHolder = getHolder();
				mHolder.addCallback(this);
				//Esta línea es requerida en versiones anteriores a la 3
				mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			} catch (Exception ex) {

			}
		}
	    
		
		/**
		 * La vista con las imágenes de la cámara se crea por primera vez
		 */
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(LOG_TAG, "LLamada a surfaceCreated");
			try {
				if (mCamera != null) {
					try {
						mCamera.stopPreview();
					} catch (Exception ignore) {
					}
					try {
						mCamera.release();
					} catch (Exception ignore) {
					}
					mCamera = null;
				}

				//La primera vez que se ejecuta este código no está inicializado 
				//el fragmento por lo que se ejecutará el código dentro del catch
				mCamera = Camera.open();
				mCamera.setPreviewDisplay(holder);
			} catch (Exception ex) {
				try {
					if (mCamera != null) {
						try {
							mCamera.stopPreview();
						} catch (Exception ignore) {
						}
						try {
							mCamera.release();
						} catch (Exception ignore) {
						}
						mCamera = null;
					}
				} catch (Exception ignore) {

				}
			}
		}
		
		
		/**
		 * La vista de las imágenes de la cámara se destruye al salir de la vista en modo
		 * cámara
		 */
		public void surfaceDestroyed(SurfaceHolder arg0) {
			if (mCamera != null) {
				mCamera.cancelAutoFocus();
				mCamera.stopPreview();	
				mCamera.release();
				mCamera = null;
	    	}
		}
		
		
		/**
		 * Cada vez que se muestra la vista en modo cámara se llama a este método que calculan
		 * el ancho y alto de la vista y distintos parámetros para obtener las imágenes con la 
		 * mejor resolución posible
		 */
		@SuppressLint("InlinedApi")
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			// If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.
			Log.d(LOG_TAG, "LLamada a surfaceChanged");
			if (mCamera == null) {
				return;
			}
			
			try {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.set("orientation","portrait");
				Log.i(LOG_TAG, "2");
				try {
					List<Camera.Size> supportedSizes = null;
					// On older devices (<1.6) the following will fail
					// the camera will work nevertheless
					supportedSizes = Compatibility.getSupportedPreviewSizes(parameters);

					Log.i(LOG_TAG, "3");
					
					// preview form factor
					float ff = (float) w / h;
					Log.d(LOG_TAG, "Screen res: w:" + w + " h:" + h
							+ " aspect ratio:" + ff);

					// holder for the best form factor and size
					float bff = 0;
					int bestw = 0;
					int besth = 0;
					Iterator<Camera.Size> itr = supportedSizes.iterator();

					while (itr.hasNext()) {
						Camera.Size element = itr.next();
						// current form factor
						float cff = (float) element.width / element.height;

						Log.d(LOG_TAG, "Candidate camera element: w:" + element.width + " h:" + element.height + " aspect ratio:" + cff);
						if ((ff - cff <= ff - bff) && (element.width <= w)
								&& (element.width >= bestw)) {
							bff = cff;
							bestw = element.width;
							besth = element.height;
						}
					}
					Log.d(LOG_TAG, "Chosen camera element: w:" + bestw + " h:"
							+ besth + " aspect ratio:" + bff);
					// Some Samsung phones will end up with bestw and besth = 0
					// because their minimum preview size is bigger then the screen
					// size. In this case, we use the default values: 480x320
					if ((bestw == 0) || (besth == 0)) {
						Log.d(LOG_TAG, "Using default camera parameters!");
						bestw = 480;
						besth = 320;
					}
					parameters.setPreviewSize(bestw, besth);
				} catch (Exception ex) {
					parameters.setPreviewSize(480, 320);
				}
				
				
				//Elige el tipo de auto-enfoque
				if(Build.VERSION.SDK_INT < 14 ) {
					parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		        } else {
		        	parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		        }

				mCamera.setParameters(parameters);
				mCamera.setDisplayOrientation(90);
				
				
				mCamera.startPreview();
				//Se retrasa el autoenfoque 200 milisegundos para que tenga tiempo de ejecutarse el starPreview
				final android.os.Handler handler = new android.os.Handler();
			    handler.postDelayed(new Runnable() {
			        @Override
			        public void run() {
			        	mCamera.autoFocus(myAutoFocusCallback);		//Inicia el auto-enfoque
			        }
			    }, 200);
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}			
		}		
		
		
		/**
		 * Interfaz necesaria para que funcione el auto-focus
		 */
		AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

			  @Override
			  public void onAutoFocus(boolean arg0, Camera arg1) { }
		};
		
	}





/* 
public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
    android.hardware.Camera.CameraInfo info =
            new android.hardware.Camera.CameraInfo();
    android.hardware.Camera.getCameraInfo(cameraId, info);
    int rotation = activity.getWindowManager().getDefaultDisplay()
            .getRotation();
    int degrees = 0;
    switch (rotation) {
        case Surface.ROTATION_0: degrees = 0; break;
        case Surface.ROTATION_90: degrees = 90; break;
        case Surface.ROTATION_180: degrees = 180; break;
        case Surface.ROTATION_270: degrees = 270; break;
    }

    int result;
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360;  // compensate the mirror
    } else {  // back-facing
        result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);
}*/