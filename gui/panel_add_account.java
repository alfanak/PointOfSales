package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
/*
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
*/
import java.util.ArrayList;
//import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import common.common;
import database.database;
import database.db_user;
import database.db_user_role;
import database.user;
import database.user_permissions;

public class panel_add_account extends JPanel/* implements ActionListener*/
{
	private static final long serialVersionUID = 1L;
	
	database db = database.get_instance();
	
	dialog_accounts dlg_accounts;
	
	custom_models.title_label lbl_title = new custom_models.title_label("إضافة مستخدم جديد: ");
	custom_models.title_label lbl_title_edit = new custom_models.title_label("تعديل بيانات المستخدم: ");
	JLabel lbl_name = new JLabel("الاسم: ");
	JLabel lbl_pass = new JLabel("كلمة المرور: ");
	JLabel lbl_pass_confirm = new JLabel("تأكيد كلمة المرور: ");
	JLabel lbl_permissions = new JLabel("الصلاحيات: ");
	JLabel lbl_edit_password_note = new JLabel("إذا لم ترغب في تعديل كلمة المرور اترك الحقل فارغا.");
	
	JTextField txt_name = new JTextField("", 15);
	JPasswordField txt_pass = new JPasswordField("", 15);
	JPasswordField txt_pass_confirm = new JPasswordField("", 15);
	
	JComboBox<common.item> cbx_roles = new JComboBox<>();
	
	//custom_models.button btn_ok = new custom_models.button("موافق", this);
	//custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	int edit_user_id = -1;
	
	boolean can_edit_user = false;
	
	panel_add_account(Component parent, int edit_user_id, boolean show_roles)
	{
		if( ! user.current_user().has_permission(user_permissions.labels.USERS_ADD))
		{
			//return;
		}
		
		this.edit_user_id = edit_user_id;
		
		db_user _user = null;
		
		if(edit_user_id > -1)
		{
			_user = new db_user(edit_user_id);
			
			txt_name.setText(_user.name);
		}
		
		if(db.status == database.STATUS_CONNECTED && show_roles)
		{
			load_roles(user.current_user().allowable_roles());
		}
		/*
		ArrayList<db_user_role> roles = user.current_user().allowable_roles();
		
		for(db_user_role role:roles)
		{
			common.item item = new common.item(role.id, role.name);
			
			cbx_roles.addItem(item);
			
			if(edit_user_id > -1)
			{
				if(role.id == _user.role_id)
				{
					cbx_roles.setSelectedItem(item);
					
					can_edit_user = true;
				}
			}
		}
		*/
		
		if(edit_user_id > -1 && ! can_edit_user)
		{
			msgbox.error(parent, "ليس بإمكانك تعديل بيانات المستخدم: "+_user.name);
			return;
		}
		
		
		JPanel rols_panel = new JPanel();
		rols_panel.add(cbx_roles);
		
		custom_models.form form_container = new custom_models.form(2);
		
		if(edit_user_id > - 1)
		{
			form_container.add(lbl_title_edit, 2);
			
			lbl_edit_password_note.setForeground(Color.red);
			form_container.add(lbl_edit_password_note, 2);
		}
		else
		{
			form_container.add(lbl_title, 2);
		}
		
		form_container.add(lbl_name);
		form_container.add(txt_name);
		form_container.add(lbl_pass);
		form_container.add(txt_pass);
		form_container.add(lbl_pass_confirm);
		form_container.add(txt_pass_confirm);
		
		form_container.add(lbl_permissions);
		
		if(db.status == database.STATUS_CONNECTED && show_roles)
		{
			form_container.add(rols_panel);
		}
		/*
		JPanel btn_container = new JPanel();
		btn_container.add(btn_ok);
		btn_container.add(btn_cancel);
		*/
		
		//JPanel container = new JPanel(new BorderLayout());
		
		//container.add(form_container, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		
		add(form_container, BorderLayout.CENTER);
		
		//container.add(btn_container, BorderLayout.PAGE_END);
	}
	
	void load_roles(ArrayList<db_user_role> allowable_roles)
	{
		System.out.println("loading roles...");
		
		//ArrayList<db_user_role> roles = user.current_user().allowable_roles();
		
		for(db_user_role role:allowable_roles)
		{
			System.out.println(role.name);
			
			common.item item = new common.item(role.id, role.name);
			
			cbx_roles.addItem(item);
			/*
			if(edit_user_id > -1)
			{
				if(role.id == _user.role_id)
				{
					cbx_roles.setSelectedItem(item);
					
					can_edit_user = true;
				}
			}
			*/
		}
	}
	
/*
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			try
			{
				String str_username = txt_name.getText();
				char[] char_password = txt_pass.getPassword();
				char[] char_password_confirm = txt_pass_confirm.getPassword();
				
				if(char_password.length == 0 && edit_user_id == -1)
				{
					msgbox.info(this, "يجب عليك كتابة كلمة المرور.");
					
					return;
				}
				
				else if( ! Arrays.equals(char_password, char_password_confirm))
				{
					msgbox.info(this, "كلمة المرور غير متطابقة.");
					
					return;
				}
				
				PreparedStatement pstmt;
				
				if(edit_user_id == -1)
				{
					if( ! can_edit_user)
					{
						return;
					}
					
					pstmt = db.pstmt("SELECT COUNT(*) FROM users WHERE name=?");
					pstmt.setString(1, str_username);
					
					ResultSet u = pstmt.executeQuery();
					
					if(u.next())
					{
						if(u.getInt(1) > 0)
						{
							msgbox.info(this, "يوجد مستخدم بنفس الاسم، الرجاء اختيار اسم آخر.");
							
							return;
						}
					}
					
					pstmt = db.pstmt("INSERT INTO users(name, pass, role_id) VALUES(?, md5(?), ?)");
					
					pstmt.setString(1, str_username);
					pstmt.setString(2, new String(char_password));
					pstmt.setInt(3, ((common.item)cbx_roles.getSelectedItem()).value);
				}
				else
				{
					if(char_password.length == 0)
					{
						pstmt = db.pstmt("UPDATE users SET name=?, role_id=? WHERE id="+edit_user_id);
						
						pstmt.setString(1, str_username);
						pstmt.setInt(2, ((common.item)cbx_roles.getSelectedItem()).value);
					}
					else
					{
						pstmt = db.pstmt("UPDATE users SET name=?, pass=md5(?), permissions=? WHERE id="+edit_user_id);
						
						pstmt.setString(1, str_username);
						pstmt.setString(2, new String(char_password));
						pstmt.setInt(3, ((common.item)cbx_roles.getSelectedItem()).value);
					}
				}
				
				pstmt.executeUpdate();
				
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			if(dlg_accounts != null)
			{
				dlg_accounts.update();
			}
		}
		else if(source == btn_cancel)
		{
		}
	}
	
*/

}
