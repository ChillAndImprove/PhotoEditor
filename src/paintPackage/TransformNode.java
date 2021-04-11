package paintPackage;

import java.util.*;
import javax.swing.tree.*;

public class TransformNode implements TreeNode 
{
	TransformNode[] m_Kids = null;
	TransformNode m_Father;
	String m_Name;
	String[] bezeichnungen = { "Scherung", "Rotation", "Skalierung", "Translation" };

	public TransformNode() 
	{
		m_Name = "transformieren                          ";
		m_Kids = new TransformNode[4];
	}

	public TransformNode(TransformNode father, int pos) 
	{
		m_Father = father;
		if (m_Father == null)
			m_Name = "transformieren";
		else
			m_Name = bezeichnungen[pos];
		m_Kids = new TransformNode[0];
	}

	public TreeNode getChildAt(int childIndex) 
	{
		if (m_Kids[childIndex] == null) 
		{
			m_Kids[childIndex] = new TransformNode(this, childIndex);
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