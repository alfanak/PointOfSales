package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import database.database;
import database.user_permissions;
import database.db_user_role;

public class dialog_add_role extends custom_models.dialog implements ItemListener
{
	private static final long serialVersionUID = 1L;
	
	private static final int USERS          = 0;
	private static final int PRODUCTS       = 1;
	private static final int SALES          = 2;
	private static final int BUYS           = 3;
	private static final int INVENTORY      = 4;
	private static final int EXPENSES       = 5;
	private static final int SHOPPING_LISTS = 6;
	private static final int SETTINGS       = 7;
	
	database db = database.get_instance();
	
	int option_groups_count = 8;
	
	JLabel lbl_role_name = new JLabel("اسم الرتبة: ");
	JTextField txt_role_name = new JTextField("", 20);
	
	options_group[] option_groups = new options_group[option_groups_count];
	
	option op_all = new option("جميع الصلاحيات", user_permissions.labels.ALL);
	
	option op_users = new option("التحكم في حسابات المستخدمين", user_permissions.labels.USERS);
	option op_users_view = new option("عرض قائمة المستخدمين", user_permissions.labels.USERS_VIEW);
	option op_users_add = new option("إضافة مستخدم", user_permissions.labels.USERS_ADD);
	option op_users_edit = new option("تعديل مستخدم", user_permissions.labels.USERS_EDIT);
	option op_users_delete = new option("حذف مستخدم", user_permissions.labels.USERS_DELETE);
	option op_users_view_permissions = new option("عرض رتب المستخدم", user_permissions.labels.USERS_VIEW_ROLES);
	option op_users_edit_permissions = new option("تعديل رتب المستخدم", user_permissions.labels.USERS_EDIT_ROLES);
	
	option op_products = new option("التحكم في السلع", user_permissions.labels.PRODUCTS);
	option op_products_view = new option("عرض قائمة السلع", user_permissions.labels.PRODUCTS_VIEW);
	option op_products_add = new option("إضافة سلع", user_permissions.labels.PRODUCTS_ADD);
	option op_products_edit = new option("تعديل السلع", user_permissions.labels.PRODUCTS_EDIT);
	option op_products_delete = new option("حذف السلع", user_permissions.labels.PRODUCTS_DELETE);
	
	option op_sales = new option("التحكم في المبيعات", user_permissions.labels.SALES);
	option op_sales_view_invoice = new option("عرض فواتير البيع", user_permissions.labels.SALES_VIEW_INVOICE);
	option op_sales_add_invoice = new option("إنشاء فاتورة مبيعات", user_permissions.labels.SALES_CREATE_INVOICE);
	option op_sales_edit_invoice = new option("تعديل فواتير المبيعات", user_permissions.labels.SALES_EDIT_INVOICE);
	option op_sales_delete_invoice = new option("حذف فواتير المبيعات", user_permissions.labels.SALES_DELETE_INVOICE);
	
	option op_buys = new option("التحكم في المشتريات", user_permissions.labels.BUYS);
	option op_buys_view_invoice = new option("عرض فواتير الشراء", user_permissions.labels.BUYS_VIEW_INVOICE);
	option op_buys_add_invoice = new option("إنشاء فاتورة مشتريات", user_permissions.labels.BUYS_VIEW_INVOICE);
	option op_buys_edit_invoice = new option("تعديل فواتير المشتريات", user_permissions.labels.BUYS_VIEW_INVOICE);
	option op_buys_delete_invoice = new option("حذف فواتير المشتريات", user_permissions.labels.BUYS_VIEW_INVOICE);
	
	option op_inventory = new option("إدارة المخزن", user_permissions.labels.INVENTORY);
	option op_inventory_view = new option("عرض المخزونات", user_permissions.labels.INVENTORY_VIEW);
	option op_inventory_edit = new option("تعديل المخزونات", user_permissions.labels.INVENTORY_EDIT);
	
	option op_expenses = new option("إدارة المصاريف", user_permissions.labels.EXPENSES);
	option op_expenses_view = new option("عرض المصاريف", user_permissions.labels.EXPENSES_VIEW);
	option op_expenses_add = new option("إضافة مصاريف", user_permissions.labels.EXPENSES_ADD);
	option op_expenses_edit = new option("تعديل المصاريف", user_permissions.labels.EXPENSES_EDIT);
	option op_expenses_delete = new option("حذف المصاريف", user_permissions.labels.EXPENSES_DELETE);
	
