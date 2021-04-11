package paintPackage;


import javax.swing.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

// Generales-Fenster oder auch Hauptfenster von dem "Bildbearbeitungsprogramms":

@SuppressWarnings("serial")
public class GenFrm extends JFrame 
{
	// Fenster:
	// Standartgroesse von dem Fenster
	int width = 350;
	int height = 300;

	// Bild/er:
	// Panel fuer das/die Bild/er deklarieren
	JPanel p;
	// Bilder ohne festgelegte Groesse
	public Vector<Image> imgVecWithoutSize = new Vector<Image>();
	// Bilder selbst
	public Vector<Image> imgVec = new Vector<Image>();
	// index von dem Bild, welches Bild liegt gerade in dem "Fokus"
	int index = 0;

	// Diashow:
	// Fenster deklarieren
	GenDiashowFrm diashowFrm;
	// starten/stoppen
	volatile boolean startStop;
	// menuItem deklarieren
	JMenuItem menuItem1;
	JMenuItem menuItem2;
	JMenuItem menuItem3;
	JMenuItem menuItem4;
	
	// Histogram
	GenHistoDialog histo;
	JFileChooser saveAsTxt = new JFileChooser();
	
	// Konstruktor:
	public GenFrm() 
	{
		// Titel vergeben:
		super("Hauptfenster");

		// Standort des Fensters und die Groesse dessen festlegen:
		setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2,
				Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2, width, height);

		// Das Bild zeichnen:
		p = new JPanel() 
		{
			{
				setPreferredSize(new Dimension(width, height));
			}

			@Override
			public void paintComponent(Graphics g) 
			{
				g.clearRect(0, 0, getWidth(), getHeight());
				if (imgVec != null) 
				{
					if (!startStop) 
					{
						if (index < imgVec.size() && index >= 0)
							g.drawImage((Image) imgVec.get(index), 0, 0, getWidth(), getHeight(), null);
					}
				}
			}
		};
	  
		// Bilder sequenziel nach vorn/hinten anschauen:
		JButton b1 = new JButton("nächstes") 
		{
			{
				addActionListener(e -> 
				{
					if (index == imgVec.size() - 1) 
					{
						index = 0;
					} else
						++index;
					p.repaint();
				});
			}
		};
		JButton b = new JButton("vorheriges") 
		{
			{
				addActionListener(e -> 
				{
					if (index == 0) 
					{
						index = imgVec.size() - 1;
					} else
						--index;
					p.repaint();
				});
			}
		};
		
