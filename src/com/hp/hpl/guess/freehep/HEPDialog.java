package com.hp.hpl.guess.freehep;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.freehep.graphicsio.cgm.CGMExportFileType;
import org.freehep.graphicsio.emf.EMFExportFileType;
import org.freehep.graphicsio.gif.GIFExportFileType;
import org.freehep.graphicsio.java.JAVAExportFileType;
import org.freehep.graphicsio.jpg.JPGExportFileType;
import org.freehep.graphicsio.pdf.PDFExportFileType;
import org.freehep.graphicsio.png.PNGExportFileType;
import org.freehep.graphicsio.ppm.PPMExportFileType;
import org.freehep.graphicsio.ps.EPSExportFileType;
import org.freehep.graphicsio.ps.PSExportFileType;
import org.freehep.graphicsio.raw.RawExportFileType;
import org.freehep.graphicsio.svg.SVGExportFileType;
import org.freehep.graphicsio.swf.SWFExportFileType;
import org.freehep.util.export.ExportFileType;

import com.hp.hpl.guess.piccolo.GFrame;
import com.hp.hpl.guess.util.PrefWrapper;

/**
 * An "Export" dialog for saving components as graphic files.
 * 
 * @author tonyj, modified by eytan adar for GUESS
 * @version $Id$
 */
public class HEPDialog extends JOptionPane {
	
	private static final long serialVersionUID = -5708203726450453533L;
	
	/**
	 * Buttons
	 */
	private static final ButtonGroup btnGroup = new ButtonGroup();
	private static final JButton advanced = new JButton();
	private static final JButton browse = new JButton();
	private static final JRadioButton exportCompleteGraphRadioButton = new JRadioButton();
	private static final JRadioButton currentViewRadioButton = new JRadioButton();

	/**
	 * Labels
	 */
	private static final JLabel imageSize = new JLabel();
	private static final JLabel rescaleLabel = new JLabel();
	private static final JLabel fileNameLabel = new JLabel();
	private static final JLabel fileTypeLabel = new JLabel();
	
	/**
	 * Text Fields
	 */
	private static JComboBox type = new JComboBox();
	private static final JTextField file = new JTextField();
	private static final JTextField scale = new JTextField();
	
	/**
	 * Other GUI Stuff
	 */
	private static final JPanel mainPanel = new JPanel();
	
	
	/**
	 * Save and loads user preferences
	 */
	private Preferences userPrefs = PrefWrapper.userNodeForPackage(getClass());

	private static Vector<ExportFileType> list = new Vector<ExportFileType>();
	private static HashMap<ExportFileType, Integer> efts = new HashMap<ExportFileType, Integer>();

	static {
		addAllExportFileTypes();
	}

	/**
	 * Register an export file type.
	 */
	private static void addExportFileType(ExportFileType fileType, int tp) 
	{
		list.addElement(fileType);
		efts.put(fileType, new Integer(tp));
	}

	private static void addAllExportFileTypes() 
	{
		addExportFileType(new JPGExportFileType(), HEPWriter.JPG);
		addExportFileType(new PNGExportFileType(), HEPWriter.PNG);
		addExportFileType(new PPMExportFileType(), HEPWriter.PPM);
		addExportFileType(new RawExportFileType(), HEPWriter.RAW);
		addExportFileType(new CGMExportFileType(), HEPWriter.CGM);
		addExportFileType(new EMFExportFileType(), HEPWriter.EMF);
		addExportFileType(new PDFExportFileType(), HEPWriter.PDF);
		addExportFileType(new EPSExportFileType(), HEPWriter.EPS);
		addExportFileType(new PSExportFileType(), HEPWriter.PS);
		addExportFileType(new SVGExportFileType(), HEPWriter.SVG);
		addExportFileType(new SWFExportFileType(), HEPWriter.SWF);
		addExportFileType(new GIFExportFileType(), HEPWriter.GIF);
		addExportFileType(new JAVAExportFileType(), HEPWriter.JAVA);
	}

   /**
    * Creates a new instance of ExportDialog with all the standard
    * export filetypes.
    */
   public HEPDialog()
   {
		this(null);
	}

	public void updateSize() {
		double rescale = 1.0;
		try {
			rescale = Double.parseDouble(scale.getText());
		} catch (Exception ex) {
		}

		if (originalSize != null) {
			imageSize.setText("Output Image Size: "
					+ (int) (originalSize.getWidth() * rescale) + " x "
					+ (int) (originalSize.getHeight() * rescale) + " px");
		}
	}

