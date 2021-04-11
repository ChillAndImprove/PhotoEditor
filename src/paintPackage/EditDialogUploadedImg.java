package paintPackage;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

@SuppressWarnings("serial")
public class EditDialogUploadedImg extends JDialog 
{
	
	// Bilder:
	// Bilder-Vektor
	Vector<Image> imgVec = new Vector<Image>();
	// Bilder Breite und Höhe
	int imgWidth = 200;
	int imgHeight = 200;
	
	// Modales Dialog um die Bilder dem "Bearbeitungsfenster" hinzuzufuegen;
	EditDialogUploadedImg(GenEditFrm father, GenFrm frame) 
	{
		
		// Vater-Fenster, Titel und Modalitaet festlegen:
		super(father, "Bild auswaehlen", true);
		
		// Panel fuer die ganzen Bilder erstellen:
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout());
		
		// Eine ScrollBar erstellen fuer das Panel wo die Bilder sich 
		// befinden sollten(das Oben erstellte Panel "p"):
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getHorizontalScrollBar();
		
		// Bilder in angebrachter Groessen in das Bilder-Vektor laden:
		MediaTracker mt = new MediaTracker(this);
		for(int i = 0; i < frame.imgVecWithoutSize.size(); ++i)
		{	
			imgVec.add((Image) frame.imgVecWithoutSize.get(i).getScaledInstance(200, 200, Image.SCALE_SMOOTH));
			mt.addImage(imgVec.get(i), 0);
		}
		try 
		{
			mt.waitForAll();
		} catch (InterruptedException e1) {}
		
		// Fuer jedes Bild ein geeignetes Panel erstellen:
		JPanel[] p1 = new JPanel[frame.imgVec.size()];
		for (int i = 0; i < frame.imgVec.size(); ++i) 
		{
			
			// j-Variable erstellt auffgrund der Benutzung der Anonymer Klassen 
			final int j = i;
			
			// Das Bild in das Panel "p1[x]" hinzufuegen 
			p1[i] = new JPanel()
			{
				{
					setPreferredSize(new Dimension(200, 200));
				}
				@Override
				public void paintComponent(Graphics g) 
				{
					g.drawImage(imgVec.get(j), 0, 0, null);
				}
			};
			
			// Reaktion auf Mausklick
			p1[i].addMouseListener(new MouseAdapter() 
			{
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					// index erhoehen
					++father.index;
					
					// das Bild dem Vater-Fenster, in ein neues Tab, hinzufuegen
					father.editImg.add(father.index, new EditImgFrm(father, frame.imgVecWithoutSize.get(j)));
					father.tabbedPane.addTab("" + father.index, father.editImg.get(father.index));
					
					// Erstellen der Einstellungspanels
					father.transformPanelHelpMethod();
					father.drawPanelHelpMethod();
					
					// Vater-Fenster "aktualisieren"
					father.revalidate();
				}
			});
			p.add(p1[i]);
		}
		// dem ScrollPane das Panel mit den Bildern anfuegen:
		scrollPane.getViewport().add(p);
		
		// "scrollPane" dem Dialog anfuegen:
		add(scrollPane);
		
		// letzte Schritte:
		pack();
		setVisible(true);
	}
}
