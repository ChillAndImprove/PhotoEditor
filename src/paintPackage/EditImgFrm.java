package paintPackage;

// Bild selbst

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;


@SuppressWarnings("serial")
class EditImgFrm extends JComponent 
{	
	GenEditFrm father;
	
	// Breite, Hoehe, X-Position und Y-Position des Bildes
	int w = 350;
	int h = 300;
	int imgX = 10;
	int imgY = 10;
	
	// Originalbild "img" fuer das Berechnen und Bild "img1" fuer die Anzeige
	Image img, img1;
	// Array mit dem Originales/Gespeichertes/Veraendertes Bild
	int[] imgPix = new int[w * h];
	// Array mit dem Anzeigebild
	int[] pixView = new int[w * h];
	MemoryImageSource imgSrc;
	
	// Transformation Variablen
	// Bereichsaswahl
	int rectWidth = 0;
	int rectHeight = 0;
	int rectX;
	int rectY;
	// Transformationserlaubnis
	boolean transformAllowed;
	// wenn ein Bereich des Bildes gedreht werden soll,dann wahr
	boolean rectDraw;
	// Punkt um den die Rotation durchgefuehr wird
	Point rectP;
	// Zeichnen des Rotationssymbols ja(true) oder nein(false)
	boolean rotieren;
	int symbolX;
	int symbolY;
	// Bereichstransformation
	// aktuelle invertierte Matrix
	public ImgMatr currentInvertedMatrix;
	// aktuelle Matrix
	public ImgMatr currentMatrix;
	// zuletzt invertierte Matrix
	public ImgMatr lastInvertedMatrix = ImgMatr.diagOneMatrix();
	// letztemal Matrix
	public ImgMatr lastMatrix = ImgMatr.diagOneMatrix();
	// Variablen um Rechteckbereich zu Speichern und zu Kopieren
	int[] arr;
	Polygon pol;
	Image areaImg;
	int areaImgX;
	int areaImgY;
	
	// Zeichnung Variablen
	// beim loslassen der Maus
	volatile boolean mouseReleased;
	// Linie
	volatile boolean line;
	// Kreis
	volatile boolean oval;
	// gefueltes Kreis
	volatile boolean fillOval;
	// bresenham zeichnung Werte
	volatile int[] Poin = new int[4];
	// radius von den Kreisen
	int radius;
	// Prozent-Wert
	public double percentValue;
	// Prozent
	public int percent;
	// Prozent zaehler
	public double percentCounter;
	// farben erster haelfte
	Color firstHalf;
	int red;
	int green;
	int blue;
	// farben zweiter haelfte
	Color secondHalf;
	int red1;
	int green1;
	int blue1;
	// damit's nur einmal, Kreis, gefuelltes Kreis oder Linie gezeichnet wird
	boolean once;
	
	// PopUpMenu
	JPopupMenu popupMenu = new JPopupMenu();
	
