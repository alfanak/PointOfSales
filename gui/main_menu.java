package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class main_menu extends custom_models.form implements MouseListener
{
	private static final long serialVersionUID = 1L;
	
	static Color ITEM_BG_COLOR = new Color(0, 20, 30);
	static Color ITEM_FG_COLOR = Color.white;
	
	static Color SUBITEM_BG_COLOR = new Color(220, 180, 0);
	static Color SUBITEM_FG_COLOR = Color.white;
	
	main_menu()
	{
		super(1);
		c.insets = new Insets(0, 0, 1, 0);
	}
	
	void add_item(item item)
	{
		add(item);
		
		if(item.submenu != null)
		{
			add(item.submenu);
		}
		
		item.addMouseListener(this);
	}
	
	void add_submenu_item(item main_item, item sub_item)
	{
		main_item.submenu.add(sub_item);
		sub_item.addMouseListener(this);
	}
	
	static class submenu extends custom_models.form
	{
		private static final long serialVersionUID = 1L;
		
		submenu()
		{
			super(1);
			c.insets = new Insets(0, 0, 1, 0);
			setVisible(false);
		}
	}
	
	static class item extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		Color bg_color;
		Color fg_color;
		
		ImageIcon icon_arrow_down = new ImageIcon(getClass().getResource("/icons/arrowdown16x16.png"));
		ImageIcon icon_arrow_up = new ImageIcon(getClass().getResource("/icons/arrowup16x16.png"));
		
		JLabel label = new JLabel();
		JLabel lbl_arrow_frame = new JLabel();
		
		submenu submenu;
		
		item()
		{
			set_properties(false);
		}
		
		void toggle_submenu()
		{
			if(submenu == null)
			{
				return;
			}
			
			if(submenu.isVisible())
			{
				submenu.setVisible(false);
				lbl_arrow_frame.setIcon(icon_arrow_down);
			}
			else
			{
				submenu.setVisible(true);
				lbl_arrow_frame.setIcon(icon_arrow_up);
			}
		}
		
		void set_properties(boolean is_submenu)
		{
			setLayout(new BorderLayout());
			setPreferredSize(new Dimension(220, 38));
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			setOpaque(true);
			
			if(is_submenu)
			{
				fg_color = SUBITEM_FG_COLOR;
				bg_color = SUBITEM_BG_COLOR;
			}
			else
			{
				fg_color = ITEM_FG_COLOR;
				bg_color = ITEM_BG_COLOR;
			}
			
			setBackground(bg_color);
			label.setForeground(fg_color);
		}
		
		item(ImageIcon icon, String text, boolean is_submenu, boolean has_submenu, MouseListener ml)
		{
			set_properties(is_submenu);
			
			label.setText(text);
			label.setIcon(icon);
			
			add(label, BorderLayout.LINE_START);
			add(new JLabel(), BorderLayout.CENTER);
			
			lbl_arrow_frame.setIcon(icon_arrow_down);
			
			if(has_submenu)
			{
				submenu = new submenu();
				add(lbl_arrow_frame, BorderLayout.LINE_END);
			}
			
			addMouseListener(ml);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == item.class)
		{
			item item = (item) source;
			
			item.toggle_submenu();
		}
	}

	@Override
	public void mousePressed(MouseEvent ev) 
	{
		Object source = ev.getSource();
		
		if(source.getClass() == item.class)
		{
			item item = (item) source;
			
			item.setBackground(new Color(Math.min(item.bg_color.getRed()+40, 250), item.bg_color.getGreen()+40, item.bg_color.getBlue()+40));
		}
	}

	@Override
	public void mouseReleased(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == item.class)
		{
			item item = (item) source;
			
			item.setBackground(new Color(Math.min(item.bg_color.getRed()+20, 250), item.bg_color.getGreen()+20, item.bg_color.getBlue()+20));
		}
	}

	@Override
	public void mouseEntered(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == item.class)
		{
			item item = (item) source;
			item.setBackground(new Color(item.bg_color.getRed()+20, item.bg_color.getGreen()+20, item.bg_color.getBlue()+20));
		}
	}

	@Override
	public void mouseExited(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == item.class)
		{
			item item = (item) source;
			
			item.setBackground(item.bg_color);
		}
	}
}
