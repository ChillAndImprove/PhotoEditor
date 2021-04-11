package paintPackage;


public class ImgMathVec 
{
	// Vektorarray Deklaration
	private int[] v;
	
	// Konstruktor -> Vektorarray initialisierung und Wertezuweisung 
	public ImgMathVec(int x, int y) {
		v = new int[3];
		v[0] = x;
		v[1] = y;
		v[2] = 1;
	}

	// Vektorwert hollen:
	// nach "i" - index
	public int getVectorValueByIndex(int i) {
		return v[i];
	}

	// nach "x"
	public int getXVector() {
		return v[0];
	}

	// nach "y"
	public int getYVector() {
		return v[1];
	}
}
