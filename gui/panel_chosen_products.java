package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import common.common;
import database.database;

public class panel_chosen_products extends JPanel implements ChangeListener
{
	private static final long serialVersionUID = 1L;
	
	ImageIcon icon_add = new ImageIcon(getClass().getResource("/icons/add16x16.png"));
	
	database db;
	
	Component parent;
	
	panel_invoice pnl_invoice;
	
	JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
	
	
	panel_chosen_products(Component parent, panel_invoice pnl_invoice, database db)
	{
		this.db = db;
		
		this.parent = parent;
		
		this.pnl_invoice = pnl_invoice;
		
		load_tabs();
		
		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
		
		tabs.addChangeListener(this);
	}
	
	void load_tabs()
	{
		tabs.removeAll();
		
		ResultSet groups = db.query("SELECT * FROM chosen_product_groups");
		
		try 
		{
			ArrayList<panel_list> panels = new ArrayList<>();
			
			while(groups.next())
			{
				panels.add(new panel_list(groups.getInt("id"), groups.getString("name")));
			}
			
			for(panel_list panel:panels)
			{
				panel.update();
				
				tabs.addTab(panel.group_name, panel);
			}
			
			tabs.addTab("", icon_add, null);
			
			tabs.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	class panel_list extends JPanel implements ActionListener, MouseListener, common.update_listener
	{
		private static final long serialVersionUID = 1L;
		
		static final int TOOLS_ADD = 0;
		static final int TOOLS_EDIT = 1;
		static final int TOOLS_DELETE = 2;
		
		String group_name;
		
		JPanel pnl_tools = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		JPanel pnl_items = new JPanel();
		
		custom_models.tools_button btn_tools_add;
		custom_models.tools_button btn_tools_edit;
		custom_models.tools_button btn_tools_delete;
		
		JPopupMenu btn_menu = new JPopupMenu();
		
		JMenuItem mitm_delete = new JMenuItem("حذف");
		
		int group_id;
		
		panel_list(int group_id, String group_name)
		{
			this.group_id = group_id;
			this.group_name = group_name;
			
			setLayout(new BorderLayout());
			
			btn_menu.add(mitm_delete);
			
			mitm_delete.addActionListener(this);
			
			btn_tools_add = new custom_models.tools_button(custom_models.tools_button.BTN_ADD, group_id, TOOLS_ADD, "إضافة عنصر إلى المجموعة", this);
			btn_tools_edit = new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, group_id, TOOLS_EDIT, "تعديل المجموعة", this);
			btn_tools_delete = new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, group_id, TOOLS_DELETE, "حذف المجموعة", this);
			
			pnl_tools.setBackground(Color.LIGHT_GRAY);
			pnl_tools.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			
			pnl_tools.add(btn_tools_add);
			pnl_tools.add(btn_tools_edit);
			pnl_tools.add(btn_tools_delete);
			
			add(pnl_tools, BorderLayout.PAGE_START);
			add(pnl_items, BorderLayout.CENTER);
		}
		
		void update()
		{
			pnl_items.removeAll();
			
			ResultSet res_chosen_products = db.query("SELECT * FROM chosen_products JOIN products ON product_id=products.id WHERE group_id="+group_id);
			
			ArrayList<common.item> itm_chosen_products = new ArrayList<>();
			
			try
			{
				while(res_chosen_products.next())
				{
					itm_chosen_products.add(new common.item(res_chosen_products.getInt("product_id"), res_chosen_products.getString("products.name")));
				}
				
				for(common.item itm_product:itm_chosen_products)
				{
					ResultSet res_product_image = db.query("SELECT * FROM product_images WHERE product_id="+itm_product.value);
					
					custom_models.square_button btn_chosen_product = new custom_models.square_button();
					
					if(res_product_image.next())
					{
				        InputStream is = res_product_image.getBinaryStream("data");
						try
						{
							BufferedImage img = ImageIO.read(is);
							
							btn_chosen_product = new custom_models.square_button(itm_product.value, new ImageIcon(img), itm_product.text, this);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						btn_chosen_product = new custom_models.square_button(itm_product.value, itm_product.text, this);
					}
					
					btn_chosen_product.addMouseListener(this);
					pnl_items.add(btn_chosen_product);
				}
				
				pnl_items.revalidate();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
		}
		
		@Override
		public void actionPerformed(ActionEvent ev)
		{
			Object source = ev.getSource();
			
			int selected_tab = tabs.getSelectedIndex();
			
			if(source.getClass() == custom_models.square_button.class)
			{
				custom_models.square_button btn = (custom_models.square_button) source;
				
				if(pnl_invoice != null)
				{
					pnl_invoice.add_product(btn.value);
				}
			}
			else if(source == btn_tools_add)
			{
				dialog_add_chosen_product dlg_add_chosen_product = new dialog_add_chosen_product();
				
				dlg_add_chosen_product.show(parent, db, group_id, this);
				
				tabs.setSelectedIndex(selected_tab);
			}
			else if(source == btn_tools_edit)
			{
				String new_group_name = msgbox.input(this, "اسم المجموعة: ", "إضافة مجموعة", group_name);
				
				if( new_group_name != null && ! new_group_name.trim().isBlank() && ! new_group_name.equals(group_name))
				{
					if(save_group(new_group_name, group_id))
					{
						load_tabs();
						
						tabs.setSelectedIndex(selected_tab);
					}
				}
			}
			else if(source == btn_tools_delete)
			{
				int response = msgbox.confirm(parent, "هل أنت متأكد من أنك تريد حذف مجموعة السلع كاملة؟");
				
				if(response == msgbox.OK)
				{
					delete_group(group_id);
				}
			}
			else if(source == mitm_delete)
			{
				Object invoker = ((JPopupMenu)mitm_delete.getParent()).getInvoker();
				
				if(invoker.getClass() == custom_models.square_button.class)
				{
					int response = msgbox.confirm(parent, "تأكيد الحذف");
					
					if(response != msgbox.OK)
					{
						return;
					}
					
					custom_models.square_button btn = (custom_models.square_button) invoker;
					
					db.query_nr("DELETE from chosen_products WHERE product_id="+btn.value+" AND group_id="+group_id);
					
					load_tabs();
					
					tabs.setSelectedIndex(selected_tab);
				}
			}
		}

		@Override
		public void updated()
		{
			load_tabs();
		}

		@Override
		public void mouseClicked(MouseEvent ev)
		{
			Object source = ev.getSource();
			
			if(ev.getButton() == MouseEvent.BUTTON3)
			{
			
				if(source.getClass() == custom_models.square_button.class)
				{
					btn_menu.show((Component)source, ev.getX(), ev.getX());
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	boolean save_group(String name, int group_id)
	{
		PreparedStatement pstmt = db.pstmt("INSERT INTO chosen_product_groups(name) VALUES(?)");
		
		if(group_id > 0)
		{
			pstmt = db.pstmt("UPDATE chosen_product_groups SET name=? WHERE id="+group_id);
		}
		
		try 
		{
			pstmt.setString(1, name);
			
			int result = pstmt.executeUpdate();
			
			if(result > 0)
			{
				return true;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		return false;
	}
	
	void delete_group(int group_id)
	{
		db.query_nr("DELETE FROM chosen_products WHERE group_id="+group_id);
		db.query_nr("DELETE FROM chosen_product_groups WHERE id="+group_id);
	}

	@Override
	public void stateChanged(ChangeEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == tabs)
		{
			int tab_index = tabs.getSelectedIndex();
			
			if(tabs.getTabCount() > 1 && tab_index == tabs.getTabCount() - 1)
			{
				String group_name = msgbox.input(this, "اسم المجموعة: ", "إضافة مجموعة", "بدون اسم");
				
				tabs.removeChangeListener(this);
				
				if( group_name != null && ! group_name.trim().isBlank())
				{
					if(save_group(group_name, -1))
					{
						load_tabs();
						
						tabs.setSelectedIndex(tabs.getTabCount() - 2);
					}
				}
				
				tabs.addChangeListener(this);
			}
		}
	}

}
