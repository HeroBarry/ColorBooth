package com.colorbooth;

import com.colorbooth.R;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ActivityColoringPage extends ActivityBase implements View.OnTouchListener {

	Bitmap alteredBitmap; 
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ImageView img = (ImageView) findViewById(R.id.iv_coloring_figure);
		BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
		Bitmap bmp = drawable.getBitmap();
		alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(alteredBitmap);
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(5);
		Matrix matrix = new Matrix();
		canvas.drawBitmap(bmp, matrix, paint);

		img.setImageBitmap(alteredBitmap);
		img.setOnTouchListener(this);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		
		ImageView img = (ImageView) findViewById(R.id.iv_coloring_figure);
		img.setImageBitmap(alteredBitmap);
		img.setOnTouchListener(this);
	}	

	public boolean onTouch(View paramView, MotionEvent event) {

		int action = event.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			ImageView img = (ImageView) findViewById(R.id.iv_coloring_figure);
			BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			Rect imageBounds = new Rect(); img.getDrawingRect(imageBounds);

			//original height and width of the bitmap
			int intrinsicHeight = drawable.getIntrinsicHeight();
			int intrinsicWidth = drawable.getIntrinsicWidth();

			//height and width of the visible (scaled) image
			int scaledHeight = imageBounds.height();
			int scaledWidth = imageBounds.width();

			//Find the ratio of the original image to the scaled image
			//Should normally be equal unless a disproportionate scaling
			//(e.g. fitXY) is used.
			float heightRatio = (float)intrinsicHeight / scaledHeight;
			float widthRatio = (float)intrinsicWidth / scaledWidth;

			//do whatever magic to get your touch point
			//MotionEvent event;

			//get the distance from the left and top of the image bounds
			float scaledImageOffsetX = event.getX() - imageBounds.left;
			float scaledImageOffsetY = event.getY() - imageBounds.top;

			//scale these distances according to the ratio of your scaling
			//For example, if the original image is 1.5x the size of the scaled
			//image, and your offset is (10, 20), your original image offset
			//values should be (15, 30). 
			int originalImageOffsetX = Math.round(scaledImageOffsetX * widthRatio);
			int originalImageOffsetY = Math.round(scaledImageOffsetY * heightRatio);
			
			int color = bitmap.getPixel(originalImageOffsetX, originalImageOffsetY);
			
			if (this.rybColorMap.containsKey(color))
			{
				RybColorSmartCode colorCode = this.rybColorMap.get(color).copy();
				MixColors(colorCode);
				int argb = RybToArgb(colorCode);
				if (color != argb)
				{
					FloodFill floodfill = new FloodFill(bitmap, color,
							argb);
					floodfill.Fill(originalImageOffsetX, originalImageOffsetY);
					img.invalidate();
				}
			}
			break;
		}

		return false;
	}
}
