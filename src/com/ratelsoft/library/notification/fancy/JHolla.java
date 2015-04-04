package com.ratelsoft.library.notification.fancy;

import java.util.ArrayList;
import javax.swing.*;

import org.pushingpixels.trident.Timeline;

import java.awt.*;
import java.awt.event.*;

public class JHolla extends JDialog implements Runnable{
	private String title;
	private ImageIcon icon;
	private ArrayList<Component> messageComponents;
	private JButton closeButton;
	private boolean closable;
	private long timeout;//in milliseconds
	private boolean built;
	private long timeGone;
	private final int INTERVAL = 100;
	private int width;
	private boolean pauseTime;
	private Timeline fadeInTimeLine, fadeOutTimeLine;
	private Handler handler;
	public final static float DEFAULT_OPACITY = 0.75f;
	private boolean shouldResetTimeOnHover;
	private ArrayList<JHollaListener> listeners;
	private boolean hasMouseFocus;
	private JPanel topPanel, centerPanel, container;
	private static final Color DEFAULT_BACKGROUND_COLOR = new Color(0xf8f8f8);
	private boolean isFullyVisible;
	private int notifID;
	
	private JHolla(){
		setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		getRootPane().setOpaque(false);
		
		timeout = 10 * 1000;//defaults to 10 seconds
		timeGone = 0;
		built = false;
		width = 0;
		title = "";
		closable = true;
		pauseTime = false;
		messageComponents = new ArrayList<Component>();
		handler = new Handler();
		shouldResetTimeOnHover = false;
		listeners = new ArrayList<JHollaListener>();
		hasMouseFocus = false;
		
		if( JHollaManager.getInstance().isTranslucencySupported() )
			setOpacity(DEFAULT_OPACITY);
	}
	
	public static JHolla create(){
		return new JHolla();
	}
	
	public boolean isFullyVisible(){
		return isFullyVisible;
	}
	
	public void setAsFullyVisible(){
		isFullyVisible = true;
		for( JHollaListener l : listeners )
			l.nowFullyVisible(notifID);
	}
	
	public JHolla addMessage(String message){
		if(built)
			throwException();
		
		JTextArea textArea = new JTextArea(message);
		textArea.setFocusable(false);
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		textArea.setBorder(null);
		textArea.setMargin(new Insets(0,0,0,0));
		textArea.setOpaque(false);
		textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
		textArea.setFont(new JLabel().getFont());
		textArea.addMouseListener(handler);
		
		messageComponents.add(textArea);
		
		return this;
	}
	
	public JHolla addMessage(JLabel label){
		if(built)
			throwException();
		
		label.setOpaque(false);		
		label.setFocusable(false);
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.addMouseListener(handler);
		label.setFont(new Font(label.getFont().getFontName(), Font.PLAIN, label.getFont().getSize()));
		messageComponents.add(label);
		
		return this;
	}
	
	public JHolla addComponent(JComponent c){
		if(built)
			throwException();
		
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		c.addMouseListener(handler);
		c.setFocusable(false);
		c.setOpaque(false);
		messageComponents.add(c);
		
		return this;
	}
	
	public JHolla setNotificationID(int id){
		notifID = id;
		return this;
	}
	
	public int getNotificationID(){
		return notifID;
	}
	
	public JHolla addIcon(ImageIcon icon){
		if(built)
			throwException();
		
		this.icon = icon;
		return this;
	}
	
	public JHolla addIcon(Image icon){
		if(built)
			throwException();
		
		this.icon = new ImageIcon(icon);
		return this;
	}
	
	private void throwException(){
		throw new RuntimeException("Invalid State Action. JHolla has already been built.");
	}
	
	/**
	 * Sets if the Close Button should show up
	 * @param closeable boolean value to indicates if a close button should be added to this JHolla
	 * @return a reference to this Object
	 */
	public JHolla setClosable(boolean closeable){
		if(built)
			throwException();
		
		this.closable = closeable;
		return this;
	}
	
	public JHolla setTimeout(long millis){
		if(built)
			throwException();
		
		timeout = millis;		
		return this;
	}
	
	private void resetTimeGone(){
		timeGone = 0;
	}
	
	public JHolla setShouldResetTimeoutOnHover(boolean shouldReset){
		shouldResetTimeOnHover = shouldReset;
		return this;
	}
	
	public JHolla setNotificationTitle(String title){
		if(built)
			throwException();
		
		this.title = title;
		return this;
	}
	
