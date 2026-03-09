package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import database.database;
import common.common;

public class dialog_add_invoice extends custom_models.dialog implements ChangeListener, common.text_change_listener
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_ADD    = 0;
	static final int TOOLS_REMOVE = 1;
	static final int TOOLS_DELETE = 2;
	
	database db = database.get_instance();
	
	ImageIcon icon_new = new ImageIcon(getClass().getResource("/icons/new16x16.png"));
	
	dialog_add_invoice dlg_add_invoice = this;
	
	JTabbedPane tabs = new JTabbedPane();
	
	String default_client_name;
	
	public void show(Component parent)
	{
		show(parent, -1);
	}
	
	public void show(Component parent, int invoice_id)
	{
		this.default_client_name = get_default_client_name();
		
		//
		
		tabs.add(default_client_name, new panel_invoice(this, invoice_id));
		tabs.addTab("", icon_new, null);

		JPanel main_container = new JPanel(new BorderLayout());
		main_container.add(tabs, BorderLayout.CENTER);
		
		tabs.addChangeListener(this);
		
		setTitle("المبيعات");
		setSize(1000, 700);
		setContentPane(main_container);
		set_visible(true, parent);
	}
	
	String get_default_client_name()
	{
		String no_client_name = "بدون اسم";
		
		ResultSet no_client = db.query("SELECT name FROM clients WHERE id=1");
		
		try
		{
			if(no_client.next())
			{
				no_client_name = no_client.getString("name");
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return no_client_name;
	}
	
	void open_invoice(int invoice_id)
	{
		panel_invoice pi = new panel_invoice(this, invoice_id);
		
		int index = tabs.getTabCount() > 0 ? tabs.getTabCount() - 1:0;
		
		add_tab(index, pi.invoice.client.name, pi);
	}
	
	void add_tab(int tab_index, String title, Component comp)
	{
		tabs.removeChangeListener(this);
		tabs.insertTab(title, null, comp, "", tab_index);
		tabs.setSelectedIndex(tab_index);
		tabs.addChangeListener(this);
	}
	
	void remove_current_tab()
	{
		remove_tab(tabs.getSelectedIndex());
	}
	
	void remove_tab(int tab_index)
	{
		tabs.removeChangeListener(this);
		tabs.remove(tab_index);
		
		if(tabs.getTabCount() == 1)
		{
			add_tab(0, default_client_name, new panel_invoice(this));
		}
		tabs.addChangeListener(this);
	}
	
	@Override
	public void stateChanged(ChangeEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == tabs)
		{
			int tab_index = tabs.getSelectedIndex();
			
			if(tab_index == tabs.getTabCount() - 1)
			{
				add_tab(tab_index, default_client_name, new panel_invoice(this));
			}
		}
	}

	@Override
	public void text_changed(String text)
	{
		tabs.setTitleAt(tabs.getSelectedIndex(), text);
	}
	
}
