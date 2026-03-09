package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import barcode.barcode_scanner;
import database.database;

public class dialog_products extends custom_models.dialog implements barcode_scanner.scanner_listener, ItemListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_EDIT = 0;
	static final int TOOLS_LIST = 1;
	static final int TOOLS_DELETE = 2;
	
	barcode_scanner scanner = new barcode_scanner(barcode_scanner.SCAN_KEYBOARD, this);
	
	dialog_products dlg_products = this;
	
	database db;
	
	dialog_products dlg_ap = this;
	
	int rows_per_page = 10;
	
	custom_models.list_table tbl_products = new custom_models.list_table(this);
	
	custom_models.list_scroll scroll_list = new custom_models.list_scroll(tbl_products);
	
	JLabel lbl_research = new JLabel("بحث: ");
	JTextField txt_research = new JTextField("", 12);
	JComboBox<common.item> lst_categories = new JComboBox<>();
	
	custom_models.button btn_add_product = new custom_models.button("إضافة سلعة..." , this);
	custom_models.button btn_add_category = new custom_models.button("إضافة صنف...", this);
	custom_models.button btn_ok = new custom_models.button("تم", this);
	
	public void show(Component parent, database db)
	{
		/*
		if( ! user.permissions.PRODUCTS)
		{
			//return;
		}
		*/
		
		this.db = db;
		
		//
		
		lst_categories.addItem(new common.item(0, "الكل"));
		
		ResultSet res_categories = db.query("SELECT * FROM product_categories");
		
		try
		{
			while(res_categories.next())
			{
				lst_categories.addItem(new common.item(res_categories.getInt("id"), res_categories.getString("name")));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		//
		
		update();
		
		custom_models.form form_tools = new custom_models.form(2);
		
		form_tools.add(btn_add_product);
		form_tools.add(btn_add_category);
		
		custom_models.form form_research = new custom_models.form(3);
		
		
		form_research.add(lbl_research);
		form_research.add(txt_research);
		form_research.add(lst_categories);
		
		JPanel top_container = new JPanel(new BorderLayout());
		
		top_container.add(form_tools, BorderLayout.LINE_START);
		top_container.add(new JPanel(), BorderLayout.CENTER);
		top_container.add(form_research, BorderLayout.LINE_END);
		
		JPanel buttons_container = new JPanel();
		
		buttons_container.add(btn_ok);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(top_container, BorderLayout.NORTH);
		container.add(scroll_list, BorderLayout.CENTER);
		container.add(buttons_container, BorderLayout.SOUTH);
		
		txt_research.addKeyListener(this);
		lst_categories.addItemListener(this);
		
		setTitle("قائمة السلع");
		setSize(700, 400);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void update()
	{
		update(((common.item)lst_categories.getSelectedItem()).value, txt_research.getText());
	}
	
	public void update(int category_id, String research_string)
	{
		/*
		if( ! user.permissions.PRODUCTS)
		{
			return;
		}
		*/
		
		StringBuilder conditions = new StringBuilder();
		
		if(category_id > 0 || ! research_string.isBlank())
		{
			conditions.append(" WHERE ");
			
			if(category_id > 0)
			{
				conditions.append("products.category_id="+category_id);
			}
			
			if( ! research_string.isBlank())
			{
				if(category_id > 0)
				{
					conditions.append(" AND ");
				}
				
				conditions.append("products.name REGEXP '^");
				
				String[] words = research_string.split(" ");
				
				for(String word:words)
				{
					conditions.append("(?=.*"+word+")");
				}
				
				conditions.append("'");
			}
		}
		
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			ResultSet products = db.query("SELECT * FROM products LEFT JOIN inventory ON products.id = inventory.product_id LEFT JOIN product_categories ON products.category_id = product_categories.id "+conditions.toString()+" ORDER BY products.category_id");
			
			while(products.next())
			{
				int product_id = products.getInt("id");
				
				JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, product_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_LIST, product_id, TOOLS_LIST, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, product_id, TOOLS_DELETE, this));
				
				data.add(new Object[] {products.getString("products.name"), products.getString("product_categories.name"), products.getString("inventory.units"), products.getString("products.selling_price"), products.getString("products.units_in_pack"), tools_panel});
				
				row_ids.add(product_id);
			}
			
		} catch (SQLException ex)
		{
			ex.printStackTrace();
			
		}
		
		String[] columns = new String[]{"التعيين", "الصنف", "المخزن", "سعر الوحدة", "وحدات في الحزمة", "أدوات"};
		
		tbl_products.set_model(data, columns);
		tbl_products.set_row_ids(row_ids);
		tbl_products.add_leading_align_column(0);
		tbl_products.set_column_width(1, 90);
		tbl_products.set_column_width(2, 90);
		tbl_products.set_column_width(3, 90);
		tbl_products.set_column_width(4, 100);
		tbl_products.set_column_width(5, 73);
	}
	
	void scroll_down()
	{
		scroll_list.scroll_down();
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		/*
		if( ! user.permissions.PRODUCTS)
		{
			return;
		}
		*/
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn = (custom_models.tools_button) source;
			
			if(btn.operation == TOOLS_EDIT)
			{
				dialog_add_product dlg_add_product = new dialog_add_product();
				dlg_add_product.show(this, db, btn.item_index);
			}
			else if(btn.operation == TOOLS_LIST)
			{
				dialog_buying_invoices dlg_buying_invoices = new dialog_buying_invoices();
				dlg_buying_invoices.show(this, db, btn.item_index);
			}
			else if(btn.operation == TOOLS_DELETE)
			{
				int res = msgbox.confirm(dlg_products, ".هل أنت متأكد من حذف هذا العنصر؟ \n حذف هذا العنصر سيؤدي إلى حذفه من المخزن أيضا");
				
				if(res == JOptionPane.YES_OPTION)
				{
					db.query_nr("UPDATE invoice_items SET product_id=NULL WHERE product_id="+btn.item_index);
					db.query_nr("UPDATE buying_invoice_items SET product_id=NULL WHERE product_id="+btn.item_index);
					
					db.query_nr("DELETE FROM inventory WHERE product_id="+btn.item_index);
					db.query_nr("DELETE FROM product_images WHERE product_id="+btn.item_index);
					db.query_nr("DELETE FROM barcodes WHERE product_id="+btn.item_index);
					
					db.query_nr("DELETE FROM products WHERE id="+btn.item_index);
					
					update();
				}
			}
		}
		else if(source == btn_add_product)
		{
			dialog_add_product dlg_add_product = new dialog_add_product();
			
			dlg_add_product.dlg_products = this;
			
			dlg_add_product.show(dlg_products, db);
		}
		else if(source == btn_add_category)
		{
			dialog_add_category dlg_add_category = new dialog_add_category();
			
			dlg_add_category.show(dlg_products, db);
		}
		else if(source == btn_ok)
		{
			dispose();
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			dialog_add_product dlg_add_product = new dialog_add_product();
			dlg_add_product.show(this, db, tbl_products.get_row_id(tbl_products.getSelectedRow()));
		}
	}

	@Override
	public void scanned(String barcode)
	{
		if(isFocused())
		{
			dialog_add_product dlg_add_product = new dialog_add_product();
			
			dlg_add_product.show(this, db, barcode);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ev)
	{
		update();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent ev)
	{
		update();
	}
	
}
