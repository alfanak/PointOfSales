package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import barcode.barcode_scanner;
import database.database;

public class dialog_add_product extends custom_models.dialog implements barcode_scanner.scanner_listener
{
	private static final long serialVersionUID = 1L;
	
	static final int IMG_MAX_WIDTH = 64;
	static final int IMG_MAX_HEIGHT = 64;
	
	database db;
	
	barcode_scanner scanner = new barcode_scanner(barcode_scanner.SCAN_KEYBOARD, this);
	
	dialog_products dlg_products;
	
	String product_image_path;
	BufferedImage product_image = null;
	
	ImageIcon icon_noproduct = new ImageIcon(getClass().getResource("/icons/noproduct64x64.png"));
	
	custom_models.barcodes_form form_barcodes = new custom_models.barcodes_form();
	
	custom_models.title_label lbl_title   = new custom_models.title_label("بيانات السلعة:");
	JLabel lbl_image           = new JLabel("صورة: ");
	JLabel lbl_barcode         = new JLabel("الترقيم: ");
	JLabel lbl_name            = new JLabel("التسمية: ");
	JLabel lbl_ref             = new JLabel("الصنف: ");
	JLabel lbl_inventory_units = new JLabel("عدد الوحدات المخزنة: ");
	JLabel lbl_units_per_pack  = new JLabel("عدد الوحدات في الحزمة: ");
	JLabel lbl_selling_price   = new JLabel("سعر البيع: ");
	
	JComboBox<custom_models.Item> lst_categories;
	
	JTextField txt_name            = new JTextField("", 20);
	JTextField txt_inventory_units = new JTextField("", 20);
	JTextField txt_units_per_pack  = new JTextField("", 20);
	JTextField txt_selling_price   = new JTextField("", 20);
	
	custom_models.square_button btn_image_frame = new custom_models.square_button(1, icon_noproduct, "", this);
		
	JButton btn_ok = new JButton("تم");
	JButton btn_cancel = new JButton("إلغاء");
	
	static String current_image_path = "";
	
	Object[][] providers;
	Object[][] containers;
	
	int product_id = -1;
	
	public void show(Component parent, database db)
	{
		show(parent, db, -1, "");
	}
	
	public void show(Component parent, database db, int product_id)
	{
		show(parent, db, product_id, "");
	}
	
	public void show(Component parent, database db, String barcode)
	{
		show(parent, db, -1, barcode);
	}
	