	public EditImgFrm(GenEditFrm father, Image img) 
	{
		this.father = father;
		symbolX = imgX;
		symbolY = imgY;
//		System.out.println(symbolY);
		
		try 
		{
//			System.out.println(father.p.getWidth());
			setBounds(father.p.getWidth()/2 - w/2,father.p.getHeight()/2 - h/2,w,h);
			img = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
			MediaTracker mt = new MediaTracker(this);
			mt.addImage(img, 0);
			mt.waitForAll();
			PixelGrabber grab1 = new PixelGrabber(img, 0, 0, w, h, imgPix, 0, w);
			grab1.grabPixels();
			imgSrc = new MemoryImageSource(w, h, pixView, 0, w);
			imgSrc.setAnimated(true);
			img1 = createImage(imgSrc);
			for (int i = 0; i < w * h; ++i) 
			{
				pixView[i] = imgPix[i];
			}
			imgSrc.newPixels();
			repaint();
		} catch (InterruptedException e) {}
		
		// hilfe fuer die Zeichnung des Rechteck's, der Linie, des Kreises
		addMouseMotionListener(new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{ 
				rectWidth = e.getX() - rectX;
				rectHeight = e.getY() - rectY;
				repaint();
			}
		});
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{	
				rectDraw = true;
				rectX = e.getX();
				rectY = e.getY();
				if(line ^ fillOval ^ oval)
				{
					once = false;
					Poin[0] = e.getX() - imgX;
					Poin[1] = e.getY() - imgY;
					mouseReleased = false;
				}
				// wenn usserhalb des Bereiches links "gepresst" wurde
				if(rectX < imgX || rectY < imgY)
				{
					if(rectX < imgX)
						rectX = imgX;
					if(rectY < imgY)
						rectY = imgY;	
				}
			}
			@Override
			public void mouseReleased(MouseEvent e)
			{		  
				// verhindert das man von rechts nach links usw. Bereich anwaehlt
				if(e.getX() < rectX || e.getY() < rectY || rectX > w + imgX || rectY > h + imgY || rectX == e.getX() && rectY == e.getY())
				{
					transformAllowed = false;
					rectDraw = false;
					repaint();
				}
				else
				{
					// wenn ausserhalb des Bildbereiches rechts die Maus "losgelassen" wurde
					if(e.getX() >= w + imgX || e.getY() >= h + imgY)
					{
						if(e.getX() >= w + imgX)
							rectWidth = w - rectX + imgX;
						if(e.getY() >= h + imgY)
							rectHeight = h - rectY + imgY;
					}
					// wenn innerhab des Bildbereiches rechts die Maus "losgelassen" wurde
					if(e.getX() < w + imgX || e.getY() < h + imgY)
					{
						if(e.getX() < w + imgX)
							rectWidth = e.getX() - rectX;
						if(e.getY() < h + imgY)
							rectHeight = e.getY() - rectY;	
					}
					transformAllowed = true;
					repaint();
				}
				// wenn Linie, gefuelltes Kreis oder ungefuelltes Kreis
				if(line ^ fillOval ^ oval)
				{
					Poin[2] = e.getX() - imgX;
					Poin[3] = e.getY() - imgY;
					mouseReleased = true;
					repaint();
				}
			}
		});
		
		// "PopupMenu":
		// Kopiermenueeintrag
		popupMenu.add(new JMenuItem("kopieren"){
			{
				addActionListener(e->{
					if(pol != null)
					{
						// die Kopie
						father.imgCopyW = pol.xpoints[1] - pol.xpoints[0];
						father.imgCopyH = pol.ypoints[2] - pol.ypoints[1];
						father.imgCopy = new int[(pol.xpoints[1] - pol.xpoints[0]) * (pol.ypoints[2] - pol.ypoints[1])];
						int tmp=0;
						// faengt beim Anfang des Rechtecks und hoert auf beim ende des Rechtecks, 
						// wie bei x-Achse, ...
						for (int x = pol.xpoints[0]; x < pol.xpoints[1]; ++x) 
						{
							// ... so auch bei y-Achse
							for(int y = pol.ypoints[1]; y < pol.ypoints[2]; ++y)
								father.imgCopy[tmp++] = imgPix[w * y + x];
						}
					}
				});
			}
		});
		
		// Einfuegemenueeitrag
		popupMenu.add(new JMenuItem("einfuegen")
		{
			{
				addActionListener(e->{
					if(father.imgCopy != null)
					{
						arr = new int[w * h];
						for (int i = 0; i < w * h; ++i) 
						{
							arr[i] = imgPix[i];
						}
						for(int i = 0; i < w * h; ++i)
							arr[i] = compPix(arr[i], Color.GRAY.getRGB(), 50);
						int tmp = 0;
						for(int x = 0; x < father.imgCopyW; ++x)
							for(int y = 0; y < father.imgCopyH; ++y)
								arr[w * y + x] = father.imgCopy[tmp++];
						imgSrc = new MemoryImageSource(w, h, arr, 0, w);
						imgSrc.setAnimated(true);
						areaImg = createImage(imgSrc);
						imgSrc.newPixels();
						EditImgFrm.this.repaint();
				    	father.requestFocus();
					}
				});
			}
		});
		// reaktion auf alle Mausereignisse
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
	
	// Methode fuer die anzeige des "PopupMenu's"
	protected void processMouseEvent(MouseEvent e) 
	{
		// ueberprueft ob "PopupMenu" angezeigt werden soll
		if (e.isPopupTrigger())  
		{
			// wenn if(wahr), dann wird "PopupMenu" angezeigt
			popupMenu.show(e.getComponent(),e.getX(),e.getY());
		}
		// auf weitere/andere Mausereignisse reagieren
		super.processMouseEvent(e);
	}
	
