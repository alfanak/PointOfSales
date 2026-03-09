package gui;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import common.common;
import database.data_security;
import database.database;

public class security_settings_panel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	database db;
	
	Component parent;
	
	common.update_listener update_listener;
	
	JPanel btn_bar;
	JPanel form;
	
	custom_models.button btn_create = new custom_models.button("إنشاء...", this);
	custom_models.button btn_restore = new custom_models.button("استرجاع...", this);
	
	public security_settings_panel(database db, Component parent, common.update_listener update_listener)
	{
		this.db = db;
		this.parent = parent;
		this.update_listener = update_listener;
		
		JPanel pnl_backup = new JPanel();
		
		pnl_backup.setBorder(BorderFactory.createTitledBorder("النسخ الاحتياطي: "));
		
		pnl_backup.add(btn_create);
		pnl_backup.add(btn_restore);
		
		add(pnl_backup);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		
		data_security ds = new data_security();
		
		if(source == btn_create)
		{
			custom_models.file_browser fb = new custom_models.file_browser("حفظ نسخة احتياطية");
			
			fb.setSelectedFile(new File(db.server_info().db()+".gz"));
			
			int response = fb.showSaveDialog(parent);
			
			if(response == JFileChooser.APPROVE_OPTION)
			{
				File selected_file = fb.getSelectedFile();
				
				if(selected_file.exists())
				{
					int confirm_reponse = msgbox.confirm(parent, "يوجد ملف بنفس الاسم، هل تريد استبداله؟");
					
					if(confirm_reponse != msgbox.OK)
					{
						return;
					}
				}
				
				int last_dot_position = selected_file.getName().lastIndexOf('.');
				
				String extension = last_dot_position == -1 ? "" : selected_file.getName().substring(last_dot_position + 1);
				
				boolean compress = extension.equals("zip") || extension.equals("gz") ? true : false;
				
				boolean use_mysqldump = false;
				
				if(use_mysqldump) 
				{
					ds.mysqldump_create_backup(db.server_info().db(), selected_file);
				}
				else
				{
					if(ds.create_backup(selected_file, compress, false, false, false))
					{
						msgbox.info(parent, "تم إنشاء النسخة الاحتياطية بنجاح.");
					}
				}
			}
		}
		else if(source == btn_restore)
		{
			custom_models.file_browser fb = new custom_models.file_browser("استرجاع نسخة احتياطية");
			
			int response = fb.showOpenDialog(parent);
			
			if(response == JFileChooser.APPROVE_OPTION)
			{
				int confirm_reponse = msgbox.confirm(parent, "تنبيه!! سيتم مسح جميع البيانات من قاعدة البيانات الحالية واستبدالها بالبيانات الموجودة على قاعدة البيانات المسترجعة، هل أنت متأكد من أنك تريد المتابعة؟");
				
				if(confirm_reponse != msgbox.OK)
				{
					return;
				}
				
				File selected_file = fb.getSelectedFile();
				
				if(selected_file.getName().endsWith(".gz"))
				{
					if(ds.restore_compressed(selected_file, true))
					{
						msgbox.info(parent, "تم استرجاع النسخة الاحتياطية بنجاح.");
					}
				}
				
				if(selected_file.getName().endsWith(".sql") || selected_file.getName().indexOf('.') == -1)
				{
					if(ds.restore(selected_file, true))
					{
						msgbox.info(parent, "تم استرجاع النسخة الاحتياطية بنجاح.");
					}
				}
				else
				{
					msgbox.error(parent, "الملف المحدد غير مطابق، يرجى اختيار ملف مناسب للقيام بعملة الاسترجاع.");
				}
			}
		}
	}
}
