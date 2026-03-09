package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import database.database;
import barcode.barcode_scanner;

public class dialog_add_buying_item extends custom_models.dialog implements KeyListener, barcode_scanner.scanner_listener
{
	private static final long serialVersionUID = 1L; 
	
	database db = database.get_instance();
	
	dialog_add_buying_invoice dlg_buyings;
	
	barcode_scanner scanner = new barcode_scanner(barcode_scanner.SCAN_KEYBOARD, this);
	
	ImageIcon icon_barcode = new ImageIcon(getClass().getResource("/icons/barcode.png"));
	
	custom_models.research_box resbox_products;
	
	custom_models.title_label lbl_title   = new custom_models.title_label("بيانات العنصر:");
	
	JLabel lbl_barcode         = new JLabel("الترقيم: ");
	JLabel lbl_name            = new JLabel("التعين: ");
	JLabel lbl_quantity        = new JLabel("الكمية: ");
	JLabel lbl_unit_price      = new JLabel("سعر الوحدة: ");
	JLabel lbl_pack_price      = new JLabel("سعر الحزمة: ");
	
	JCheckBox chx_pack         = new JCheckBox("حزمة");
	
	JTextField txt_barcode         = new JTextField("", 11);
	JTextField txt_quantity        = new JTextField("", 10);
	JTextField txt_unit_price      = new JTextField("", 10);
	JTextField txt_pack_price      = new JTextField("", 10);
	
	custom_models.button btn_ok = new custom_models.button("تم", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	Object[][] providers;
	Object[][] containers;
	
	ArrayList<common.invoice_item> items;
	
	String current_barcode = "NONE";
	int current_product_id = -1;
	
	private int item_index = -1;
	
	public void show(Component parent, database db, ArrayList<common.invoice_item> items_list)
	{
		show(parent, items_list, -1, "");
	}
	
	public void show(Component parent, database db, ArrayList<common.invoice_item> items_list, String barcode)
	{
		show(parent, items_list, -1, barcode);
	}
	
	public void show(Component parent, database db, ArrayList<common.invoice_item> items_list, int item_index)
	{
		show(parent, items_list, item_index, "");
	}
	
	public void show(Component parent, ArrayList<common.invoice_item> items_list, int item_index, String barcode)
	{
		/*
		if( ! user.permissions.BUYS)
		{
			return;
		}
		*/
		
		this.items = items_list;
		
		this.item_index = item_index;
		
		if (parent.getClass() == dialog_add_buying_invoice.class) dlg_buyings = (dialog_add_buying_invoice) parent;
		
		//
		
		ArrayList<common.item> product_items = new ArrayList<>();
		
		try
		{
			ResultSet products = db.query("SELECT * FROM products");
			
			while(products.next())
			{
				product_items.add(new common.item(products.getInt("id"), products.getString("name")));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		resbox_products = new custom_models.research_box(product_items, this);

		if(item_index > -1)
		{
			common.invoice_item item = items.get(item_index);
			
			txt_barcode.setText(item.barcode);
			resbox_products.setText(item.name);
			txt_quantity.setText(item.quantity+"");
			txt_unit_price.setText(common.monetary(item.unit_price));
			txt_pack_price.setText(common.monetary(item.pack_price));
			
			current_barcode = item.barcode;
			current_product_id = item.product_id;
		}
		
		if(item_index > -1)
		{
			update_fields_by_index(item_index);
		}
		else if( ! barcode.isBlank())
		{
			update_fields(barcode);
		}
		
		//
		
		custom_models.form form = new custom_models.form(2);
		
		JPanel barcode_panel = new JPanel();
		JLabel lbl_icon_barcode = new JLabel(icon_barcode);
		barcode_panel.add(txt_barcode);
		barcode_panel.add(lbl_icon_barcode);
		
		form.add(lbl_title, 2);
		form.add(lbl_barcode);
		form.add(barcode_panel);
		form.add(lbl_name);
		form.add(resbox_products);
		form.add(lbl_quantity);
		form.add(txt_quantity);
		form.add(lbl_unit_price);
		form.add(txt_unit_price);
		form.add(lbl_pack_price);
		form.add(txt_pack_price);
		
		JPanel buttons_container = new JPanel();
		buttons_container.add(btn_ok);
		buttons_container.add(btn_cancel);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(form, BorderLayout.PAGE_START);
		container.add(buttons_container, BorderLayout.PAGE_END);
		
		txt_barcode.addKeyListener(this);
		resbox_products.addKeyListener(this);
		
		setTitle("إضافة مشتريات");
		setContentPane(container);
		setSize(380, 250);
		set_visible(true, parent);
	}
	
	void update_fields_by_index(int item_index)
	{
		update_fields(items.get(item_index).product_id);
	}
	
	void update_fields(int product_id)
	{
		ResultSet product = db.query("SELECT * FROM products LEFT JOIN barcodes ON products.id = product_id WHERE products.id="+product_id);
		
		update_fields(product);
	}
	 
	void update_fields(String barcode)
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM products LEFT JOIN barcodes ON products.id = product_id WHERE barcode=?");
		
		try 
		{
			txt_barcode.setText(barcode);
			
			pstmt.setString(1, barcode);
			ResultSet product = pstmt.executeQuery();
			
			update_fields(product);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	void update_fields(ResultSet product)
	{
		if(product == null)
		{
			return;
		}
		
		try
		{
			int product_id = -1;
			
			if(product.next())
			{
				product_id = product.getInt("id");
				
				if(product_id != current_product_id);
				{
					txt_barcode.setText(product.getString("barcode")+"");
					resbox_products.setText(product.getString("name"));
					
					current_product_id = product_id;
					current_barcode = product.getString("barcode");
				}
			}
		
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	void clear_fields()
	{
		txt_barcode.setText("");
		resbox_products.setText("");
		resbox_products.selected_item = null;
		txt_quantity.setText("");
		txt_unit_price.setText("");
		txt_pack_price.setText("");
		
		current_product_id = -1;
		current_barcode = "NONE";
		item_index = -1;
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			common.invoice_item item;
			
			if(item_index > -1)
			{
				item = items.get(item_index);
			}
			else
			{
				item = new common.invoice_item();
			}
			
			item.product_id = current_product_id;
			item.barcode = current_barcode;
			item.quantity = (int)common.txt2num(txt_quantity.getText());
			item.unit_price = common.txt2decimal(txt_unit_price.getText());
			item.pack_price = common.txt2decimal(txt_pack_price.getText());
			item.name = resbox_products.getText();
			
			if(item_index < 0)
			{
				items.add(item);
			}
			
			if(dlg_buyings != null)
			{
				dlg_buyings.update_items_list();
			}
			
			dispose();
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent ev)
	{
		if(ev.getSource() == txt_barcode)
		{
			update_fields(txt_barcode.getText());
		}
		else if(ev.getSource() == resbox_products)
		{
			if(resbox_products.selected_item != null)
			{
				update_fields(resbox_products.selected_item.value);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {}
	
	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void scanned(String barcode)
	{
		if(isFocused())
		{
			clear_fields();
			
			update_fields(barcode);
		}
	}
	
}
