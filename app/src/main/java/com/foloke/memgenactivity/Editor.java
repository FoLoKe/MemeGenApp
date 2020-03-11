package com.foloke.memgenactivity;
import android.view.*;
import android.view.InputQueue.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import android.view.ScaleGestureDetector.*;
import java.util.*;
import java.io.*;
import android.os.*;

public class Editor extends SurfaceView implements SurfaceHolder.Callback
{
	Camera camera;
	RectF canvasRegion;
	Paint canvasColor;
	public EditorThread thread;
	List<Layer> layers;
	
	boolean gestureInProgress = false;
	
	Context context;
	
	public Editor(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.context = context;
		
		camera = new Camera();
		canvasRegion = new RectF(0, 0, 320, 320);
		canvasColor = new Paint();
		canvasColor.setColor(Color.WHITE);
		thread = new EditorThread(this); 
		layers = new ArrayList<>();
		
		getHolder().addCallback(this);
        setFocusable(false);
		
		thread.start();
	}
	
	public Layer newLayer() {
		Layer layer = new Layer("test") ;
		layers.add(layer);
		
		return layer;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder p1)
	{
		// TODO: Implement this method
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder p1)
	{
		// TODO: Implement this method
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
	{
		// TODO: Implement this method
		
	}

	
	public void render(Canvas canvas)
	{
		// TODO: Implement this method
		
		canvas.save();
		canvas.translate(camera.x, camera.y);
		canvas.scale(camera.zoom, camera.zoom);
		canvas.drawColor(Color.GRAY);
		canvas.drawRect(canvasRegion, canvasColor);
		
		Iterator<Layer> iterator = layers.iterator();
		while(iterator.hasNext()) {
			Layer layer = iterator.next();
			layer.render(canvas);
		}
		canvas.restore();
		
		//super.draw(canvas);
	}
	
	private class Camera {
		public float x, y;
		public float zoom;
		
		public Camera() {
			x = 0;
			y = 0;
			zoom = 1;
		}
	}
	
	private class EditorThread extends Thread
	{
		public boolean running = true;
		SurfaceHolder surfaceHolder;
		Editor editor;
		
		public EditorThread(Editor editor) {
			this.surfaceHolder = editor.getHolder();
			this.editor = editor;
		}
		
		@Override
		public void run()
		{
			// TODO: Implement this method
			super.run();
			long waitTime;
			long timeMillis;
			long startTime;
			long targetTime = 1000/30;
			Canvas canvas;
			
			while(running) {
				canvas = null;
				startTime = System.nanoTime();
				
				try {
					canvas = surfaceHolder.lockCanvas();
					synchronized(surfaceHolder) {
						if(canvas != null) {
							editor.render(canvas);
						}
					}
				} catch (Exception e) {
					 System.out.println(e);
				} finally {
					if(canvas!=null) {
						try {
							surfaceHolder.unlockCanvasAndPost(canvas);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
				
				timeMillis=(System.nanoTime()-startTime)/1000000;
				waitTime=targetTime-timeMillis;

				try
				{
					if(waitTime>0)
						sleep(waitTime);
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
			}
		}
		
	}

	private SimpleOnScaleGestureListener gestureListener = new SimpleOnScaleGestureListener()
	{
    	@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			pressed=false;
			gestureInProgress = true;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			if(selectedLayer  == null) {
				camera.zoom *= detector.getScaleFactor();
				// Don't let the object get too small or too large.
				camera.zoom = Math.max(0.5f, Math.min(camera.zoom, 4.0f));
			} else {
				selectedLayer.scale *= detector.getScaleFactor();
				selectedLayer.scale = Math.max(0.2f, Math.min(selectedLayer.scale, 10.0f));
			}
			
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			super.onScaleEnd(detector);
			gestureInProgress=false;
		}	


	};
	
    private ScaleGestureDetector gestureDetector = new ScaleGestureDetector(getContext(), gestureListener);
	
	boolean pressed;
	PointF pressPoint = new PointF();
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		gestureDetector.onTouchEvent(event);
			
		if(!gestureInProgress) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					pressed = true;
					break;
				case MotionEvent.ACTION_MOVE:
					if(pressed) {
						if(selectedLayer == null) {
							camera.x += event.getX() - pressPoint.x;
							camera.y += event.getY() - pressPoint.y;
						} else {
							selectedLayer.x += (event.getX() - pressPoint.x) / (selectedLayer.scale + camera.zoom);
							selectedLayer.y += (event.getY() - pressPoint.y) / (selectedLayer.scale + camera.zoom);
						}
					} else {
						pressed = true;
					}
					break;
					
				case MotionEvent.ACTION_UP:
					pressed = false;
					break;
			}
		}
		
		pressPoint.set(event.getX(), event.getY());
		return true;
	}
	
	public static class Layer {
		public String text;
		public float x, y;
		public float scale;
		public Paint textPaint;
		public Bitmap bitmap;
		
		public Layer(String text) {
			this.text = text;
			scale = 1;
			textPaint = new Paint();
			textPaint.setColor(Color.BLACK);
		}
		
		public void render(Canvas canvas) {
			canvas.save();
			canvas.scale(scale, scale);
			
			if (bitmap == null) {
				canvas.drawText(text, x, y, textPaint);
			} else {
				canvas.drawBitmap(bitmap, x, y, null);
			}
			
			canvas.restore();
			
		}
	}

	public void save() {
		try {
		Bitmap toDisk = getBitmap();
		File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
		toDisk.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(dir, "meme.jpg")));
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public Bitmap getBitmap() {
		Bitmap bitmap = null;
		try {

			thread.running = false;
			thread.join();
			Canvas c = getHolder().lockCanvas();
			synchronized(getHolder()) {
				
				bitmap = Bitmap.createBitmap((int)canvasRegion.width(),(int)canvasRegion.height(),Bitmap.Config.ARGB_8888);
				c.setBitmap(bitmap);
				c.drawRect(canvasRegion, canvasColor);

				Iterator<Layer> iterator = layers.iterator();
				while(iterator.hasNext()) {
					Layer layer = iterator.next();
					layer.render(c);
				}
				c.setBitmap(null);
				
				getHolder().unlockCanvasAndPost(c);
			}
			thread.running = true;
			thread.start();
		} catch (Exception e) {
			System.out.println(e);
		}
		return bitmap;
	}

	public void resume() {
		if(!thread.running) {
			thread.running = true;
			thread.start();
		}
	}
	
	public Layer selectedLayer;
	
	public void selectLayer(Layer layer){
		selectedLayer = layer;
	}
	
}
