package net.bengleason.castoff;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.bengleason.castoff.CastoffEngine.Element;

/**
 * 
 * "Modifying" an element will actually involve deleting and replacing it.
 * That is, elements will in a sense be immutable, like String objects.
 * 
 * A subclass of JFrame to get input from the user for creating or modifying elements.
 * @author bgleason
 *
 */
@SuppressWarnings("serial")
public class ModifyElementFrame extends JFrame {
	private final int FRAMEWIDTH = 700;
	private final int FRAMEHEIGHT = 400;
	private final int DEFAULT_PAGES_PER_ELEMENT = 1;
	private JTextField nameField, keymarkField;
	private JFormattedTextField numberField, valueField;
	private JButton enterButton, clearButton;
	private JRadioButton keymarkButton, numberButton;
	private ButtonGroup buttonGroup;
	private Listener listener;
	private Element newElement, elementToModify;

	public ModifyElementFrame() {
		super("Add, remove, or change elements");
		setSize(FRAMEWIDTH, FRAMEHEIGHT);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(0,2));
		listener = new Listener();
		
		newElement = elementToModify = null;
		
		nameField = new JTextField();
		keymarkField = new JTextField();
		numberField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		numberField.addPropertyChangeListener("value", listener);
		keymarkButton = new JRadioButton("Keymark?");
		keymarkButton.addActionListener(listener);
		numberButton = new JRadioButton("How many times?");
		numberButton.addActionListener(listener);
		buttonGroup = new ButtonGroup();
		buttonGroup.add(keymarkButton);
		buttonGroup.add(numberButton);
		valueField = new JFormattedTextField(DecimalFormat.getNumberInstance());
		valueField.addPropertyChangeListener("value", listener);
		enterButton = new JButton("Enter");
		enterButton.addActionListener(listener);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(listener);
			
		add(new JLabel(" Name: "));
		add(nameField);
		add(keymarkButton);
		add(keymarkField);
		add(numberButton);
		add(numberField);
		add(new JLabel(" Value (pages): "));
		add(valueField);
		valueField.setText("" + DEFAULT_PAGES_PER_ELEMENT);
		add(clearButton);
		add(enterButton);
		
		
		setVisible(true);
	}
	
	public ModifyElementFrame(final Element e) {
		this();
		nameField.setText(e.getName());
		if (e.hasKeymark()) {
			keymarkButton.setSelected(true);
			keymarkField.setText(e.getKeymark());
			numberField.setEnabled(false);
		} else {
			numberButton.setSelected(true);
			numberField.setText("" + e.getTimes());
			keymarkField.setEnabled(false);
		}
		valueField.setText("" + e.getValue());
		newElement = null;
		elementToModify = e;
	}
	
	private void updateCastoffEngine() {
		
		// check whether any significant change was made
		if (! newElement.equalsIgnoreCase(elementToModify)) {
			// destroy and replace...
			Main.castoffEngine.removeElement(elementToModify);
			//TODO: add a method to the engine: addElement(Element element)
		}
	}
	
	/**
	 * Clears the contents of all text fields.
	 */
	private void clearFields() {
		nameField.setText("");
		keymarkField.setText("");
		numberField.setText("");
		valueField.setText("");
	}
	
	class Listener implements ActionListener, PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			// do nothing...
		} // end propertyChange()

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == keymarkButton 
					|| e.getSource() == numberButton) {
				if (numberButton.isSelected()) {
					numberField.setEnabled(true);
					numberField.requestFocusInWindow();
					keymarkField.setEnabled(false);
					keymarkField.setText("");
				} else if (keymarkButton.isSelected()) {
					numberField.setEnabled(false);
					keymarkField.setEnabled(true);
					keymarkField.requestFocusInWindow();
					numberField.setText("");
				}
				
			} else if (e.getSource() == enterButton) {
				// TODO: check for blank fields
				// TODO: show warning if there are blanks
				
				
			} else if (e.getSource() == clearButton) {
				clearFields();
			}
			
		} // end actionPerformed()
		
	}
}


