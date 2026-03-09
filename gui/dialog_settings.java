package gui;
import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import common.common;
import database.database;

public class dialog_settings extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	static final int GENERAL_SETTINGS = 0;
	static final int DB_SETTINGS = 1;

	int current_tab = GENERAL_SETTINGS;
	
	JTabbedPane tabbed_pane = new JTabbedPane();
	
	database db;
	
	ImageIcon icon_db = new ImageIcon(getClass().getResource("/icons/db32x32.png"));
	
	void show(Component parent, database db, common.update_listener update_listener)
	{
		this.db = db;
		
		app_settings_panel app_settings_panel = new app_settings_panel(this);
		db_settings_panel db_settings_panel = new db_settings_panel(update_listener);
		security_settings_panel security_settings_panel = new security_settings_panel(db,this, update_listener);
		
		tabbed_pane.add("إعدادات البرنمج", app_settings_panel);
		tabbed_pane.add("قاعدة البيانات", db_settings_panel);
		tabbed_pane.add("أمن البيانات", security_settings_panel );
		
		tabbed_pane.setSelectedIndex(current_tab);
		
		add(tabbed_pane);
		
		setTitle("الإعدادات");
		setSize(350, 510);
		setResizable(false);
		setModal(true);
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
	void set_tab(int index)
	{
		current_tab = index;
	}
}
