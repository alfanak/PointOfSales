package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import common.common;
import database.database;

public class dialog_shopping_lists extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	private static final int TOOLS_VIEW = 0;
	private static final int TOOLS_EDIT = 1;
	private static final int TOOLS_DELETE = 2;
	
	database db;
	
	ImageIcon icon_add = new ImageIcon(getClass().getResource("/icons/add16x16.png"));
	
	custom_models.list_table tbl_shopping_lists = new custom_models.list_table();
	custom_models.list_scroll list_scroll = new custom_models.list_scroll(tbl_shopping_lists);
	
	custom_models.button btn_add = new custom_models.button("إنشاء", icon_add, this);
	
	void show(Component parent, database db)
	{
		this.db = db;
		
		update();
		
		JPanel buttons_panel = new JPanel();
		buttons_panel.setLayout(new FlowLayout(SwingConstants.LEADING));
		buttons_panel.add(btn_add);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(buttons_panel, BorderLayout.PAGE_START);
		container.add(list_scroll, BorderLayout.CENTER);
		
		tbl_shopping_lists.addMouseListener(this);
		
		setTitle("قوائم المشتريات");
		setSize(600, 400);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void scroll_down()
	{
		list_scroll.scroll_down();
	}
	
	void update()
	{
		ResultSet shopping_lists = db.query("SELECT * FROM shopping_lists");
		
		ArrayList<Object[]> data_rows = new ArrayList<>();
		
		ArrayList<String> list_titles = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			
			while(shopping_lists.next())
			{
				int list_id = shopping_lists.getInt("id");
				
				/*
				JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_VIEW, list_id, TOOLS_VIEW, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, list_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, list_id, TOOLS_DELETE, this));
				
				data_rows.add(new Object[] {shopping_lists.getString("title"), tools_panel});
				*/
				
				list_titles.add(shopping_lists.getString("title"));
				
				row_ids.add(list_id);
				
			}
			
			for(int i = 0; i < row_ids.size(); i++)
			{
				int list_id = row_ids.get(i);
				
				ResultSet list_items = db.query("SELECT * FROM shopping_list_items WHERE list_id="+list_id);
				
				int items_count = 0;
				double total_amount = 0;
				
				while(list_items.next())
				{
					total_amount += list_items.getInt("quantity") * list_items.getDouble("price");
					items_count++;
				}
				
				JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_VIEW, list_id, TOOLS_VIEW, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, list_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, list_id, TOOLS_DELETE, this));
				
				data_rows.add(new Object[] {list_titles.get(i), items_count, common.monetary(total_amount), tools_panel});
			}
			
		} catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"العنوان", "عدد العناصر", "المبلغ", "أدوات"};
		
		tbl_shopping_lists.set_model(data_rows, columns);
		tbl_shopping_lists.set_row_ids(row_ids);
		tbl_shopping_lists.add_leading_align_column(0);
		tbl_shopping_lists.set_column_width(1, 70);
		tbl_shopping_lists.set_column_width(2, 70);
		tbl_shopping_lists.set_column_width(3, 73);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn_tools = (custom_models.tools_button) source;
			
			if(btn_tools.operation == TOOLS_VIEW || btn_tools.operation == TOOLS_EDIT)
			{
				dialog_add_shopping_list dlg_add_shopping_list = new dialog_add_shopping_list();
				dlg_add_shopping_list.show(this, db, btn_tools.item_index);
			}
			else if(btn_tools.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "هل أنت متأكد من أنك تريد حذف القائمة؟");
				
				if(response == msgbox.YES)
				{
					db.query_nr("DELETE FROM shopping_list_items WHERE list_id="+btn_tools.item_index);
					db.query_nr("DELETE FROM shopping_lists WHERE id="+btn_tools.item_index);
					
					update();
				}
			}
		}
		
		else if(source == btn_add)
		{
			dialog_add_shopping_list dlg_add_shopping_list = new dialog_add_shopping_list();
			
			dlg_add_shopping_list.dlg_shopping_lists = this;
			
			dlg_add_shopping_list.show(this, db);
		}
	}

	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			dialog_add_shopping_list dlg_add_shopping_list = new dialog_add_shopping_list();
			dlg_add_shopping_list.show(this, db, tbl_shopping_lists.get_row_id(tbl_shopping_lists.getSelectedRow()));
		}
	}
	
}
