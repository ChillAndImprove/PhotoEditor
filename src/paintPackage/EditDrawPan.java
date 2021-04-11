package paintPackage;

import java.awt.*;
import java.util.Hashtable;

import javax.swing.*;

// Zeichnungseinstellungen/aktionen

@SuppressWarnings("serial")
public class EditDrawPan extends JPanel 
{	
	EditImgFrm editImgFrm;
	int r, g, b, r1, g1, b1 = 0;
	
	public EditDrawPan(EditImgFrm editImgFrm, GenEditFrm frame, String shape) 
	{
		this.editImgFrm = editImgFrm;
		editImgFrm.line = false;
		editImgFrm.oval = false;
		editImgFrm.fillOval = false;
		editImgFrm.red = 0;
		editImgFrm.green = 0;
		editImgFrm.blue = 0;
		editImgFrm.red1 = 0;
		editImgFrm.green1 = 0;
		editImgFrm.blue1 = 0;
		editImgFrm.percent = 0;
		editImgFrm.percentCounter = 0;
		editImgFrm.percentValue = 0;
		if (shape == "ungefuelltesKreis") 
		{
			editImgFrm.oval = true;
			colorSliders();
		} 
		else if (shape == "gefuelltesKreis") 
		{
			editImgFrm.fillOval = true;
			colorSliders();
		} 
		else if (shape == "linie") 
		{
			editImgFrm.line = true;
			colorSliders();
		}
	}

