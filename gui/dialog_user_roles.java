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

import database.database;

public class dialog_user_roles extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_EDIT = 0;
	static final int TOOLS_DELETE = 1;
	
	database db = database.get_instance();
	
	custom_models.list_table lst_roles = new custom_models.list_table(this);
	
	custom_models.button btn_add = new custom_models.button("إضافة...", this);
	
	void show(Component parent)
	{
		JPanel top_panel = new JPanel();
		
		top_panel.add(btn_add);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(top_panel, BorderLayout.PAGE_START);
		container.add(lst_roles, BorderLayout.CENTER);
		
		update();
		
		setTitle("رتب المستخدمين");
		setContentPane(container);
		setSize(600, 400);
		set_visible(true, parent);
	}
	
	void update()
	{
		ResultSet res_roles = db.query("SELECT * FROM user_roles");
		
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			while(res_roles.next())
			{
				int role_id = res_roles.getInt("id");
				
				JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, role_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, role_id, TOOLS_DELETE, this));
				
				data.add(new Object[] {res_roles.getString("name"), tools_panel});
				
				row_ids.add(role_id);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"الرتبة", "أدوات"};
		
		lst_roles.set_model(data, columns);
		lst_roles.set_row_ids(row_ids);
		lst_roles.add_leading_align_column(0);
		lst_roles.set_column_width(1, 50);
	}

	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn = (custom_models.tools_button) source;
			
			if(btn.operation == TOOLS_EDIT)
			{
				dialog_add_role dlg_add_role = new dialog_add_role();
				
				dlg_add_role.show(this, btn.item_index);
			}
			else if(btn.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "تأكيد الحذف");
				
				if(response == msgbox.YES)
				{
					db.query_nr("DELETE FROM user_roles WHERE id="+btn.item_index);
				}
				
				update();
			}
		}
		
		else if(source == btn_add)
		{
			dialog_add_role dlg_add_role = new dialog_add_role();
			
			dlg_add_role.show(this);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			int role_id = lst_roles.get_row_id(lst_roles.getSelectedRow());
			
			dialog_add_role dlg_add_role = new dialog_add_role();
			
			dlg_add_role.show(this, role_id);
		}
	}
}
