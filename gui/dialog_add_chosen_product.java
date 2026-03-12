package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import database.database;

public class dialog_add_chosen_product extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	static final int IMG_MAX_WIDTH = 64;
	static final int IMG_MAX_HEIGHT = 64;
	
	database db;
	
	panel_invoice parent_invoice_panel;
	
	common.update_listener update_listener;
	
	String product_image_path;
	BufferedImage product_image;
	
	ImageIcon icon_barcode = new ImageIcon(getClass().getResource("/icons/barcode.png"));
	ImageIcon icon_noproduct = new ImageIcon(getClass().getResource("/icons/noproduct64x64.png"));
	
	custom_models.research_box res_product;
	
	custom_models.title_label lbl_title   = new custom_models.title_label("بيانات السلعة:");
	
	JLabel lbl_barcode         = new JLabel("الترقيم: ");
	JLabel lbl_name            = new JLabel("التسمية: ");
	
	JTextField txt_barcode     = new JTextField("", 11);
	
	custom_models.square_button btn_image_frame = new custom_models.square_button(1, icon_noproduct, "", this);
	
	custom_models.button btn_add = new custom_models.button("إضافة", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	int group_id;
	
	void show(Component parent, database db, int group_id, common.update_listener update_listener)
	{
		this.db = db;
		this.group_id = group_id;
		this.update_listener = update_listener;
		
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
		
		res_product = new custom_models.research_box(product_items);
		
		//
		
		custom_models.form form_research = new custom_models.form(2);
		
		form_research.setBorder(BorderFactory.createTitledBorder("البحث عن سلعة: "));
		form_research.add(res_product);
		
		JPanel barcode_panel = new JPanel();
		JLabel lbl_icon_barcode = new JLabel(icon_barcode);
		barcode_panel.add(txt_barcode);
		barcode_panel.add(lbl_icon_barcode);
		
		custom_models.form form = new custom_models.form(2);
		
		form.add(lbl_title, 1, GridBagConstraints.NONE, GridBagConstraints.PAGE_START);
		form.add(btn_image_frame, 1, GridBagConstraints.NONE, GridBagConstraints.BASELINE_TRAILING);
		
		form.add(lbl_barcode, 1);
		form.add(barcode_panel);
		form.add(lbl_name);
		form.add(res_product);
		
		JPanel buttons_panel = new JPanel();
		buttons_panel.add(btn_add);
		buttons_panel.add(btn_cancel);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(form, BorderLayout.CENTER);
		container.add(buttons_panel, BorderLayout.PAGE_END);
		
		setTitle("إضافة سلعة مختارة");
		setSize(400, 250);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_add)
		{
			if(res_product.selected_item == null)
			{
				msgbox.info(this, "عليك اختيار سلعة أولا.");
				
				return;
			}
			
			try
			{
				int product_id = res_product.selected_item.value;
				
				if(product_id <= 0)
				{
					msgbox.info(this, "عليك تحديد السلعة أولا.");
					return;
				}
				
				PreparedStatement pstmt = db.pstmt("INSERT INTO chosen_products(group_id, product_id) VALUES(?, ?)");
				pstmt.setInt(1, group_id);
				pstmt.setInt(2, product_id);
				pstmt.executeUpdate();
				
				// تحميل الصورة في حال اختيار واحدة
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				if(product_image != null)
				{
					ImageIO.write(product_image, "JPG", baos);
					
					pstmt = db.pstmt("INSERT INTO product_images(product_id, data) VALUES(?, ?)");
					pstmt.setInt(1, product_id);
					pstmt.setBytes(2, baos.toByteArray());
					pstmt.executeUpdate();
				}
				
				update_listener.updated();
			}
			catch (IOException | SQLException ex)
			{
				ex.printStackTrace();
			}
			
			dispose();
		}
		if(source == btn_image_frame)
		{
			custom_models.image_browser file_chooser = new custom_models.image_browser("البحث عن صورة");
			
			int option = file_chooser.showOpenDialog(this);
			
			if(option == JFileChooser.APPROVE_OPTION)
			{
				File img_file = file_chooser.getSelectedFile();
				
				try
				{
					BufferedImage bi = ImageIO.read(img_file);
					
					BufferedImage img_scaled = common.scale_image(bi, IMG_MAX_WIDTH, IMG_MAX_HEIGHT);
					
					btn_image_frame.setIcon(new ImageIcon(img_scaled));
					
					product_image = img_scaled;
					
					product_image_path = img_file.getAbsolutePath();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}
	
}
