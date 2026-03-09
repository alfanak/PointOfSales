package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import common.common;
import config.configs;
import database.database;
import database.user;

public class main_container extends JPanel implements ActionListener, MouseListener, common.update_listener
{
	private static final long serialVersionUID = 1L;
	
	main_window mw;
	database db = database.get_instance();
	
	JPanel menu_container = new JPanel(new BorderLayout());
	JPanel middle_panel;
	
	ImageIcon icon_home      = new ImageIcon(getClass().getResource("/icons/home32x32.png"));
	ImageIcon icon_invoice   = new ImageIcon(getClass().getResource("/icons/invoice32x32.png"));
	ImageIcon icon_products  = new ImageIcon(getClass().getResource("/icons/products32x32.png"));
	ImageIcon icon_buyings   = new ImageIcon(getClass().getResource("/icons/buyings32x32.png"));
	ImageIcon icon_clients   = new ImageIcon(getClass().getResource("/icons/clients32x32.png"));
	ImageIcon icon_suppliers = new ImageIcon(getClass().getResource("/icons/providers32x32.png"));
	
	ImageIcon icon_inventory = new ImageIcon(getClass().getResource("/icons/inventory32x32.png"));
	ImageIcon icon_expenses  = new ImageIcon(getClass().getResource("/icons/expenses32x32.png"));
	ImageIcon icon_accounts  = new ImageIcon(getClass().getResource("/icons/accounts32x32.png"));
	ImageIcon icon_list      = new ImageIcon(getClass().getResource("/icons/list32x32.png"));
	ImageIcon icon_stats      = new ImageIcon(getClass().getResource("/icons/stats32x32.png"));
	ImageIcon icon_settings  = new ImageIcon(getClass().getResource("/icons/settings32x32.png"));
	
	main_menu main_menu = new main_menu();
	
	JScrollPane menu_scroll = new JScrollPane(main_menu);
	
	main_menu.item m_sellings        = new main_menu.item(icon_invoice, "المبيعات", false, true, this);
	main_menu.item m_products        = new main_menu.item(icon_products, "السلع", false, true, this);
	main_menu.item m_buyings         = new main_menu.item(icon_buyings, "المشتريات", false, true, this);
	main_menu.item m_clients         = new main_menu.item(icon_clients, "الزبائن", false, true, this);
	main_menu.item m_suppliers       = new main_menu.item(icon_suppliers, "الموردون", false, true, this);
	main_menu.item m_inventory       = new main_menu.item(icon_inventory, "المخزن", false, true, this);
	main_menu.item m_expenses        = new main_menu.item(icon_expenses, "المصاريف", false, true, this);
	main_menu.item m_accounts        = new main_menu.item(icon_accounts, "حسابات المستخدمين", false, true, this);
	main_menu.item m_shopping_lists  = new main_menu.item(icon_list, "قائمة المشتريات", false, true, this);
	main_menu.item m_stats           = new main_menu.item(icon_stats, "إحصائيات", false, true, this);
	main_menu.item m_settings        = new main_menu.item(icon_settings, "الإعدادات", false, true, this);
	
	
	main_menu.item si_new_invoice       = new main_menu.item(null, "فاتورة بيع...", true, false, this);
	main_menu.item si_invoices          = new main_menu.item(null, "قواتير البيع...", true, false, this);
	main_menu.item si_product_list      = new main_menu.item(null, "قائمة السلع...", true, false, this);
	main_menu.item si_add_product       = new main_menu.item(null, "إضافة سلعة...", true, false, this);
	main_menu.item si_buying_invoice    = new main_menu.item(null, "فاتورة مشتريات...", true, false, this);
	main_menu.item si_buying_invoices   = new main_menu.item(null, "قواتير الشراء...", true, false, this);
	main_menu.item si_client_list       = new main_menu.item(null, "قائمة الزبائن...", true, false, this);
	main_menu.item si_add_client        = new main_menu.item(null, "إضافة زبون...", true, false, this);
	main_menu.item si_supplier_list     = new main_menu.item(null, "قائمة الموردين...", true, false, this);
	main_menu.item si_add_supplier      = new main_menu.item(null, "إضافة مورد...", true, false, this);
	main_menu.item si_inventory         = new main_menu.item(null, "المخزن...", true, false, this);
	main_menu.item si_expenses          = new main_menu.item(null, "المصاريف...", true, false, this);
	main_menu.item si_account_list      = new main_menu.item(null, "حسابات المستخدمين...", true, false, this);
	main_menu.item si_add_account       = new main_menu.item(null, "إضافة حساب...", true, false, this);
	main_menu.item si_user_roles        = new main_menu.item(null, "رتب المستخدمين ...", true, false, this);
	main_menu.item si_shopping_lists    = new main_menu.item(null, "قوائم المشتريات...", true, false, this);
	main_menu.item si_add_shopping_list = new main_menu.item(null, "إنشاء قائمة مشتريات...", true, false, this);
	main_menu.item si_stats             = new main_menu.item(null, "إحصائيات...", true, false, this);
	main_menu.item si_settings          = new main_menu.item(null, "إعدادات...", true, false, this);
	
