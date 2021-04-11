package paintPackage;

// Farbsubstitution

public class GenApprox {
	
	// Rotfarbarray
	public int[] rArr;
	// Gruenfarbarray
	public int[] gArr;
	// Blaufarbenarray
	public int[] bArr;
	
	// Deklaration des Anfangswertes fuer jede moegliche Farbe des Pixels
	int rPix;
	int gPix;
	int bPix;
	
	// Berechne den Abstand zwischen zwei Farben dem "pix"-Farbwert und r-|g-|b-Farbwert
	// Satz des Pythagoras(a*a + b*b + c*c = d*d) 
	public double calcDif(int zahl1, int zahl2) {
		int r = (((zahl1 >> 16) & 0xff) - ((zahl2 >> 16) & 0xff));
		int g = (((zahl1 >> 8) & 0xff) - ((zahl2 >> 8) & 0xff));
		int b = ((zahl1 & 0xff) - (zahl2 & 0xff));
		return (double) Math.sqrt((r * r) + (g * g) + (b * b));
	}

	// Findet die naehste Farbe fuer den Pixel
	public int searchClosestCol(int pix) {
		rPix = (pix >> 16) & 0xff;
		gPix = (pix >> 8) & 0xff;
		bPix = pix & 0xff;

		// Binaersuche fuer alle Farbanteile fuer aktuellen Pixel
		// liefert bestimmter Farbe die naehste Farbe 
		int rIndex = ApproxBinSearch.binSearch(rArr, rPix, 16);
		int gIndex = ApproxBinSearch.binSearch(gArr, gPix, 8);
		int bIndex = ApproxBinSearch.binSearch(bArr, bPix, 0);

		// rechten und linken Nachbarn fuer den Vergleich deklarieren und initialisieren
		int rRed = 0;
		int lRed = 0;
		int rGreen = 0;
		int lGreen = 0;
		int rBlue = 0;
		int lBlue = 0;
		
		// mit naehstem Array-Wert in Abhaengigkeit zu bestimmter Farbe(r-^g-^b- Farbe)
		// den linken und rechten Nachbarindex bestimmen:
		// Rot
		if (rPix < ((rArr[rIndex] >> 16) & 0xff)) {
			if (rIndex - 1 >= 0) {
				lRed = rIndex - 1;
			}
			rRed = rIndex;
		} else {
			lRed = rIndex;
			if (rIndex + 1 < rArr.length) {
				rRed = rIndex + 1;
			}
		}
		// Gruen
		if (gPix < ((gArr[gIndex] >> 8) & 0xff)) {
			if (gIndex - 1 >= 0) {
				lGreen = gIndex - 1;
			}
			rGreen = gIndex;
		} else {
			lGreen = gIndex;
			if (gIndex + 1 < gArr.length) {
				rGreen = gIndex + 1;
			}
		}
		// Blau
		if (bPix < (bArr[bIndex] & 0xff)) {
			if (bIndex - 1 >= 0) {
				lBlue = bIndex - 1;
			}
			rBlue = bIndex;
		} else {
			lBlue = bIndex;
			if (bIndex + 1 < bArr.length) {
				rBlue = bIndex + 1;
			}
		}
		
		// Abstandsberechnung
		double rDist1 = calcDif(pix, rArr[lRed]);
		double rDist2 = calcDif(pix, rArr[rRed]);
		double gDist1 = calcDif(pix, gArr[lGreen]);
		double gDist2 = calcDif(pix, gArr[rGreen]);
		double bDist1 = calcDif(pix, bArr[lBlue]);
		double bDist2 = calcDif(pix, bArr[rBlue]);
		
		// Standartwert fuer die neue Farbe, der kleinste der Abstaende 
		// zwischen dem linken und rechten Nachbar der Farbe
		int newColor = rArr[lRed];
		double dist = rDist1;
		if (rDist2 < dist) {
			dist = rDist2;
			newColor = rArr[rRed];
		}
		if (gDist1 < dist) {
			dist = gDist1;
			newColor = gArr[lGreen];
		}
		if (gDist2 < dist) {
			dist = gDist2;
			newColor = gArr[rGreen];
		}
		if (bDist1 < dist) {
			dist = bDist1;
			newColor = bArr[lBlue];
		}
		if (bDist2 < dist) {
			dist = bDist2;
			newColor = bArr[rBlue];
		}

		// nach "links" oder "rechts" gehen
		boolean rR = true;
		boolean rG = true;
		boolean rB = true;
		boolean lB = true;
		boolean lG = true;
		boolean lR = true;
		
		// Variablen fuer rechten und linken Weg
		rRed = rIndex;
		lRed = rIndex;
		rGreen = gIndex;
		lGreen = gIndex;
		rBlue = bIndex;
		lBlue = bIndex;
		// "distTemp" besseren Abstand gefunden
		double distTmp = 0;
	
		// Verbesserte Abstandssuche fuer alle Farbanteile in beide Richtungen
		// vom gefunden Index der Binaersuche
		while (lR || rR || lB || rB || lG || rG) {
			// Red Max:
			// "rR" ist Wahr und "rRed" ist kleiner als "rArr" laenge und "rArr[rRed]"(Farbanteil) ist kleiner als "rArr[rIndex]"(Farbanteil) + "dist"
			if (rR && rRed < rArr.length && ((rArr[rRed] >> 16) & 0xFF) < (((rArr[rIndex] >> 16) & 0xFF)) + dist) {
				if (dist > (distTmp = calcDif(pix, rArr[rRed]))) {
					dist = distTmp;
					newColor = rArr[rRed];
				}
				++rRed;
			} else {
				rR = false;
			}
			// Red Min:
			if (lR && lRed >= 0 && ((rArr[lRed] >> 16) & 0xFF) > (((rArr[rIndex] >> 16) & 0xFF)) - dist) {
				if (dist > (distTmp = calcDif(pix, rArr[lRed]))) {
					dist = distTmp;
					newColor = rArr[lRed];
				}
				--lRed;
			} else {
				lR = false;
			}
			if (rG && rGreen < gArr.length && ((gArr[rGreen] >> 8) & 0xFF) < (((gArr[gIndex] >> 8) & 0xFF)) + dist) {
				if (dist > (distTmp = calcDif(pix, gArr[rGreen]))) {
					dist = distTmp;
					newColor = gArr[rGreen];
				}
				++rGreen;
			} else {
				rG = false;
			}
			if (lG && lGreen >= 0 && ((gArr[lGreen] >> 8) & 0xFF) > (((gArr[gIndex] >> 8) & 0xFF)) - dist) {
				if (dist > (distTmp = calcDif(pix, gArr[lGreen]))) {
					dist = distTmp;
					newColor = gArr[lGreen];
				}
				--lGreen;
			} else {
				lG = false;
			}
			if (rB && rBlue < bArr.length && (bArr[rBlue] & 0xFF) < ((bArr[bIndex] & 0xFF)) + dist) {
				if (dist > (distTmp = calcDif(pix, bArr[rBlue]))) {
					dist = distTmp;
					newColor = bArr[rBlue];
				}
				++rBlue;
			} else {
				rB = false;
			}
			if (lB && lBlue >= 0 && (bArr[lBlue] & 0xFF) > ((bArr[bIndex] & 0xFF)) - dist) {
				if (dist > (distTmp = calcDif(pix, bArr[lBlue]))) {
					dist = distTmp;
					newColor = bArr[lBlue];
				}
				--lBlue;
			} else {
				lB = false;
			}
		}
		return newColor;
	}
}
