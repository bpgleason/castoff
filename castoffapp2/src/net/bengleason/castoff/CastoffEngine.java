package net.bengleason.castoff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Determine how many pages a manuscriptText (Word document or text file) will
 * become once it is designed and typeset.<br/>
 * 
 * For more information, please see the Wikipedia entry on <a
 * href="http://en.wikipedia.org/wiki/Castoff_(publishing)">Castoff
 * (publishing)</a><br/>
 * 
 * This program can be quite accurate with basic manuscripts, but it's only as
 * accurate as the data given by the user. <br/>
 * 
 * @version First Perl version completed 2010-05-13
 * @version 0.1.2 (2011-05-05)
 * @version 0.2 (in progress 2012-07-12)
 * @author Ben Gleason bengleason.net
 * 
 *         TODO reinvent how to deal with verse lines private int verseLines; //
 *         number of lines of verse private int verseLineChars; // number of
 *         characters per line of verse public int getNumberOfVerseLines(){
 *         return verseLines; } public void setVerseLines(int v) {verseLines =
 *         v;verseLineChars = 60;}
 *         
 *         IN PROGRESS: now using a single Element structure for all elements;
 *         you can tell whether it's a keymark by whether the keymark field is
 *         null.
 */
public class CastoffEngine {
	private final int SIGNATURE = 16;
	private String manuscriptString; // entire MS
	private int charactersPerPage; // number of chars per page in model
	protected ArrayList<Element> elementsList;

	/**
	 * Default constructor, sets all fields to empty or zero (non-null) values.
	 */
	public CastoffEngine() {
		elementsList = new ArrayList<Element>(0);
		manuscriptString = ""; // just a placeholder
		charactersPerPage = 0; // just a placeholder
	}
	
	/**
	 * copy constructor
	 * @param o
	 */
	public CastoffEngine(final CastoffEngine o) {
		this.elementsList = new ArrayList<Element>(o.elementsList);
		this.manuscriptString = new String(o.manuscriptString);
		this.charactersPerPage = o.charactersPerPage;
	}

	/**
	 * Note: you have to fill in charactersPerPage with an accurate number.
	 * 
	 * @param manuscriptString
	 *            entire text of the manuscript in a String object
	
	public CastoffEngine(String manuscriptString) {
		this.elementsList = new ArrayList<Element>(0);
		this.manuscriptString = manuscriptString.toLowerCase(); // entire MS, now all lowercase
		this.charactersPerPage = 1; // just a placeholder
	}
	 */
	
	/**
	 * Constructor.
	 * @param manuscriptString
	 * @param charactersPerPage
	 */
	public CastoffEngine(final String manuscriptString, final int charactersPerPage) {
		this.elementsList = new ArrayList<Element>(0);
		this.manuscriptString = manuscriptString.toLowerCase();
		this.charactersPerPage = charactersPerPage;
	}

	/**
	 * Constructor using a File pointing at a docx, doc, txt, or pdf file.
	 * 
	 * @param manuscriptFile
	
	public CastoffEngine(File manuscriptFile) {
		this.elementsList = new ArrayList<Element>(0);
		this.manuscriptString = getTextFromManuscript(manuscriptFile).toLowerCase();
		this.charactersPerPage = 1; // just a placeholder
	}
	 */
	
	/**
	 * Constructor using a File pointing at a docx, doc, txt, or pdf file.
	 * @param manuscriptFile
	 * @param charactersPerPage
	 */
	public CastoffEngine(final File manuscriptFile, final int charactersPerPage) {
		this.elementsList = new ArrayList<Element>(0);
		this.manuscriptString = getTextFromManuscript(manuscriptFile).toLowerCase();
		this.charactersPerPage = charactersPerPage;
	}

	/**
	 * Constructor using a File pointing at a docx, doc, txt, or pdf file, which
	 * gets the characters per page from a model PDF file.
	 * @param manuscriptFile
	 * @param modelFile
	 * @throws IOException
	 */
	public CastoffEngine(final File manuscriptFile, final File modelFile) throws IOException {
		elementsList = new ArrayList<Element>(0);
		manuscriptString = getTextFromManuscript(manuscriptFile).toLowerCase();
		charactersPerPage = calculateCharactersPerPage(modelFile);
	}

