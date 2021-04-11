package paintPackage;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

// Generales-Bearbeitungs-Fenster

@SuppressWarnings("serial")
public class GenEditFrm extends JFrame
{
	// Bearbeitung Ausschneiden/Kopieren:
	EditTransformPan transform;
	JPanel transformPanel;
	EditDrawPan draw;
	JPanel drawPanel;
	boolean p1Besetzt;
	// unterschiedliche Baumblaetter des transformPanels
	JTree transformTree;
	// unterschiedliche Baumblaetter des drawPanels
	JTree drawTree;
	// unterschiedliche Baumblaetter des areaPanels
	JTree areaTree;

	// index der auf die Bilder zeigt
	int index;
	// Bilder was fuer die Veraenderungen zur verfuegung stehen
	Vector<EditImgFrm> editImg = new Vector<EditImgFrm>();
	// Panel die die Ordner-Bauume zur Bearbeitung der Bilder beinhaltet 
	JPanel p = new JPanel(new BorderLayout());
	// Panel fuer die Button's die fuer die Bearbeitung ausgewaehlt wurden
	JPanel p1 = new JPanel();
	int abstandZwischenTeiler = 100;
	final JSplitPane splitPane;
	JSplitPane splitPane2;
	
	// Die Bilderseite von dem SplitPane
	JTabbedPane tabbedPane;
	
	// Speicher der allgemeinmatrizen
	ImgMatr transformMatr;
	
	// Kopie eines Bereiches des Bildes
	int[] imgCopy;
	int imgCopyW;
	int imgCopyH;
	
