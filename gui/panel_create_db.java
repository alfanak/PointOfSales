package gui;
import java.awt.ComponentOrientation;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import config.configs;
import database.database;

public class panel_create_db extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	database db;
	
	JPanel btn_bar;
	JPanel form;
	
	JButton btn_create = new JButton("إنشاء");
	JButton btn_cancel = new JButton("إلغاء");
	
	ImageIcon icon_db = new ImageIcon(getClass().getResource("/icons/db32x32.png"));
	
	JLabel lbl_title     = new JLabel("إنشاء قاعدة بيانات جديدة");
	JLabel lbl_dbname 	 = new JLabel("اسم قاعدة البيانات:");
	JLabel lbl_charset 	 = new JLabel("ترميز الحروف");
	JLabel lbl_collation = new JLabel("مجموعة الحروف");
	
	JTextField txt_dbname 	= new JTextField(configs.DEFAULT_DB_NAME, 20);
	JTextField txt_charset 	= new JTextField(configs.DEFAULT_CHARSET, 20);
	JTextField txt_collation = new JTextField(configs.DEFAULT_COLLATION, 20);
	
	
	panel_create_db()
	{
		custom_models.form form = new custom_models.form(2);
		
		lbl_title.setIcon(icon_db);
		
		form.add(lbl_title, 2);
		form.add(lbl_dbname);
		form.add(txt_dbname);
		form.add(lbl_charset);
		form.add(txt_charset);
		form.add(lbl_collation);
		form.add(txt_collation);
		
		this.add(form);
		
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	}
}
