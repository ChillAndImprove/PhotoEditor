package paintPackage;

// Anzeige fuer die Bilder/ Speicherung, sortierung der Farbwerte etc. 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

@SuppressWarnings("serial")
public class GenApproxFrm extends JFrame {
	// Bild:
	// Panel fuer das veraenderte Bild
	JPanel p2;
	// geaendertes Bild
	Image changedImg;
	// Origin Img
	Image originImg;
	// Panel fuer die beiden Bilder
	JPanel p;
	// Image Source fuer die erstellung vom Bild erforderlich
	MemoryImageSource mImgSrc;
	// Array von dem aus die Berechnungen stattfinden
	int[] imgOriginArr;
	// Arry der veraendert wird
	int[] imgArr;
	// Bildbreite
	int imgW;
	// Bildhoehe
	int imgH;

	// HashMap:
	// unsortierte "HashMap"
	Map<Integer, Integer> unsortHashMap = new ConcurrentHashMap<Integer, Integer>();
	// sortierte "HashMap"
	Map<Integer, Integer> sortHashMap = new HashMap<Integer, Integer>();

	// Slider:
	JSlider slider;

	// Naeherungsverfahren:
	// Groesse des Farbanteils
	int sizeColArr;
	// Alle Farben-Array
	private int[] colArr;
	// erhoehen in "createArrays"-Methode notwendig
	int counter;

	// Vater-Fenster:
	GenFrm f;

	// Speicher-Button
	JButton b;

	// Approximation
	GenApprox genApprox;

	public GenApproxFrm(GenFrm f, Image img, int w, int h, GenApprox genApprox) {
		this.f = f;
		this.genApprox = genApprox;
		imgW = w;
		imgH = h;
		setLayout(new BorderLayout());

		// initialisierung von dem originalem Bild
		originImg = img.getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(originImg, 0);
		try {
			mt.waitForAll();
		} catch (Exception ex) {}

		// initialisierung von dem veraenderbares Bild
		changedImg = originImg;
		imgArr = new int[imgW * imgH];

		// JSlider initialisieren
		slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(1);
		slider.addChangeListener(e -> {
			if (!slider.getValueIsAdjusting()) {
				if (slider.getValue() == 0) {
					// bei 0, automatischer sprung auf 1
					slider.setValue(1);
				}
				// Berechnen neue Farb-Array-Groesse
				sizeColArr = sortHashMap.size() * slider.getValue() / 100;
				// ersellen das Bild mit dem neuen Farbanteil
				createNewImg();
				// speicher-Button
				if (slider.getValue() != 100)
					b.setEnabled(true);
				else
					b.setEnabled(false);
			}
		});

		// Bildarray initialisieren und mit Farbwerten befuellen
		imgOriginArr = new int[imgW * imgH];
		PixelGrabber grab = new PixelGrabber(originImg, 0, 0, imgW, imgH, imgOriginArr, 0, imgW);
		try {
			grab.grabPixels();
		} catch (InterruptedException e) {
		}

		// HashMap mit Farbwerten befuellen:
		fillHashMap();
		
		// Standartwert dem Nicht veraendertem Bild zuweisen:
		sizeColArr = unsortHashMap.size();
		
		// sortiere die HashMap, initialisiere und befuelle das "colArr":
		createArray();

		// Bilder-Panel's initialisieren
		p = new JPanel();
		p.setLayout(new GridLayout(0, 2));

		// veraenderbar Bild Panel
		p2 = new JPanel() {
			{
				setPreferredSize(new Dimension(imgW, imgH));
			}

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(changedImg, 0, 0, getWidth(), getHeight(), null);
				g.setColor(Color.BLUE);
				g.drawString(sizeColArr + " Farben", 0, 10);
			}
		};

