package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import common.common;
import common.date;
import database.database;
import database.buying_invoice;
import barcode.barcode_scanner;

public class dialog_add_buying_invoice extends custom_models.dialog implements ItemListener, CaretListener, barcode_scanner.scanner_listener
{
	private static final long serialVersionUID = 1L;
	public static final int TOOLS_EDIT = 0;
	public static final int TOOLS_DELETE = 1;
	
	private database db = database.get_instance();
	
	common.update_listener list_update_listener;
	
	suppliers_list_update_listener suppliers_list_update_listener = new suppliers_list_update_listener();
	
	barcode_scanner scanner = new barcode_scanner(barcode_scanner.SCAN_KEYBOARD, this);
	
	dialog_add_buying_invoice dlg_buyings = this;
	
	JLabel lbl_supplier       = new JLabel("المورد: ");
	JLabel lbl_invoice_number = new JLabel("رقم الفاتورة: ");
	JLabel lbl_invoice_date   = new JLabel("تاريخ الفاتورة: ");
	JLabel lbl_invoice_time   = new JLabel("الوقت: ");
	JLabel lbl_pay_status     = new JLabel("حالة السداد: ");
	JLabel lbl_payed_amount   = new JLabel("المبلغ المسدد: ");
	JLabel lbl_due_date       = new JLabel("أجل السداد: ");
	JLabel lbl_discount       = new JLabel("تخفيضات: ");
	JLabel lbl_total_amount   = new JLabel("المبلغ الإجمالي: ");
	JLabel lbl_net_amount     = new JLabel("المبلغ الصافي: ");
	JLabel lbl_rest           = new JLabel("المبلغ المتبقي: ");
	
	JLabel lbl_total_amount_value = new JLabel();
	JLabel lbl_net_amount_value = new JLabel();
	JLabel lbl_rest_value = new JLabel();
	
	JComboBox<common.item> lst_suppliers = new JComboBox<>();
	JComboBox<common.item> lst_pay_status = new JComboBox<>();
	
	custom_models.textfield txt_invoice_number = new custom_models.textfield("", 4);
	custom_models.textfield txt_payed_amount   = new custom_models.textfield("", 7);
	custom_models.textfield txt_discount       = new custom_models.textfield("0", 7);
	
	JTextField txt_invoice_date   = new JTextField("", 7);
	JTextField txt_invoice_time   = new JTextField("", 6);
	JTextField txt_due_date       = new JTextField("", 7);
	
	JCheckBox cbx_update_inventory = new JCheckBox("تحديث المخزن");
	
	custom_models.list_table tbl_items = new custom_models.list_table(this);
	
	custom_models.button btn_add_item     = new custom_models.button("إضافة...", this);
	custom_models.button btn_new_product  = new custom_models.button("سلعة جديدة...", this);
	custom_models.button btn_new_category = new custom_models.button("صنف جديد...", this);
	custom_models.button btn_new_supplier = new custom_models.button("مورد جديد...", this);
	custom_models.button btn_ok           = new custom_models.button("تم", this);
	custom_models.button btn_cancel       = new custom_models.button("إلغاء", this);
	
	private double total_amount = 0;
	private double net_amount = 0;
	
	buying_invoice invoice;
	
	void show(Component parent)
	{
		show(parent, -1);
	}
	
