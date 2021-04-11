package paintPackage;

import java.awt.*;
import java.awt.image.*;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.*;

// zeigt Diashow

@SuppressWarnings("serial")
public class GenDiashowFrm extends JComponent
{
	// minimale Bildergroesse
	final int W;
	final int H;
	
	// Bild
	Image mImg;
	// int-Array
	int[] mPix;
	// Bild als int-Array
	MemoryImageSource mImgSrc;
	
	// Bilder Pixelarrays
	Vector<int[]> pixVec = new Vector<int[]>();
	
	// Fenster in dem Diashow untergebracht ist
	JFrame frame = new JFrame();
	
	// Index der ueber das Bild-Vector laeuft
	int indexPixVec = -1;
	
	// TODO: die Bilder aus imgWithoutSize raus hollen
	
	// Generales-Diashow-Fenster
	public GenDiashowFrm(GenFrm f, int index) 
	{
		indexPixVec = index;
		W = f.p.getWidth();
		H = f.p.getHeight();
		mPix = new int[f.p.getWidth() * f.p.getHeight()];
		PixelGrabber[] grab = new PixelGrabber[f.imgVec.size()];
		for(int i = 0; i < f.imgVec.size(); ++i)
		{
			if(i < f.imgVec.size() && i >= 0)
			{
				int[] tmp_arr = new int[f.p.getWidth() * f.p.getHeight()];
				pixVec.add(tmp_arr);
				grab[i] = new PixelGrabber(f.imgVec.get(i), 0, 0, f.p.getWidth(), f.p.getHeight(), pixVec.get(i), 0, f.p.getWidth());
				try {
					grab[i].grabPixels();
				} catch (InterruptedException ex) {}
			}
		}
		mImgSrc = new MemoryImageSource(f.p.getWidth(), f.p.getHeight(), mPix, 0, f.p.getWidth());
		mImgSrc.setAnimated(true);
		mImg = createImage(mImgSrc);
		f.setVisible(false);
		frame.setBounds(f.getBounds());
		frame.add(this);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				f.setBounds(frame.getBounds());
				f.startStop = false;
				f.setVisible(true);
				frame.dispose();
			}
		});
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Datei");
		JMenuItem menuItem = new JMenuItem("Diashow stoppen")
		{
			{
				addActionListener(e->
				 {
					 f.setBounds(frame.getBounds());
					 f.startStop = false;
					 f.setVisible(true);
					 frame.dispose();
				 });
			}
		};
		menu.add(menuItem);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
		
		// Laeuft durch das ganze Vector und zeigt ein Bild nach dem Anderem
		new Thread(()->{	
				try 
				{
					while(f.startStop)
					{
						for(int i = 0; i <= 100; ++i)
						{
							shuffle(i);
							Thread.sleep(10);
						}
						if(indexPixVec < pixVec.size() - 1)
							++indexPixVec;
						else
							indexPixVec = 0;
					}
				} catch (Exception ex) {}
		}).start();
	}
	
	public Dimension getPreferredSize() 
	{
		return getMinimumSize();
	}

	public Dimension getMinimumSize() 
	{
		return new Dimension(W, H);
	}

	public void paint(Graphics g) 
	{
		g.drawImage(mImg, 0, 0, getWidth(), getHeight(), this);
	}
	
	private int compColor(int x1, int x2, int p) 
	{
		return x1 + (x2 - x1) * p / 100;
	}

	protected int compPix(int pix1, int pix2, int p) 
	{
		final int RED = compColor((pix1 >> 16) & 0xff, (pix2 >> 16) & 0xff, p);
		final int GREEN = compColor((pix1 >> 8) & 0xff, (pix2 >> 8) & 0xff, p);
		final int BLUE = compColor(pix1 & 0xff, pix2 & 0xff, p);
		return 0xff000000 | (RED << 16) | (GREEN << 8) | BLUE;
	}
	
	public void shuffle(int p1) 
	{
		for (int i = 0; i < W * H; ++i)
		{
			if(indexPixVec < pixVec.size() - 1)
			{
				mPix[i] = compPix(pixVec.get(indexPixVec)[i], pixVec.get(indexPixVec + 1)[i], p1);
			}
			else
			{
				mPix[i] = compPix(pixVec.get(indexPixVec)[i], pixVec.get(0)[i], p1);
			}
		}
		mImgSrc.newPixels();
		repaint();
	}
}