	public JHolla setMinWidth(int pixels){
		width = pixels;
		return this;
	}
	
	public void remove(){
		JHollaManager.getInstance().removeNotification(this);
	}
	
	public boolean hasBeenBuilt(){
		return built;
	}
	
	public void setWidth(int width){
		this.width = width;
		setSize(width, getHeight());
		revalidate();
		repaint();
	}
	
	public synchronized void animateToWidth(int width){
		if( isVisible() ){
			//setWidth(width);
			Timeline t = new Timeline(this);
			t.addPropertyToInterpolate("width", getWidth(), width);
			t.setDuration(100);
			t.play();
		}
	}
	
	private void fadeIn(){
		if( !JHollaManager.getInstance().isTranslucencySupported() )
			return;
		
		if( fadeOutTimeLine != null && !fadeOutTimeLine.isDone() )
			fadeOutTimeLine.cancel();
		
		fadeInTimeLine = new Timeline(this);
		fadeInTimeLine.addPropertyToInterpolate("opacity", getOpacity(), 1.0f);
		fadeInTimeLine.setDuration(500);
		fadeInTimeLine.play();
	}
	
	public JHolla addJHollaListener(JHollaListener listener){
		listeners.add(listener);
		return this;
	}
	
	public JHollaListener[] getListeners(){
		return listeners.toArray(new  JHollaListener[0]);
	}
	
	private void fadeOut(){
		if( !JHollaManager.getInstance().isTranslucencySupported() )
			return;
		
		if( fadeInTimeLine != null && !fadeInTimeLine.isDone() )
			fadeInTimeLine.cancel();
		
		fadeOutTimeLine = new Timeline(this);
		fadeOutTimeLine.addPropertyToInterpolate("opacity", getOpacity(), DEFAULT_OPACITY);
		fadeInTimeLine.setDuration(1500);
		fadeOutTimeLine.play();
	}
	
	public void build(){build(null);}
	