	public GenEditFrm(GenFrm frame)
	{
		frame.setVisible(false);
			
		// Das Bild-/Der Bilderbereich:
		// der Komponent der als Bild erscheint	wird hinzugefuegt	
		editImg.add(index, new EditImgFrm(this, frame.imgVecWithoutSize.get(frame.index)));
		// Fuegt dem ScrollPane "scrollPane1" das Panel "p" hinzu
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(""+index, editImg.get(index));
		tabbedPane.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				GenEditFrm.this.requestFocus();
			}
		});
		// Reaktion auf wechsel zwischen den Tabs und aktualisierung des Fensters 
		tabbedPane.addChangeListener(new ChangeListener() 
		{
	        public void stateChanged(ChangeEvent e) 
	        {
	            index = tabbedPane.getSelectedIndex();
	            drawPanelHelpMethod();
	            transformPanelHelpMethod();
	            p1.removeAll();
				p1.revalidate();
				p.remove(p1);
				p.revalidate();
				splitPane.revalidate();
				splitPane2.revalidate();
	            GenEditFrm.this.revalidate();
	        }
	    });
		
		// die Panel's mit den unterschiedlichen Einstellungsmoeglichkeiten
		transformPanelHelpMethod();
		drawPanelHelpMethod();
		
		// Die Einstellungen:
		JScrollPane scrollPane = new JScrollPane();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true,drawPanel,transformPanel);
		splitPane.setDividerLocation(abstandZwischenTeiler);
		p.add(splitPane, BorderLayout.CENTER);
		// Hat nen coolen Bug hofentlich bleibt dieser auch bei Windows
		// Fuegt dem ScrollPane "scrollPane" das Panel "p" hinzu, der die splitPane "splitPane" beinhaltet
		scrollPane.getViewport().add(p);
		
		// Fenster-Event's:
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				frame.repaint();
				frame.setVisible(true);
				dispose();
			}
		});
		// Wenn man bei der Rotationseinstellung ist, so kann man Punkt aussuchen
		// ueber Tastaturklicks um den es Rotiert werden soll 
		addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(editImg.get(index).rotieren)
				{
					if(e.getKeyChar() == ('w'))
						--editImg.get(index).symbolY;
					if(e.getKeyChar() == ('s'))
						++editImg.get(index).symbolY;
					if(e.getKeyChar() == ('d'))
						++editImg.get(index).symbolX;
					if(e.getKeyChar() == ('a'))
						--editImg.get(index).symbolX;
					editImg.get(index).repaint();
				}
				else if(editImg.get(index).arr != null)
				{
					if(e.getKeyCode() != KeyEvent.VK_ENTER)
					{
						if(e.getKeyChar() == ('w'))
							if(editImg.get(index).areaImgY > 0)
								--editImg.get(index).areaImgY;
						if(e.getKeyChar() == ('s'))
							if(editImg.get(index).areaImgY < editImg.get(index).h - imgCopyH)
								++editImg.get(index).areaImgY;
						if(e.getKeyChar() == ('d'))
							if(editImg.get(index).areaImgX < editImg.get(index).w - imgCopyW)
								++editImg.get(index).areaImgX;
						if(e.getKeyChar() == ('a'))
							if(editImg.get(index).areaImgX > 0)
								--editImg.get(index).areaImgX;
						// arr neue Deklaration
						editImg.get(index).arr = new int[editImg.get(index).w * editImg.get(index).h];
						// arr mit Originalarraybild "imgPix" befuellen
						for (int i = 0; i < editImg.get(index).w * editImg.get(index).h; ++i) 
						{
							editImg.get(index).arr[i] = editImg.get(index).imgPix[i];
						}
						// mit grau mischen
						for(int i = 0; i < editImg.get(index).w * editImg.get(index).h; ++i)
							editImg.get(index).arr[i] = editImg.get(index).compPix(editImg.get(index).arr[i], Color.GRAY.getRGB(), 50);
						// das kopierte Bild hinzufuegen
						int tmp = 0;
						for(int x = editImg.get(index).areaImgX; x < imgCopyW + editImg.get(index).areaImgX; ++x)
							for(int y = editImg.get(index).areaImgY; y < imgCopyH + editImg.get(index).areaImgY; ++y)
								editImg.get(index).arr[editImg.get(index).w * y + x] = imgCopy[tmp++];
						// arr zu Bild umwandeln und zeichnen
						editImg.get(index).imgSrc = new MemoryImageSource(editImg.get(index).w, editImg.get(index).h, 
						editImg.get(index).arr, 0, editImg.get(index).w);
						editImg.get(index).imgSrc.setAnimated(true);
						editImg.get(index).areaImg = createImage(editImg.get(index).imgSrc);
						editImg.get(index).imgSrc.newPixels();
						editImg.get(index).repaint();
					}
					else
					{
						// Originalbild mit der Kopie verschmelzen
						int tmp = 0;
						for(int x = editImg.get(index).areaImgX; x < imgCopyW + editImg.get(index).areaImgX; ++x)
							for(int y = editImg.get(index).areaImgY; y < imgCopyH + editImg.get(index).areaImgY; ++y)
								editImg.get(index).imgPix[editImg.get(index).w * y + x] = imgCopy[tmp++];
						editImg.get(index).arr = null;
						editImg.get(index).setNewPixView();
						editImg.get(index).setNewPicture();
					}
				}
			}
		});
		
		// SplitPane bestehend aus dem Einstellungs- und Bilde/er-Bereich:
		// Beide "Panel's" Scroll-Panel "scrollPane" und "tabbedPane" werden, zu dem erstelltem SplitPane "pane", hinzugefuegt
		splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,scrollPane,tabbedPane);
		splitPane2.setContinuousLayout(!splitPane2.isContinuousLayout());
		splitPane2.setDividerLocation(255);
		add(splitPane2, BorderLayout.CENTER);
		
		// MenuBar und co.:
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Datei");
		JMenuItem menuItem = new JMenuItem("zum Startfenster")
		{
			{
				addActionListener(e->
				 {
					 frame.setVisible(true);
					 dispose();
				 });
			}
		};
		JMenuItem menuItem1 = new JMenuItem("geladene Bilder bearbeiten")
		{
			{
				addActionListener(e->
				{
					drawTree.setSelectionPath(drawTree.getPathForRow(0));
					transformTree.setSelectionPath(transformTree.getPathForRow(0));
					new EditDialogUploadedImg(GenEditFrm.this, frame);
				});
			}
		};
		menu.add(menuItem);
		menu.add(menuItem1);
		menuBar.add(menu);
		setJMenuBar(menuBar);
		
		// letzte Schritte:
		pack();
		setBounds((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - 900 / 2, 
				(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - 400 / 2, 
				900, 400);
		setVisible(true);
	}
	
	public void drawPanelHelpMethod() 
	{
		drawPanel = new JPanel()
		{
			{
				drawTree = new JTree(new DrawNode());
				drawTree.setSelectionPath(drawTree.getPathForRow(0));
				drawTree.addTreeSelectionListener(e -> 
				{
					if (e.getPath().toString().equals("[zeichnen                                    ,"
							+ " ungefuelltes Kreis]")) 
					{
						draw = new EditDrawPan(editImg.get(index), GenEditFrm.this, "ungefuelltesKreis");
						drawHelpMethod();
					} 
					else if (e.getPath().toString().equals("[zeichnen                                    ,"
							+ " gefuelltes Kreis]")) 
					{
						draw = new EditDrawPan(editImg.get(index), GenEditFrm.this, "gefuelltesKreis");
						drawHelpMethod();
					} 
					else if (e.getPath().toString().equals("[zeichnen                                    ,"
							+ " linie]")) 
					{
						draw = new EditDrawPan(editImg.get(index), GenEditFrm.this, "linie");
						drawHelpMethod();
					}
				});
				drawTree.addTreeSelectionListener(new TreeSelectionListener() 
				{
					@Override
					public void valueChanged(TreeSelectionEvent e) 
					{
						editImg.get(index).line = false;
						editImg.get(index).oval = false;
						editImg.get(index).fillOval = false;
						p1Besetzt = false;
						p1.removeAll();
						p1.revalidate();
						p.remove(p1);
						p.revalidate();
						splitPane2.setDividerLocation(250);
						splitPane2.revalidate();
						revalidate();
					}
				});
				add(new JScrollPane(drawTree));
			}

			// Methode die zwieschen den Flaechen-Formen wechselt
			private void drawHelpMethod() 
			{
				if(p1Besetzt)
				{
					p1Besetzt = false;
					p1.removeAll();
					p1.revalidate();
					p.remove(p1);
					p.revalidate();
				}
				transformTree.setSelectionPath(transformTree.getPathForRow(0));
				splitPane2.setDividerLocation(500);
				p1.setPreferredSize(new Dimension(100,100));
				p1.add(draw);
				p.add(p1, BorderLayout.NORTH);
				p.revalidate();
				splitPane2.revalidate();
				revalidate();
				p1Besetzt = true;		
			}
		};
		tabbedPane.setSelectedIndex(index);
	}

	// stellt die einzelnen Bearbeitungsfkt. der EditTransformFrm-Klasse/-Einstellungsmoeglichkeit dar
	public void transformPanelHelpMethod() 
	{
		transformPanel = new JPanel()
		{
			{
				transformTree = new JTree(new TransformNode());
				transformTree.setSelectionPath(transformTree.getPathForRow(0));
				transformTree.addTreeSelectionListener(e -> {
					if (e.getPath().toString().equals("[transformieren                          ,"
							+ " Scherung]")) 
					{
						transform = new EditTransformPan(editImg.get(index), GenEditFrm.this, "Scherung");
						drawTree.setSelectionPath(drawTree.getPathForRow(0));
						splitPane2.setDividerLocation(550);
						transformHelpMethod();
					} 
					else if (e.getPath().toString().equals("[transformieren                          ,"
							+ " Rotation]")) 
					{
						transform = new EditTransformPan(editImg.get(index), GenEditFrm.this, "Rotation");
						drawTree.setSelectionPath(drawTree.getPathForRow(0));
						splitPane2.setDividerLocation(260);
						transformHelpMethod();
					} 
					else if (e.getPath().toString().equals("[transformieren                          ,"
							+ " Skalierung]")) 
					{
						transform = new EditTransformPan(editImg.get(index), GenEditFrm.this, "Skalierung");
						drawTree.setSelectionPath(drawTree.getPathForRow(0));
						splitPane2.setDividerLocation(260);
						transformHelpMethod();
					} 
					else if (e.getPath().toString().equals("[transformieren                          ,"
							+ " Translation]")) 
					{
						transform = new EditTransformPan(editImg.get(index), GenEditFrm.this, "Translation");
						drawTree.setSelectionPath(drawTree.getPathForRow(0));
						splitPane2.setDividerLocation(450);
						transformHelpMethod();
					}
				});
				transformTree.addTreeSelectionListener(new TreeSelectionListener() 
				{
					@Override
					public void valueChanged(TreeSelectionEvent e) 
					{
						editImg.get(index).line = false;
						editImg.get(index).oval = false;
						editImg.get(index).fillOval = false;
						p1Besetzt = false;
						p1.removeAll();
						p1.revalidate();
						p.remove(p1);
						p.revalidate();
						splitPane2.setDividerLocation(250);
						splitPane2.revalidate();
						revalidate();
					}
				});
				add(new JScrollPane(transformTree));
			}

			// Methode die zwieschen den Flaechen-Formen wechselt
			private void transformHelpMethod() 
			{
				if(p1Besetzt)
				{
					p1Besetzt = false;
					p1.removeAll();
					p1.revalidate();
					p.remove(p1);
					p.revalidate();
				}
				p1.setPreferredSize(new Dimension(100,100));
				p1.add(transform);
				p.add(p1, BorderLayout.NORTH);
				p.revalidate();
				splitPane2.revalidate();
				GenEditFrm.this.requestFocus();
				revalidate();
				p1Besetzt = true;		
			}
		};
		tabbedPane.setSelectedIndex(index);
	}
}