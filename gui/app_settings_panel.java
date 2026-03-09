package gui;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import config.configs;

public class app_settings_panel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	
	JCheckBox cbx_show_tab_labels = new JCheckBox("عرض اسم الإطار");
	
	custom_models.button btn_ok = new custom_models.button("موافق", this);
	custom_models.button btn_apply = new custom_models.button("تطبيق", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	String tip_show_tab_labels = "عرض اسم الإطار بجانب الأيقونة";
	
	app_settings_panel(Component parent_window)
	{
		add(cbx_show_tab_labels);
		
		add(btn_ok);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok || source == btn_apply)
		{
			configs.put_boolean("SETTINGS_SHOW_TAB_LABEL", cbx_show_tab_labels.isSelected());
			
			
			if(source == btn_ok)
			{
				//dispose();
			}
		}
		else if(source == btn_cancel)
		{
			//dispose();
		}
	}

}
