package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.database;

public class dialog_add_client extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	database db;
	
	dialog_clients dlg_clients;
	
	custom_models.title_label lbl_title   = new custom_models.title_label("معلومات الزبون:");
	JLabel lbl_name    = new JLabel("اسم الزبون: ");
	JLabel lbl_address = new JLabel("العنوان: ");
	JLabel lbl_email   = new JLabel("البريد الإلكتروني: ");
	JLabel lbl_phone   = new JLabel("رقم الهاتف: ");
	
	JTextField txt_name    = new JTextField("", 20);
	JTextField txt_address = new JTextField("", 20);
	JTextField txt_email   = new JTextField("", 20);
	JTextField txt_phone   = new JTextField("", 20);
	
	custom_models.button btn_ok = new custom_models.button("تم", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	private int client_id = -1;
	
	void show(Component parent, database db)
	{
		show(parent, db, -1);
	}
	
	void show(Component parent, database db, int client_id)
	{
		this.db = db;
		
		this.client_id = client_id;
		
		if(parent.getClass() == dialog_clients.class)
		{
			dlg_clients = (dialog_clients) parent;
		}
		
		if(client_id > -1)
		{
			try
			{
				ResultSet client = db.query("SELECT * FROM clients WHERE id="+client_id);
			
				if(client.next())
				{
					txt_name.setText(client.getString("name"));
					txt_address.setText(client.getString("address"));
					txt_email.setText(client.getString("email"));
					txt_phone.setText(client.getString("phone"));
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		
		custom_models.form form = new custom_models.form(2);
		
		form.add(lbl_title, 2);
		form.add(lbl_name);
		form.add(txt_name);
		form.add(lbl_address);
		form.add(txt_address);
		form.add(lbl_email);
		form.add(txt_email);
		form.add(lbl_phone);
		form.add(txt_phone);
		
		JPanel buttons_container = new JPanel();
		
		buttons_container.add(btn_ok);
		buttons_container.add(btn_cancel);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(form, BorderLayout.CENTER);
		container.add(buttons_container, BorderLayout.SOUTH);
		
		setTitle("زبون جديد");
		setSize(320, 250);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			String str_name = txt_name.getText();
			
			if(str_name.isBlank())
			{
				JOptionPane.showMessageDialog(null, "عليك كتابة اسم الزبون", "خطأ", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				if(db.exist("clients", "name='"+str_name+"'") && client_id < 0)
				{
					db.show_error_msg("يوجد زبون بنفس الاسم.");
				}
				else
				{
					PreparedStatement pstmt = db.pstmt("INSERT INTO clients(name, address, email, phone) VALUES(?, ?, ?, ?)");
					
					if(client_id > -1)
					{
						pstmt = db.pstmt("UPDATE clients SET name=?, address=?, email=?, phone=? WHERE id="+client_id);
					}
					
					try
					{
						pstmt.setString(1, str_name);
						pstmt.setString(2, txt_address.getText());
						pstmt.setString(3, txt_email.getText());
						pstmt.setString(4, txt_phone.getText());
						
						pstmt.executeUpdate();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
					
					if(dlg_clients != null)
					{
						dlg_clients.update();
					}
					
					dispose();
				}
			}
		}
		
		if(source == btn_cancel)
		{
			dispose();
		}
	}
}