	// Sliders: 
	JSlider red;
	JSlider green;
	JSlider blue;
	JSlider red1;
	JSlider green1;
	JSlider blue1;
	private void colorSliders() 
	{	
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(3, 0));
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		JLabel l = new JLabel("R:");
		red = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		red.addChangeListener(e -> {
			editImgFrm.red = red.getValue();
			editImgFrm.once = false;
			editImgFrm.repaint();
			r=red.getValue();
			setSliderLabels(red, -1, g, b);
			setSliderLabels(green, r, -1, b);
			setSliderLabels(blue, r, g, -1);		
		});
		p2.add(l, BorderLayout.WEST);
		p2.add(red, BorderLayout.CENTER);
		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		JLabel l1 = new JLabel("G:");
		green = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		green.addChangeListener(e -> {
			editImgFrm.green = green.getValue();
			editImgFrm.once = false;
			editImgFrm.repaint();
			g=green.getValue();
			setSliderLabels(red, -1, g, b);
			setSliderLabels(green, r, -1, b);
			setSliderLabels(blue, r, g, -1);
		});
		p3.add(l1, BorderLayout.WEST);
		p3.add(green, BorderLayout.CENTER);
		JPanel p4 = new JPanel();
		p4.setLayout(new BorderLayout());
		JLabel l2 = new JLabel("B:");
		blue = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		blue.addChangeListener(e -> {
			editImgFrm.blue = blue.getValue();
			editImgFrm.once = false;
			editImgFrm.repaint();
			b=blue.getValue();
			setSliderLabels(red, -1, g, b);
			setSliderLabels(green, r, -1, b);
			setSliderLabels(blue, r, g, -1);
		});
		p4.add(l2, BorderLayout.WEST);
		p4.add(blue, BorderLayout.CENTER);
		p.add(p2);
		p.add(p3);
		p.add(p4);
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(3, 0));
		JPanel p5 = new JPanel();
		p5.setLayout(new BorderLayout());
		JLabel l3 = new JLabel("R:");
		red1 = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		red1.addChangeListener(e -> {
			editImgFrm.red1 = red1.getValue();
			editImgFrm.once = false;
			editImgFrm.repaint();
			r1=red1.getValue();
			setSliderLabels(red1, -1, g1, b1);
			setSliderLabels(green1, r1, -1, b1);
			setSliderLabels(blue1, r1, g1, -1);
		});
		p5.add(l3, BorderLayout.WEST);
		p5.add(red1, BorderLayout.CENTER);
		JPanel p6 = new JPanel();
		p6.setLayout(new BorderLayout());
		JLabel l4 = new JLabel("G:");
		green1 = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		green1.addChangeListener(e -> {
			editImgFrm.green1 = green1.getValue();
			editImgFrm.once = false;
			editImgFrm.repaint();
			g1=green1.getValue();
			setSliderLabels(red1, -1, g1, b1);
			setSliderLabels(green1, r1, -1, b1);
			setSliderLabels(blue1, r1, g1, -1);
		});			
		p6.add(l4, BorderLayout.WEST);
		p6.add(green1, BorderLayout.CENTER);
		JPanel p7 = new JPanel();
		p7.setLayout(new BorderLayout());
		JLabel l5 = new JLabel("B:");
		blue1 = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		blue1.addChangeListener(e -> {
			editImgFrm.blue1 = blue1.getValue();
			editImgFrm.once = false;
			editImgFrm.repaint();
			b1=blue1.getValue();
			setSliderLabels(red1, -1, g1, b1);
			setSliderLabels(green1, r1, -1, b1);
			setSliderLabels(blue1, r1, g1, -1);
			
		});
		p7.add(l5, BorderLayout.WEST);
		p7.add(blue1, BorderLayout.CENTER);
		p1.add(p5);
		p1.add(p6);
		p1.add(p7);
		
		setLayout(new BorderLayout());
		// Anfuegen der Komponente dem SplitPane und dem generalem Panel das SplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p, p1);
		splitPane.setOneTouchExpandable(true);
		add(splitPane, BorderLayout.CENTER);
		
		setDefaultSliderLabels();
	}
	
	public void setDefaultSliderLabels()
	{
		// Sliderfarben
        // Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			tmp.setBackground(new Color(i, 0, 0));
			labels.put(i, tmp);
		}
		red.setLabelTable(labels);
	    red.setPaintLabels(true);
	    
		// Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
        Hashtable<Integer, JLabel> labels1 = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			tmp.setBackground(new Color(0, i, 0));
			labels1.put(i, tmp);
		}
		green.setLabelTable(labels1);
	    green.setPaintLabels(true);
	    
		// Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
        Hashtable<Integer, JLabel> labels2 = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			tmp.setBackground(new Color(0, 0, i));
			labels2.put(i, tmp);
		}
		blue.setLabelTable(labels2);
	    blue.setPaintLabels(true);
	    
        // Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
        Hashtable<Integer, JLabel> labels3 = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			tmp.setBackground(new Color(i, 0, 0));
			labels3.put(i, tmp);
		}
		red1.setLabelTable(labels3);
	    red1.setPaintLabels(true);
	    
		// Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
        Hashtable<Integer, JLabel> labels4 = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			tmp.setBackground(new Color(0, i, 0));
			labels4.put(i, tmp);
		}
		green1.setLabelTable(labels4);
	    green1.setPaintLabels(true);
	    
		// Labels der Hashtabelle anfuegen, dem Slider hinzufuegen und auf slider "s" "setPaintLabels(true)"-Methode aufrufen
        Hashtable<Integer, JLabel> labels5 = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			tmp.setBackground(new Color(0, 0, i));
			labels5.put(i, tmp);
		}
		blue1.setLabelTable(labels5);
	    blue1.setPaintLabels(true);
	}
	
	// setzt die Slider im nachhinein mit der Anderer, als zum Anfang, Farbe
	public void setSliderLabels(JSlider s, int r, int g, int b)
	{
		Hashtable<Integer, JLabel> labels1 = new Hashtable<>();
		for(int i = 0; i < 255; ++i)
		{
			JLabel tmp = new JLabel(" ");
			tmp.setOpaque(true);
			if(r == -1)
				tmp.setBackground(new Color(i, g, b));
			else if(g == -1)
				tmp.setBackground(new Color(r, i, b));
			else if(b == -1)
				tmp.setBackground(new Color(r, g, i));
			labels1.put(i, tmp);
		}
		s.setLabelTable(labels1);
		s.repaint();
		s.revalidate();
		repaint();
		revalidate();
	}
}