	public void show(Component parent, database db, int product_id, String barcode)
	{
		/*
		if( ! user.permissions.PRODUCTS)
		{
			return;
		}
		*/
		
		this.db = db;
		
		this.product_id = product_id;
		
		
		
		if(parent.getClass() == dialog_products.class)
		{
			dlg_products = (dialog_products) parent;
		}
		
		// جلب  تصنيفات السلع من قاعدة البيانات
		
		try
		{
			ArrayList<custom_models.Item> category_name_items = new ArrayList<custom_models.Item>();
			
			ResultSet categories = db.query("SELECT * FROM product_categories");
			
			while(categories.next())
			{
				category_name_items.add(new custom_models.Item(categories.getInt("id"), categories.getString("name"), ""));
			}
			
			lst_categories = new JComboBox<custom_models.Item>(category_name_items.toArray(new custom_models.Item[category_name_items.size()]));
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		form_barcodes.add_barcode(barcode);
		
		if(product_id > 0)
		{
			update_fields(product_id);
		}
		
		else if( ! barcode.isBlank())
		{
			update_fields(barcode);
		}
		
		//
		
		custom_models.form form = new custom_models.form(2);
		
		form.add(lbl_title, 1, GridBagConstraints.NONE, GridBagConstraints.PAGE_START);
		form.add(btn_image_frame, 1, GridBagConstraints.NONE, GridBagConstraints.BASELINE_TRAILING);
		
		form.add(lbl_barcode, 1);
		form.add(form_barcodes);
		form.add(lbl_name);
		form.add(txt_name);
		form.add(lbl_ref);
		form.add(lst_categories);
		form.add(lbl_inventory_units);
		form.add(txt_inventory_units);
		form.add(lbl_units_per_pack);
		form.add(txt_units_per_pack);
		form.add(lbl_selling_price);
		form.add(txt_selling_price);
		
		JPanel buttons_container = new JPanel();
		buttons_container.add(btn_ok);
		buttons_container.add(btn_cancel);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(form, BorderLayout.PAGE_START);
		container.add(buttons_container, BorderLayout.PAGE_END);
		
		btn_ok.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setTitle("سلعة جديدة");
		setSize(380, 350);
		setContentPane(container);
		set_visible(true, parent);
		
	}
	
	void update_fields(int product_id)
	{
		if(product_id <= 0)
		{
			return;
		}
		
		ResultSet product = db.query("SELECT * FROM products WHERE id="+product_id);
		
		update_fields(product);
	}
	
	void update_fields(String barcode)
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM barcodes WHERE barcode=?");
		
		try
		{
			pstmt.setString(1, barcode);
			ResultSet res_barcode = pstmt.executeQuery();
			
			if(res_barcode.next())
			{
				ResultSet product = db.query("SELECT * FROM products JOIN barcodes ON products.id = barcodes.product_id WHERE id="+res_barcode.getInt("product_id"));
				update_fields(product);
			}
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
			if( ! product.next())
			{
				return;
			}
			
			product_id = product.getInt("id");
			
			txt_name.setText(product.getString("name"));
			txt_units_per_pack.setText(product.getString("units_in_pack"));
			txt_selling_price.setText(product.getString("selling_price"));
			
			int category_id = product.getInt("category_id");
			
			ResultSet inventory = db.query("SELECT * FROM inventory WHERE product_id="+product_id);
			
			if(inventory.next())
			{
				txt_inventory_units.setText(inventory.getInt("units")+"");
			}
			
			// صورة السلعة
			
			ResultSet img = db.query("SELECT * FROM product_images WHERE product_id="+product_id);
			
			if(img.next())
			{
				InputStream is = img.getBinaryStream("data");
				BufferedImage bi = ImageIO.read(is);
				ImageIcon icon = new ImageIcon(bi);
				
				btn_image_frame.setIcon(icon);
			}
			
			for(int i = 0; i < lst_categories.getItemCount(); i++)
			{
				if(lst_categories.getItemAt(i).value == category_id)
				{
					lst_categories.setSelectedIndex(i);
				}
			}
			
			// رموز السلعة
			
			ResultSet res_barcodes = db.query("SELECT * FROM barcodes WHERE product_id="+product_id);
			
			ArrayList<String> product_barcodes = new ArrayList<>();
			
			while(res_barcodes.next())
			{
				product_barcodes.add(res_barcodes.getString("barcode"));
			}
			
			form_barcodes.add_barcodes(product_barcodes);
		}
		catch (SQLException | IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	void clear_fields()
	{
		product_id = -1;
		
		txt_name.setText("");
		form_barcodes.clear();
		txt_units_per_pack.setText("");
		txt_selling_price.setText("");
		txt_inventory_units.setText("");
		btn_image_frame.setIcon(icon_noproduct);
		lst_categories.setSelectedIndex(0);
	}
	
	void add_barcode(String barcode)
	{
		int barcode_product_id = product_exists(barcode);
		
		if(barcode_product_id > 0)
		{
			if(barcode_product_id == product_id)
			{
				return;
			}
			
			int response = msgbox.OK;
			
			if(product_id > 0)
			{
				response = msgbox.confirm(this, "هذا الرمز مسجل لسلعة أخرى، هل ترغب في تحميل بيانات السلعة المعنية؟");
			}
			
			if(response == msgbox.OK)
			{
				update_fields(barcode);
				
				this.revalidate();
			}
		}
		else
		{
			form_barcodes.add_barcode(barcode);;
			
			this.revalidate();
		}
	}
	
	int product_exists(String barcode)
	{
		PreparedStatement pstmt = db.pstmt("SELECT id FROM products JOIN barcodes ON products.id = product_id WHERE barcode=?");
		
		try
		{
			pstmt.setString(1, barcode);
			
			ResultSet res_product = pstmt.executeQuery();
			
			if(res_product.next())
			{
				return res_product.getInt("id");
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return -1;
	}

	@Override
	public void scanned(String barcode)
	{
		if( ! (this.isDisplayable() && this.isVisible()))
		{
			return;
		}
		add_barcode(barcode);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_image_frame)
		{
			custom_models.image_browser browser = new custom_models.image_browser("تحميل صورة");
			
			File current_dir = new File(current_image_path);
			
			if(current_dir.exists())
			{
				browser.setCurrentDirectory(current_dir);
			}
			int option = browser.showOpenDialog(this);
			
			if(option == JFileChooser.APPROVE_OPTION)
			{
				File img_file = browser.getSelectedFile();
				
				try
				{
					BufferedImage bi = ImageIO.read(img_file);
					
					product_image = common.scale_image(bi, IMG_MAX_WIDTH, IMG_MAX_HEIGHT);
					
					btn_image_frame.setIcon(new ImageIcon(product_image));
					
					product_image_path = img_file.getAbsolutePath();
					
					current_image_path = browser.getSelectedFile().getParent();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		if(source == btn_ok)
		{
			String str_name = txt_name.getText().trim();
			
			if(str_name.isBlank())
			{
				msgbox.error(this, "يجب كتابة اسم المنتج");
				return;
			}
			
			boolean is_update = product_id > 0;
			
			try
			{
				boolean inventory_record_exists = db.count("inventory", "product_id="+product_id) > 0;
				
				PreparedStatement pstmt = db.pstmt("INSERT INTO products(category_id, name, units_in_pack, selling_price) VALUES(?, ?, ?, ?)");
				
				if(is_update)
				{
					pstmt = db.pstmt("UPDATE products SET category_id=? , name=?, units_in_pack=?, selling_price=? WHERE id ="+product_id);
				}
				
				pstmt.setInt(1, ((custom_models.Item) lst_categories.getSelectedItem()).value);
				pstmt.setString(2, str_name);
				pstmt.setInt(3, (int) common.txt2num(txt_units_per_pack.getText().trim()));
				pstmt.setDouble(4, common.txt2decimal(txt_selling_price.getText().trim()));
				
				pstmt.executeUpdate();
				
				// حفظ الرموز الخاصة بالسلعة
				
				int product_id = this.product_id;
				
				int img_id = -1;
				
				if(is_update && inventory_record_exists)
				{
					// تعديل المخزن
					
					pstmt = db.pstmt("UPDATE inventory SET units=? WHERE product_id="+product_id);
					
					pstmt.setInt(1, (int) common.txt2num(txt_inventory_units.getText().trim()));
					
					pstmt.executeUpdate();
					
					// تعديل صورة المنتج إن وجدت
					
					ResultSet img = db.query("SELECT id FROM product_images WHERE product_id="+product_id);
					
					if(img.next())
					{
						img_id = img.getInt("id");
					}
				}
				else
				{
					// إضافة إلى المخزن
					
					if( ! is_update)
					{
						ResultSet generated_keys = pstmt.getGeneratedKeys();
						
						generated_keys.next();
						
						product_id = generated_keys.getInt(1);
					}
					
					pstmt.close();
					
					pstmt = db.pstmt("INSERT INTO inventory(product_id, units) VALUES(?, ?)");
					
					pstmt.setInt(1, product_id);
					pstmt.setInt(2, (int) common.txt2num(txt_inventory_units.getText().trim()));
					
					pstmt.executeUpdate();
				}
				
				// إضافة رموز السلعة
				
				for(String barcode:form_barcodes.barcodes)
				{
					boolean barcode_exists = db.exist("barcodes", "barcode="+barcode);
					
					if( ! barcode_exists)
					{
						pstmt = db.pstmt("INSERT INTO barcodes(barcode, product_id) VALUES(?, ?)");
						
						pstmt.setString(1, barcode);
						pstmt.setInt(2, product_id);
						
						pstmt.executeUpdate();
					}
				}
				
				// تحميل الصورة إن وجدت
				
				if(product_image != null)
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					
					ImageIO.write(product_image, "JPG", baos);
					
					if(img_id > -1)
					{
						pstmt = db.pstmt("UPDATE product_images SET data=?, path=? WHERE id=?");
						
						pstmt.setBytes(1, baos.toByteArray());
						pstmt.setString(2, product_image_path);
						pstmt.setInt(3, img_id);
					}
					else
					{
						pstmt = db.pstmt("INSERT INTO product_images(product_id, data, path) VALUES(?, ?, ?)");
						pstmt.setInt(1, product_id);
						pstmt.setBytes(2, baos.toByteArray());
						pstmt.setString(3, product_image_path);
					}
					
					pstmt.executeUpdate();
				}
				
				if(dlg_products != null)
				{
					dlg_products.update();
					
					if( ! is_update)
					{
						dlg_products.scroll_down();
					}
				}
			}
			catch (SQLException | IOException ex)
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
