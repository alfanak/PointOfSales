package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.database;
import setup.setup;

public class dialog_createdb extends JDialog implements ActionListener
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
	
	JTextField txt_dbname 	= new JTextField("", 20);
	JTextField txt_charset 	= new JTextField("utf8", 20);
	JTextField txt_collation = new JTextField("utf8_unicode_ci", 20);
	
	dialog_createdb(Component parent, database dbase)
	{
		db = dbase;
		
		txt_dbname.setText(db.server_info().db());
		
		setLayout(new BorderLayout());
		
		btn_bar = new JPanel(new GridBagLayout());
		form = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 2, 2, 2);
		form.add(lbl_dbname, c);
		
		c.gridx = 1;
		c.gridy = 0;
		form.add(txt_dbname, c);
		
		c.gridx = 0;
		c.gridy = 1;
		form.add(lbl_charset, c);
		
		c.gridx = 1;
		c.gridy = 1;
		form.add(txt_charset, c);
		
		c.gridx = 0;
		c.gridy = 2;
		form.add(lbl_collation, c);
		
		c.gridx = 1;
		c.gridy = 2;
		form.add(txt_collation, c);
		
		//------
		
		btn_cancel.addActionListener(this);
		btn_create.addActionListener(this);
		
		btn_bar.add(btn_create);
		btn_bar.add(btn_cancel);
		btn_bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		
		lbl_title.setIcon(icon_db);
		
		add(lbl_title, BorderLayout.PAGE_START);
		add(form, BorderLayout.CENTER);
		add(btn_bar, BorderLayout.PAGE_END);
		
		
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setSize(350, 200);
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(parent);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_create)
		{
			String str_charset = txt_charset.getText();
			String str_collation = txt_collation.getText();
			String str_dbname = txt_dbname.getText();
			
			if(str_dbname.isBlank())
			{
				JOptionPane.showMessageDialog(null, "يجب كتابة اسم قاعدة البيانات");
			}
			else
			{
				if(db.create_db(str_dbname, str_charset, str_collation))
				{
					db.server_info().set_db(str_dbname);
					
					db.connect(true);
					
					db.save_parameters();
					
					setup s = new setup();
					
					s.create_tables();
					
					db.show_info_msg(this, "تم إنشاء قاعدة البيانات بنجاح");
				}
				else
				{
					db.show_error_msg("حدث خطأ أثناء محاولة إنشاء قاعدة البيانات");
				}
			}
		}
		if(source == btn_cancel)
		{
			dispose();
		}
	}
}