		// Fenster-Event:
		addComponentListener(new ComponentAdapter() 
		{
			@Override
			public void componentResized(ComponentEvent e) 
			{
				width = getWidth();
				height = getHeight();
				imgVec.clear();
				for (int i = 0; i < imgVecWithoutSize.size(); ++i) 
				{
					imgVec.addElement(imgVecWithoutSize.get(i).getScaledInstance(p.getWidth(), p.getHeight(),
							Image.SCALE_SMOOTH));
				}
				MediaTracker mt = new MediaTracker(GenFrm.this);
				for (int i = 0; i < imgVec.size(); ++i) 
				{
					mt.addImage(imgVec.get(i), 0);
				}
				try 
				{
					mt.waitForAll();
				} catch (Exception ex) {}
				p.repaint();
			}
		});
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if(histo != null)
					histo.dispose();
				dispose();
			}
		});

		// MenuBar und co.:
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Datei");
		JMenuItem menuItem = new JMenuItem("Bild holen") 
		{
			{
				addActionListener(e -> 
				{
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(GenFrm.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) 
					{
						imgVecWithoutSize.addElement(getToolkit()
								.getImage(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName()));
						imgVec.addElement(getToolkit()
								.getImage(chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName())
								.getScaledInstance(p.getWidth(), p.getHeight(), Image.SCALE_SMOOTH));
					}
					if(imgVec.size() > 0)
					{
						menuItem2.setEnabled(true);
						menuItem3.setEnabled(true);
						menuItem4.setEnabled(true);
						if(imgVec.size() > 1)
							menuItem1.setEnabled(true);
					}
					MediaTracker mt = new MediaTracker(this);
					for (int i = 0; i < imgVec.size(); ++i)
					{
						mt.addImage(imgVec.get(i), 0);
					}
					try 
					{
						mt.waitForAll();
					} catch (Exception ex) {}
					p.repaint();
				});
			}
		};
		menuItem1 = new JMenuItem("Diashow starten") 
		{
			{
				addActionListener(e -> 
				{
					startStop = true;
					if (startStop) 
					{
						diashowFrm = new GenDiashowFrm(GenFrm.this, index);
					} 
					else 
					{
						p.repaint();
					}
				});
			}
		};
		menuItem1.setEnabled(false);
		menuItem2 = new JMenuItem("Bild bearbeiten") 
		{
			{
				addActionListener(e -> 
				{
					if (imgVec.size() != 0) 
					{
						new GenEditFrm(GenFrm.this);
					}
				});
			}
		};
		menuItem2.setEnabled(false);
		// Approximation(Naeherungsverfahren)
		menuItem4 = new JMenuItem("Komprimierungsprozent setzen")
		{
			{
				addActionListener(e -> 
				{
					if (imgVec.size() != 0) 
					{
						GenFrm.this.setVisible(false);
						new GenApproxFrm(GenFrm.this, imgVecWithoutSize.get(index), 
								p.getWidth(), p.getHeight(), new GenApprox());
					}
				});
			}
		};
		menuItem4.setEnabled(false);
		JMenu menu1 = new JMenu("Bearbeiten");
		menu1.add(menuItem2);
		menu1.add(menuItem4);
		JMenu menu2 = new JMenu("Farbenzaehler");
		menuItem3 = new JMenuItem("darstellen und speichern als .txt") 
		{
			{
				addActionListener(e -> 
				{
					if(imgVec.size() > 0)
					{
						histo = new GenHistoDialog(imgVec.get(index), width, height);
						writeHistoToFile();
					}
				});
			}
		};
		menuItem3.setEnabled(false);
		menu2.add(menuItem3);
		menu.add(menuItem);
		menu.add(menuItem1);
		menuBar.add(menu);
		menuBar.add(menu1);
		menuBar.add(menu2);
		setJMenuBar(menuBar);
		JPanel p1 = new JPanel();
		p1.setLayout(new FlowLayout());
		p1.add(b);
		p1.add(b1);
		add(p, BorderLayout.CENTER);
		add(p1, BorderLayout.SOUTH);

		// letzte Schritte:
		pack();
		setVisible(true);
	}
	
	// schreibe histo in die Datei
	public void writeHistoToFile() 
	{
		Runtime runtime = Runtime.getRuntime();
		// txtPath, damit man die Datei nach der Speicherung wieder oeffnen kann
		String txtPath = "default.txt";
		try 
		{
			if (saveAsTxt.showDialog(this, "speichern und oeffnen") == JFileChooser.APPROVE_OPTION) 
			{
				txtPath = "" + saveAsTxt.getSelectedFile();
				FileWriter fileWriter = new FileWriter(txtPath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				Iterator<Entry<Integer, Integer>> iterator = histo.histo.entrySet().iterator();
				bufferedWriter.write("Farbwert und Vorkommensanzahl:");
				bufferedWriter.newLine();
				bufferedWriter.newLine();
				while (iterator.hasNext()) 
				{
					Map.Entry<Integer, Integer> pair = (Map.Entry<Integer, Integer>) iterator.next();
					bufferedWriter.write("" + Integer.toHexString(pair.getKey()) + " " + pair.getValue());
					bufferedWriter.newLine();
				}
				bufferedWriter.close();
				try 
				{
					// oeffnen der .txt 
					runtime.exec("notepad " + txtPath);
				}catch (IOException e) {}
			}
		}catch (Exception e1) {}
	}
}
