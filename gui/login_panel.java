package gui;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import common.common;
import database.database;
import database.user;

public class login_panel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	common.update_listener update_listener;
	
	Component parent;
	
	JButton btn_login = new JButton("دخول");
	
	JLabel lbl_user = new JLabel("اسم المستخدم");
	JLabel lbl_password = new JLabel("كلمة المرور");

	custom_models.textfield txt_user = new custom_models.textfield("", 15);
	JPasswordField txt_password = new JPasswordField("", 15);
	
	login_panel(Component parent, database db, common.update_listener update_listener)
	{
		this.update_listener = update_listener;
		
		this.parent = parent;
		
		custom_models.form login_form = new custom_models.form(2);
		login_form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("تسجيل الدخول: "), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		login_form.add(lbl_user);
		login_form.add(txt_user);
		login_form.add(lbl_password);
		login_form.add(txt_password);
		login_form.add(btn_login, 2, GridBagConstraints.NONE, GridBagConstraints.CENTER);
		
		add(login_form);
		
		btn_login.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_login)
		{
			String username = txt_user.getText();
			
			if(username.isBlank())
			{
				msgbox.info(parent, "عليك كتابة اسم المستخدم.");
				return;
			}
			
			user.current_user().login(txt_user.getText(), new String(txt_password.getPassword()));
			
			if(user.current_user().loggedin)
			{
				update_listener.updated();
			}
			else
			{
				msgbox.error(parent, "بيانات المستخدم خاطئة، عليك التحقق من كتابة الاسم وكلمة المرور بشكل صحيح");
				return;
			}
		}
	}
}
