package gui;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.UIManager;

import common.lang;
import config.configs;
import database.database;

public class main_window extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	database db = database.get_instance();
	
	main_container mc;
	
	public main_window()
	{
		new configs();
		
		boolean is_first_start = configs.get_init_counter() == 0 ? true:false;
		
		if(is_first_start)
		{
			configs.set_defaults();
		}
		
		database.load_parameters();
		
		db.connect(false);
		
		custom_models.init();
		
		lang.set_locale(lang.ARABIC);
		
		set_env();
		
		UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 11));
		UIManager.put("Button.font", new Font("Dialog", Font.PLAIN, 11));
		UIManager.put("ComboBox.font", new Font("Dialog", Font.PLAIN, 11));
		UIManager.put("CheckBox.font", new Font("Dialog", Font.PLAIN, 11));
		UIManager.put("List.font", new Font("Dialog", Font.PLAIN, 11));
		UIManager.put("TableHeader.font", new Font("Dialog", Font.BOLD, 11));
		UIManager.put("TableHeader.background", Color.gray);
		UIManager.put("TableHeader.foreground", Color.white);
		
		mc = new main_container();
		
		mc.mw = this;
		mc.db = db;
		
		mc.build(0);
		
		add(mc);
		
		setTitle("سجل المبيعات");
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setVisible(true);
		
		
		configs.put_int("INIT_COUNTER", configs.get_init_counter()+1);
	}
	
	void set_env()
	{
		UIManager.put("FileChooser.lookInLabelText", lang.get("LOOK_IN"));
		UIManager.put("FileChooser.saveInLabelText", lang.get("SAVE_IN"));
		UIManager.put("FileChooser.openButtonText", lang.get("OPEN"));
		UIManager.put("FileChooser.saveButtonText", lang.get("SAVE"));
		UIManager.put("FileChooser.cancelButtonText", lang.get("CANCEL"));
		UIManager.put("FileChooser.fileNameLabelText", lang.get("FILE_NAME"));
		UIManager.put("FileChooser.filesOfTypeLabelText", lang.get("FILE_TYPE"));
		UIManager.put("FileChooser.openButtonToolTipText", lang.get("OPEN_SELECTED_FILE"));
		UIManager.put("FileChooser.saveButtonToolTipText", lang.get("SAVE_FILE"));
		UIManager.put("FileChooser.cancelButtonToolTipText", lang.get("CANCEL"));
		UIManager.put("FileChooser.fileNameHeaderText", lang.get("FILE_NAME"));
		UIManager.put("FileChooser.upFolderToolTipText", lang.get("UP_ONE_LEVEL"));
		UIManager.put("FileChooser.homeFolderToolTipText", lang.get("HOME_FOLDER"));
		UIManager.put("FileChooser.newFolderButtonText", lang.get("CREATE_NEW_FOLDER"));
		UIManager.put("FileChooser.newFolderToolTipText", lang.get("CREATE_NEW_FOLDER"));
		UIManager.put("FileChooser.listViewButtonToolTipText", lang.get("LIST_VIEW_NOTE"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText", lang.get("DETAILS_VIEW_NOTE"));
		UIManager.put("FileChooser.refreshActionLabelText", lang.get("REFRESH"));
		UIManager.put("FileChooser.newFolderActionLabelText", lang.get("CREATE_NEW_FOLDER"));
		UIManager.put("FileChooser.viewActionLabelText", lang.get("VIEW"));
		UIManager.put("FileChooser.renameFileButtonText", lang.get("RENAME_FILE"));
		UIManager.put("FileChooser.filterLabelText", lang.get("FILE_TYPE"));
		UIManager.put("FileChooser.fileSizeHeaderText", lang.get("SIZE"));
		UIManager.put("FileChooser.fileDateHeaderText", lang.get("DATE_MODIFIED"));
		UIManager.put("FileChooser.fileSizeGigaBytes", "{0} "+lang.get("GIGABYTES"));
		UIManager.put("FileChooser.fileSizeKiloBytes", "{0} "+lang.get("KILOBYTES"));
		UIManager.put("FileChooser.fileSizeMegaBytes", "{0} "+lang.get("MEGABYTES"));
		UIManager.put("FileChooser.viewMenuLabelText", lang.get("VIEW"));
		UIManager.put("FileChooser.listViewActionLabelText", lang.get("LIST"));
		UIManager.put("FileChooser.detailsViewActionLabelText", lang.get("DETAILS"));
	}
	
	void update()
	{
		mc.updated();
	}
	
	public static void main(String[] args)
	{
		new main_window();
	}
}
