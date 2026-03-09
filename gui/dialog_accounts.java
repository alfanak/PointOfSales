package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import database.database;

public class dialog_accounts extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_EDIT        = 0;
	static final int TOOLS_DELETE      = 2;
	
	database db;
	
	dialog_accounts dlg_accounts;
	
	JLabel lbl_users_count = new JLabel("عدد المستخدمين: ");
	
	custom_models.button btn_add_user = new custom_models.button("إضافة مستخدم", this);
	
	custom_models.list_table lst_users = new custom_models.list_table(this);
	
	void show(Component parent, database db)
	{
		/*
		if( ! user.permissions.USERS_VIEW)
		{
			return;
		}
		*/
		
		this.db = db;
		
		JPanel top_container = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		top_container.add(btn_add_user);
		
		JPanel info_panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		info_panel.add(lbl_users_count);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(top_container, BorderLayout.PAGE_START);
		container.add(new JScrollPane(lst_users), BorderLayout.CENTER);
		container.add(info_panel, BorderLayout.PAGE_END);
		
		update();
		
		setTitle("حسابات المستخدمين");
		setSize(350, 300);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void update()
	{
		ResultSet res_users = db.query("SELECT * FROM users LEFT JOIN user_roles ON users.role_id = user_roles.id");
		
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			while(res_users.next())
			{
				int user_id = res_users.getInt("id");				
				
				boolean can_edit_user = false;
				boolean can_delete_user = false;
				
				JPanel tools_panel = new JPanel();
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, user_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, user_id, TOOLS_DELETE, this));
				
				data.add(new Object[] {res_users.getString("users.name"), res_users.getString("user_roles.name"), tools_panel});
				
				row_ids.add(user_id);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"المستخدم", "الرتبة", "أدوات"};
		
		lst_users.set_model(data, columns);
		lst_users.set_row_ids(row_ids);
		lst_users.add_leading_align_column(0);
		lst_users.set_column_width(1, 100);
		lst_users.set_column_width(2, 50);
		
		lbl_users_count.setText(lbl_users_count.getText()+row_ids.size());
	}

	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn_tools = (custom_models.tools_button) source;
			
			if(btn_tools.operation == TOOLS_EDIT)
			{
				dialog_add_account dlg_add_user = new dialog_add_account();
				dlg_add_user.dlg_accounts = this;
				dlg_add_user.show(this, btn_tools.item_index);
			}
			else if(btn_tools.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "هل أنت متأكد من أنك تريد حذف المستخدم المحدد؟");
				
				if(response == JOptionPane.YES_OPTION)
				{
					db.query_nr("DELETE FROM users WHERE id="+btn_tools.item_index);
				}
				update();
			}
		}
		
		else if(source == btn_add_user)
		{
			dialog_add_account dlg_add_user = new dialog_add_account();
			
			dlg_add_user.dlg_accounts = this;
			
			dlg_add_user.show(this, -1);
		}
	}

	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			dialog_add_account dlg_add_user = new dialog_add_account();
			
			dlg_add_user.dlg_accounts = this;
			
			dlg_add_user.show(this, lst_users.get_row_id(lst_users.getSelectedRow()));
		}
	}
	
}