	custom_models.button btn_db_settings = new custom_models.button("الإعدادات", this);
	custom_models.button btn_setup = new custom_models.button("ضبط الإعدادات", this);
	
	public void build(int db_connection_status)
	{
		setLayout(new BorderLayout());
		
		middle_panel = new JPanel();
		
		main_menu.add_item(m_sellings);
		main_menu.add_item(m_products);
		main_menu.add_item(m_buyings);
		main_menu.add_item(m_clients);
		main_menu.add_item(m_suppliers);
		main_menu.add_item(m_inventory);
		main_menu.add_item(m_expenses);
		main_menu.add_item(m_accounts);
		main_menu.add_item(m_shopping_lists);
		main_menu.add_item(m_stats);
		main_menu.add_item(m_settings);
		
		main_menu.add_submenu_item(m_sellings, si_new_invoice);
		main_menu.add_submenu_item(m_sellings, si_invoices);
		main_menu.add_submenu_item(m_products, si_product_list);
		main_menu.add_submenu_item(m_products, si_add_product);
		main_menu.add_submenu_item(m_buyings, si_buying_invoice);
		main_menu.add_submenu_item(m_buyings, si_buying_invoices);
		main_menu.add_submenu_item(m_clients, si_client_list);
		main_menu.add_submenu_item(m_clients, si_add_client);
		main_menu.add_submenu_item(m_suppliers, si_supplier_list);
		main_menu.add_submenu_item(m_suppliers, si_add_supplier);
		main_menu.add_submenu_item(m_inventory, si_inventory);
		main_menu.add_submenu_item(m_expenses, si_expenses);
		main_menu.add_submenu_item(m_accounts, si_account_list);
		main_menu.add_submenu_item(m_accounts, si_add_account);
		main_menu.add_submenu_item(m_accounts, si_user_roles);
		main_menu.add_submenu_item(m_shopping_lists, si_shopping_lists);
		main_menu.add_submenu_item(m_shopping_lists, si_add_shopping_list);
		main_menu.add_submenu_item(m_stats, si_stats);
		main_menu.add_submenu_item(m_settings, si_settings);
		
		main_menu.c.weighty = 1.0;
		main_menu.add(Box.createGlue());
		
		menu_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		add(menu_container, BorderLayout.LINE_START);
		
		add(middle_panel, BorderLayout.CENTER);
		
		update();
	}
	
