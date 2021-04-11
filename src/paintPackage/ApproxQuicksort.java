package paintPackage;

// Sortierung des Arrays nach der meist vorkommender Farbe:

public class ApproxQuicksort 
{
	// Array zur Sortierung
	int[] arr;
	// Verschiebungdzahl
	int shift;
	
	public ApproxQuicksort(int[] arr, int shift) {
		if (arr == null || arr.length == 0) 
			return;
		this.arr = arr;
		this.shift = shift;
		quickSortHelp(0, arr.length - 1);
	}

	// Hilfsmethode
	public void quickSortHelp(int left, int right) 
	{
		final int MID = (arr[(left + right) / 2] >> shift) & 0xff;
		int l = left;
		int r = right;
		while (l < r) 
		{
			while (((arr[l] >> shift) & 0xff) - MID < 0) 
			{
				++l;
			}
			while (((arr[r] >> shift) & 0xff) - MID > 0) 
			{
				--r;
			}
			if (l <= r)
				exchangePos(l++, r--);
		}
		if (left < r)
			quickSortHelp(left, r);
		if (right > l)
			quickSortHelp(l, right);
	}

	// vertauschen der Positionen
	public void  exchangePos(int i, int j) 
	{
		int tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}
}