	public JPanel createPanel() 
	{
		mainPanel.setPreferredSize(new Dimension(418, 160));
		mainPanel.setLayout(null);
		mainPanel.setBounds(0, 0, 480, 178);

		rescaleLabel.setBounds(10, 113, 90, 16);
		rescaleLabel.setDisplayedMnemonic(KeyEvent.VK_R);
		rescaleLabel.setText("Rescale Image:");
		mainPanel.add(rescaleLabel);

		rescaleLabel.setLabelFor(scale);
		scale.setBounds(96, 109, 90, 25);
		scale.setName("scale");
		scale.setText("" + scaling);
		mainPanel.add(scale);
		scale.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				updateSize();
			}
		});

		imageSize.setBounds(10, 140, 400, 16);
		imageSize.setText("Output Image Size:");
		mainPanel.add(imageSize);

		file.setBounds(100, 9, 280, 25);
		file.setName("file");
		mainPanel.add(file);

		type = new JComboBox(list);
		type.setBounds(100, 39, 280, 25);
		type.setName("type");
		mainPanel.add(type);
		


		browse.setMnemonic('b');
		browse.setBounds(385, 9, 90, 25);
		browse.setActionCommand("Browse...");
		browse.setName("browse");
		browse.setText("Browse...");
		mainPanel.add(browse);

		advanced.setMnemonic('o');
		advanced.setBounds(385, 39, 90, 25);
		advanced.setActionCommand("Options...");
		advanced.setName("advanced");
		advanced.setText("Options...");
		mainPanel.add(advanced);


		exportCompleteGraphRadioButton.setBounds(140, 82, 160, 25);
		exportCompleteGraphRadioButton.setMnemonic('x');
		btnGroup.add(exportCompleteGraphRadioButton);
		exportCompleteGraphRadioButton.setText("Export Complete Graph");

		currentViewRadioButton.setText("Export Current View");
		currentViewRadioButton.setMnemonic('e');
		currentViewRadioButton.setBounds(10, 82, 123, 25);
		btnGroup.add(currentViewRadioButton);
		mainPanel.add(currentViewRadioButton);
		mainPanel.add(exportCompleteGraphRadioButton);

		currentViewRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					setFullImage(false);
				} else {
					setFullImage(true);
				}
			}
		});

		// Load user preferences
		currentViewRadioButton.setSelected(userPrefs.getBoolean(
				"currentViewRadioButton", true));
		type.setSelectedIndex(userPrefs.getInt("Type", 0));
		scale.setText(userPrefs.get("Scale", "1.0"));


		
		fileNameLabel.setDisplayedMnemonic(KeyEvent.VK_F);
		fileNameLabel.setLabelFor(file);
		fileNameLabel.setText("File Name:");
		fileNameLabel.setBounds(10, 14, 74, 14);
		mainPanel.add(fileNameLabel);

		fileTypeLabel.setDisplayedMnemonic(KeyEvent.VK_T);
		fileTypeLabel.setLabelFor(type);
		fileTypeLabel.setText("File Type:");
		fileTypeLabel.setBounds(10, 44, 74, 14);
		mainPanel.add(fileTypeLabel);

		return mainPanel;
	}

	/**
	 * Save the full image or just the current canvas?
	 * 
	 * @param Save full image?
	 */
	private void setFullImage(boolean myfullImage) {
		fullImage = myfullImage;
		scale.setEnabled(myfullImage);
		rescaleLabel.setEnabled(myfullImage);
		imageSize.setEnabled(myfullImage);
	}

	/**
	 * Creates a new instance of ExportDialog with all the standard export
	 * filetypes.
	 * @param creator The "creator" to be written into the header of the file (may be null)
	 */
	public HEPDialog(String creator) 
	{

		super(null, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		try {
			if (baseDir == null)
				baseDir = System.getProperty("user.home");
		} catch (SecurityException x) {
			trusted = false;
		}

		ButtonListener bl = new ButtonListener();

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(480, 160));
		panel.setLayout(null);
		panel.add(createPanel());
		type.setMaximumRowCount(16); // rather than 8

		browse.addActionListener(bl);
		advanced.addActionListener(bl);
		type.setRenderer(new SaveAsRenderer());
		type.addActionListener(bl);
		setMessage(panel);
	}

	Rectangle2D originalSize = null;

	boolean fullImage = true;

	public void showHEPDialog(Component parent, String title, Component target,
			String defFile) {
		showHEPDialog(parent, title, target, defFile, false);
	}

    /**
     * Show the dialog.
     * @param parent The parent for the dialog
     * @param title The title for the dialog
     * @param target The component to be saved.
     * @param defFile The default file name to use.
     */
	public void showHEPDialog(Component parent, String title, Component target,
			String defFile, boolean fullImage) {
		if (target instanceof GFrame) {
			this.gframe = (GFrame) target;
			originalSize = gframe.getFullImageSize();
		} else {
			this.component = target;
		}

		updateSize();

		advanced.setEnabled(currentType() != null
				&& currentType().hasOptionPanel());
		if (trusted) {
			// Get last filename
			file.setText(userPrefs.get("Filename", ""));
			if (file.getText() == "") {
				// No filename? Then set to file in home directory
				defFile = baseDir + File.separator + defFile;
				File f = new File(defFile);
				if (currentType() != null)
					f = currentType().adjustFilename(f, props);
				file.setText(f.toString());
			}
		} else {
			file.setEnabled(false);
			browse.setEnabled(false);
		}

		JDialog dlg = createDialog(parent, title);
		dlg.pack();
		dlg.setVisible(true);
	}

	private ExportFileType currentType() {
		return (ExportFileType) type.getSelectedItem();
	}

	/**
	 * Called to open a "file browser". Override this method to provide special
	 * handling (e.g. in a WebStart app)
	 * 
	 * @return The full name of the selected file, or null if no file selected
	 */
	protected String selectFile() {
		JFileChooser dlg = new JFileChooser();
		String f = file.getText();
		if (f != null)
			dlg.setSelectedFile(new File(f));
		dlg.setFileFilter(currentType().getFileFilter());
		if (dlg.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
			return dlg.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * Called to acually write out the file. Override this method to provide
	 * special handling (e.g. in a WebStart app)
	 * 
	 * @return true if the file was written, or false to cancel operation
	 */
	protected boolean writeFile(ExportFileType t) throws IOException {
		File f = new File(file.getText());
		if (f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					"Replace existing file?");
			if (ok != JOptionPane.OK_OPTION)
				return false;
		}

		if (gframe != null) {
			if (fullImage) {
				gframe.writeFullImage(file.getText(), getEFType(t), scaling,
						props);
			} else {
				HEPWriter.export(file.getText(), gframe, getEFType(t), props);
			}
		} else {
			HEPWriter.export(file.getText(), component, getEFType(t), props);
		}

		baseDir = f.getParent();
		return true;
	}

	private int getEFType(ExportFileType t) {
		Integer i = (Integer) efts.get(t);
		if (i == null) {
			return (HEPWriter.PNG);
		} else {
			return (i.intValue());
		}
	}

	public void setValue(Object value) {
		if (value instanceof Integer
				&& ((Integer) value).intValue() == OK_OPTION) {
			try {
				if (!writeFile(currentType()))
					return;
			} catch (IOException x) {
				JOptionPane.showMessageDialog(this, x, "Error...",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		super.setValue(value);
	}

	private GFrame gframe = null;
	private Component component = null;
	private static double scaling = 1;
	private boolean trusted = true;
	private Properties props = new Properties();
	private static String baseDir = null;

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == browse) {
				String fileName = selectFile();
				if (fileName != null) {
					if (currentType() != null) {
						File f = currentType().adjustFilename(
								new File(fileName), props);
						file.setText(f.getPath());
					} else {
						file.setText(fileName);
					}
				}
			} else if (source == advanced) {
				JPanel panel = currentType().createOptionPanel(props);
				int rc = JOptionPane
						.showConfirmDialog(
								HEPDialog.this,
								panel,
								"Options for " + currentType().getDescription(),
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE);
				if (rc == JOptionPane.OK_OPTION) {
					currentType().applyChangedOptions(panel, props);
					File f1 = new File(file.getText());
					File f2 = currentType().adjustFilename(f1, props);
					if (!f1.equals(f2) && file.isEnabled())
						file.setText(f2.toString());
				}
			} else if (source == type) {
				advanced.setEnabled(currentType().hasOptionPanel());
				File f1 = new File(file.getText());
				File f2 = currentType().adjustFilename(f1, props);
				if (!f1.equals(f2) && file.isEnabled())
					file.setText(f2.toString());
			}

			userPrefs.putBoolean("currentViewRadioButton",
					currentViewRadioButton.isSelected());
			userPrefs.put("Filename", file.getText());
			userPrefs.put("Scale", scale.getText());
			userPrefs.putInt("Type", type.getSelectedIndex());

		}
	}

	private static class SaveAsRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -2766383579391267646L;

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if (value instanceof ExportFileType) {
				this.setText(((ExportFileType) value).getFileFilter()
						.getDescription());
			}
			return this;
		}
	}
}
