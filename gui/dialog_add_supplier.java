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

import common.common;
import database.database;

public class dialog_add_supplier extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	database db;
	
	dialog_suppliers dlg_suppliers;
	
	common.update_listener update_listener;
	
	custom_models.title_label lbl_title   = new custom_models.title_label("معلومات المورد:");
	JLabel lbl_name    = new JLabel("اسم المورد: ");
	JLabel lbl_company = new JLabel("المؤسسة: ");
	JLabel lbl_address = new JLabel("العنوان: ");
	JLabel lbl_email   = new JLabel("البريد الإلكتروني: ");
	JLabel lbl_phone   = new JLabel("رقم الهاتف: ");
	
	JTextField txt_name    = new JTextField("", 20);
	JTextField txt_company = new JTextField("", 20);
	JTextField txt_address = new JTextField("", 20);
	JTextField txt_email   = new JTextField("", 20);
	JTextField txt_phone   = new JTextField("", 20);
	
	custom_models.button btn_ok = new custom_models.button("تم", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	private int supplier_id = -1;

	void show(Component parent, database db)
	{
		show(parent, db, -1);
	}
	
	void show(Component parent, database db, common.update_listener update_listener)
	{
		this.update_listener = update_listener;
		
		show(parent, db, -1);
	}
	
	void show(Component parent, database db, int supplier_id)
	{
		this.db = db;
		
		this.supplier_id = supplier_id;
		
		if(parent.getClass() == dialog_suppliers.class)
		{
			dlg_suppliers = (dialog_suppliers) parent;
		}
		
		if(supplier_id > -1)
		{
			try
			{
				ResultSet supplier = db.query("SELECT * FROM suppliers WHERE id="+supplier_id);
			
				if(supplier.next())
				{
					txt_name.setText(supplier.getString("name"));
					txt_company.setText(supplier.getString("company"));
					txt_address.setText(supplier.getString("address"));
					txt_email.setText(supplier.getString("email"));
					txt_phone.setText(supplier.getString("phone"));
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
		form.add(lbl_company);
		form.add(txt_company);
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
		
		setTitle("إضافة مورد");
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
				JOptionPane.showMessageDialog(null, "عليك كتابة اسم المورد", "خطأ", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				if(db.exist("suppliers", "name='"+str_name+"'") && supplier_id < 0)
				{
					db.show_error_msg("يوجد مورد بنفس الاسم.");
				}
				else
				{
					PreparedStatement pstmt = db.pstmt("INSERT INTO suppliers(name, company, address, email, phone) VALUES(?, ?, ?, ?, ?)");
					
					if(supplier_id > -1)
					{
						pstmt = db.pstmt("UPDATE suppliers SET name=?, company=?, address=?, email=?, phone=? WHERE id="+supplier_id);
					}
					
					try
					{
						pstmt.setString(1, str_name);
						pstmt.setString(2, txt_company.getText());
						pstmt.setString(3, txt_address.getText());
						pstmt.setString(4, txt_email.getText());
						pstmt.setString(5, txt_phone.getText());
						
						pstmt.executeUpdate();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
					
					if(dlg_suppliers != null)
					{
						dlg_suppliers.update();
					}
					
					if(update_listener != null)
					{
						update_listener.updated();
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