/*       Matrix       */
	
	// transformationen des ganzes Bildes
	public void transform(ImgMatr m)
	{
		setNewPixView();
		for (int i = 0; i < w; i++) 
		{
			for (int j = 0; j < h; j++) 
			{
				ImgMathVec v =  ImgMatr.multiply(m, new ImgMathVec(i, j));
				if(v.getXVector() >= 0 && v.getYVector() >= 0 && v.getXVector() < w && v.getYVector() < h)
				{
					// j = y i = x
					pixView[w * j + i] = imgPix[w * v.getYVector() + v.getXVector()];
				}
			}
		}
		setNewPicture();
	}
	
	// setze den pix-Array "pixView" wieder neu auf 
	public void setNewPixView()
	{
		pixView = new int[w * h];
		for (int i = 0; i < w * h; ++i) 
		{
			pixView[i] = imgPix[i];
		}
	}
	public void setNewPicture()
	{
		imgSrc = new MemoryImageSource(w, h, pixView, 0, w);
		img1 = createImage(imgSrc);
		imgSrc.newPixels();
		repaint();
	}
	
	// Bereichstransformation:
	// Rotation fuer den Rechteckbereich wird aus dem "EditTransformFrm" abgerufen 
	public void rotate(Point p, double rotate) 
	{
					  // Matrizen multiplikation-(invertierte Tranlation) 
			transform(matrixInversMultiplicationWithTranslationRotation(ImgMatr.rotateInverted(rotate), p),
					// Matrizen multiplikation-(Translation)  
					matrixMultiplicationWithTranslationRotation(ImgMatr.rotate(rotate), p), 
					// Punktepolygon
					pol);
	}
		
	//Translation fuer den Rechteckbereich 
	public void translate(int x, int y) 
	{
			transform(matrixMultiplicationWithTranslation(ImgMatr.translateInverted(x, y), x, y),
					ImgMatr.translate(x, y), 
					pol);
	}
	
	//Scalierung fuer den Rechteckbereich 
	public void scale(double scaleValue) 
	{
		transform(matrixMultiplicationWithTranslation(ImgMatr.scaleInverted(scaleValue)),
				matrixMultiplicationWithTranslation(ImgMatr.scale(scaleValue)), 
				pol);
	}
	
	// Scherung fuer den Rechteckbereich 
	public void shear(double x, double y) 
	{
				 // invertierte Scherung
		transform(ImgMatr.multiply(ImgMatr.xShearInverted(x), ImgMatr.yShearInverted(y)), 
				// Scherung
				ImgMatr.multiply(ImgMatr.yShear(y), ImgMatr.xShear(x)), 
				// Polygon-/"pol"-Werte
				pol);
	}
	
	// Matrizen multiplikation-(Translation) fuer die Rotation
	private ImgMatr matrixMultiplicationWithTranslationRotation(ImgMatr m, Point p1) 
	{
		ImgMatr mm = ImgMatr.multiply(ImgMatr.translate(-p1.x, -p1.y), m);
		mm = ImgMatr.multiply(mm, ImgMatr.translate(p1.x, p1.y));
		return mm;
	}
	
	// Matrizen multiplikation-(invertierte Tranlation) fuer die Rotaion
	private ImgMatr matrixInversMultiplicationWithTranslationRotation(ImgMatr m, Point p1) 
	{
		ImgMatr mm = ImgMatr.multiply(ImgMatr.translateInverted(p1.x, p1.y), m);
		mm = ImgMatr.multiply(mm, ImgMatr.translateInverted(-p1.x, -p1.y));
		return mm;
	}

	// Invertierte Translation fuer die Translation
	private ImgMatr matrixMultiplicationWithTranslation(ImgMatr m, int x, int y) {
		ImgMatr mm = null;
		mm = ImgMatr.multiply(ImgMatr.translateInverted(x, y), m);
		mm = ImgMatr.multiply(mm, ImgMatr.translateInverted(-x, -y));
		return mm;
	}
	
	// Invertirte Translation fuer die Skalierung
	private ImgMatr matrixMultiplicationWithTranslation(ImgMatr m) 
	{
		// Die Verschiebung nach Unten
		ImgMatr mm = null;
		mm = ImgMatr.multiply(ImgMatr.translateInverted(pol.xpoints[0], pol.ypoints[0]), m);
		mm = ImgMatr.multiply(mm,ImgMatr.translateInverted(-pol.xpoints[0], -pol.ypoints[0]));
		return mm;
	}
		
	boolean scale;
	// Transformation (fuer alle Bereichsmatrizen notwendig)
	public void transform(ImgMatr mInver, ImgMatr m, Polygon p) 
	{
		currentInvertedMatrix = ImgMatr.copyMatrix(mInver);
		currentMatrix = ImgMatr.copyMatrix(m);
		if (lastInvertedMatrix != null && scale) 
		{
			currentInvertedMatrix = ImgMatr.multiply(lastInvertedMatrix, mInver);
			currentMatrix = ImgMatr.multiply(m, lastMatrix);
		} 
		else if (lastInvertedMatrix != null) 
		{
			currentMatrix = ImgMatr.multiply(lastMatrix, m);
			currentInvertedMatrix = ImgMatr.multiply(mInver, lastInvertedMatrix);
		}
		transformHelper(currentInvertedMatrix, currentMatrix);
	}
	// Transformationshelfer
	public void transformHelper(ImgMatr mInv, ImgMatr m) 
	{
		Point min = calculateMin(pol, m);
		Point max = calculateMax(pol, m);
		pixView = new int[w * h];
		if (max.x > w && max.y > h) 
		{
			max = new Point(w, h);
		}
		// setze den pix-Array "pixView" wieder neu auf durch die kopie des Originalen Arrays 
		System.arraycopy(imgPix, 0, pixView, 0, w * h);
		min = checkIfMinOutOfCompon(min);
		max = checkIfMaxOutOfCompon(max);
		for (int x = min.x; x < max.x; ++x) 
		{
			for (int y = min.y; y < max.y; ++y) 
			{
				ImgMathVec v = ImgMatr.multiply(mInv, new ImgMathVec(x, y));
				if (pol.contains(v.getXVector(), v.getYVector())) 
				{
					pixView[w * y + x] = imgPix[w * v.getYVector() + v.getXVector()];
				}
			}
		}
		setNewPicture();
	}
	
	// Damit das ganze nicht verschwindet 
	// Berechne Punkteminimum
	private Point calculateMin(Polygon p, ImgMatr m) 
	{
		int minX = imgX;
		int minY = imgY;
		for (int i = 0; i < p.xpoints.length; i++) 
		{
			ImgMathVec ver = ImgMatr.multiply(m, new ImgMathVec(p.xpoints[i], p.ypoints[i]));
			if (minX > ver.getXVector())
			{
				minX = ver.getXVector();
			}
			if (minY > ver.getYVector()) 
			{
				minY = ver.getYVector();
			}
		}
		return new Point(minX, minY);
	}
	// Berechne Punktemaximum
	private Point calculateMax(Polygon p, ImgMatr m) 
	{
		// die ersten Werte sind die anfangswerte von dem Bild 
		int maxX = imgX;
		int maxY = imgY;
		// iteriere ueber die Laenge von den "pol"(p)-X-Punkt-Werte und die Punkte 
		for (int i = 0; i < p.xpoints.length; i++) 
		{
			ImgMathVec ver = ImgMatr.multiply(m, new ImgMathVec(p.xpoints[i], p.ypoints[i]));
			if (maxX < ver.getXVector())
			{
				maxX = ver.getXVector();
			}
			if (maxY < ver.getYVector()) 
			{
				maxY = ver.getYVector();
			}
		}
		return new Point(maxX, maxY);
	}
	// Checke, ob minimum kleiner als Komponentminimum(Bildminimum) ist
	private Point checkIfMinOutOfCompon(Point min) 
	{
		if (min.x < imgX || min.y < imgY)
			return new Point(0, 0);
		return min;
	}
	// Checke, ob maximum groesser als Komponentmaximum(Bildmaximum) ist
	private Point checkIfMaxOutOfCompon(Point max) 
	{
		if (max.x > w || max.y > h)
			return new Point(w, h);
		return max;
	}
	
	// Notwendig fuer alle der Bereichstranformationen und fuer die Kopie
	private void rectImgCreateHelpMethod() 
	{
		pol = new Polygon();
		// Punkt oben Links
		pol.addPoint(rectX - imgX, rectY - imgY);
		// Punkt oben Rechts
		pol.addPoint(rectX + rectWidth - imgX, rectY - imgY);
		// Punkt unten Rechts
		pol.addPoint(rectX + rectWidth - imgX, rectY + rectHeight - imgY);
		// Punkt unten Links
		pol.addPoint(rectX - imgX, rectY + rectHeight - imgY);
		// zeichnungserlaubnis
		rectDraw = true;
	}
	
	
