/*
 * Castoff GUI App
 * By Ben Gleason
 * Earlier modified 2011-05-24
 * Last modified 2013-01-15
 * 
 * TODO print castoff data to file
 * TODO better help
 * TODO applet version?
 */
package net.bengleason.castoff;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.bengleason.castoff.CastoffEngine.Element;

/**
 * Main class and GUI.
 * 
 * @author Ben Gleason bengleason.net
 */
@SuppressWarnings("serial")
public class Main extends JFrame {

	public static CastoffEngine castoffEngine;
	private File MSFile, modelFile;

	// constants for sizes of windows and fields
	private final Dimension FRAMESIZE = new Dimension(500, 500);
	private final int DEFAULT_CHARACTERS_PER_PAGE = 2100;
	private final int WIDE_FIELD_WIDTH = 25; 
	private final int NARROW_FIELD_WIDTH = 8;
	
	private JPanel elementsPanel, elementsPanel_1, resultsPanel,
			resultsPanel_1, resultsPanel_2, resultsPanel_3, resultsPanel_4,
			resultsPanel_5, resultsPanel_6, resultsPanel_7;
	private JList<Element> elementsList;
	private DefaultListModel<Element> listModel;
	private JScrollPane elementsScrollPane;
	private JButton modifyElementButton, addElementButton;
	//private JLabel manuscriptLabel, modelLabel;
	private JTextField manuscriptTextField, modelTextField;
	private JFormattedTextField charactersPerPageTextField;
	private JLabel rawCastoffLabel, basicCastoffLabel, longCastoffLabel, shortCastoffLabel;
	
	// one listener to hear them all
	private Listener listener;

	public Main() {
		super("Castoff Calculator by Ben Gleason");
		setSize(FRAMESIZE);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(0,1));
		listener = new Listener();
		try {
			MSFile = CastoffEngine.getManuscriptFile();
			if (MSFile == null) {
				System.exit(0);
			}
			modelFile = CastoffEngine.getModelFile();
			if (modelFile == null) {
				castoffEngine = new CastoffEngine(MSFile, DEFAULT_CHARACTERS_PER_PAGE);
			} else {
				castoffEngine = new CastoffEngine(MSFile, modelFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		resultsPanel = new JPanel();
		resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
		manuscriptTextField = new JTextField(MSFile.getName(), WIDE_FIELD_WIDTH);
		//manuscriptLabel = new JLabel(MSFile.getName());
		if (modelFile != null) {
			//modelLabel = new JLabel(modelFile.getName());
			modelTextField = new JTextField(modelFile.getName(), WIDE_FIELD_WIDTH);
		} else {
			//modelLabel = new JLabel("(none)");
			modelTextField = new JTextField("(none)",WIDE_FIELD_WIDTH);
		}
		rawCastoffLabel = new JLabel();
		basicCastoffLabel = new JLabel();
		longCastoffLabel = new JLabel();
		shortCastoffLabel = new JLabel();
		charactersPerPageTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		charactersPerPageTextField.addPropertyChangeListener("value", listener);
		charactersPerPageTextField.setColumns(NARROW_FIELD_WIDTH);
		
		resultsPanel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_1.add(new JLabel("Manuscript: "));
		resultsPanel_1.add(manuscriptTextField);
		//resultsPanel_1.add(manuscriptLabel);
		resultsPanel.add(resultsPanel_1);
		
		resultsPanel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_2.add(new JLabel("Model book: "));
		resultsPanel_2.add(modelTextField);
		//resultsPanel_2.add(modelLabel);
		resultsPanel.add(resultsPanel_2);
		
		resultsPanel_3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_3.add(new JLabel("Characters per page: "));
		
		resultsPanel_3.add(charactersPerPageTextField);
		resultsPanel.add(resultsPanel_3);
		
		resultsPanel_4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_4.add(new JLabel("Raw castoff: "));
		resultsPanel_4.add(rawCastoffLabel);
		resultsPanel.add(resultsPanel_4);
		
		resultsPanel_5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_5.add(new JLabel("Basic castoff: "));
		resultsPanel_5.add(basicCastoffLabel);
		resultsPanel.add(resultsPanel_5);
		
		resultsPanel_6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_6.add(new JLabel("Previous signature: "));
		resultsPanel_6.add(shortCastoffLabel);
		resultsPanel.add(resultsPanel_6);
		
		resultsPanel_7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resultsPanel_7.add(new JLabel("Next signature: "));
		resultsPanel_7.add(longCastoffLabel);
		resultsPanel.add(resultsPanel_7);
		
		elementsPanel = new JPanel();
		elementsPanel = new JPanel(new BorderLayout());
		elementsPanel.setBorder(
                BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.black),
                "Keymarks and other elements"));
        elementsPanel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modifyElementButton = new JButton("Remove selected element");
        modifyElementButton.addActionListener(listener);
        modifyElementButton.setEnabled(false);

        listModel = new DefaultListModel<Element>();
        elementsList = new JList<Element>(listModel);
        elementsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        elementsList.addListSelectionListener(listener);
        elementsScrollPane = new JScrollPane(elementsList);
        addElementButton = new JButton("Add...");
        addElementButton.addActionListener(listener);
        elementsPanel_1.add(addElementButton);
        elementsPanel_1.add(modifyElementButton);
        elementsPanel.add(elementsScrollPane, BorderLayout.CENTER);
        elementsPanel.add(elementsPanel_1, BorderLayout.NORTH);
        //elementsPanel.add(elementsPanel_2);
        
        // TODO: remove these test statements
		castoffEngine.addElement("Test", 2, 5);
		castoffEngine.addElement("Foo", 7, 11);
		castoffEngine.addElement("chapter title", 5, "[[CT]]");
		add(resultsPanel);
		add(elementsPanel);
		
		updateLabels();
		setVisible(true);
	} // end constructor

	private void updateLabels() {
		charactersPerPageTextField.setValue(new Integer(castoffEngine
				.getCharactersPerPage()));
		rawCastoffLabel.setText("" + castoffEngine.getRawCastoff());
		basicCastoffLabel.setText("" + castoffEngine.getCastoff());
		longCastoffLabel.setText("" + castoffEngine.getLongCastoff());
		shortCastoffLabel.setText("" + castoffEngine.getShortCastoff());
		listModel.clear();
		for (Element e : castoffEngine.getAllElementsArray()) {
			listModel.addElement(e);
		}
	}

	class Listener implements ActionListener, PropertyChangeListener,
			ListSelectionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == addElementButton) {
				new ModifyElementFrame();
			} else if (e.getSource() == modifyElementButton) {
				new ModifyElementFrame(elementsList.getSelectedValue());
			}
		} // end actionPerformed()

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			if (e.getSource() == charactersPerPageTextField) {
				if (charactersPerPageTextField.getValue() == null)
					castoffEngine.setCharactersPerPage(DEFAULT_CHARACTERS_PER_PAGE);
				else {
					
					try {
						castoffEngine
								.setCharactersPerPage(((Number) charactersPerPageTextField
										.getValue()).intValue());
					} catch (ClassCastException cce) {
						cce.printStackTrace();
					}
				}
				updateLabels();
			}
			
		} // end propertyChange()
		
		public void valueChanged(ListSelectionEvent e) {
		    if (elementsList.isSelectionEmpty())
		    	modifyElementButton.setEnabled(false);
		    else
		    	modifyElementButton.setEnabled(true);
		   
		} // end valueChanged()

	} // end inner class Listener

	public static void main(String[] args) throws Exception {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				new Main();

			}
		});
	} // end main()
} // end class Main()

