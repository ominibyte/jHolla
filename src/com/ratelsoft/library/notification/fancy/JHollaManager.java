package com.ratelsoft.library.notification.fancy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

public class JHollaManager {
	private ArrayList<JHolla> notifs;
	private static JHollaManager manager;
	private static int CAPACITY = 5;
	private ExecutorService executor;
	private boolean isTranslucencySupported;
	private Dimension screenDimension;
	private int taskBarHeight, insetsRight, insetsLeft;
	private int position, width;
	private boolean sameWidths;
	public static enum Alignment{LEFT, RIGHT};
	public Alignment alignment;
	private boolean isBusy = false;
	private LinkedList<JHolla> queue;
	
	private JHollaManager(){
		notifs = new ArrayList<JHolla>();
		executor = Executors.newCachedThreadPool();
		sameWidths = true;
		alignment = Alignment.RIGHT;
		queue = new LinkedList<JHolla>();
		
		GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		isTranslucencySupported = d.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
		
		screenDimension = Toolkit.getDefaultToolkit().getScreenSize();		
		Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(d.getDefaultConfiguration());
		taskBarHeight = scnMax.bottom;
		insetsRight = scnMax.right;
		insetsLeft = scnMax.left;
	}
	
	protected boolean isTranslucencySupported(){
		return isTranslucencySupported;
	}
	
	public JHollaManager setSameWidths(boolean same){
		sameWidths = same;
		return this;
	}
	
	public JHollaManager setDefaultAlignment(Alignment alignment){
		this.alignment = alignment;
		return this;
	}
	
	public static JHollaManager getInstance(){
		if( manager == null ){
			manager = new JHollaManager();
		}
		return manager;
	}
	
	public void setCapacity(int capacity){
		if( capacity <= 0 )
			throw new IllegalArgumentException("Capacity MUST be greater than 0");
		
		CAPACITY = capacity;
	}
	
	public void repaint(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				synchronized(notifs){
					for( int i = 0 ; i < notifs.size(); i++ ){
						notifs.get(i).pack();
						((BorderLayout) notifs.get(i).getContentPane().getLayout()).invalidateLayout(notifs.get(i).getContentPane());
						notifs.get(i).revalidate();
						notifs.get(i).repaint();
						notifs.get(i).pack();
					}
					
					position = taskBarHeight + 10;
					width = 0;
					//find largest width
					for( int i = 0 ; i < notifs.size(); i++ ){
						if( notifs.get(i).getBounds().width > width )
							width = notifs.get(i).getBounds().width;
					}
					//animate all width
					for( int i = 0 ; i < notifs.size(); i++ ){
						if(sameWidths)
							notifs.get(i).animateToWidth(width);
					}
					for( int i = 0 ; i < notifs.size(); i++ ){
						//recalculate position
						if( notifs.get(i).getOpacity() >= JHolla.DEFAULT_OPACITY ){//After the JHolla must have fully showed							
							Timeline tl = new Timeline(notifs.get(i));
							tl.addPropertyToInterpolate("location", notifs.get(i).getLocation(), new Point(alignment == Alignment.LEFT ? insetsLeft + 20 : screenDimension.width - (sameWidths ? width : notifs.get(i).getWidth()) - insetsRight - 20, screenDimension.height - notifs.get(i).getHeight() - position));
							tl.setDuration(500);
							tl.play();
						}
						else{
							notifs.get(i).setLocation(alignment == Alignment.LEFT ? insetsLeft + 20 : screenDimension.width - (sameWidths ? width : notifs.get(i).getWidth()) - insetsRight - 20, screenDimension.height - notifs.get(i).getHeight() - position);
						}
						position += notifs.get(i).getHeight() + 10;
					}
					
				}
			}
		});
	}
	
	public static JHollaManager getNotificationManager(){
		return getInstance();
	}
	
	public void addNotification(final JHolla n){
		/*
		if(CAPACITY == notifs.size())
			return;
			*/
		queue.add(n);
		
		nextCall();
	}
	
	private void nextCall(){
		if( isBusy || queue.isEmpty() )
			return;
		
		synchronized( notifs ){
			if( notifs.size() >= CAPACITY )
				return;
		}
		
		isBusy = true;
		final JHolla n = queue.poll();
		
		if( !n.hasBeenBuilt() )
			n.build();
		
		//n.setLocation(new Point(screenDimension.width - n.getPreferredSize().width - 10, screenDimension.height));
		if( alignment == Alignment.LEFT )
			n.setLocation(new Point(insetsLeft + 20, screenDimension.height));
		else
			n.setLocation(new Point(screenDimension.width - n.getPreferredSize().width - insetsRight - 20, screenDimension.height));
		n.setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				n.pack();
				n.revalidate();
				n.repaint();
				((BorderLayout) n.getContentPane().getLayout()).invalidateLayout(n.getContentPane());
				n.pack();
				
			}
		});
		
		Timeline t = new Timeline(n);
		t.setDuration(500);
		t.addPropertyToInterpolate("location", n.getLocation(), new Point(n.getLocation().x, screenDimension.height - n.getHeight() - 10));
		t.addCallback(new TimelineCallbackAdapter() {				
			@Override
			public void onTimelineStateChanged(TimelineState state1, TimelineState state2,
					float arg2, float arg3) {
				if( state2 == TimelineState.DONE ){
					n.setAsFullyVisible();
					notifs.add(n);
					executor.execute(n);//start timeout thread
					repaint();
					
					isBusy = false;
					
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							nextCall();
						}
					});
				}
			}
		});
		t.play();
	}
	
	public void removeNotification(final JHolla n){
		synchronized( queue ){
			if( queue.contains(n) ){
				queue.remove();
				return;
			}
		}
		
		synchronized( queue ){
			if( !notifs.contains(n) )
				return;
		}
		
		
		Timeline t = new Timeline(n);
		t.setDuration(500);
		//t.addPropertyToInterpolate("opacity", n.getOpacity(), 0.0f);
		t.addPropertyToInterpolate("location", n.getLocation(), new Point(n.getLocation().x, screenDimension.height));
		t.addCallback(new TimelineCallbackAdapter() {				
			@Override
			public void onTimelineStateChanged(TimelineState state1, TimelineState state2,
					float arg2, float arg3) {
				if( state2 == TimelineState.DONE ){
					JHolla.JHollaListener[] listeners = n.getListeners();
					
					n.setVisible(false);
					n.dispose();
					notifs.remove(n);
					
					for( int i = 0 ; i < listeners.length; i++ )
						listeners[i].removed();
					
					repaint();
					
					SwingUtilities.invokeLater(new Runnable(){
						public void run(){
							nextCall();
						}
					});
				}
			}
		});
		t.play();
	}
	
	public void removeAllVisible(){
		synchronized( notifs ){
			for( int i = 0 ; i < notifs.size(); i++ )
				removeNotification(notifs.get(i));
		}
	}
	
	public void clearQueue(){
		synchronized(queue){
			queue.clear();
		}
	}
	
	public void removeAll(){
		clearQueue();
		removeAllVisible();
	}
}