/* Bresenham */
	
	// x0 und y0-anfang der Linie. x1 und y1-ende der Linie. iCircle ist'n Kreis
	// oder nicht
	public void drawLine(int x0, int y0, int x1, int y1, boolean oval) 
	{
		final int dx = Math.abs(x0 - x1);
		final int dy = Math.abs(y0 - y1);
		final int sgnDx = x0 < x1 ? 1 : -1;
		final int sgnDy = y0 < y1 ? 1 : -1;
		int shortD, longD, incXshort, incXlong, incYshort, incYlong;
		if (dx > dy) 
		{
			shortD = dy;
			longD = dx;
			incXlong = sgnDx;
			incXshort = 0;
			incYlong = 0;
			incYshort = sgnDy;
		}
		else 
		{
			shortD = dx;
			longD = dy;
			incXlong = 0;
			incXshort = sgnDx;
			incYlong = sgnDy;
			incYshort = 0;
		}
		int d = longD / 2, x = x0, y = y0;
		for (int i = 0; i <= longD; ++i) 
		{
			setPixel(x, y, oval);
			x += incXlong;
			y += incYlong;
			d += shortD;
			if (d >= longD) 
			{
				d -= longD;
				x += incXshort;
				y += incYshort;
			}
		}
		if (!oval)
		{
			save();
			setNewPicture();
		}
	}

	// setze Pixel
	boolean onceonce;
	public void setPixel(int x, int y, boolean oval) 
	{
		if (!oval) 
		{
			percentCounter += percentValue;
			percent = (int) percentCounter;
		}
		if (x < w && x > 0 && y < h && y > 0) 
		{
			pixView[w * y + x] = compPix(firstHalf.getRGB(), secondHalf.getRGB(), percent);
		}
	}
	
	// berechne den ProzenWert(ueber die laenge der Linie) und den Radius
	public void calculateDif() 
	{
		int distX = Poin[0] - Poin[2] < 0 ? -(Poin[0] - Poin[2])
				: Poin[0] - Poin[2];
		int distY = Poin[1] - Poin[3] < 0 ? -(Poin[1] - Poin[3])
				: Poin[1] - Poin[3];
		double distMax = (double)(distX < distY ? distY : distX);
		if (line)
		{
			percentValue = 100 / distMax;
		}
		else 
		{
			radius = (int)distMax;
			percentValue = 100 / (Math.PI * radius);
		}
	}
	
	// Farben verschieben/mischen aus der Vorlesung
	public int compPix(int pix1, int pix2, int p) 
	{
		final int RED = compColor((pix1 >> 16) & 0xff, (pix2 >> 16) & 0xff, p);
		final int GREEN = compColor((pix1 >> 8) & 0xff, (pix2 >> 8) & 0xff, p);
		final int BLUE = compColor(pix1 & 0xff, pix2 & 0xff, p);
		return 0xff000000 | (RED << 16) | (GREEN << 8) | BLUE;

	}
	private int compColor(int x1, int x2, int p) 
	{
		return x1 + (x2 - x1) * p / 100;
	}
	
	// Kreis
	// zeichne Kreis: x0 und y0 Mitellpunkt Punkte und r der Radius
	public void drawOval(int x0, int y0, int r) 
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// bei 0 Prozent start ...
		percentCounter = 0;
		while (y <= x) 
		{
			// ... und wird erhoeht
			percentCounter = percentCounter + percentValue;
			percent = (int) percentCounter;
			drawLine(x0 + y, y0 + x, x0 + y, y0 + x, true);
			drawLine(x0 - y, y0 + x, x0 - y, y0 + x, true);
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) {
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		drawOval1(x0, y0, r);
	}
	public void drawOval1(int x0, int y0, int r)
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// bei 50 Prozent mit Varbverlauf beginnen ...
		percentCounter = 50; 
		while (y <= x) 
		{
			// ... und diesen dann verringern
			percentCounter = percentCounter	- percentValue;
			percent = (int) percentCounter;
			drawLine(x0 + x, y0 + y, x0 + x, y0 + y, true);
			drawLine(x0 - x, y0 + y, x0 - x, y0 + y, true);
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) {
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		drawOval2(x0, y0, r);
	}
	public void drawOval2(int x0, int y0, int r) 
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// 50 Prozen Varbverlauf vortfuehren
		percentCounter = 50;
		while (y <= x) 
		{
			// ... und wird erhoeht
			percentCounter = percentCounter + percentValue;
			percent = (int) percentCounter;
			drawLine(x0 + x, y0 - y, x0 + x, y0 - y, true);
			drawLine(x0 - x, y0 - y, x0 - x, y0 - y, true);
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) {
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		drawOval3(x0, y0, r);
	}
	public void drawOval3(int x0, int y0, int r)
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// bei 100 Prozent mit Varbverlauf beginnen ...
		percentCounter = 100; 
		while (y <= x) 
		{
			// ... und diesen dann verringern
			percentCounter = percentCounter	- percentValue;
			percent = (int) percentCounter;
			drawLine(x0 + y, y0 - x, x0 + y, y0 - x, true);
			drawLine(x0 - y, y0 - x, x0 - y, y0 - x, true);
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) {
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		save();
		setNewPicture();
	}

	// Zeichne einen gefuellten Kreis: x0 und y0 die Mitellpunkt Werte und r der Radius
	public void drawFillOval(int x0, int y0, int r) 
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// bei 0 Prozent start ...
		percentCounter = 0;
		while (y <= x) 
		{
			// ... und wird erhoeht
			percentCounter = percentCounter + percentValue;
			percent = (int) percentCounter;
			for (int i = x0 - y; i <= x0; i++) {
				drawLine(i, y0 - x, i + y, y0 - x, true); 
			}
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) 
			{
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		drawFillOval1(x0, y0, r);
	}
	public void drawFillOval1(int x0, int y0, int r) 
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// bei 50 Prozent mit Varbverlauf beginnen ...
		percentCounter = 50; 
		while (y <= x) 
		{
			// ... und diesen dann verringern
			percentCounter = percentCounter	- percentValue;
			percent = (int) percentCounter;
			for (int i = x0 - x; i <= x0; i++) 
			{
				drawLine(i, y0 - y, i + x, y0 - y, true);
			}
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) 
			{
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		drawFillOval2(x0, y0, r);
	}
	public void drawFillOval2(int x0, int y0, int r) 
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		// 50 Prozen Varbverlauf vortfuehren
		percentCounter = 50;
		while (y <= x) 
		{
			percentCounter = percentCounter + percentValue;
			percent = (int)percentCounter;
			for (int i = x0 - x; i <= x0; i++) 
			{
				drawLine(i, y0 + y, i + x, y0 + y, true);
			}
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) 
			{
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		drawFillOval3(x0, y0, r);
	}	
	public void drawFillOval3(int x0, int y0, int r) 
	{
		int y = 0;
		int x = r;
		int F = -r;
		int dy = 1;
		int dyx = -2 * r + 3;
		percentCounter = 100;
		while (y <= x) 
		{
			percentCounter = percentCounter - percentValue;
			percent = (int)percentCounter;
			for (int i = x0 - y; i <= x0; i++) 
			{
				drawLine(i, y0 + x, i + y, y0 + x, true);
			}
			++y;
			dy += 2;
			dyx += 2;
			if (F > 0) 
			{
				F += dyx;
				--x;
				dyx += 2;
			} 
			else 
			{
				F += dy;
			}
		}
		save();
		setNewPicture();
	}

	// Speichert entgueltig das Bild im Array
	public void save()
	{
		for (int i = 0; i < w * h; ++i) 
		{
			imgPix[i] = pixView[i];
		}
	}

	// zeichnen
	public void paintComponent(Graphics g) 
	{
		g.setColor(Color.BLUE);
		g.clearRect(imgX, imgY, w, h);
		if(arr == null)
			g.drawImage(img1,  imgX, imgY, w, h, this);
		else
		{
			g.drawImage(areaImg, imgX, imgY, w, h, this);
		}
		// anzeiger um den sich Rotation statt finden sollte
		if(rotieren)
		{
			rectP = null;
			rectP = new Point(symbolX, symbolY);
			g.drawLine(symbolX - 10, symbolY, symbolX - 5, symbolY);
			g.drawLine(symbolX + 10, symbolY, symbolX + 5, symbolY);
			g.drawLine(symbolX, symbolY - 10, symbolX, symbolY - 5);
			g.drawLine(symbolX, symbolY + 10, symbolX, symbolY + 5);
			g.drawLine(symbolX, symbolY, symbolX, symbolY);
		}
		// zeichne den Bereich umrandet und einmal notiere die Punkte des Bereichs, in welchem Transfomationen
		// durchgefuehrt werden sollen bzw. gezeichnet werden soll, sowie der Bereich kopiert werden soll
		if(rectDraw)
		{
			g.drawRect(rectX, rectY, rectWidth, rectHeight);
			if(transformAllowed)
			{
				transformAllowed = false;
				rectImgCreateHelpMethod();
			}
		}
		if(mouseReleased)
		{
			if(!once)
			{
				firstHalf = new Color(red, green, blue);
				secondHalf = new Color(red1, green1, blue1);
				percent = 0;
				percentCounter = 0;
				percentValue = 0;
				calculateDif();
				if(line)
					drawLine(Poin[0], Poin[1], Poin[2], Poin[3], false);
				else if(oval)
					drawOval(Poin[0], Poin[1], radius);
				else if(fillOval)
					drawFillOval(Poin[0], Poin[1], radius);			
				once = true;
			}
		}
	}
}