	option op_shopping_lists = new option("إدارة قوائم المشتريات", user_permissions.labels.SHOPPING_LISTS);
	option op_shopping_lists_view = new option("عرض قوائم المشتريات", user_permissions.labels.SHOPPING_LISTS_VIEW);
	option op_shopping_lists_add = new option("إضافة قائمة مشتريات", user_permissions.labels.SHOPPING_LISTS_ADD);
	option op_shopping_lists_edit = new option("تعديل قوائم المشتريات", user_permissions.labels.SHOPPING_LISTS_EDIT);
	option op_shopping_lists_delete = new option("حذف قوائم المشتريات", user_permissions.labels.SHOPPING_LISTS_DELETE);
	
	option op_settings = new option("التحكم في الإعدادات", user_permissions.labels.SETTINGS);
	option op_settings_general = new option("تعديل الإعدادات العامة", user_permissions.labels.SETTINGS_GENERAL);
	option op_settings_database = new option("تعديل إعدادات قاعدة البيانات", user_permissions.labels.SETTINGS_DATABASE);
	
	custom_models.button btn_ok = new custom_models.button("موافق", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	ArrayList<user_permissions.labels> selected_permissions = new ArrayList<>();
	
	int role_id = -1;
	
	void show(Component parent)
	{
		show(parent, -1);
	}
	
	void show(Component parent, int role_id)
	{
		this.role_id = role_id;
		
		if(role_id > -1)
		{
			db_user_role role = new db_user_role();
			
			role.load(role_id);
			
			user_permissions perms = new user_permissions();
			
			perms.load(role.permissions);
								
			selected_permissions = perms.granted;
			
			txt_role_name.setText(role.name);
		}
		
		for(int i = 0; i < option_groups_count; i++)
		{
			option_groups[i] = new options_group();
		}
		
		options_group option_groups_all = new options_group();
		option_groups_all.add_parent(op_all);
		
		option_groups[USERS].add_parent(op_users);
		option_groups[USERS].add_option(op_users_view);
		option_groups[USERS].add_option(op_users_add);
		option_groups[USERS].add_option(op_users_edit);
		option_groups[USERS].add_option(op_users_delete);
		option_groups[USERS].add_option(op_users_view_permissions);
		option_groups[USERS].add_option(op_users_edit_permissions);
		
		option_groups[PRODUCTS].add_parent(op_products);
		option_groups[PRODUCTS].add_option(op_products_view);
		option_groups[PRODUCTS].add_option(op_products_add);
		option_groups[PRODUCTS].add_option(op_products_edit);
		option_groups[PRODUCTS].add_option(op_products_delete);
		
		option_groups[SALES].add_parent(op_sales);
		option_groups[SALES].add_option(op_sales_view_invoice);
		option_groups[SALES].add_option(op_sales_add_invoice);
		option_groups[SALES].add_option(op_sales_edit_invoice);
		option_groups[SALES].add_option(op_sales_delete_invoice);
		
		option_groups[BUYS].add_parent(op_buys);
		option_groups[BUYS].add_option(op_buys_view_invoice);
		option_groups[BUYS].add_option(op_buys_add_invoice);
		option_groups[BUYS].add_option(op_buys_edit_invoice);
		option_groups[BUYS].add_option(op_buys_delete_invoice);
		
		option_groups[INVENTORY].add_parent(op_inventory);
		option_groups[INVENTORY].add_option(op_inventory_view);
		option_groups[INVENTORY].add_option(op_inventory_edit);
		
		option_groups[EXPENSES].add_parent(op_expenses);
		option_groups[EXPENSES].add_option(op_expenses_view);
		option_groups[EXPENSES].add_option(op_expenses_add);
		option_groups[EXPENSES].add_option(op_expenses_edit);
		option_groups[EXPENSES].add_option(op_expenses_delete);
		
		option_groups[SHOPPING_LISTS].add_parent(op_shopping_lists);
		option_groups[SHOPPING_LISTS].add_option(op_shopping_lists_view);
		option_groups[SHOPPING_LISTS].add_option(op_shopping_lists_add);
		option_groups[SHOPPING_LISTS].add_option(op_shopping_lists_edit);
		option_groups[SHOPPING_LISTS].add_option(op_shopping_lists_delete);
		
		option_groups[SETTINGS].add_parent(op_settings);
		option_groups[SETTINGS].add_option(op_settings_general);
		option_groups[SETTINGS].add_option(op_settings_database);
		
		custom_models.form permissions_panel = new custom_models.form(4);
		
		permissions_panel.add(option_groups_all, 4);
		
		permissions_panel.add(option_groups);
		
		custom_models.form form_role = new custom_models.form(2);
		
		form_role.add(lbl_role_name);
		form_role.add(txt_role_name);
		
		JPanel btn_container = new JPanel();
		btn_container.add(btn_ok);
		btn_container.add(btn_cancel);
		
		JPanel container = new JPanel(new BorderLayout());
		
		JPanel x = new JPanel(new FlowLayout(FlowLayout.LEADING)); x.add(form_role);
		
		container.add(x, BorderLayout.PAGE_START);
		container.add(permissions_panel, BorderLayout.CENTER);
		container.add(btn_container, BorderLayout.PAGE_END);
		
		op_all.addItemListener(this);
		
		update_components(selected_permissions);
		
		setTitle("إضافة رتبة");
		setContentPane(container);
		setSize(700, 500);
		set_visible(true, parent);
	}
	
	void update_selected_permissions()
	{
		selected_permissions.clear();
		
		if(op_all.isSelected())
		{
			selected_permissions.add(user_permissions.labels.ALL);
			
			return;
		}
		
		else
		{
			for(options_group g:option_groups)
			{
				if(g.parent.isSelected() && g.parent.isEnabled())
				{
					selected_permissions.add(g.parent.permission_label);
				}
				
				for(option o:g.options)
				{
					if(o.isSelected() && o.isEnabled())
					{
						selected_permissions.add(o.permission_label);
					}
				}
			}
		}
	}
	
	void update_components(ArrayList<user_permissions.labels> permissions)
	{
		if(permissions == null)
		{
			return;
		}
		
		if(permissions.contains(user_permissions.labels.ALL))
		{
			op_all.setSelected(true);
		}
		
		for(options_group g:option_groups)
		{
			if(permissions.contains(g.parent.permission_label))
			{
				g.parent.setSelected(true);
				
				if(permissions.contains(user_permissions.labels.ALL))
				{
					g.parent.setEnabled(false);
				}
			}
			
			for(option o:g.options)
			{
				if(permissions.contains(o.permission_label))
				{
					o.setSelected(true);;
				}
				
				if(permissions.contains(user_permissions.labels.ALL) || permissions.contains(g.parent.permission_label))
				{
					o.setEnabled(false);
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			update_selected_permissions();
			
			try
			{
				PreparedStatement pstmt = db.pstmt("INSERT INTO user_roles(name, permissions) VALUES(?, ?)");
				
				if(role_id > -1)
				{
					pstmt = db.pstmt("UPDATE user_roles SET permissions=? WHERE id="+role_id);
					
					pstmt.setString(1, common.to_string(selected_permissions, ", "));
				}
				else
				{
					pstmt.setString(1, txt_role_name.getText());
					pstmt.setString(2, common.to_string(selected_permissions, ", "));
				}
				
				pstmt.executeUpdate();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			if(this.parent.getClass() == dialog_user_roles.class)
			{
				((dialog_user_roles) parent).update();
			}
			
			dispose();
		}
		if(source == btn_cancel)
		{
			dispose();
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent ev)
	{
		if(ev.getSource() == op_all)
		{
			for(options_group g:option_groups)
			{
				g.set_enable_state( ! op_all.isSelected(), true);
			}
		}
	}
	
	class option extends JCheckBox
	{
		private static final long serialVersionUID = 1L;
		
		user_permissions.labels permission_label;
		
		option(String caption, user_permissions.labels permission_label)
		{
			super(caption);
			this.permission_label = permission_label;
		}
	}
	
	class options_group extends custom_models.form implements ItemListener
	{
		private static final long serialVersionUID = 1L;
		
		ArrayList<option> options = new ArrayList<>();
		
		option parent;
		
		void add_parent(option parent)
		{
			add(parent);
			parent.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			this.parent = parent;
			this.parent.addItemListener(this);
		}
		
		void add_option(option comp)
		{
			add(comp);
			options.add(comp);
		}
		
		void set_enable_state(boolean state, boolean set_parent)
		{
			for(option op:options)
			{
				op.setEnabled(state);
			}
			if(set_parent)
			{
				parent.setEnabled(state);
			}
		}
		
		@Override
		public void itemStateChanged(ItemEvent ev)
		{
			if(ev.getSource() == parent)
			{
				set_enable_state( ! parent.isSelected(), false);
			}
		}
	}
	
}
