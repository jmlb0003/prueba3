package com.jmlb0003.prueba3.utilidades.Paintables;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;


/**
 * Clase abstracta con las operaciones base para dibujar en la pantalla del dispositivo elementos 
 * de la interfaz de la aplicación.
 * @author Jose
 *
 */
public abstract class PaintableObject {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    
    public PaintableObject() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setTextSize(16);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract void paint(Canvas canvas);

    
    /**
     * Indica si el relleno del objeto es de tipo fill o stroke
     * @param fill Tipo de fill (FILL o STROKE)
     */
    public void setFill(boolean fill) {
        if (fill) {
        	mPaint.setStyle(Paint.Style.FILL);
        }else{
        	mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    public void setColor(int c) {
        mPaint.setColor(c);
    }

    public void setStrokeWidth(float w) {
        mPaint.setStrokeWidth(w);
    }

    public float getTextWidth(String txt) {
    	if (txt == null) {
    		throw new NullPointerException();
    	}
    	
        return mPaint.measureText(txt);
    }

    public float getTextAsc() {
        return -mPaint.ascent();
    }

    public float getTextDesc() {
        return mPaint.descent();
    }

    public void setFontSize(float size) {
        mPaint.setTextSize(size);
    }

    public void paintLine(Canvas canvas, float x1, float y1, float x2, float y2) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        canvas.drawLine(x1, y1, x2, y2, mPaint);
    }

    public void paintRect(Canvas canvas, float x, float y, float width, float height) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        canvas.drawRect(x, y, x + width, y + height, mPaint);
    }

    public void paintRoundedRect(Canvas canvas, float x, float y, float width, float height) {
        if (canvas == null) {
        	throw new NullPointerException();
        }

        RectF rect = new RectF(x, y, x + width, y + height);
        canvas.drawRoundRect(rect, 15F, 15F, mPaint);
    }

    public void paintBitmap(Canvas canvas, Bitmap bitmap, Rect src, Rect dst) {
        if (canvas == null || bitmap == null) {
        	throw new NullPointerException();
        }
        
        canvas.drawBitmap(bitmap, src, dst, mPaint);
    }
 
    public void paintBitmap(Canvas canvas, Bitmap bitmap, float left, float top) {
    	if (canvas == null || bitmap == null) {
    		throw new NullPointerException();
    	}
    	
        canvas.drawBitmap(bitmap, left, top, mPaint);
    }
    
    public void paintCircle(Canvas canvas, float x, float y, float radius) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        canvas.drawCircle(x, y, radius, mPaint);
    }

    public void paintText(Canvas canvas, float x, float y, String text) {
    	if (canvas == null || text == null) {
    		throw new NullPointerException();
    	}
    	
        canvas.drawText(text, x, y, mPaint);
    }

    public void paintObj(	Canvas canvas, PaintableObject obj, 
    						float x, float y, 
    						float rotation, float scale) {
    	if (canvas == null || obj == null) {
    		throw new NullPointerException();
    	}
    	
        canvas.save();
        canvas.translate(x+obj.getWidth()/2, y+obj.getHeight()/2);
        canvas.rotate(rotation);
        canvas.scale(scale,scale);
        canvas.translate(-(obj.getWidth()/2), -(obj.getHeight()/2));
        obj.paint(canvas);
        canvas.restore();
    }

    public void paintPath(	Canvas canvas, Path path, 
    						float x, float y, float width, 
    						float height, float rotation, float scale) {
    	if (canvas == null || path == null) {
    		throw new NullPointerException();
    	}
    	
    	canvas.save();
        canvas.translate(x + width / 2, y + height / 2);
        canvas.rotate(rotation);
        canvas.scale(scale, scale);
        canvas.translate(-(width / 2), -(height / 2));
        canvas.drawPath(path, mPaint);
        canvas.restore();
    }
}