	public void build(final JHollaDrawer drawer){
		if( built )
			return;
		
		//BorderLayout dialogLayout = new BorderLayout();
		//getContentPane().setLayout(dialogLayout);
		addMouseListener(handler);
		
		BorderLayout layout = new BorderLayout();
		layout.setVgap(10);
		container = new JPanel(layout){
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				if( drawer != null )
					drawer.drawOnContainer(g, container);
			}
		};
		container.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x999999), 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		container.setFocusable(false);
		
		BorderLayout layout2 = new BorderLayout();
		layout2.setHgap(20);
		topPanel = new JPanel(layout2){
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				if( drawer != null )
					drawer.drawOnTitlePanel(g, topPanel);
			}
		};
		topPanel.setOpaque(false);
		topPanel.setFocusable(false);
		JLabel titleLabel = new JLabel(title);
		titleLabel.setOpaque(false);
		titleLabel.setFont(new Font("SanSerif", Font.BOLD, 15));
		titleLabel.addMouseListener(handler);
		if( icon != null ){
			//resize icon
			Image image = icon.getImage();
			if( icon.getIconHeight() > 24 )
				image = image.getScaledInstance(icon.getIconWidth() * 24 / icon.getIconHeight(), 24, Image.SCALE_SMOOTH);
			
			icon = new ImageIcon(image);
			
			titleLabel.setIcon(icon);
			titleLabel.setVerticalAlignment(SwingConstants.CENTER);
		}
		topPanel.add(titleLabel, BorderLayout.CENTER);
		
		if( closable ){
			closeButton = new JButton("X");
			closeButton.setFocusable(false);
			closeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			closeButton.setPreferredSize(new Dimension(24, 24));
			closeButton.setBorderPainted(false);
			closeButton.setContentAreaFilled(false);
			closeButton.setFocusPainted(false);
			closeButton.setOpaque(false);
			closeButton.addActionListener(new ActionListener(){				
				public void actionPerformed(ActionEvent ev){
					for( int i = 0 ; i < listeners.size(); i++ ){
						listeners.get(i).closeButtonClicked();
					}
					
					remove();
				}
			});
			closeButton.addMouseListener(handler);
			closeButton.setToolTipText("Close");
			topPanel.add(closeButton, BorderLayout.EAST);
		}	
		
		centerPanel = new JPanel(){
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				if( drawer != null )
					drawer.drawOnContentsPanel(g, centerPanel);
			}
		};
		centerPanel.setOpaque(false);
		centerPanel.setFocusable(false);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		for( int i = 0 ; i < messageComponents.size(); i++ ){
			centerPanel.add(messageComponents.get(i));
			if( i != messageComponents.size() - 1 )
				//centerPanel.add(Box.createRigidArea(new Dimension(10, 10)));
			centerPanel.add(Box.createVerticalStrut(10));
		}
		
		container.add(topPanel, BorderLayout.NORTH);
		container.add(centerPanel, BorderLayout.CENTER);
		
		//getContentPane().add(container);
		setContentPane(container);
				
		if( width == 0 ){
			pack();
		}
		else{
			if( !messageComponents.isEmpty() ){
				messageComponents.get(0).setPreferredSize(new Dimension(width, messageComponents.get(0).getPreferredSize().height));
				//messageComponents.get(0).setSize(new Dimension(width, messageComponents.get(0).getPreferredSize().height));
			}
		}
		
		width = getWidth();
		
		built = true;
		
		Component c = getContentPane();
		while( c.getParent() != null ){
			c = c.getParent();
			try{
				c.setBackground(new Color(0, 0, 0, 0));
			}
			catch(Exception e){}
		}
		container.setOpaque(true);
		container.setBackground(DEFAULT_BACKGROUND_COLOR);
	}
	
	public JPanel getNotificationContainer(){
		if( !built )
			throw new RuntimeException("Invalid State Action. JHolla has NOT yet been built.");
		
		return container;
	}
	
	public JPanel getTitlePanel(){
		if( !built )
			throw new RuntimeException("Invalid State Action. JHolla has NOT yet been built.");
		
		return topPanel;
	}
	
	public JPanel getContentsPanel(){
		if( !built )
			throw new RuntimeException("Invalid State Action. JHolla has NOT yet been built.");
		
		return centerPanel;
	}
	
	private boolean isWithinBounds(Point p){
		if( !isShowing() )
			return false;
		
		Point point = getLocationOnScreen();
		
		if( p.x >= point.x && p.x <= point.x + getWidth() && p.y >= point.y && p.y <= point.y + getHeight() )
			return true;
		
		return false;
	}
	
	public void run(){
		if( timeout <= 0 )//JHolla should show indefinitely
			return;
		
		while( timeGone < timeout ){
			if( pauseTime ){
				try{
					Thread.sleep(INTERVAL);
				}
				catch(Exception e){}
				
				continue;
			}
			
			try{
				Thread.sleep(INTERVAL);
			}
			catch(Exception e){}
			
			timeGone += INTERVAL;
		}
		
		remove();
	}
	
	private class Handler extends MouseAdapter{
		@Override
		public void mouseEntered(MouseEvent ev) {
			if( !hasMouseFocus ){
				hasMouseFocus = true;
				for(int i = 0; i < listeners.size(); i++)
					listeners.get(i).mouseEntered();
				
				fadeIn();
				pauseTime = true;
				
				if(shouldResetTimeOnHover)
					resetTimeGone();
			}
		}
		@Override
		public void mouseExited(MouseEvent event) {
			//check if within bounds
			if( !isWithinBounds(event.getLocationOnScreen()) ){
				hasMouseFocus = false;
				for(int i = 0; i < listeners.size(); i++)
					listeners.get(i).mouseExited();
			}
			
			fadeOut();
			pauseTime = false;
		}
		@Override
		public void mouseClicked(MouseEvent e){
			if( e.getClickCount() > 1 ){
				e.consume();
				for(int i = 0; i < listeners.size(); i++)
					listeners.get(i).doubleClicked();
			}
			else{
				for(int i = 0; i < listeners.size(); i++)
					listeners.get(i).clicked();
			}
		}
	}
	
	public static interface JHollaListener{
		public void nowFullyVisible(int notifID);
		public void mouseEntered();
		public void mouseExited();
		public void closeButtonClicked();
		public void removed();
		public void clicked();
		public void doubleClicked();
	}
	
	public static interface JHollaDrawer{
		public void drawOnTitlePanel(Graphics g, JPanel titlePanel);
		public void drawOnContentsPanel(Graphics g, JPanel contentsPanel);
		public void drawOnContainer(Graphics g, JPanel container);
	}
	
	//future methods setFixedOpacity...float(0-1), setShouldFade...boolean, setDraggable...boolean
	//for the setdraggable dialogs, on first drag, we would exempt them from calculations on positions and call JHollaManager's repaint method
}