	/**
	 * @param manuscriptFile
	 * @return String containing the entire text of the document
	 */
	private String getTextFromManuscript(File manuscriptFile) {
		String text = "";
		int c = 0;

		try {

			// extract from docx
			if (manuscriptFile.getName().toLowerCase().endsWith("docx")) {
				FileInputStream input = new FileInputStream(
						manuscriptFile.getAbsolutePath());
				try {
					XWPFWordExtractor ex = new XWPFWordExtractor(
							new XWPFDocument(input));
					text = ex.getText();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Not a valid .docx file!", "Error!",
							JOptionPane.ERROR_MESSAGE);
				}

				// extract from doc
			} else if (manuscriptFile.getName().toLowerCase().endsWith("doc")) {
				FileInputStream input = new FileInputStream(
						manuscriptFile.getAbsolutePath());
				try {
					WordExtractor ex = new WordExtractor(input);
					text = ex.getText();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Not a valid .doc file!", "Error!",
							JOptionPane.ERROR_MESSAGE);
				}

				// extract from txt
			} else if (manuscriptFile.getName().toLowerCase().endsWith("txt")) {
				FileReader reader = new FileReader(manuscriptFile);
				StringBuffer sb = new StringBuffer();
				while ((c = reader.read()) != -1) {
					sb.append((char) c);
				}
				text = sb.toString();
				reader.close();

				// extract from pdf
			} else if (manuscriptFile.getName().toLowerCase().endsWith("pdf")) {
				PDDocument pddDocument = PDDocument.load(manuscriptFile);
				PDFTextStripper textStripper = new PDFTextStripper();
				text = textStripper.getText(pddDocument);
				pddDocument.close();
			}

		} catch (FileNotFoundException FNFE) {
			// not sure what to do...
		} catch (IOException IOE) {
			// not sure what to do...
		} catch (Exception generic) {
			// not sure what to do...
		}
		return text;
	}

	/**
	 * @return number of characters per page in the model book.
	 */
	public int getCharactersPerPage() {
		return charactersPerPage;
	}
	
	/**
	 * Set the number of characters per page in the model book.
	 * @param c
	 */
	public void setCharactersPerPage(int c) {
		charactersPerPage = c;
	}

	/**
	 * Add an element with a keymark.
	 * @param name
	 * @param value
	 * @param keymark
	 */
	public void addElement(final String name, final double value, final String keymark) {
		
		// if the keymark already exists, just update its name and value
		 for (Element x : elementsList) {
			if (x.hasKeymark()
					&& x.getKeymark().toUpperCase().equals(keymark.toUpperCase())) {
				x.setName(name);
				x.setValue(value);
				return;
			}
		}

		 // otherwise, create a new Element
		 try {
			 	elementsList.add(new Element(name, value, keymark));

		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}

	/**
	 * Add an element without a keymark.
	 * @param name
	 * @param value
	 * @param times
	 */
	public void addElement(final String name, final double value, final int times) {
		elementsList.add(new Element(name, value, times));
	}
	
	/**
	 * Returns the castoff including extra pages, rounded down to the nearest
	 * multiple of 16
	 * 
	 * @return castoff including extra pages, rounded down to the nearest
	 *         multiple of 16
	 */
	public int getShortCastoff() {
		return getCastoff() - getCastoff() % SIGNATURE;
		
	}

	/**
	 * Returns the castoff including extra pages, rounded up to the nearest
	 * multiple of 16
	 * 
	 * @return castoff including extra pages, rounded up to the nearest multiple
	 *         of 16
	 */
	public int getLongCastoff() {
		return getCastoff() + SIGNATURE - getCastoff() % SIGNATURE;
	}

	/**
	 * @return total raw value of number of characters in the manuscriptText
	 */
	public int getTotalChars() {
		return manuscriptString.length();
	}

	/**
	 * @return castoff including extra pages, but not rounded to an even signature
	 */
	public int getCastoff() {
		
		int extras = 0;
		for (Element e : elementsList)
			extras += e.getValue() * e.getTimes();
		return extras + manuscriptString.length() / charactersPerPage;
	}
	
	/**
	 * @return castoff without any extras, not rounded to an even signature
	 */
	public int getRawCastoff() {
		return manuscriptString.length() / charactersPerPage;
	}

	/**
	 * Returns an array of all Elements.
	 * @return array of all Elements
	 */
	public Element[] getAllElementsArray() {
		if (elementsList.isEmpty()) {
			return new Element[0];
		} else {
			Element[] array = new Element[elementsList.size()];
			for (int i = 0; i < elementsList.size(); i++) {
				array[i] = elementsList.get(i);
			}
			return array;
		}
	}

	/**
	 * Returns an array of all Elements that have keymarks.
	 * @return array of all marked Elements
	 */
	public Element[] getMarkedElements() {
		if (elementsList.isEmpty()) {
			return new Element[0];
		} else {
			ArrayList<Element> markedElementsList = new ArrayList<Element>();
			for (int i = 0; i < elementsList.size(); i++) {
				if (elementsList.get(i).getKeymark() != null) {
					markedElementsList.add(elementsList.get(i));
				}
			}

			Element[] result = new Element[markedElementsList.size()];
			for (int i = 0; i < markedElementsList.size(); i++) {
				result[i] = markedElementsList.get(i);
			}
			return result;
		}
	}

	/**
	 * Returns an array of all Elements that don't have keymarks.
	 * @return array of all unmarked elements
	 */
	public Element[] getUnmarkedElements() {
		if (elementsList.isEmpty()) {
			return new Element[0];
		} else {
			ArrayList<Element> unmarkedElementsList = new ArrayList<Element>();
			for (int i = 0; i < elementsList.size(); i++) {
				if (elementsList.get(i).getKeymark() == null) {
					unmarkedElementsList.add(elementsList.get(i));
				}
			}

			Element[] result = new Element[unmarkedElementsList.size()];
			for (int i = 0; i < unmarkedElementsList.size(); i++) {
				result[i] = unmarkedElementsList.get(i);
			}
			return result;
		}
	}

	/**
	 * Returns a String representation of all Elements with keymarks.
	 * @return String representation of all Elements with keymarks
	 */
	public String getMarkedElementsString() {
		if (elementsList.isEmpty()) {
			return "";
		} else {
			StringBuffer result = new StringBuffer();

			for (Element k : getMarkedElements()) {
				result.append(k.toString() + "\n");
			}
			return result.toString();
		}
	}

	/**
	 * Returns a String representation of all Elements without keymarks.
	 * @return String representation of all Elements without keymarks
	 */
	public String getUnmarkedElementsString() {
		if (elementsList.isEmpty()) {
			return "";
		} else {
			StringBuffer result = new StringBuffer();

			for (Element k : getUnmarkedElements()) {
				result.append(k.toString() + "\n");
			}
			return result.toString();
		}
	}

	/**
	 * Returns a String representation of all Elements.
	 * 
	 * @return String representation of all Elements
	 */
	public String getAllElementsString() {
		StringBuffer result = new StringBuffer();

		for (Element k : elementsList) {
			result.append(k.toString() + "\n");
		}
		return result.toString();
	}
	
	/**
	 * Removes the first Element equivalent to the specified Element, ignoring case.
	 * @param element
	 * @return if Element exists and was removed; otherwise returns false
	 */
	public boolean removeElement(Element element) {
		for (int i = 0; i < elementsList.size(); i++) {
			if (elementsList.get(i).equalsIgnoreCase(element)){
				elementsList.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the first element with the specified keymark.
	 * 
	 * @param keymark
	 * @return true if keymark exists and was removed; otherwise returns false
	 */
	public boolean removeElement(String keymark) {
		for (int i = 0; i < elementsList.size(); i++) {
			if (elementsList.get(i).getKeymark().equalsIgnoreCase(keymark)) {
				elementsList.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the element with the specified index if it exists.
	 * 
	 * @param index
	 * @return true if element existed and was removed; otherwise returns false
	 */
	public boolean removeElement(int index) {
		try {
			elementsList.remove(index);
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * @return number of marked Elements (i.e., keymarks)
	 */
	public int getNumberOfMarkedElements() {
		int count = 0;
		for (Element x : elementsList) {
			if (x.getKeymark() != null)
				count++;
		}
		
		return count;
	}

	/**
	 * @return number of unmarked (non-keymark) Elements
	 */
	public int getNumberOfUnmarkedElements() {
		int count = 0;
		for (Element x : elementsList) {
			if (x.getKeymark() == null)
				count++;
		}
		
		return count;
	}

	/**
	 * Returns number of all Elements by calling the size() method of the elementsList
	 * @return number of all Elements
	 */
	public int getNumberOfAllElements() {
		return elementsList.size();
	}
	


	/**
	 * Returns a File pointing to the manuscript, which must be a doc, docx, 
	 * pdf, or txt.
	 * @return File object representing the manuscript.
	 */
	public static File getManuscriptFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"plain text, Word doc or docx, or PDF", "doc", "docx", "pdf",
				"txt");
		chooser.setFileFilter(filter);
		int result = chooser.showDialog(null, "Select manuscript to cast off");
		if (result != JFileChooser.CANCEL_OPTION) {
			File theFile = chooser.getSelectedFile();
			while (!filter.accept(theFile)) {
				JOptionPane.showMessageDialog(null,
						"The MS must be a doc, docx, or pdf!", "Error",
						JOptionPane.ERROR_MESSAGE);
				result = chooser.showDialog(null, "Select manuscript to cast off");
			}
			return theFile;
		} else
			return null;
	}
	
	/**
	 * Returns a File pointing to the model PDF.
	 * @return File object representing the PDF of the model book
	 */
	public static File getModelFile() {
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF",
				"pdf");
		chooser.setFileFilter(filter);

		int result = chooser.showDialog(null,
				"Select PDF of book to cast off against");
		chooser.setToolTipText("Select CANCEL to enter a number manually.");
		if (result != JFileChooser.CANCEL_OPTION) {

			File theFile = chooser.getSelectedFile();
			while (!filter.accept(theFile)) {
				JOptionPane.showMessageDialog(null,
						"The model file can only be a pdf!", "Error",
						JOptionPane.ERROR_MESSAGE);
				result = chooser.showDialog(null,
						"Select PDF of book to cast off against");
			}
			return theFile;
		} else
			return null;
	}

	 /**
     * TODO Smart exception handling to catch non-pdf files.
	 * 
     * @param pdfFile
     * @return average characters per page in model pdf
     * @throws IOException 
     */
	public static int calculateCharactersPerPage(File pdfFile) throws IOException {
		PDDocument pddDocument = PDDocument.load(pdfFile);
		PDFTextStripper textStripper = new PDFTextStripper();
		
		int maxChars1 = 0;
		int maxChars2 = 0;
		int maxChars3 = 0;
		int chars = 0;

		for (int i = 30; i < 35; i++) {
			chars = 0;
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);
			chars = textStripper.getText(pddDocument).length();
			if (chars > maxChars1) {
				maxChars1 = chars;
			}
		}
		for (int i = 30; i < 35; i++) {
			chars = 0;
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);
			chars = textStripper.getText(pddDocument).length();
			if (chars > maxChars2) {
				maxChars2 = chars;
			}
		}
		for (int i = 30; i < 35; i++) {
			chars = 0;
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);
			chars = textStripper.getText(pddDocument).length();
			if (chars > maxChars3) {
				maxChars3 = chars;
			}
		}
		pddDocument.close();
		return (int) ((maxChars1 + maxChars2 + maxChars3) / 3); // average
	}

	/**
	 * An element is something that will add pages to the book when the
	 * manuscript is typeset, but it isn't labeled in the manuscript with a
	 * keymark. For example: index, illustrations, foreword, author bio. (For
	 * elements that are labeled in the manuscript, see the derived class,
	 * MarkedElement.)
	 * 
	 * @author Ben Gleason bengleason.net
	 * 
	 */
	class Element implements Comparable<Element> {
		protected String keymark;
		private String name;
		private double value;
		private int times;

		/**
		 * Constructor for an element with a keymark. The number of times
		 * the element appears is determined by calling the getKeymarkCount()
		 * method in the CastoffEngine.
		 * @param name
		 * @param value
		 * @param keymark
		 * @throws Exception 
		 */
		public Element(final String name, final double value,
				final String keymark) throws Exception {
			this.name = name;
			
			if (value < 0)
				throw new Exception("negative value");
			else
				this.value = value;
			
			if (keymark == null)
				throw new Exception("null keymark");
			else
				this.keymark = keymark;
			
			this.times = 0;
			if (!manuscriptString.isEmpty()) {
				// Element constructor now lowercases the manuscript string
				//String big = manuscriptText.toLowerCase();

				int p = manuscriptString.indexOf(keymark.toLowerCase());
				while (p >= 0) {
					this.times++;
					p = manuscriptString.indexOf(keymark.toLowerCase(), p + 1);
				}
			}
		}
		
		/**
		 * Constructor for an element without keymarks.
		 * 
		 * @param name
		 * @param value
		 * @param times
		 */
		public Element(final String name, final double value, final int times) {
			this.keymark = null;
			this.name = name;
			setValue(value);
			setTimes(times);
		}

		/**
		 * Set this Element's name, e.g., "chapter title," "index," or "photo."
		 * @param name
		 */
		public void setName(final String name) {
			this.name = name;
		}

		/**
		 * Returns this Element's name.
		 * @return name 
		 */
		public String getName() {
			return name;
		}

		/**
		 * Set the number of pages this element takes up.
		 * @param v
		 */
		public void setValue(final double value) {
			if (value < 0)
				this.value = 0;
			else 
				this.value = value;
		}

		/**
		 * Get the number of pages this element will take up.
		 * @return the element's value (number of pages the element will occupy)
		 */
		public double getValue() {
			return value;
		}

		/**
		 * Set the number of times this element occurs in the manuscript.
		 * @param times
		 */
		public void setTimes(final int times) {
			if (times < 0) 
				this.times = 0;
			else 
				this.times = times;
		}
		
		/**
		 * Get the number of times this element occurs in the manuscript.
		 * @return times the element will appear in the book
		 */
		public int getTimes() {
			return times;
		}

		/**
		 * 
		 * @return this Element's keymark, which will be null if it hasn't one
		 */
		public String getKeymark() {
			return keymark;
		}
		
		/**
		 * 
		 * @return true if this Element has a keymark
		 */
		public boolean hasKeymark() {
			return keymark != null;
		}

		@Override
		public String toString() {
			return String.format("%s, %s (%dx, %.2f pp. each): %.2f pp.",
					getName(), (hasKeymark() ? getKeymark()
							: "(no keymark)"), getTimes(), getValue(), getTimes()
							* getValue());
		}

		@Override
		public int compareTo(Element arg0) {
			if (this.getKeymark() == null) {
				return this.getName().compareTo(arg0.getName());
			} else return this.getKeymark().compareTo(arg0.getKeymark());
		}
		
		public int compareToIgnoreCase(Element arg0) {
			if (this.getKeymark() == null) {
				return this.getName().compareToIgnoreCase(arg0.getName());
			} else return this.getKeymark().compareToIgnoreCase(arg0.getKeymark());
		}
		
		@Override
		public boolean equals(Object e) {
			return this.compareTo((Element)e) == 0;
		}
		
		public boolean equalsIgnoreCase(Object e) {
			return this.compareToIgnoreCase((Element)e) == 0;
		}

	} // end inner class Element
}
