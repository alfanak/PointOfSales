package gui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.common;
import config.configs;
import database.database;
import setup.setup;

public class dialog_setup extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	common.update_listener update_listener;
	
	database db = database.get_instance();
	
	setup inst = new setup();
	
	panel_create_db form_db;
	panel_add_account form_account;
	
	ImageIcon icon_settings = new ImageIcon(getClass().getResource("/icons/settings32x32.png"));
	ImageIcon icon_server = new ImageIcon(getClass().getResource("/icons/server32x32.png"));
	ImageIcon icon_db = new ImageIcon(getClass().getResource("/icons/db32x32.png"));
	ImageIcon icon_ok = new ImageIcon(getClass().getResource("/icons/ok32x32.png"));
	
	custom_models.title_label lbl_welcome_title = new custom_models.title_label("ضبط الإعدادات", icon_settings);
	
	custom_models.mllabel lbl_welcome = new custom_models.mllabel(
			"أهلا وسهلا بك في معالج ضبط إعداد البرنامج"+"\n"+
			"تأكد من تثبيت وتشغيل خادم قواعد البيانات ثم أدخل الإعدادات المناسبة"+"\n"+
			"اضغط \"التالي\" للبد في ضبط الإعدادات"
			);
	
	custom_models.title_label lbl_finish = new custom_models.title_label("تم ضبط الإعدادات بنجاح!", icon_ok);
	
	custom_models.title_label lbl_title = new custom_models.title_label("إعدادات خادم قواعد البيانات", icon_server);
	
	JLabel lbl_host = new JLabel("السرفر:");
	JLabel lbl_port = new JLabel("البوابة:");
	JLabel lbl_user = new JLabel("اسم المستخدم:");
	JLabel lbl_pass = new JLabel("كلمة المرور:");
	
	custom_models.textfield txt_host = new custom_models.textfield(configs.DEFAULT_SERVER_HOST, 20);
	custom_models.textfield txt_port = new custom_models.textfield(configs.DEFAULT_SERVER_PORT, 5);
	custom_models.textfield txt_user = new custom_models.textfield(configs.DEFAULT_SERVER_USER, 15);
	custom_models.textfield txt_pass = new custom_models.textfield(configs.DEFAULT_SERVER_PASS, 15);
	
	custom_models.button btn_next = new custom_models.button("التالي", this);
	custom_models.button btn_previous = new custom_models.button("السابق", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	CardLayout layout = new CardLayout();
	JPanel contents_panel = new JPanel(layout);
	
	step current_step = step.WELCOME;
	
	void show(Component parent, common.update_listener update_listener)
	{
		this.update_listener = update_listener;
		
		if(configs.is_installed())
		{
			msgbox.info(this, "تم ضبط الإعدادات مسبقا، إذا واجهتك مشكلة في تشغيل البرنامج حاول ضبط الإعدادات من النافذة المخصصة لذلك.");
		}
		
		custom_models.form form_welcome = new custom_models.form(1);
		
		form_welcome.add(lbl_welcome_title);
		form_welcome.add(lbl_welcome);
		
		form_db = new panel_create_db();
		form_account = new panel_add_account(this, -1, false);
		
		custom_models.form form_server = new custom_models.form(2);
		
		form_server.add(lbl_title, 2);
		form_server.add(lbl_host);
		form_server.add(txt_host);
		form_server.add(lbl_port);
		form_server.add(txt_port);
		form_server.add(lbl_user);
		form_server.add(txt_user);
		form_server.add(lbl_pass);
		form_server.add(txt_pass);
		
		custom_models.form form_finish = new custom_models.form(1);
		
		form_finish.add(lbl_finish);
		
		contents_panel.add(form_welcome, step.WELCOME.name());
		contents_panel.add(form_server, step.SERVER.name());
		contents_panel.add(form_db, step.DATABASE.name());
		contents_panel.add(form_account, step.ACCOUNT.name());
		contents_panel.add(form_finish, step.FINISH.name());
		
		
		layout.show(contents_panel, "WELCOME");
		current_step = step.WELCOME;
		btn_previous.setEnabled(false);
		
		custom_models.form btn_form = new custom_models.form(3);
		
		btn_form.add(btn_previous);
		btn_form.add(btn_next);
		btn_form.add(btn_cancel);
		
		JPanel main_container = new JPanel(new BorderLayout());
		main_container.add(contents_panel, BorderLayout.CENTER);
		main_container.add(btn_form, BorderLayout.PAGE_END);
		
		setTitle("ضبط الإعدادات");
		setSize(450, 250);
		setContentPane(main_container);
		set_visible(true, parent);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_next)
		{
			switch(current_step)
			{
			case WELCOME:
				
				layout.show(contents_panel, "SERVER");
				
				current_step = step.SERVER;
				
				btn_previous.setEnabled(true);
				
				break;
			
			case SERVER:
				
				String host = txt_host.getText().trim();
				String port = txt_port.getText().trim();
				String user = txt_user.getText().trim();
				String pass = txt_pass.getText().trim();
				
				if(inst.connect_server(host, port, user, pass))
				{
					db.save_parameters();
					
					layout.show(contents_panel, "DATABASE");
					
					current_step = step.DATABASE;
				}
				else
				{
					db.show_error_msg(this, "حدث خطأ أثناء محاولة الاتصال بالخادم");
				}
				
				break;
				
			case DATABASE:
				
				String str_dbname = form_db.txt_dbname.getText().trim();
				String str_charset = form_db.txt_charset.getText().trim();
				String str_collation = form_db.txt_collation.getText().trim();
				
				if(str_dbname.isBlank())
				{
					msgbox.error(this, "يجب كتابة اسم قاعدة البيانات");
					
					break;
				}
				
				if( ! inst.valid_charset(str_charset))
				{
					msgbox.error(this, "خطأ في كتابة ترميز الأحرف.");
					
					break;
				}
				
				if( ! inst.valid_charset_collation(str_charset, str_collation))
				{
					if(inst.valid_collation(str_collation))
					{
						msgbox.error(this, "قواعد الفرز المختارة لا تناسب ترميز الحروف المحدد.");
					}
					else
					{
						msgbox.error(this, "خطأ في كتابة قواعد الفرز.");
					}
					break;
				}
				
				if(inst.db_exists(str_dbname))
				{
					msgbox.error(this, "لا يمكن إنشاء قاعدة البيانات لأن هناك واحدة بهذا الاسم.");
					
					break;
				}
				
				if(db.create_db(str_dbname, str_charset, str_collation))
				{
					db.server_info().set_db(str_dbname);
					
					if(inst.connect_db(str_dbname))
					{
						setup s = new setup();
						
						s.create_tables();
						
						s.set_primary_data();
						
						db.save_parameters();
						
						db.show_info_msg(this, "تم إنشاء قاعدة البيانات بنجاح");
						
						layout.show(contents_panel, "ACCOUNT");
						
						current_step = step.ACCOUNT;
					}
					else
					{
						db.show_error_msg("حدث خطأ أثناء محاولة الاتصال بقاعدة البيانات");
					}
				}
				else
				{
					db.show_error_msg("حدث خطأ أثناء محاولة إنشاء قاعدة البيانات");
				}
				break;
			
			case ACCOUNT:
				
				String str_username = form_account.txt_name.getText();
				char[] char_password = form_account.txt_pass.getPassword();
				char[] char_password_confirm = form_account.txt_pass_confirm.getPassword();
				
				// رتبة المدير تحميل رقم التعريف 1 في حال تم تثبيت البيانات الابتدائية بشكل صحيح، ولكن لا بأس من تأكيد أن حساب المدير يجب أن يملك جميع الصلاحيات في حال حدوث خطأ ما
				
				int role_id = 1;
				
				ResultSet rs_admin_role = db.query("SELECT * FROM user_roles WHERE permissions='ALL'");
				
				try
				{
					if(rs_admin_role.next())
					{
						role_id = rs_admin_role.getInt("id");
					}
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
				
				
				if(char_password.length == 0)
				{
					msgbox.info(this, "يجب عليك كتابة كلمة المرور.");
					
					return;
				}
				
				else if( ! Arrays.equals(char_password, char_password_confirm))
				{
					msgbox.info(this, "كلمة المرور غير متطابقة.");
					
					return;
				}
				
				if(inst.create_account(str_username, new String (char_password), role_id))
				{
					msgbox.info(this, "تم إنشاء حساب المدير بنجاح.");
					
					configs.set_installed();
					
					layout.show(contents_panel, "FINISH");
					
					current_step = step.FINISH;
					
					btn_next.setText("تم");
					btn_cancel.setVisible(false);
				}
				else
				{
					msgbox.error(this, "فشل إنشاء حساب المدير.");
					
					return;
				}
				
				break;
				
			case FINISH:
				
				dispose();
				
				break;
			}
			
		}
		else if(source == btn_previous)
		{
			switch(current_step)
			{
			case WELCOME: break;
			case SERVER:
				layout.show(contents_panel, "WELCOME");
				
				current_step = step.WELCOME;
				
				btn_previous.setEnabled(false);
				
				break;
				
			case DATABASE:
				
				layout.show(contents_panel, "SERVER");
				
				current_step = step.SERVER;
				
				break;
				
			case ACCOUNT:
				
				layout.show(contents_panel, "DATABASE");
				
				current_step = step.DATABASE;
			
				break;
				
			case FINISH:
				
				layout.show(contents_panel, "ACCOUNT");
				
				current_step = step.ACCOUNT;
				
				btn_next.setText("التالي");
				btn_cancel.setVisible(true);
				
				break;
			}
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}
	
	enum step
	{
		WELCOME,
		SERVER,
		DATABASE,
		ACCOUNT,
		FINISH
	}
	
}