		// Originalbild Panel
		JPanel p1 = new JPanel() {
			{
				setPreferredSize(new Dimension(imgW, imgH));
			}

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(originImg, 0, 0, getWidth(), getHeight(), null);
				g.setColor(Color.BLUE);
				g.drawString(unsortHashMap.size() + " Farben", 0, 10);
			}
		};
		// Bild-Panel's in eine andere Panel einfuegen
		p.add(p1);
		p.add(p2);
		// scroll-Panel, fuer Faelle falls das Bild/die Bilder zu gross
		// wird/werden
		JScrollPane scrollP = new JScrollPane(p);

		// Closing-Event:
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				f.setVisible(true);
				dispose();
			}
		});

		// speicher-Button:
		b = new JButton("veraendertes Bild speichern unter ...") {
			{
				addActionListener(e -> {

					JFileChooser chooser = new JFileChooser();
					if (chooser.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
						BufferedImage bufferedImage = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
						bufferedImage.getGraphics().drawImage(changedImg, 0, 0, null);
						try {
							ImageIO.write(bufferedImage, "jpg", new File("" + chooser.getSelectedFile()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		};
		b.setEnabled(false);

		// Panel fur den Slider und den Button
		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p3.add(slider, BorderLayout.NORTH);
		p3.add(b, BorderLayout.SOUTH);

		// rest
		setResizable(false);
		add(scrollP, BorderLayout.CENTER);
		add(p3, BorderLayout.SOUTH);
		pack();
		// groesse des Fensters und die Position setzen
		setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2,
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2, getWidth(), getHeight());
		setVisible(true);
	}

	// erstellen und anzeigen des Bildes mit den neuen Farbanteilen
	public void createNewImg() {
		// Farb-Arrays auf den festgelegten Farbanteil setzen
		genApprox.rArr = new int[sizeColArr];
		genApprox.gArr = new int[sizeColArr];
		genApprox.bArr = new int[sizeColArr];
		for (int i = 0; i < sizeColArr; ++i) {
			genApprox.rArr[i] = colArr[i];
			genApprox.gArr[i] = colArr[i];
			genApprox.bArr[i] = colArr[i];
		}
		// sortiert immer wieder nach r-|g-|b- Aufsteigend
		sort();
		// fuegt die Farben hinzu
		for (int i = 0; i < imgArr.length; i++) {
			imgArr[i] = genApprox.searchClosestCol(imgOriginArr[i]);
		}
		// fuegen das neue Bild dem GenFrm-Fenster/p-Panel/changeImg-Bild
		// hinzu und Zeichnung aktualisieren
		mImgSrc = new MemoryImageSource(p2.getWidth(), p2.getHeight(), imgArr, 0, p2.getWidth());
		changedImg = createImage(mImgSrc);
		p2.repaint();
	}

	public void sort() {
		new ApproxQuicksort(genApprox.rArr, 16);
		new ApproxQuicksort(genApprox.gArr, 8);
		new ApproxQuicksort(genApprox.bArr, 0);
	}

	// Addiere die Farben von dem aktuellem FarbhashMap zu den RGB-Arrays
	public void createArray() {
		counter = 0;
		// Farben nach absteigender Reihenfolde sortieren
		sortHashMap = sortByValues(unsortHashMap);
		// Maximal moegliche Array-Groesse von dem Farbanteil,
		// Standartwert = 100 %(sortHashMap.size())
		sizeColArr = sortHashMap.size();
		colArr = new int[sizeColArr];
		// "colArr" mit den Farbwerten aus der sortierter "HashMap"
		// befuellen
		sortHashMap.forEach((col, count) -> {
			if (sizeColArr <= counter)
				return;
			colArr[counter] = col;
			counter++;
		});
	}

	// Sortiere die Map
	public static Map<Integer, Integer> sortByValues(final Map<Integer, Integer> map) {
		Comparator<Integer> valueComparator = new Comparator<Integer>() {
			public int compare(Integer k1, Integer k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};
		Map<Integer, Integer> sortedByValues = new TreeMap<Integer, Integer>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	// einfuegen der Farben ins HashMap
	public void fillHashMap() {
		for (int i = 0; i < imgOriginArr.length; i++) {
			if (unsortHashMap.get(imgOriginArr[i]) != null) {
				int counter = unsortHashMap.get(imgOriginArr[i]);
				unsortHashMap.put(imgOriginArr[i], ++counter);
			} else {
				unsortHashMap.put(imgOriginArr[i], 1);
			}
		}
	}
}
