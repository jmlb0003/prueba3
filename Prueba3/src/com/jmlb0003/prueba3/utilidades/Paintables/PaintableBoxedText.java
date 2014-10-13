package com.jmlb0003.prueba3.utilidades.Paintables;


import java.text.BreakIterator;
import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Color;


/**
 * Clase que permite dibujar un texto con un recuadro alrededor
 * @author Jose
 *
 */
public class PaintableBoxedText extends PaintableObject {
    private float mWidth = 0, mHeight = 0;
	private float mAreaWidth = 0, mAreaHeight = 0;
	private ArrayList<String> mLineList = null;
	private String[] mLines = null;
	private float[] mLineWidths = null;
	private float mLineHeight = 0;
	private float mMaxLineWidth = 0;
	private float mPad = 0;

	private String mTxt = null;
    private float mFontSize = 12;
	private int mBorderColor = Color.rgb(255, 255, 255);
	private int mBackgroundColor = Color.argb(160, 0, 0, 0);
	private int mTextColor = Color.rgb(255, 255, 255);

	public PaintableBoxedText(String txtInit, float fontSizeInit, float maxWidth) {
		this(txtInit, fontSizeInit, maxWidth, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0), Color.rgb(255, 255, 255));
	}

	public PaintableBoxedText(String txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor) {
		set(txtInit, fontSizeInit, maxWidth, borderColor, bgColor, textColor);
	}

	public void set(String txtInit, float fontSizeInit, float maxWidth, int borderColor, int bgColor, int textColor) {
		if (txtInit == null) {
			throw new NullPointerException();
		}
		
		mBorderColor = borderColor;
		mBackgroundColor = bgColor;
		mTextColor = textColor;
		mPad = getTextAsc();

		set(txtInit, fontSizeInit, maxWidth);
	}

	public void set(String txtInit, float fontSizeInit, float maxWidth) {
		if (txtInit == null) {
			throw new NullPointerException();
		}

		try {
			prepTxt(txtInit, fontSizeInit, maxWidth);
		} catch (Exception ex) {
			ex.printStackTrace();
			prepTxt("TEXT PARSE ERROR", 12, 200);
		}
	}
	
	
	/**
	 * //TODO: Esta funcion arregla el texto que se muestra debajo del marcador. Sería interesante modificarla al gusto...
	 * @param txtInit
	 * @param fontSizeInit
	 * @param maxWidth
	 */
	private void prepTxt(String txtInit, float fontSizeInit, float maxWidth) {
		if (txtInit == null) {
			throw new NullPointerException();
		}
		
		setFontSize(fontSizeInit);

		mTxt = txtInit;
		mFontSize = fontSizeInit;
		mAreaWidth = maxWidth - mPad;
		mLineHeight = getTextAsc() + getTextDesc();

		if (mLineList == null) {
			mLineList = new ArrayList<String>();
		}else{
			mLineList.clear();
		}

		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(mTxt);

		int start = boundary.first();
		int end = boundary.next();
		int prevEnd = start;
		while (end != BreakIterator.DONE) {
			String line = mTxt.substring(start, end);
			String prevLine = mTxt.substring(start, prevEnd);
			float lineWidth = getTextWidth(line);

			if (lineWidth > mAreaWidth) {
				if(prevLine.length()>0) mLineList.add(prevLine);

				start = prevEnd;
			}

			prevEnd = end;
			end = boundary.next();
		}
		String line = mTxt.substring(start, prevEnd);
		mLineList.add(line);

		if (mLines == null || mLines.length != mLineList.size()) {
			mLines = new String[mLineList.size()];
		}
		if (mLineWidths == null || mLineWidths.length != mLineList.size()) {
			mLineWidths = new float[mLineList.size()];
		}
		mLineList.toArray(mLines);

		mMaxLineWidth = 0;
		for (int i = 0; i < mLines.length; i++) {
			mLineWidths[i] = getTextWidth(mLines[i]);
			if (mMaxLineWidth < mLineWidths[i]) {
				mMaxLineWidth = mLineWidths[i];
			}
		}
		mAreaWidth = mMaxLineWidth;
		mAreaHeight = mLineHeight * mLines.length;

		mWidth = mAreaWidth + mPad * 2;
		mHeight = mAreaHeight + mPad * 2;
	}

	@Override
	public void paint(Canvas canvas) {
		if (canvas == null) {
			throw new NullPointerException();
		}
		
	    setFontSize(mFontSize);

		setFill(true);
		setColor(mBackgroundColor);
		paintRoundedRect(canvas, 0, 0, mWidth, mHeight);

		setFill(false);
		setColor(mBorderColor);
		paintRoundedRect(canvas, 0, 0, mWidth, mHeight);
		
		for (int i = 0; i < mLines.length; i++) {
			String line = mLines[i];
			setFill(true);
			setStrokeWidth(0);
			setColor(mTextColor);
			paintText(canvas, mPad, mPad + mLineHeight * i + getTextAsc(), line);
		}
	}

	@Override
	public float getWidth() {
		return mWidth;
	}

	@Override
	public float getHeight() {
		return mHeight;
	}
}
