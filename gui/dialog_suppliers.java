package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import database.database;

public class dialog_suppliers extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	private static final int TOOLS_EDIT = 0;
	private static final int TOOLS_DELETE = 1;
	
	database db;
	
	custom_models.list_table lst_suppliers = new custom_models.list_table(this);
	
	custom_models.button btn_ok = new custom_models.button("تم", this);
	custom_models.button btn_add_supplier = new custom_models.button("إضافة مورد", this);
	
	public void show(Component parent, database db)
	{
		this.db = db;
		
		update();
		
		JPanel top_container = new JPanel();
		
		top_container.add(btn_add_supplier);
		
		JPanel buttons_container = new JPanel();
		
		buttons_container.add(btn_ok);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(top_container, BorderLayout.PAGE_START);
		container.add(new JScrollPane(lst_suppliers), BorderLayout.CENTER);
		container.add(buttons_container, BorderLayout.PAGE_END);
		
		setTitle("الموردون");
		setSize(400, 300);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	public void update()
	{
		ResultSet suppliers = db.query("SELECT * FROM suppliers");
		
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			while(suppliers.next())
			{
				int supplier_id = suppliers.getInt("id");
				
				JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, supplier_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, supplier_id, TOOLS_DELETE, this));
				
				data.add(new Object[] {suppliers.getString("name"), tools_panel});
				
				row_ids.add(supplier_id);
			}
			
		} catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[]{"اسم المورد", "أدوات"};
		
		lst_suppliers.set_model(data, columns);
		lst_suppliers.set_row_ids(row_ids);
		lst_suppliers.add_leading_align_column(0);
		lst_suppliers.set_column_width(1, 50);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn_tools = (custom_models.tools_button) source;
			
			if(btn_tools.operation == TOOLS_EDIT)
			{
				dialog_add_supplier dlg_add_supplier = new dialog_add_supplier();
				dlg_add_supplier.show(this, db, btn_tools.item_index);
			}
			else if(btn_tools.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "هل أنت متأكد من أنك تريد حذف المورد؟");
				
				if(response == msgbox.YES)
				{
					db.query_nr("DELETE FROM suppliers WHERE id="+btn_tools.item_index);
					
					update();
				}
			}
		}
		else if(source == btn_ok)
		{
			dispose();
		}
		else if(source == btn_add_supplier)
		{
			dialog_add_supplier dlg_add_supplier = new dialog_add_supplier();
			
			dlg_add_supplier.show(this, db);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			dialog_add_supplier dlg_add_supplier = new dialog_add_supplier();
			dlg_add_supplier.show(this, db, lst_suppliers.get_row_id(lst_suppliers.getSelectedRow()));
		}
	}
	
}