	void update()
	{
		middle_panel.removeAll();
		
		boolean is_installed = configs.get_boolean("INSTALLED", false);
		
		boolean users_table_exists = db.check_table("users");
		
		if(db.status == database.STATUS_CONNECTED && users_table_exists)
		{
			if( ! user.current_user().loggedin)
			{
				login_panel login_panel = new login_panel(mw, db, this);
				
				middle_panel.add(login_panel);
			}
			else
			{
				menu_container.add(menu_scroll);
				menu_container.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			}
		}
		else
		{
			if( ! is_installed)
			{
				custom_models.form setup_panel = new custom_models.form(1);
				
				setup_panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "إعدادات قاعدة البيانات: "));
				setup_panel.setBackground(Color.LIGHT_GRAY);
				
				JLabel lbl_notice = new JLabel("عليك ضبط بعض الإعدادات قبل البدء في استعمال البرنامج.");
				
				
				setup_panel.add(new JLabel());
				setup_panel.add(lbl_notice);
				setup_panel.add(new JLabel());
				setup_panel.add(btn_setup, 1, 0, GridBagConstraints.CENTER);
				setup_panel.add(new JLabel());
				
				middle_panel.add(setup_panel);
			}
			else
			{
				ImageIcon error_icon = new ImageIcon(getClass().getResource("icons/error.png"));
							
				JPanel db_error_panel = new JPanel();
				
				String error_msg = "خطأ في الاتصال بقاعدة البيانات";
				
				if(db.error_code == 0 ||db.error_code == 1042)
				{
					error_msg += " (لا يمكن الاتصال بالخادم)";
				}
				else if(db.error_code == 1049)
				{
					error_msg += " (لا توجد قاعدة بيانات مطابقة للبيانات المدخلة)";
				}
				
				JLabel lbl_error_icon = new JLabel(error_icon);
				JLabel lbl_db_error = new JLabel(error_msg);
				
				db_error_panel.add(lbl_error_icon);
				db_error_panel.add(lbl_db_error);
				db_error_panel.add(btn_db_settings);
				
				middle_panel.add(db_error_panel);
			}
		}
		
		middle_panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		middle_panel.repaint();
		middle_panel.revalidate();
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_db_settings)
		{
			dialog_settings dlg_settings = new dialog_settings();
			dlg_settings.set_tab(dialog_settings.DB_SETTINGS);
			dlg_settings.show(mw, db, this);
		}
		else if(source == btn_setup)
		{
			dialog_setup dlg_setup = new dialog_setup();
			dlg_setup.show(mw, this);
		}
		
	}

	@Override
	public void updated()
	{
		update();
	}

	@Override
	public void mouseClicked(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		this.repaint();
		this.revalidate();
		
		if(source == si_new_invoice)
		{
			dialog_add_invoice dlg_add_invoice = new dialog_add_invoice();
			
			dlg_add_invoice.show(mw);
		}
		else if(source == si_product_list)
		{
			dialog_products dlg_products = new dialog_products();
			
			dlg_products.show(mw, db);
		}
		else if(source == si_add_product)
		{
			dialog_add_product dlg_add_product = new dialog_add_product();
			
			dlg_add_product.show(mw, db);
		}
		else if(source == si_invoices)
		{
			dialog_invoice_list dlg_invoice_list = new dialog_invoice_list();
			
			dlg_invoice_list.show(mw);
		}
		else if(source == si_buying_invoice)
		{
			dialog_add_buying_invoice b_dlg = new dialog_add_buying_invoice();
			
			b_dlg.show(mw);
		}
		else if(source == si_buying_invoices)
		{
			dialog_buying_invoices dlg_buying_invoices = new dialog_buying_invoices();
			
			dlg_buying_invoices.show(mw, db);
		}
		else if(source == si_client_list)
		{
			dialog_clients dlg_clients = new dialog_clients();
			
			dlg_clients.show(mw, db);
		}
		else if(source == si_add_client)
		{
			dialog_add_client dlg_add_client = new dialog_add_client();
			
			dlg_add_client.show(mw, db);
		}
		else if(source == si_supplier_list)
		{
			dialog_suppliers dlg_suppliers = new dialog_suppliers();
			
			dlg_suppliers.show(mw, db);
		}
		else if(source == si_add_supplier)
		{
			dialog_add_supplier dlg_add_supplier = new dialog_add_supplier();
			
			dlg_add_supplier.show(mw, db);
		}
		else if(source == si_inventory)
		{
			dialog_inventory dlg_inventory = new dialog_inventory();
			
			dlg_inventory.show(mw, db);
		}
		else if(source == si_expenses)
		{
			dialog_expenses dlg_expenses = new dialog_expenses();
			
			dlg_expenses.show(mw, db);
		}
		else if(source == si_account_list)
		{
			dialog_accounts dlg_accounts = new dialog_accounts();
			
			dlg_accounts.show(mw, db);
		}
		else if(source == si_add_account)
		{
			dialog_add_account dlg_add_account = new dialog_add_account();
			
			dlg_add_account.show(mw, -1);
		}
		else if(source == si_user_roles)
		{
			dialog_user_roles dlg_user_roles = new dialog_user_roles();
			
			dlg_user_roles.show(mw);
		}
		else if(source == si_shopping_lists)
		{
			dialog_shopping_lists dlg_shopping_lists = new dialog_shopping_lists();
			
			dlg_shopping_lists.show(mw, db);
		}
		else if(source == si_add_shopping_list)
		{
			dialog_add_shopping_list dlg_add_shopping_list = new dialog_add_shopping_list();
			
			dlg_add_shopping_list.show(mw, db);
		}
		else if(source == si_stats)
		{
			/*
			dialog_stats dlg_stats = new dialog_stats();
			
			dlg_stats.show(mw, db);
			*/
		}
		else if(source == si_settings)
		{
			dialog_settings dlg_settings = new dialog_settings();
			
			dlg_settings.show(mw, db, this);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
