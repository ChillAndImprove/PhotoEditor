package paintPackage;

import java.awt.*;
import java.util.Hashtable;

import javax.swing.*;

// Transformationspanel

@SuppressWarnings("serial")
public class EditTransformPan extends JPanel 
{	
	// Scherung Variablen
	double xSheareAround;
	double ySheareAround;
	JSlider s;
	JSlider s1;
	
	// Drehung/ Rotation
	double rad;
	
	// Matrize merken
	boolean once;
	boolean once1;
	
	public EditTransformPan(EditImgFrm editImg, GenEditFrm frame, String transformation) 
	{
		// falls gefuelltes Kreis schonmal gezeichnet wurde
		editImg.fillOval = false;
		// falls Kreis schonmal gezeichnet wurde
		editImg.oval = false;
		// falls Linie schonmal gezeichnet wurde
		editImg.line = false;
		// erstmal Rotation ausschliesen
		editImg.rotieren = false;
		if (transformation == "Scherung") 
		{	
			setLayout(new BorderLayout());
			// Panel fuer die x-Scherungseinstellungen
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			
			// Panel fuer "Label" und "TextField"
			JPanel p1 = new JPanel();
			p1.setLayout(new FlowLayout());
			JLabel l = new JLabel("x-Scherung um: ");
			JTextField t = new JTextField();
			t.setText("0.001");
			// hinzufuegen der Komponente dem Panel "p1"
			p1.add(l);
			p1.add(t);

			// Verschiebe um (Standartwert: 0.001)
			xSheareAround = Double.valueOf(t.getText());
			
			// Button fuer die Uebernahme von dem Scherungsfaktor
			JButton b = new JButton("uebernehmen");
			b.addActionListener(e->{
				xSheareAround = Double.valueOf(t.getText());
			});
			s = new JSlider(JSlider.HORIZONTAL, -editImg.w, editImg.w, 0);
			s.addChangeListener(e -> {
				if(editImg.rectDraw)
				{
					if(s.getValue() < 0)
					{
						editImg.shear(s.getValue() * xSheareAround, 0);
					}
					else if(s.getValue() > 0)
					{
						editImg.shear(s.getValue() * xSheareAround, 0);
					}	
				}
				else if(!editImg.rectDraw)
				{
					if(s.getValue() < 0)
					{
						editImg.transform(new ImgMatr(new double[][]{{ 1, s.getValue() * xSheareAround, 0 }, { 0, 1, 0 }, { 0, 0, 1 }}));
					}
					else if(s.getValue() > 0)
					{
						editImg.transform(new ImgMatr(new double[][]{{ 1, s.getValue() * xSheareAround, 0 }, { 0, 1, 0 }, { 0, 0, 1 }}));
					}	
				}
				if(!once)
				{
					// Matrizen
					if(editImg.currentMatrix != null)
					{
						editImg.lastMatrix = editImg.currentMatrix;
						editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
					}
					s1.setValue(0);
					once = true;
					once1 = false;
				}
				editImg.repaint();
			});
			
			// Hinzufuegen der Komponente zu dem Panel "p"
			p.add(p1, BorderLayout.NORTH);
			p.add(b, BorderLayout.CENTER);
			p.add(s, BorderLayout.SOUTH);
			
			// Panel fuer die y-Scherungseinstellungen
			JPanel p2 = new JPanel();
			p2.setLayout(new BorderLayout());
			
			// Panel fuer "Label" und "TextField"
			JPanel p3 = new JPanel();
			p3.setLayout(new FlowLayout());
			JLabel l1 = new JLabel("y-Scherung um: ");
			JTextField t1 = new JTextField();
			t1.setText("0.001");
			// Hinzufuegen der Komponente dem Panel "p3"
			p3.add(l1);
			p3.add(t1);
			
			// Verschiebe um (Standartwert: 0.001)
			ySheareAround = Double.valueOf(t1.getText());
			
			// Button fuer die Uebernahme von dem Scherungsfaktor
			JButton b1 = new JButton("uebernehmen");
			b1.addActionListener(e->{
				ySheareAround = Double.valueOf(t1.getText());
			});
			s1 = new JSlider(JSlider.HORIZONTAL, -editImg.h, editImg.h, 0);
			s1.addChangeListener(e ->{
				if(editImg.rectDraw)
				{
					if(s1.getValue() < 0)
					{
						editImg.shear(0, s1.getValue() * ySheareAround);
					}
					else if(s1.getValue() > 0)
					{
						editImg.shear(0, s1.getValue() * ySheareAround);
					}	
				}
				else if(!editImg.rectDraw)
				{
					if(s1.getValue() < 0)
					{
						editImg.transform(new ImgMatr(new double[][]{ { 1, 0, 0 }, { s1.getValue() * ySheareAround, 1, 0 }, { 0, 0, 1 } }));
					}
					else if(s1.getValue() > 0)
					{
						editImg.transform(new ImgMatr(new double[][]{ { 1, 0, 0 }, { s1.getValue() * ySheareAround, 1, 0 }, { 0, 0, 1 } }));
					}
				}
				if(!once1)
				{
					// Matrizen
					if(editImg.currentMatrix != null)
					{
						editImg.lastMatrix = editImg.currentMatrix;
						editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
					}
					s.setValue(0);
					once1 = true;
					once = false;
				}
				editImg.repaint();
			});
			
			// Hinzufuegen der Komponente zu dem Panel "p2"
			p2.add(p3, BorderLayout.NORTH);
			p2.add(b1, BorderLayout.CENTER);
			p2.add(s1, BorderLayout.SOUTH);
			
			JPanel p4 = new JPanel();
			p4.setLayout(new GridLayout(2, 0));
			
			JButton b2 = new JButton("verwerfen")
			{
				{
					addActionListener(e->{
						s.setValue(0);
						s1.setValue(0);
						// Matrizen Loeschen
						editImg.lastInvertedMatrix = ImgMatr.diagOneMatrix();
						editImg.lastMatrix =         ImgMatr.diagOneMatrix();
						editImg.currentInvertedMatrix = ImgMatr.diagOneMatrix();
						editImg.currentMatrix = ImgMatr.diagOneMatrix();
						// Neu zeichnen ohne Rechteckbereich
						editImg.rectDraw = false;
				    	editImg.setNewPixView();
				    	editImg.setNewPicture();
					});
				}
			};
			p4.add(b2);
			
			JButton b3 = new JButton("speichern")
			{
				{
					addActionListener(e->{
						editImg.save();
						s.setValue(0);
						s1.setValue(0);
						editImg.rectDraw = false;
				    	editImg.rectDraw = false;
						editImg.setNewPixView();
				    	editImg.setNewPicture();
					});
				}
			};
			p4.add(b3);
			
			add(p4, BorderLayout.WEST);
			
			// Anfuegen der Komponente dem SplitPane und dem generalem Panel
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p, p2);
			splitPane.setOneTouchExpandable(true);
			add(splitPane, BorderLayout.CENTER);
		} 
		else if (transformation == "Rotation") 
		{
			// Matrizen
			if(editImg.currentMatrix != null)
			{
				//Transfcormatin verwerfen kannst 
				editImg.lastMatrix = editImg.currentMatrix;
				editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
			}

			// Falls Rotation angecklieckt dann dem wahr setzen und dadurch zeichnung des Symboles setzen
			editImg.rotieren = true;
			setLayout(new BorderLayout());
			JLabel l = new JLabel("       x-Grad nach links/rechts");
			
			// Slider wird erstellt
			JSlider s = new JSlider(JSlider.HORIZONTAL, -360, 360, 0);
			s.setMinorTickSpacing(30);
	        s.setMajorTickSpacing(180);
	        s.setPaintTicks(true);
	        
	        // Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
	        Hashtable<Integer, JLabel> labels = new Hashtable<>();
			labels.put(-360, new JLabel("-360"));
			labels.put(-180, new JLabel("-180"));
			labels.put(0, new JLabel("0"));
			labels.put(180, new JLabel("180"));
			labels.put(360, new JLabel("360"));
			s.setLabelTable(labels);
		    s.setPaintLabels(true);
			
		    // ChangeListener wird "aktiviert" und rotationsfkt.-nen erstellt
		    s.addChangeListener(e -> {
				if(editImg.rectDraw)
				{
					// Rotation im Uhrzeigersinn 
					if(s.getValue() < 0)
					{
						editImg.rotate(editImg.rectP, s.getValue());
					}
					// Rotation gegen den Uhrzeigersinn (invertierte Matrix)
					else if(s.getValue() > 0)
					{
						editImg.rotate(editImg.rectP, s.getValue());
					}
				}
				else if(!editImg.rectDraw)
				{
				// Rotation im Uhrzeigersinn 
				if(s.getValue() < 0)
				{
					rad = (Math.PI * s.getValue() / 180);
					editImg.transform(new ImgMatr(new double[][]{{ Math.cos(rad), -Math.sin(rad), 0}, { Math.sin(rad), Math.cos(rad), 0}, { 0, 0, 1}}));
				}
				// Rotation gegen den Uhrzeigersinn (invertierte Matrix)
				else if(s.getValue() > 0)
				{
					rad = (Math.PI * s.getValue() / 180);
					editImg.transform(new ImgMatr(new double[][]{{ Math.cos(rad), -Math.sin(rad), 0}, { Math.sin(rad), Math.cos(rad), 0}, { 0, 0, 1}}));
				}
				}
				editImg.repaint();
				frame.requestFocus();
			});
		    
		    JPanel p = new JPanel();
		    p.setLayout(new FlowLayout());
		    
		    JButton b = new JButton("verwerfen");
		    b.addActionListener(e->{
		    	s.setValue(0);
				// Matrizen Loeschen
				editImg.lastInvertedMatrix = ImgMatr.diagOneMatrix();
				editImg.lastMatrix =         ImgMatr.diagOneMatrix();
				editImg.currentInvertedMatrix = ImgMatr.diagOneMatrix();
				editImg.currentMatrix = ImgMatr.diagOneMatrix();
				// Neu zeichnen ohne Rechteckbereich
				editImg.rectDraw = false;
		    	editImg.setNewPixView();
		    	editImg.setNewPicture();
		    	editImg.symbolX = editImg.imgX;
		    	editImg.symbolY = editImg.imgY;
		    	frame.requestFocus();
		    	editImg.repaint();
		    });
		    p.add(b);
		    JButton b1 = new JButton("speichern");
		    b1.addActionListener(e->{
		    	editImg.save();
		    	editImg.setNewPixView();
		    	editImg.setNewPicture();
		    	s.setValue(0);
		    	editImg.symbolX = editImg.imgX;
		    	editImg.symbolY = editImg.imgY;
		    	editImg.rectDraw = false;
		    	editImg.rectDraw = false;
		    	frame.requestFocus();
		    	editImg.repaint();
		    });
		    p.add(b1);
		    
		    // Dem generalem Panel die Komponente anfuegen
			add(l, BorderLayout.NORTH);
		    add(s, BorderLayout.CENTER);
			add(p, BorderLayout.SOUTH);
		} 
		else if (transformation == "Skalierung") 
		{	
			// Matrizen
			if(editImg.currentMatrix != null)
			{
				editImg.lastMatrix = editImg.currentMatrix;
				editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
			}
			
			editImg.scale = true;
			
			setLayout(new BorderLayout());
			
			JPanel p = new JPanel();
			p.setLayout(new BorderLayout());
			
			JLabel l = new JLabel("       Skalierungsfaktor 0.001");
			
			// 											standart 2001 staat 20001
			JSlider s = new JSlider(JSlider.HORIZONTAL, 1, 2001, 1001);
			s.addChangeListener(e -> {
				if(editImg.rectDraw)
				{
					// Verkleinerung 
					if(s.getValue() < 1001)
					{
						editImg.scale(s.getValue() * 0.001);
					}
					// Vergroesserung
					else if(s.getValue() > 1001)
					{
						editImg.scale(s.getValue() * 0.001);
					}	
				}
				else if(!editImg.rectDraw)
				{
					// Verkleinerung 
					if(s.getValue() < 1001)
					{
						editImg.transform(new ImgMatr(new double[][]{{1/(Math.abs(s.getValue()) * 0.001), 0, 0},
							{0, 1/(Math.abs(s.getValue()) * 0.001), 0},{0, 0, 1}}));
					}
					// Vergroesserung
					else if(s.getValue() > 1001)
					{
						editImg.transform(new ImgMatr(new double[][]{{1/(((double)(s.getValue() - 1) * 0.001)), 0, 0},
							{0, 1/(((double)(s.getValue() - 1) * 0.001)), 0},{0, 0, 1}}));
					}	
				}
				editImg.repaint();
			});
			s.setMinorTickSpacing(100);
	        s.setMajorTickSpacing(500);
	        s.setPaintTicks(true);
	        
	        // Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
	        Hashtable<Integer, JLabel> labels = new Hashtable<>();
			labels.put(1, new JLabel("0.001"));
			labels.put(500, new JLabel("0.500"));
			labels.put(1001, new JLabel("0"));
			labels.put(1501, new JLabel("500"));
			labels.put(2001, new JLabel("1000"));
			s.setLabelTable(labels);
		    s.setPaintLabels(true);
			
			p.add(l, BorderLayout.NORTH);
			p.add(s, BorderLayout.CENTER);
			add(p, BorderLayout.CENTER);
			
			JPanel p1 = new JPanel(); 
			p1.setLayout(new FlowLayout());
			
			JButton b = new JButton("verwerfen")
			{
				{
					addActionListener(e->{
						s.setValue(1001);
						// Matrizen Loeschen
						editImg.lastInvertedMatrix = ImgMatr.diagOneMatrix();
						editImg.lastMatrix =         ImgMatr.diagOneMatrix();
						editImg.currentInvertedMatrix = ImgMatr.diagOneMatrix();
						editImg.currentMatrix = ImgMatr.diagOneMatrix();
						// Neu zeichnen ohne Rechteckbereich
						editImg.rectDraw = false;
				    	editImg.setNewPixView();
				    	editImg.setNewPicture();
					});
				}
			};
			p1.add(b);
			
			JButton b1 = new JButton("speichern")
			{
				{
					addActionListener(e->{
						editImg.save();
						s.setValue(1001);
						editImg.rectDraw = false;
				    	editImg.setNewPixView();
				    	editImg.setNewPicture();
					});
				}
			};
			p1.add(b1);
			
			add(p1, BorderLayout.SOUTH);
		}
		else if (transformation == "Translation") 
		{	
			// Matrizen
			if(editImg.currentMatrix != null)
			{
				editImg.lastMatrix = editImg.currentMatrix;
				editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
			}
					
			setLayout(new BorderLayout());
			
			JPanel p = new JPanel(); 
			p.setLayout(new GridLayout(2, 0));
			
			JLabel l = new JLabel("horizonale Translation: ");
			s = new JSlider(JSlider.HORIZONTAL, -editImg.w, editImg.w, 0);
			s.addChangeListener(e -> {
				if(editImg.rectDraw)
				{
					// Die Verschiebung nach Links
					if(s.getValue() < 0)
					{
						editImg.translate(s.getValue(), 1);
					}
					// Die Verschiebung nach Rechts
					else if(s.getValue() > 0)
					{
						editImg.translate(s.getValue(), 1);
					}
				}
				else if(!editImg.rectDraw)
				{
					// Die Verschiebung nach Links
					if(s.getValue() < 0){
						editImg.transform(new ImgMatr(new double[][]{ { 1, 0, -s.getValue()}, { 0, 1, 1}, { 0, 0, 1 }}));
					}
					// Die Verschiebung nach Rechts
					else if(s.getValue() > 0)
					{
						editImg.transform(new ImgMatr(new double[][]{ { 1, 0, -s.getValue()}, { 0, 1, 1}, { 0, 0, 1 }}));
					}
				}
				editImg.repaint();
				if(!once)
				{
					// Matrizen
					if(editImg.currentMatrix != null)
					{
						editImg.lastMatrix = editImg.currentMatrix;
						editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
					}
					s1.setValue(0);
					once = true;
					once1 = false;
				}
			});
			p.add(l);
			p.add(s);
			
			JLabel l1 = new JLabel("veritkale Translation: ");
			s1 = new JSlider(JSlider.HORIZONTAL, -editImg.h, editImg.h, 0);
			s1.addChangeListener(e -> {
				if(editImg.rectDraw)
				{
					// Die Verschiebung nach Unten
					if(s1.getValue() < 0)
					{
						editImg.translate(1, s1.getValue());
					}
					// Die Verschibung nach Oben
					else if(s1.getValue() > 0)
					{
						editImg.translate(1, s1.getValue());
					}
				}
				else if(!editImg.rectDraw)
				{
					// Die Verschiebung nach Unten
					if(s1.getValue() < 0)
					{
						editImg.transform(new ImgMatr(new double[][]{ { 1, 0, 1}, { 0, 1, s1.getValue()}, { 0, 0, 1 }}));
					}
					// Die Verschibung nach Oben
					else if(s1.getValue() > 0)
					{
						editImg.transform(new ImgMatr(new double[][]{ { 1, 0, 1}, { 0, 1, s1.getValue()}, { 0, 0, 1 }}));
					}
				}
				editImg.repaint();
				if(!once1)
				{
					// Matrizen
					if(editImg.currentMatrix != null)
					{
						editImg.lastMatrix = editImg.currentMatrix;
						editImg.lastInvertedMatrix = editImg.currentInvertedMatrix;
					}
					s.setValue(0);
					once1 = true;
					once = false;
				}
			});
			p.add(l1);
			p.add(s1);
			add(p, BorderLayout.CENTER);
			
			JPanel p1 = new JPanel(); 
			p1.setLayout(new FlowLayout());
			
			JButton b = new JButton("zuruecksetzen")
			{
				{
					addActionListener(e->{
						s.setValue(0);
						s1.setValue(0);
						// Matrizen Loeschen
						editImg.lastInvertedMatrix = ImgMatr.diagOneMatrix();
						editImg.lastMatrix =         ImgMatr.diagOneMatrix();
						editImg.currentInvertedMatrix = ImgMatr.diagOneMatrix();
						editImg.currentMatrix = ImgMatr.diagOneMatrix();
						// Neu zeichnen ohne Rechteckbereich
						editImg.rectDraw = false;
				    	editImg.setNewPixView();
				    	editImg.setNewPicture();
					});
				}
			};
			p1.add(b);
			
			JButton b1 = new JButton("speichern")
			{
				{
					addActionListener(e->{
						editImg.save();
						s.setValue(0);
						s1.setValue(0);
						editImg.rectDraw = false;
						editImg.setNewPixView();
				    	editImg.setNewPicture();
				    	editImg.rectDraw = false;
						editImg.repaint();
					});
				}
			};
			p1.add(b1);
			
			add(p1, BorderLayout.SOUTH);
		}
	}
}
