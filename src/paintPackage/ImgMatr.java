package paintPackage;


public class ImgMatr 
{
	
	// Zweidimensionale Matrize
	private double[][] m;

	public ImgMatr(double[][] m) 
	{
		this.m = m;
	}
	
	// invertierte x-Scherung
	public static ImgMatr xShearInverted(double sx) 
	{
		double d_M[][] = { { 1, -sx, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// x-Scherung
	public static ImgMatr xShear(double sx) 
	{
		double d_M[][] = { { 1, sx, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// invertierte y-Scherung
	public static ImgMatr yShearInverted(double sy) 
	{
		double d_M[][] = { { 1, 0, 0 }, { -sy, 1, 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// y-Scherung
	public static ImgMatr yShear(double sy) 
	{
		double d_M[][] = { { 1, 0, 0 }, { sy, 1, 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}
	
	// invertierte Rotation
	public static ImgMatr rotateInverted(double grad) 
	{
		// Bogenmass
		double rad = -(Math.PI * grad / 180);
		double d_M[][] = { { Math.cos(rad), -Math.sin(rad), 0 },{ Math.sin(rad), Math.cos(rad), 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// Rotation
	public static ImgMatr rotate(double grad) 
	{
		// Bogenmass
		double rad = (Math.PI * grad / 180);
		double d_M[][] = { { Math.cos(rad), -Math.sin(rad), 0 },{ Math.sin(rad), Math.cos(rad), 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// invertierte Skalierung
	public static ImgMatr scaleInverted(double s) 
	{
		double d_M[][] = { { 1 / s, 0, 0 }, { 0, 1 / s, 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// Skalierung
	public static ImgMatr scale(double s) 
	{
		double d_M[][] = { { s, 0, 0 }, { 0, s, 0 }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}
	

	// invertierte Translation
	public static ImgMatr translateInverted(int dx, int dy) 
	{
		double d_M[][] = { { 1, 0, -dx }, { 0, 1, -dy }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// Translation
	public static ImgMatr translate(int dx, int dy) 
	{
		double d_M[][] = { { 1, 0, dx }, { 0, 1, dy }, { 0, 0, 1 } };
		return new ImgMatr(d_M);
	}

	// Matrixmultiplikation
	public static ImgMatr multiply(ImgMatr m1, ImgMatr m2) 
	{
		double[][] res = new double[3][3];
		for (int i = 0; i < 3; ++i) 
		{
			for (int j = 0; j < 3; j++) 
			{
				for (int k = 0; k < 3; k++) 
				{
					res[i][j] += +m1.m[k][j] * m2.m[i][k];
				}
			}
		}
		return new ImgMatr(res);
	}
	
	// Matrix-/Vektor-multiplikation
	public static ImgMathVec multiply(ImgMatr m1, ImgMathVec v) 
	{
		int[] res = new int[m1.m[0].length];
		for (int i = 0; i < m1.m[0].length; i++) 
		{
			for (int j = 0; j < m1.m.length; j++) 
			{
				res[i] += m1.m[i][j] * v.getVectorValueByIndex(j);
			}
		}
		ImgMathVec vNew = new ImgMathVec(res[0], res[1]);
		return vNew;
	}

	// Einheitsmatrix
	public static ImgMatr diagOneMatrix() {
		double[][] ma = new double[3][3];
		for (int i = 0; i < ma.length; i++) {
			for (int j = 0; j < ma[i].length; j++)
				ma[i][j] = 0;
		}
		ma[0][0] = 1;
		ma[1][1] = 1;
		ma[2][2] = 1;
		return new ImgMatr(ma);
	}

	// Matrizenkopie
	public static ImgMatr copyMatrix(ImgMatr m1) {
		double[][] ma = new double[3][3];
		for (int i = 0; i < m1.getMa().length; i++)
			for (int j = 0; j < m1.getMa()[i].length; j++)
				ma[i][j] = m1.getMa()[i][j];
		return new ImgMatr(ma);
	}
	
	// holle die Matrix
	public double[][] getMa() {
		return m;
	}

	// setze die Matrix
	public void setMa(double[][] ma) {
		this.m = ma;
	}
}