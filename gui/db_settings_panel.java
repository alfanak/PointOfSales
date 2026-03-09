package gui;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import database.database;
import setup.setup;

public class db_settings_panel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	database db = database.get_instance();
	
	common.update_listener update_listener;
	
	JPanel btn_bar;
	JPanel form;
	
	custom_models.button btn_connect_server = new custom_models.button("اتصال", this);
	custom_models.button btn_connect_user   = new custom_models.button("اتصال", this);
	custom_models.button btn_connect_db     = new custom_models.button("اتصال", this);
	custom_models.button btn_create_db      = new custom_models.button("إنشاء...", this);
	custom_models.button btn_create_tables  = new custom_models.button("إنشاء الجداول", this);
	
	ImageIcon icon_server = new ImageIcon(getClass().getResource("/icons/server32x32.png"));
	
	JLabel lbl_title         = new JLabel("الاتصال بخادم قواعد البيانات");
	JLabel lbl_host 	     = new JLabel("السرفر:");
	JLabel lbl_port 	     = new JLabel("البوابة:");
	JLabel lbl_user 	     = new JLabel("اسم المستخدم:");
	JLabel lbl_password	     = new JLabel("كلمة المرور:");
	JLabel lbl_dbname 	     = new JLabel("اسم قاعدة البيانات:");
	
	custom_models.light_indicator server_status_indicator = new custom_models.light_indicator("متصل", custom_models.light_indicator.GREEN);
	custom_models.light_indicator user_status_indicator = new custom_models.light_indicator("متصل", custom_models.light_indicator.GREEN);
	custom_models.light_indicator db_status_indicator = new custom_models.light_indicator("متصل", custom_models.light_indicator.GREEN);
	
	JTextField txt_host 	= new JTextField("", 20);
	JTextField txt_port 	= new JTextField("", 20);
	JTextField txt_user 	= new JTextField("", 20);
	JTextField txt_password = new JTextField("", 20);
	JTextField txt_dbname 	= new JTextField("", 20);
	
	db_settings_panel(common.update_listener update_listener)
	{
		this.update_listener = update_listener;
		
		txt_host.setText(db.server_info().host());
		txt_port.setText(db.server_info().port());
		txt_user.setText(db.server_info().user());
		txt_password.setText(db.server_info().pass());
		txt_dbname.setText(db.server_info().db());
		
		setLayout(new BorderLayout());
		
		//*** استمارة الاتصال بالخادم ***//
		
		JPanel server_panel = new JPanel(new GridBagLayout());
		server_panel.setBorder(BorderFactory.createTitledBorder("الاتصال بالخادم "));
		
		custom_models.GBConstraints c = new custom_models.GBConstraints();
		
		if(db.status == database.STATUS_NOT_CONNECTED)
		{
			server_status_indicator.set("غير متصل", custom_models.light_indicator.RED);
		}
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		server_panel.add(server_status_indicator, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 1;
		server_panel.add(lbl_host, c);
		
		c.gridx = 1;
		c.gridy = 1;
		server_panel.add(txt_host, c);
		
		c.gridx = 0;
		c.gridy = 2;
		server_panel.add(lbl_port, c);
		
		c.gridx = 1;
		c.gridy = 2;
		server_panel.add(txt_port, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		server_panel.add(btn_connect_server, c);
		
		//*** استمارة اتصال المستخدم ***//
		
		JPanel user_panel = new JPanel(new GridBagLayout());
		user_panel.setBorder(BorderFactory.createTitledBorder("فحص بيانات المستخدم "));
		
		c = new custom_models.GBConstraints();
		
		if(db.status < database.STATUS_USER_APPROVED)
		{
			user_status_indicator.set("غير متصل", custom_models.light_indicator.RED);
		}
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		user_panel.add(user_status_indicator, c);
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 1;
		user_panel.add(lbl_user, c);
		
		c.gridx = 1;
		c.gridy = 1;
		user_panel.add(txt_user, c);
		
		c.gridx = 0;
		c.gridy = 2;
		user_panel.add(lbl_password, c);
		
		c.gridx = 1;
		c.gridy = 2;
		user_panel.add(txt_password, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		user_panel.add(btn_connect_user, c);
		
		//*** استمارة الاتصال بقاعدة البيانات ***//
		
		JPanel db_panel = new JPanel(new GridBagLayout());
		db_panel.setBorder(BorderFactory.createTitledBorder("الاتصال بقاعدة البيانات "));
		
		c = new custom_models.GBConstraints();
		
		if(db.status < database.STATUS_DATABASE_FOUND)
		{
			db_status_indicator.set("غير متصل", custom_models.light_indicator.RED);
		}
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		db_panel.add(db_status_indicator, c);
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 1;
		db_panel.add(lbl_dbname, c);
		
		c.gridx = 1;
		c.gridy = 1;
		db_panel.add(txt_dbname, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		JPanel db_buttons_panel = new JPanel();
		db_buttons_panel.add(btn_connect_db);
		db_buttons_panel.add(btn_create_db);
		db_buttons_panel.add(btn_create_tables);
		db_panel.add(db_buttons_panel, c);
		
		//***//
		
		btn_bar = new JPanel(new GridBagLayout());
		form = new JPanel(new GridBagLayout());
		
		btn_bar.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		
		lbl_title.setIcon(icon_server);
		
		JPanel main_container = new JPanel(new BorderLayout());
		main_container.add(server_panel, BorderLayout.PAGE_START);
		main_container.add(user_panel, BorderLayout.CENTER);
		main_container.add(db_panel, BorderLayout.PAGE_END);
		
		add(lbl_title, BorderLayout.PAGE_START);
		add(main_container, BorderLayout.CENTER);
		add(btn_bar, BorderLayout.PAGE_END);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_connect_server)
		{
			String str_host = txt_host.getText();
			String str_port = txt_port.getText();
			
			if(str_host.isBlank() || str_port.isBlank())
			{
				JOptionPane.showMessageDialog(null, "يجب كتابة عنوان الخادم ورقم البوابة", "خطأ", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				db.connect(str_host, str_port, "", "", "", true);
				
				if(db.status >= database.STATUS_MYSQL_SERVER_ON)
				{
					server_status_indicator.set("متصل", custom_models.light_indicator.GREEN);
					
					// حفظ بيانات الخادم
					
					db.server_info().set_host(str_host);
					db.server_info().set_port(str_port);
					db.save_parameters();
				}
				else
				{
					server_status_indicator.set("غير متصل", custom_models.light_indicator.RED);
				}
			}
		}
		else if(source == btn_connect_user)
		{
			String str_user = txt_user.getText();
			String str_pass = txt_password.getText();
			
			if(str_user.isBlank())
			{
				JOptionPane.showMessageDialog(null, "يجب كتابة اسم المستخدم", "خطأ", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				db.connect(db.server_info().host(), db.server_info().port(), str_user, str_pass, "", true);
				
				if(db.status >= database.STATUS_USER_APPROVED)
				{
					user_status_indicator.set("متصل", custom_models.light_indicator.GREEN);
					
					// حفظ بيانات الخادم
					
					db.server_info().set_user(str_user);
					db.server_info().set_pass(str_pass);
					db.save_parameters();
				}
				else
				{
					user_status_indicator.set("غير متصل", custom_models.light_indicator.RED);
				}
			}
		}
		else if(source == btn_connect_db)
		{
			String str_dbname     = txt_dbname.getText();
			
			if(str_dbname.isBlank())
			{
				JOptionPane.showMessageDialog(null, "يجب كتابة اسم قاعدة البيانات", "خطأ", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				db.connect(db.server_info().host(), db.server_info().port(), db.server_info().user(), db.server_info().pass(), str_dbname, true);
				
				if(db.status >= database.STATUS_DATABASE_FOUND)
				{
					db_status_indicator.set("متصل", custom_models.light_indicator.GREEN);
					
					db.server_info().set_db(str_dbname);
					db.save_parameters();
					
					int tables_count = db.tables_count();
					
					if(tables_count > 0)
					{
						msgbox.info(this, "تم الاتصال بقاعدة البيانات.");
					}
					else
					{
						msgbox.info(this, "تم الاتصال بقاعدة البيانات ولكنها لا تحتوي على جداول.");
					}
				}
				else
				{
					db_status_indicator.set("غير متصل", custom_models.light_indicator.RED);
				}
			}
		}
		else if(source == btn_create_db)
		{
			dialog_createdb d = new dialog_createdb(this, db);
			d.setVisible(true);
		}
		else if(source == btn_create_tables)
		{
			setup inst = new setup();
			
			if(inst.create_tables())
			{
				msgbox.info(this, "تم إنشاء الجداول بنجاح.");
			}
			else
			{
				db.show_error_msg("حدث خطأ أثناء محاولة إنشاء الجداول.");
			}
		}
		
		update_listener.updated();
	}
}
