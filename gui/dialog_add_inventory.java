package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.database;
import common.common;

public class dialog_add_inventory extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	database db;
	dialog_inventory dlg_inventory;
	
	custom_models.research_box txt_research_product;
	
	custom_models.title_label lbl_title = new custom_models.title_label("تفاصيل السلعة: ");
	JLabel lbl_product_name = new JLabel("اسم السلعة: ");
	JLabel lbl_product_barcode = new JLabel("الترقيم: ");
	JLabel lbl_units = new JLabel("الوحدات المخزنة: ");
	
	JLabel lbl_product_name_frame = new JLabel();
	JLabel lbl_product_barcode_frame = new JLabel();
	
	JTextField txt_units = new JTextField("", 5);
	JTextField txt_barcode = new JTextField("", 10);

	custom_models.button btn_save = new custom_models.button("حفظ", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	int inventory_id = -1;
	
	void show(Component parent, database db, int inventory_entry_id)
	{
		this.db = db;
		this.inventory_id = inventory_entry_id;
		
		if(parent.getClass() == dialog_inventory.class)
		{
			dlg_inventory = (dialog_inventory) parent;
		}
		
		try
		{
			if(inventory_entry_id > -1)
			{
				ResultSet inventory_entry = db.query("SELECT * FROM inventory WHERE id="+inventory_entry_id);
				
				if(inventory_entry.next())
				{
					txt_units.setText(inventory_entry.getInt("units")+"");
					
					int product_id = inventory_entry.getInt("product_id");
					
					ResultSet product = db.query("SELECT * FROM products JOIN barcodes ON products.id = barcodes.product_id WHERE id="+product_id);
					
					if(product.next())
					{
						lbl_product_name_frame.setText(product.getString("name"));
						lbl_product_barcode_frame.setText(product.getString("barcode"));
					}
				}
			}
			else
			{
				ResultSet products = db.query("SELECT * FROM products");
				
				ArrayList<common.item> product_items = new ArrayList<>();
				
				while(products.next())
				{
					product_items.add(new common.item(products.getInt("id"), products.getString("name")));
				}
				
				txt_research_product = new custom_models.research_box(product_items);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		custom_models.form form = new custom_models.form(2);
		
		form.add(lbl_title, 2);
		
		if(inventory_entry_id > -1)
		{
			form.add(lbl_product_name);
			form.add(lbl_product_name_frame);
			form.add(lbl_product_barcode);
			form.add(lbl_product_barcode_frame);
			
		}
		else
		{
			form.add(lbl_product_barcode);
			form.add(txt_barcode);
			form.add(lbl_product_name);
			form.add(txt_research_product);
		}
		
		form.add(lbl_units);
		form.add(txt_units);
		
		JPanel buttons_panel = new JPanel();
		
		buttons_panel.add(btn_save);
		buttons_panel.add(btn_cancel);

		JPanel container = new JPanel(new BorderLayout());
		container.add(form, BorderLayout.CENTER);
		container.add(buttons_panel, BorderLayout.PAGE_END);
		
		setTitle("تعديل");
		setSize(320, 200);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_save)
		{
			try
			{
				PreparedStatement pstmt = db.pstmt("UPDATE inventory SET units=?, units_on_shelves=units_on_shelves WHERE id="+inventory_id);
				
				pstmt.setInt(1, (int) common.txt2num(txt_units.getText()));
				
				if(inventory_id == -1)
				{
					int selected_product_id = txt_research_product.selected_item.value;
						
					ResultSet inventory_record = db.query("SELECT * FROM inventory WHERE product_id="+selected_product_id);
					
					if(inventory_record.next())
					{
						int inventory_id = inventory_record.getInt("id");
						
						int response = msgbox.confirm(this, "هذه السلعة موجودة في المخزن من قبل، هل تريد تحديث الكمية المخزنة لهذه السلعة؟");
						
						if(response != msgbox.YES)
						{
							return;
						}
						
						pstmt = db.pstmt("UPDATE inventory SET units=?, units_on_shelves=units_on_shelves WHERE id="+inventory_id);
						pstmt.setInt(1, (int) common.txt2num(txt_units.getText()));
					}
					else
					{
						pstmt.close();
						pstmt = db.pstmt("INSERT INTO inventory(product_id, units, units_on_shelves) VALUES(?, ?, units_on_shelves)");
						
						pstmt.setInt(1, selected_product_id);
						pstmt.setInt(2, (int) common.txt2num(txt_units.getText()));
					}
				}
				
				pstmt.executeUpdate();
				
				if(dlg_inventory != null)
				{
					dlg_inventory.update();
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			dispose();
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}

}
