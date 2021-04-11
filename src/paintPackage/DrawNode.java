package paintPackage;

import java.util.*;
import javax.swing.tree.*;

public class DrawNode implements TreeNode 
{
	DrawNode[] m_Kids = null;
	DrawNode m_Father;
	String m_Name;
	String[] bezeichnungen = {"ungefuelltes Kreis", "gefuelltes Kreis", "linie"};

	public DrawNode() 
	{
		m_Name = "zeichnen                                    ";
		m_Kids = new DrawNode[3];
	}

	public DrawNode(DrawNode father, int pos) 
	{
		m_Father = father;
		if (m_Father == null)
			m_Name = "zeichnen";
		else
			m_Name = bezeichnungen[pos];
		m_Kids = new DrawNode[0];
	}

	public TreeNode getChildAt(int childIndex) 
	{
		if (m_Kids[childIndex] == null) 
		{
			m_Kids[childIndex] = new DrawNode(this, childIndex);
		}
		return m_Kids[childIndex];
	}

	public int getChildCount() 
	{
		return m_Kids.length;
	}

	public TreeNode getParent() 
	{
		return m_Father;
	}

	public boolean isLeaf() 
	{
		return m_Kids.length == 0;
	}

	public String toString() 
	{
		return m_Name;
	}

	public int getIndex(TreeNode kid) 
	{
		return -1;
	}

	public boolean getAllowsChildren() 
	{
		return false;
	}

	@SuppressWarnings("rawtypes")
	public Enumeration children() 
	{
		return null;
	}
} // end of MyNode declaration