	void show(Component parent, int invoice_id)
	{
		this.invoice = new buying_invoice(invoice_id);
		
		if(parent instanceof common.update_listener)
		{
			this.list_update_listener = (common.update_listener)parent;
		}
		
		//
		
		lst_pay_status.addItem(new common.item(buying_invoice.INVOICE_STATUS_PAYED, "سداد كلي"));
		lst_pay_status.addItem(new common.item(buying_invoice.INVOICE_STATUS_PARTIALY_PAYED, "سداد جزئي"));
		lst_pay_status.addItem(new common.item(buying_invoice.INVOICE_STATUS_NOT_PAYED, "غير مسددة"));
		
		txt_invoice_date.setText(date.current_date());
		txt_invoice_time.setText(date.current_time());
		
		//
		
		update();
		
		//
		
		JPanel top_container = new JPanel();
		top_container.add(btn_add_item);
		top_container.add(btn_new_product);
		top_container.add(btn_new_category);
		top_container.add(btn_new_supplier);
		
		JPanel invoice_container = new JPanel(new BorderLayout());
		
		custom_models.form invoice_header = new custom_models.form(20);
		invoice_header.setBackground(Color.lightGray);
		invoice_header.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		invoice_header.align_contents_right();
		
		invoice_header.add(lbl_supplier);
		invoice_header.add(lst_suppliers);
		invoice_header.add(new custom_models.separator(5, 10));
		
		invoice_header.add(lbl_invoice_number);
		invoice_header.add(txt_invoice_number);
		
		invoice_header.add(new custom_models.separator(5, 10));
		invoice_header.add(lbl_invoice_date);
		invoice_header.add(txt_invoice_date);
		invoice_header.add(txt_invoice_time);
		
		invoice_header.add(new custom_models.separator(5, 10));
		invoice_header.add(lbl_pay_status);
		invoice_header.add(lst_pay_status);
		
		invoice_header.add(new custom_models.separator(5, 10));
		invoice_header.add(lbl_payed_amount);
		invoice_header.add(txt_payed_amount);
		invoice_header.add(lbl_due_date);
		invoice_header.add(txt_due_date);
		
		custom_models.form form_stats = new custom_models.form(2);
		
		form_stats.add(lbl_discount);
		form_stats.add(txt_discount);
		form_stats.add(lbl_total_amount);
		form_stats.add(lbl_total_amount_value);
		form_stats.add(lbl_net_amount);
		form_stats.add(lbl_net_amount_value);
		form_stats.add(lbl_rest);
		form_stats.add(lbl_rest_value);
		
		JPanel invoice_footer = new JPanel(new BorderLayout());
		invoice_footer.add(new JPanel(), BorderLayout.CENTER);
		invoice_footer.add(form_stats, BorderLayout.LINE_END);
		
		invoice_container.add(invoice_header, BorderLayout.PAGE_START);
		invoice_container.add(new JScrollPane(tbl_items), BorderLayout.CENTER);
		invoice_container.add(invoice_footer, BorderLayout.PAGE_END);
		
		JPanel bottom_container = new JPanel();
		
		cbx_update_inventory.setSelected(true);
		
		bottom_container.add(cbx_update_inventory);
		bottom_container.add(btn_ok);
		bottom_container.add(btn_cancel);

		JPanel container = new JPanel(new BorderLayout());
		
		container.add(top_container, BorderLayout.PAGE_START);
		container.add(invoice_container, BorderLayout.CENTER);
		container.add(bottom_container, BorderLayout.PAGE_END);
		
		cbx_update_inventory.addActionListener(this);
		lst_pay_status.addItemListener(this);
		txt_discount.addCaretListener(this);
		txt_payed_amount.addCaretListener(this);
		
		setTitle("فاتورة مشتريات جديدة");
		setSize(1000, 600);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void update_suppliers_list(int supplier_id)
	{
		ResultSet suppliers = db.query("SELECT * FROM suppliers");
		
		lst_suppliers.removeAllItems();
		
		lst_suppliers.addItem(new common.item(0, "غير محدد"));
		
		try
		{
			while(suppliers.next())
			{
				common.item sitem = new common.item(suppliers.getInt("id"), suppliers.getString("name"));
				
				lst_suppliers.addItem(sitem);
				
				if(sitem.value == supplier_id)
				{
					lst_suppliers.setSelectedItem(sitem);
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	void update()
	{
		txt_invoice_number.setText(invoice.number+"");
		txt_invoice_date.setText(date.format_date(invoice.date));
		txt_invoice_time.setText(date.format_time(invoice.date));
		txt_due_date.setText(date.format_date(invoice.due_date));
		txt_discount.setText(common.monetary_ns(invoice.discount));
		lst_pay_status.setSelectedIndex(invoice.pay_status);
		txt_payed_amount.setText(common.monetary_ns(invoice.payed_amount));
		
		update_due_date_panel();
		
		update_suppliers_list(invoice.supplier.id);
		
		update_items_list();
	}
	
	
	void update_due_date_panel()
	{
		boolean partially_payed = ((common.item)lst_pay_status.getSelectedItem()).value == buying_invoice.INVOICE_STATUS_PARTIALY_PAYED;
		boolean not_payed = ((common.item)lst_pay_status.getSelectedItem()).value == buying_invoice.INVOICE_STATUS_NOT_PAYED;
		boolean fully_payed = ! partially_payed && ! not_payed;
		
		if( ! partially_payed)
		{
			txt_payed_amount.setText("");
		}
		if(fully_payed)
		{
			txt_due_date.setText("");
		}
		
		lbl_payed_amount.setVisible(partially_payed);
		txt_payed_amount.setEnabled(partially_payed);
		txt_payed_amount.setVisible(partially_payed);
		
		lbl_due_date.setVisible(partially_payed || not_payed);
		txt_due_date.setEnabled(partially_payed || not_payed);
		txt_due_date.setVisible(partially_payed || not_payed);
		
		lbl_rest.setVisible(partially_payed || not_payed);
		lbl_rest_value.setVisible(partially_payed || not_payed);
	}
	
	void update_items_list()
	{
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		int row = 0;
		
		total_amount = 0;
		net_amount = 0;
		
		for(common.invoice_item item:invoice.items)
		{
			String product_name = "";
			try
			{
				ResultSet product = db.query("SELECT name FROM products WHERE id="+item.product_id);
				
				if(product.next())
				{
					product_name = product.getString("name");
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			JPanel tools_panel = new JPanel();
			
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, row, TOOLS_EDIT, this));
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, row, TOOLS_DELETE, this));
			
			double item_cost = item.cost();
			
			total_amount += item_cost;

			data.add(new Object[]
					{
						product_name,
						item.quantity+"",
						common.monetary(item.unit_price),
						common.monetary(item_cost),
						tools_panel
					});
			
			row_ids.add(row);
			row++;
		}
		
		String[] columns = new String[] {"التعيين", "عدد الوحدات", "سعر الوحدة", "ثمن الشراء", "أدوات"};
		
		tbl_items.set_model(data, columns);
		tbl_items.set_row_ids(row_ids);
		tbl_items.add_leading_align_column(0);
		tbl_items.set_column_width(1, 90);
		tbl_items.set_column_width(2, 90);
		tbl_items.set_column_width(3, 90);
		tbl_items.set_column_width(4, 50);
		
		net_amount = total_amount - common.txt2decimal(txt_discount.getText());
		
		double rest = net_amount - invoice.payed_amount;
		
		lbl_total_amount_value.setText(common.monetary(total_amount));
		lbl_net_amount_value.setText(common.monetary(net_amount));
		lbl_rest_value.setText(common.monetary(rest));
	}
	
	double total_amount()
	{
		total_amount = 0;
		
		for(common.invoice_item item:invoice.items)
		{
			total_amount += item.cost();
		}
		
		return total_amount;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button tools_btn = (custom_models.tools_button) source;
			
			if(tools_btn.operation == TOOLS_EDIT)
			{
				dialog_add_buying_item dlg_add_buying_item = new dialog_add_buying_item();
				
				dlg_add_buying_item.show(dlg_buyings, db, invoice.items, tools_btn.item_index);
			}
			else if(tools_btn.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "تأكيد الحذف");
				
				if(response == msgbox.YES)
				{
					common.invoice_item item = invoice.items.get(tools_btn.item_index);
					
					if(item.id > 0)
					{
						invoice.deleted_items.add(item);
					}
					
					invoice.items.remove(tools_btn.item_index);
				}
				
				update_items_list();
			}
		}
		
		else if(source == btn_add_item)
		{
			dialog_add_buying_item dlg_add_buying_item = new dialog_add_buying_item();
			
			dlg_add_buying_item.show(dlg_buyings, db, invoice.items);
		}
		else if(source == btn_new_product)
		{
			dialog_add_product dlg_add_product = new dialog_add_product();
			
			dlg_add_product.show(dlg_buyings, db);
		}
		else if(source == btn_new_category)
		{
			dialog_add_category dlg_add_category = new dialog_add_category();
			
			dlg_add_category.show(dlg_buyings, db);
		}
		else if(source == btn_new_supplier)
		{
			dialog_add_supplier dlg_add_supplier= new dialog_add_supplier();
			
			dlg_add_supplier.show(dlg_buyings, db, suppliers_list_update_listener);
		}
		
		else if(source == btn_ok)
		{
			if(invoice.items.size() == 0 && invoice.id <= 0)
			{
				msgbox.error(parent, "قائمة المشتريات فارغة!");
				
				return;
			}
			
			invoice.supplier.id = ((common.item)lst_suppliers.getSelectedItem()).value;
			invoice.number = (int) common.txt2num(txt_invoice_number.getText());
			invoice.date = date.parse_date_time(txt_invoice_date.getText(), txt_invoice_time.getText());
			invoice.due_date = date.parse_date_only(txt_due_date.getText());
			invoice.pay_status = lst_pay_status.getSelectedIndex();
			invoice.total_amount = total_amount;
			invoice.net_amount = net_amount;
			invoice.discount = common.txt2decimal(txt_discount.getText());
			invoice.payed_amount = common.txt2decimal(txt_payed_amount.getText());
			
			invoice.save(cbx_update_inventory.isSelected());
			
			if(list_update_listener != null)
			{
				list_update_listener.updated();
			}
			
			dispose();
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			dialog_add_buying_item dlg_add_buying_item = new dialog_add_buying_item();
			
			dlg_add_buying_item.show(this, db, invoice.items, tbl_items.get_row_id(tbl_items.getSelectedRow()));
		}
	}

	@Override
	public void itemStateChanged(ItemEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == lst_pay_status)
		{
			update_due_date_panel();
		}
	}

