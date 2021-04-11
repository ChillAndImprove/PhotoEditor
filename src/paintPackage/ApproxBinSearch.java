package paintPackage;

// Suche nach der naehsten Farbe zu bestimmter Pix-Farbe

public class ApproxBinSearch 
{
	// Binaere Suche
	public static Integer binSearch(int[] colArr, int pixKey, int shift) 
	{
		int iL = 0;
		int iR = colArr.length - 1;
		int MIDDLE = (iL + iR) / 2;
		while (iL <= iR) 
		{
			MIDDLE = (iL + iR) / 2;
			final int RES = ((colArr[MIDDLE] >> shift) & 0xff) - pixKey;
			if (RES == 0)
				return MIDDLE;
			else if (RES < 0)
				iL = MIDDLE + 1;
			else
				iR = MIDDLE - 1;
		}
		return MIDDLE;
	}

}