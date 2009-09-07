package com.hp.hpl.guess.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.hp.hpl.guess.Guess;
import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.piccolo.Morpher;
import com.hp.hpl.guess.storage.StorageFactory;

public class VideoWindow extends JPanel implements Dockable {
	
	private static final int DEFAULT_MORPH_DURATION = 1000;
	private static final int VIDEO_FPS = 25;
	
	private static final long serialVersionUID = 4804899713384217303L;

	/**
	 * The guess main window or another
	 * parent window
	 */
    private GuessJFrame parentWindow = null;

    /**
     * This window and the visibility
     */
    private static VideoWindow singleton = null;
    protected boolean visible = false;

    /**
     * GUI Elements
     */
    private JButton btnRemove;
    private JPanel contentFrame;
    private JScrollPane scrollpane;
    private JButton btnMoveRight;
    private JButton btnMoveLeft;
    private JButton btnInsert;
    private JButton btnExport;
    private JButton btnStop;
    private JButton btnPlay;
   
    /**
     * The list of states for the video
     */
    private LinkedList<VideoState> videoStates = new LinkedList<VideoState>();
    
    /**
     * The current selected video state
     */
    private VideoState selected = null;
    
    /**
     * true if the video is playing
     */
    private boolean playing = false;
    
    /**
     * action listener to select the next state while
     * playing a video
     */
    private ActionListener nextStateActionListener = new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		if (e.getActionCommand().equals("finished")) {
    			if (playing) {
    				setSelected(nextState(selected));
    				gotoNextState();
    			}
    		}
    	}
    };


    public VideoWindow() { 
    	initGUI();
    }
    
    
    /**
     * stop playing and reset cursor position
     */
    private void stop() {
    	playing = false;
    	Morpher.removeActionListener(nextStateActionListener);
    	Guess.getInterpreter().freeze(false);

		// Setting buttons enabled / disabled
		btnPlay.setEnabled(true);
		btnStop.setEnabled(false);
		btnRemove.setEnabled(true);
		btnMoveLeft.setEnabled(true);
		btnMoveRight.setEnabled(true);
		btnInsert.setEnabled(true);
		btnExport.setEnabled(true);
    	
    	rebuildContent();
    }

    /**
     * start playing
     */
    private void play() {
    	if (playing) {
    		return;
    	}
    	
    	playing = true;
    	
    	// Setting buttons enabled / disabled
    	btnPlay.setEnabled(false);
    	btnStop.setEnabled(true);
    	btnRemove.setEnabled(false);
    	btnMoveLeft.setEnabled(false);
    	btnMoveRight.setEnabled(false);
    	btnInsert.setEnabled(false);
    	btnExport.setEnabled(false);
    	
		if (selected==null) {
			if (videoStates.size()==0) {
				return;
			} else {
				setSelected(videoStates.getFirst());
			}
		}
    	
    	Guess.getInterpreter().freeze(true);
		StorageFactory.getSL().loadState(selected.name);
    	
		Morpher.addActionListener(nextStateActionListener);
		gotoNextState();
    	
    }
    
    /**
     * A shortcut for selecting the next state
     * @param aState
     * @return
     */
    private synchronized VideoState nextState(VideoState aState) {
    	try {
    		return videoStates.listIterator(videoStates.indexOf(aState) + 1).next();
    	} catch (NoSuchElementException e) {
    		return null;
    	}
    }
    
    /**
     * A shortcut for selecting the previous state
     * @param aState
     * @return
     */
    private synchronized VideoState previousState(VideoState aState) {
    	try {
    		return videoStates.listIterator(videoStates.indexOf(aState)).previous();
    	} catch (NoSuchElementException e) {
    		return null;
    	}
    }
    
    /**
     * Remove a VideoState from the list
     * @param selected
     */
	private void removeState(VideoState aState) {	
		videoStates.remove(aState);
		StorageFactory.getSL().deleteState(aState.name);
	}
    
    /**
     * Morph to the next state
     */
    private void gotoNextState() {
		if (nextState(selected)==null) {
			stop();
			return;
		}

		Morpher.morph(Guess.getGraph(), nextState(selected).name, selected.morphTime, false);
    }
    
    /**
     * Save the video to a file
     */
    private void exportVideo(String fileName, int fps) {
    	
    	// No states --> return
    	if (videoStates.size()==0) {
    		return;
    	}
    	
    	// Freeze console
    	Guess.getInterpreter().freeze(true);
    	
    	// Load first video state
		StorageFactory.getSL().loadState(selected.name);

    	
    	Iterator<VideoState> videoStateIterator = videoStates.iterator();
    	long morphTime = selected.morphTime;
    	
    	((GFrame)VisFactory.getFactory().getDisplay()).startMovie(fps, fileName);
    	
    	while (videoStateIterator.hasNext()) {
    		VideoState nextState = videoStateIterator.next();
    		Morpher.morph(Guess.getGraph(), nextState.name, morphTime, true);
    		morphTime = nextState.morphTime;
    	}

    	((GFrame)VisFactory.getFactory().getDisplay()).stopMovie();
    	
    	// Unfreeze console
    	Guess.getInterpreter().freeze(false);
    	
    }
    
    /**
     * Set a new position for a state
     * @param aState
     * @param newPosition
     */
    private void changePosition(VideoState aState, VideoState insertAfterState) {
    	
    	// Remove state (old position)
    	removeState(aState);
    	
    	// Add state to new position
    	int lastIndex = 0;
    	if (videoStates.contains(insertAfterState)) {
    		lastIndex = videoStates.indexOf(insertAfterState) + 1;
    	}
    	
    	videoStates.add(lastIndex, aState);
    }
    
    /**
     * Insert a new state
     */
    private void insertState(VideoState aVideoState) {
    	// Add state to new position
    	int newIndex = 0;
    	if (videoStates.contains(selected)) {
    		newIndex = videoStates.indexOf(selected) + 1;
    	}
    	videoStates.add(newIndex, aVideoState);
    	setSelected(aVideoState);
    }

    
	/**
	 * Return the prefered horizontal
	 * direction for this frame
	 */
	public int getDirectionPreference() {
		return MainUIWindow.HORIZONTAL_DOCK;
	}

	/**
	 * Return the title of the video window
	 */
	public String getTitle() {
		return "Video Window";
	}


    /**
     * Get the parent window
     */
    public GuessJFrame getWindow() {
    	return(parentWindow);
    }

    /**
     * Set the parent window
     */
    public void setWindow(GuessJFrame gjf) {
    	parentWindow = gjf;
    }

	/**
	 * set the visibility for the window
	 * @return
	 */
	public void opening(boolean state) {
		visible = state;
	}
	
	/**
	 * is the window visible
	 * @return
	 */
    public static boolean isVWVisible() {
    	if (singleton == null) {
    		return(false);
    	}

    	return(singleton.visible);
    }

    /**
     * Return the videowindow or create a new one
     * @return
     */
    public static VideoWindow getVideoWindow() {
    	if (singleton == null) {
    		singleton = new VideoWindow();
    	}
    	return singleton ;
    }

    /**
     * Dock the window if not visible
     */
    public static void create() {
    	if ((getVideoWindow().getWindow() == null) || 
    			(!getVideoWindow().getWindow().isVisible())) {
    		Guess.getMainUIWindow().dock(getVideoWindow());
    	}
    }


	/**
	 * Init GUI elements
	 */
	private void initGUI() {
		
		setBackground(Guess.getMainUIWindow().getBgColor());
		
		try {
			{
				GridBagLayout thisLayout = new GridBagLayout();
				this.setPreferredSize(new java.awt.Dimension(815, 226));
				thisLayout.rowWeights = new double[] {0.0, 0.0, 1.0, 0.0};
				thisLayout.rowHeights = new int[] {11, 7, 7, 12};
				thisLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
				thisLayout.columnWidths = new int[] {11, 7, 3, 7, 12, 7, 12, 7, 3, 20, 12, 20, 3, 20, 7, 12};
				this.setLayout(thisLayout);
				this.add(getBtnPlay(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getBtnStop(), new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getBtnExport(), new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getBtnInsert(), new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getBtnMoveLeft(), new GridBagConstraints(11, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getBtnMoveRight(), new GridBagConstraints(13, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getScrollpane(), new GridBagConstraints(1, 2, 14, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				this.add(getBtnRemove(), new GridBagConstraints(9, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return and create play button
	 * @return
	 */
	private JButton getBtnPlay() {
		if(btnPlay == null) {
			btnPlay = new JButton();
		    btnPlay.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/media-playback-start.png"))));
			btnPlay.setText("Play");
			btnPlay.setSize(110, 25);
			btnPlay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					play();
				}
			});
		}
		return btnPlay;
	}
	
	/**
	 * Return and create stop button
	 * @return
	 */
	private JButton getBtnStop() {
		if(btnStop == null) {
			btnStop = new JButton();
			btnStop.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/media-playback-pause.png"))));			
			btnStop.setText("Pause");
			btnStop.setSize(110, 25);
			btnStop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stop();
				}
			});
		}
		return btnStop;
	}
	
	/**
	 * Return and create export button
	 * @return
	 */
	private JButton getBtnExport() {
		if(btnExport == null) {
			btnExport = new JButton();
			btnExport.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/document-save-as.png"))));
			btnExport.setText("Export As...");
			btnExport.setSize(110, 25);
			btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SunFileFilter filter = new SunFileFilter();
					JFileChooser chooser = new JFileChooser();
					filter.addExtension("mov");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showSaveDialog(Guess.getMainUIWindow());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String fileName = chooser.getSelectedFile().getAbsolutePath();
						
						exportVideo(fileName, VIDEO_FPS);
					}
				}
			});
		}
		return btnExport;
	}
	
	/**
	 * Return and create insert button
	 * @return
	 */
	private JButton getBtnInsert() {
		if(btnInsert == null) {
			btnInsert = new JButton();
			btnInsert.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/list-add.png"))));
			btnInsert.setText("Add");
			btnInsert.setSize(110, 25);
			btnInsert.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Create a new state and insert into
					// state list
					insertState(createState());
					rebuildContent();
				}
			});
		}
		return btnInsert;
	}
	
	/**
	 * Return and create move left button
	 * @return
	 */
	private JButton getBtnMoveLeft() {
		if(btnMoveLeft == null) {
			btnMoveLeft = new JButton();
			btnMoveLeft.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/go-previous.png"))));
			btnMoveLeft.setText("Move Left");
			btnMoveLeft.setSize(110, 25);
			btnMoveLeft.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
			    	int lastIndex = 0;
			    	if (videoStates.contains(selected)) {
			    		lastIndex = videoStates.indexOf(selected) - 1;
			    	}
			    	
			    	VideoState prev = null;
			    	if (lastIndex>-1) {
			    		ListIterator<VideoState> stateAddIterator = videoStates.listIterator(lastIndex);
				    	try {
				    		prev = stateAddIterator.previous();
				    	} catch (NoSuchElementException e1) {}
			    	} else {
			    		prev = videoStates.get(videoStates.size() - 1);
			    	}
   	
					
					changePosition(selected, prev);
					rebuildContent();
				}
				
			});
		}
		return btnMoveLeft;
	}
	
	/**
	 * Return and create move right button
	 * @return
	 */
	private JButton getBtnMoveRight() {
		if(btnMoveRight == null) {
			btnMoveRight = new JButton();
			btnMoveRight.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/go-next.png"))));
			btnMoveRight.setText("Move Right");
			btnMoveRight.setSize(110, 25);
			btnMoveRight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
			    	int selectedIndex = 0;
			    	if (videoStates.contains(selected)) {
			    		selectedIndex = videoStates.indexOf(selected);
			    	}
			    	
			    	VideoState next = null;
			    	try {
			    		next = videoStates.get(selectedIndex + 1);
			    	} catch (IndexOutOfBoundsException e1) {}
			    	
					
					changePosition(selected, next);
					rebuildContent();
				}
				
			});
		}
		return btnMoveRight;
	}
	
	/**
	 * Return and create scroll pane
	 * @return
	 */
	private JScrollPane getScrollpane() {
		if(scrollpane == null) {
			scrollpane = new JScrollPane();
			scrollpane.setOpaque(false);
			scrollpane.setBorder(null);
			scrollpane.setViewportView(getContentFrame());
		}
		return scrollpane;
	}
	
	/**
	 * Return and create content frame
	 * @return
	 */
	private JPanel getContentFrame() {
		if(contentFrame == null) {
			contentFrame = new JPanel(); 
			contentFrame.setBorder(null);
			contentFrame.setBackground(Guess.getMainUIWindow().getBgColor());
		}
		return contentFrame;
	}
	
	
	/**
	 * set the new selected state
	 * @param aState
	 */
	private void setSelected(VideoState aState) {
		selected = aState;
		rebuildContent();		
	}
	
	/**
	 * rebuild the video thumbnails
	 */
	private void rebuildContent() {
		getContentFrame().removeAll();
		getContentFrame().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		int i = 0;
		
		c.weightx = 0;
		c.weighty = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		
		Iterator<VideoState> stateIterator = videoStates.iterator();
		while (stateIterator.hasNext()) {
			final VideoState nextState = stateIterator.next();
			
			// Thumbnail
			c.gridx = i;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(1,1,1,1);
			JLabel iconLabel = new JLabel(nextState.img);
			if (nextState.equals(selected)) {
				iconLabel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(94, 147, 212)));
			} else {
				iconLabel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
			}
			iconLabel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					StorageFactory.getSL().loadState(nextState.name);
					setSelected(nextState);
				}
			});
			getContentFrame().add(iconLabel, c);		
			
			// Textbox (Morphtime)
			c.insets = new Insets(4,4,4,4);
			c.gridx = i + 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			final JFormattedTextField morphTimeText = new JFormattedTextField(NumberFormat.getIntegerInstance());
			morphTimeText.setText(String.valueOf(nextState.morphTime));
			morphTimeText.addKeyListener(new KeyAdapter() {
			    public void keyTyped(KeyEvent e) {
			    	if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			    		try {
							morphTimeText.commitEdit();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
			    		nextState.morphTime = Long.valueOf(morphTimeText.getText());
			    	}
			    }				
			});
			morphTimeText.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
		    		try {
						morphTimeText.commitEdit();
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
		    		nextState.morphTime = Long.valueOf(morphTimeText.getText());
				}
			});
			// Do not add last textbox
			if ((i/2)!=(videoStates.size()-1)) {
				getContentFrame().add(morphTimeText, c);	
			}
			
			i += 2;
		}
		
		c.gridx = i + 1;
		c.weightx = 1;
		getContentFrame().add(new JLabel(), c);
		getContentFrame().updateUI();
		
	}
	
	/**
	 * Save the current state as a new state object and create
	 * a thumbnail. Return the VideoState object.
	 * @return
	 */
	private VideoState createState() {
		
		// Save state
		String name = "_" + UUID.randomUUID().toString();
		StorageFactory.getSL().saveState(name);
		
		// Create Thumbnail
		BufferedImage imageSrc = Guess.getFrame().getFullImage();
		
		int thumbWidth = 100;
		int thumbHeight = 100;
		
		Image thumb = imageSrc.getScaledInstance(thumbWidth, thumbHeight ,Image.SCALE_SMOOTH);

		BufferedImage bi = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D biContext = bi.createGraphics();
		biContext.drawImage(thumb, 0, 0, null);
			
		imageSrc = null;
		bi = null;
		
		return new VideoState(name, new ImageIcon(thumb), null, DEFAULT_MORPH_DURATION);
	}
	
	/**
	 * Return and create remove button
	 * @return
	 */
	private JButton getBtnRemove() {
		if(btnRemove == null) {
			btnRemove = new JButton();
			btnRemove.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Thread.currentThread().getContextClassLoader().getResource("images/list-remove.png"))));
			btnRemove.setText("Remove");
			btnRemove.setSize(110, 25);
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (selected!=null) {

						VideoState removeState = selected;
						if (previousState(selected)!=null) {
							StorageFactory.getSL().loadState(previousState(selected).name);
						}
						setSelected(previousState(selected));
						removeState(removeState);

						rebuildContent();
					}
				}
			});
		}
		return btnRemove;
	}

	/**
	 * unused
	 */
	public void attaching(boolean state) {}

}




class VideoState {
	public String name;
	public ImageIcon img;
	public long morphTime;
	
	public VideoState(String name, ImageIcon image, VideoState nextState, long morphTime) {
		this.name = name;
		this.img = image;
		this.morphTime = morphTime;
	}
}
