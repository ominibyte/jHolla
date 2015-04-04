package com.ratelsoft.library.notification.fancy;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ratelsoft.library.notification.fancy.JHolla.JHollaDrawer;
import com.ratelsoft.library.notification.fancy.JHolla.JHollaListener;


public class TestJHolla extends JFrame implements ActionListener{
	private JTextArea log;
	public TestJHolla() {
		setTitle("jHolla Test");
		
		getContentPane().setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton spawnButton = new JButton("Spawn New Set");
		spawnButton.addActionListener(this);
		buttonPanel.add(spawnButton);
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		
		log = new JTextArea();
		log.setEditable(false);
		log.setOpaque(false);
		log.setMargin(new Insets(5, 5, 5, 5));
		getContentPane().add(new JScrollPane(log));
		
		setSize(400, 250);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				//JFrame.setDefaultLookAndFeelDecorated(true);
				//JDialog.setDefaultLookAndFeelDecorated(false);
				
				//SubstanceLookAndFeel.setSkin("org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");
				try {
					//UIManager.setLookAndFeel("org.pushingpixels.substance.api.SubstanceLookAndFeel");
				} catch (Exception e) {
				}
				new TestJHolla();
			}
			
		});		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				final JHolla n = JHolla.create();
				JButton removeButton = new JButton("Remove Me");
				removeButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent event){
						n.remove();//remove/close notification
					}
				});
				
				///*
				JHollaManager.getInstance().addNotification(n
					.setNotificationTitle("Test JHolla with Custom Close Button")
					.addMessage("A sample message for a sample notification")
					.addMessage("What next when you get to add more messages to this beautiful JHolla")
					.addMessage("You can actually add infinite messages to this JHolla. I also hope your system's height is infinite...lol")
					.setClosable(false)
					.addComponent(removeButton)
					.setMinWidth(400));
				
				JHollaManager.getInstance().addNotification(JHolla.create()
						.setNotificationTitle("Test JHolla with Icon and without Timeout")
						.addMessage(new JLabel("Simple Label for a test"))
						.addMessage("A sample message for a sample notification which has no timeout. This notification would only be closed via the close button.")
						.addIcon(new ImageIcon(getClass().getResource("check.png")))
						.setMinWidth(400)
						.setTimeout(0));
				
				JHollaManager.getInstance().addNotification(JHolla.create()
					.setNotificationTitle("Test JHolla with NotificationListener")
					.addMessage(new JLabel("Simple label"))
					.setTimeout(30000)
					.setMinWidth(400)				
					.addJHollaListener(new JHollaListener() {
						
						@Override
						public void removed() {
							log.append("JHolla Removed\n");
						}
						
						@Override
						public void mouseExited() {
							log.append("Mouse Exited\n");
						}
						
						@Override
						public void mouseEntered() {
							log.append("Mouse Entered\n");
						}
						
						@Override
						public void closeButtonClicked() {
							log.append("Close Button clicked\n");
						}

						@Override
						public void clicked() {
							log.append("Clicked\n");
						}

						@Override
						public void doubleClicked() {
							log.append("Double Clicked\n");
						}

						@Override
						public void nowFullyVisible(int notifID) {
							log.append("now fully visible\n");
						}
					}));
				
				JHolla notif = JHolla.create()
						.setNotificationTitle("Title for Simple Styled JHolla")
						.addMessage("Another simple message for a simple styled JHolla")
						.addMessage("You can add any component you feel like")
						.addComponent(new JButton("You can even have a button"))
						.addComponent(new JComboBox<String>(new String[]{"Even a Combo Box"}))
						.setMinWidth(400);
				notif.build();
				notif.getNotificationContainer().setBackground(new Color(0x70cbff));
				notif.getTitlePanel().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(), BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x666666))));		
				JHollaManager.getNotificationManager().addNotification(notif);
				
				//*/
				
				JPanel panel = new JPanel(new BorderLayout());
				JLabel label1 = new JLabel(new ImageIcon(getClass().getResource("user.png")));
				label1.setOpaque(false);
				JLabel label2 = new JLabel("A Complex styling...showing you possibilities.");
				label2.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
				label2.setOpaque(false);
				panel.add(label1, BorderLayout.WEST);
				panel.add(label2, BorderLayout.CENTER);
				
				
				JButton removeB = new JButton("X");
				removeB.setPreferredSize(new Dimension(16, 24));
				removeB.setContentAreaFilled(false);
				removeB.setFocusable(false);
				removeB.setBorderPainted(false);
				removeB.setBorder(null);
				removeB.setOpaque(false);
				removeB.setVerticalAlignment(SwingConstants.CENTER);
				removeB.setHorizontalAlignment(SwingConstants.RIGHT);
				panel.add(removeB, BorderLayout.EAST);
				
				final JHolla styled = JHolla.create();		
				styled
				.addComponent(panel)
				.setShouldResetTimeoutOnHover(true)
				.setTimeout(0)
				.setMinWidth(400)
				.build(new RoundedShadowPanel(new Dimension(30, 30)));
				styled.getNotificationContainer().remove(styled.getTitlePanel());
				styled.getNotificationContainer().setOpaque(false);
				styled.getNotificationContainer().setBackground(Color.WHITE);
				styled.getNotificationContainer().setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
				JHollaManager.getInstance().addNotification(styled);
				
				removeB.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent event){
						styled.remove();
					}
				});
				
				
			}
			
		});	
		
	}
	
	public static class RoundedShadowPanel implements JHollaDrawer {

	    /** Stroke size. it is recommended to set it to 1 for better view */
	    protected int strokeSize = 1;
	    /** Color of shadow */
	    protected Color shadowColor = new Color(0x999999);
	    /** Sets if it drops shadow */
	    protected boolean shady = true;
	    /** Sets if it has an High Quality view */
	    protected boolean highQuality = true;
	    /** Double values for Horizontal and Vertical radius of corner arcs */
	    protected Dimension arcs = new Dimension(40, 40);
	    /** Distance between shadow border and opaque panel border */
	    protected int shadowGap = 5;
	    /** The offset of shadow.  */
	    protected int shadowOffset = 5;
	    /** The transparency value of shadow. ( 0 - 255) */
	    protected int shadowAlpha = 200;

	    public RoundedShadowPanel() {
	        this(new Dimension(40, 40));
	    }
	    public RoundedShadowPanel( Dimension arcDim ) {
	       arcs = arcDim;
	    }
		@Override
		public void drawOnTitlePanel(Graphics g, JPanel titlePanel) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void drawOnContentsPanel(Graphics g, JPanel contentsPanel) {
			// TODO Auto-generated method stub			
		}
		@Override
		public void drawOnContainer(Graphics g, JPanel container) {
			int width = container.getWidth();
	        int height = container.getHeight();
	        int shadowGap = this.shadowGap;
	        Color shadowColorA = new Color(shadowColor.getRed(), 
	        		shadowColor.getGreen(), shadowColor.getBlue(), shadowAlpha);
	        Graphics2D graphics = (Graphics2D) g;

	        //Sets antialiasing if HQ.
	        if (highQuality) {
	            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
	        }

	        //Draws shadow borders if any.
	        if (shady) {
	            graphics.setColor(shadowColorA);
	            graphics.fillRoundRect(
	                    shadowOffset,// X position
	                    shadowOffset,// Y position
	                    width - strokeSize - shadowOffset, // width
	                    height - strokeSize - shadowOffset, // height
	                    arcs.width, arcs.height);// arc Dimension
	        } else {
	            shadowGap = 1;
	        }

	        //Draws the rounded opaque panel with borders.
	        graphics.setColor(container.getBackground());
	        graphics.fillRoundRect(0, 0, width - shadowGap, 
			height - shadowGap, arcs.width, arcs.height);
	        graphics.setColor(container.getForeground());
	        graphics.setStroke(new BasicStroke(strokeSize));
	        graphics.drawRoundRect(0, 0, width - shadowGap, 
			height - shadowGap, arcs.width, arcs.height);

	        //Sets strokes to default, is better.
	        graphics.setStroke(new BasicStroke());
		}
	}
}
