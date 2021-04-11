package paintPackage;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

import javax.swing.*;

/* Histogram */	

@SuppressWarnings("serial")
public class GenHistoDialog extends JDialog
{

	int[] imgArr;
	Map<Integer, Integer> histo = new ConcurrentHashMap<Integer, Integer>();
	
	public GenHistoDialog(Image img, int w, int h)
	{
		// Bildarray initialisieren und mit Farbwerten befuellen
		imgArr = new int[w * h];
		PixelGrabber grab = new PixelGrabber(img, 0, 0, w, h, imgArr, 0, w);
		try 
		{
			grab.grabPixels();
		} catch (InterruptedException e) {}
		putColorsInHashMap();
		// Darstellung der Histogram
		setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 400 / 2, 
				(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - 200 / 2,
				400,200);
		JPanel p = new JPanel()
		{
			{
				setPreferredSize(new Dimension(histo.size(),findMaxValue()));
			}
			@Override
			public void paintComponent(Graphics g)
			{
				g.clearRect(0, 0, getWidth(), getHeight());
				int i = 0; 
				Iterator<Entry<Integer, Integer>> it = histo.entrySet().iterator();	
				while (it.hasNext()) 
				{
					Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>) it.next();
					g.setColor(new Color(pair.getKey()));
					g.drawLine(i, getHeight(), i, getHeight() - pair.getValue());
					++i;
				}
			}
		};
		JScrollPane scrollPane = new JScrollPane(p, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		setVisible(true);
	}
	
	// Maximum der vorkommender Farbe finden 
	public int findMaxValue()
	{
		int i = 0;
		Iterator<Entry<Integer, Integer>> it = histo.entrySet().iterator();	
		while (it.hasNext()) 
		{
			Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>) it.next();
			if(pair.getValue() > i)
				i = pair.getValue();
		}
		return i;
	}
	
	// einfuegen der Farben ins HashMap
	public void putColorsInHashMap() 
	{
		for (int i = 0; i < imgArr.length; i++) 
		{
			if (histo.get(imgArr[i]) != null) 
			{
				int counter = histo.get(imgArr[i]);
				histo.put(imgArr[i], ++counter);
			} 
			else 
			{
				histo.put(imgArr[i], 1);
			}
		}
	}
}