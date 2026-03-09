package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.database;

public class dialog_add_category extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	database db;
	
	dialog_add_category dlg_add_category = this;
	
	custom_models.title_label lbl_title   = new custom_models.title_label("تفاصيل التصنيف:");
	JLabel lbl_category         = new JLabel("التصنيف: ");
	JLabel lbl_description      = new JLabel("الوصف: ");
	
	JTextField txt_category     = new JTextField("", 11);
	JTextField txt_description  = new JTextField("", 11);
	
	JButton btn_ok = new JButton("تم");
	JButton btn_cancel = new JButton("إلغاء");
	
	void show(Component parent, database dbase)
	{
		db = dbase;
		
		JPanel container = new JPanel(new BorderLayout());
		
		JPanel form_container = new JPanel(new GridBagLayout());
		custom_models.GBConstraints c = new custom_models.GBConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		form_container.add(lbl_title, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		form_container.add(lbl_category, c);
		c.gridx = 1;
		c.gridy = 1;
		form_container.add(txt_category, c);
		
		c.gridx = 0;
		c.gridy = 2;
		form_container.add(lbl_description, c);
		c.gridx = 1;
		c.gridy = 2;
		form_container.add(txt_description, c);
		
		JPanel buttons_container = new JPanel();
		buttons_container.add(btn_ok);
		buttons_container.add(btn_cancel);
		
		container.add(form_container, BorderLayout.PAGE_START);
		container.add(buttons_container, BorderLayout.PAGE_END);
		
		btn_ok.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setTitle("إنشاء تصنيف جديد");
		setSize(280, 150);
		setModal(true);
		setContentPane(container);
		setLocationRelativeTo(parent);
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			String str_name = txt_category.getText();
			
			if(str_name.isBlank())
			{
				msgbox.error(dlg_add_category, "عليك كتابة اسم التصنيف أولا.");
				return;
			}
			
			PreparedStatement pstmt = db.pstmt("INSERT INTO product_categories(name, description) VALUES(?, ?)");
			
			try {
				pstmt.setString(1, str_name);
				pstmt.setString(2, txt_description.getText());
				pstmt.executeUpdate();
				
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			dispose();
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}
	
}