	@Override
	public void caretUpdate(CaretEvent arg0)
	{
		net_amount = total_amount() - common.txt2decimal(txt_discount.getText());
		
		double rest = net_amount - common.txt2decimal(txt_payed_amount.getText());
		
		lbl_net_amount_value.setText(common.monetary(net_amount)); 
		lbl_rest_value.setText(common.monetary(rest));
	}

	@Override
	public void scanned(String barcode)
	{
		if(isFocused())
		{
			PreparedStatement pstmt = db.pstmt("SELECT COUNT(*) FROM products JOIN barcodes ON id=product_id WHERE barcode=?");
			
			try
			{
				pstmt.setString(1, barcode);
				
				ResultSet product_record = pstmt.executeQuery();
				
				if(product_record.next() && product_record.getInt(1) > 0)
				{
					dialog_add_buying_item dlg_add_buying_tem = new dialog_add_buying_item();
					
					dlg_add_buying_tem.show(dlg_buyings, db, invoice.items, barcode);
				}
				else
				{
					dialog_add_product dlg_add_product = new dialog_add_product();
					
					dlg_add_product.show(dlg_add_product, db, barcode);
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	class suppliers_list_update_listener implements common.update_listener
	{
		@Override
		public void updated()
		{
			update_suppliers_list(invoice.supplier.id);
		}
		
	}

}
