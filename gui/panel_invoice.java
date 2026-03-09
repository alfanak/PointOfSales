package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import common.common;
import common.date;
import database.database;
import database.Invoice;
import barcode.barcode_scanner;
import print.printer;

public class panel_invoice extends JPanel implements MouseListener, ItemListener, ActionListener, common.update_listener, barcode_scanner.scanner_listener
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_ADD    = 0;
	static final int TOOLS_REMOVE = 1;
	static final int TOOLS_DELETE = 2;

	private database db = database.get_instance();
	
	ImageIcon icon_add = new ImageIcon(getClass().getResource("/icons/add16x16.png"));
	
	boolean autosave = true; // TODO: ضمن الخيار يجب أن يكون ضمن الإعدادات
	
	int tab_index = -1;
	
	private double total_amount = 0;
	private double net_amount = 0;
	
	barcode_scanner scanner = new barcode_scanner(barcode_scanner.SCAN_KEYBOARD, this);
	
	panel_chosen_products pcp;
	
	Component parent;
	
	custom_models.form chosen_products_panel = new custom_models.form(3);
	
	custom_models.list_table tbl_items_list = new custom_models.list_table(this);
	
	JLabel lbl_client           = new JLabel("الزبون");
	JLabel lbl_invoice_date     = new JLabel("تاريخ الفاتورة: ");
	JLabel lbl_invoice_due_date = new JLabel("أجل التسديد: ");
	JLabel lbl_pay_status       = new JLabel("التسديد: ");
	JLabel lbl_total_amount     = new JLabel("المبلغ الإجمالي: ");
	JLabel lbl_discount         = new JLabel("تخفيضات: ");
	JLabel lbl_net_amount       = new JLabel("المبلغ الصافي: ");
	JLabel lbl_payed_amount     = new JLabel("المبلغ المسدد: ");
	
	custom_models.lcd_screen lbl_net_amount_screen = new custom_models.lcd_screen("0.0");
	
	custom_models.research_box txt_research_product;
	custom_models.research_box txt_research_client;
	
	JComboBox<common.item> lst_pay_status;
	custom_models.form form_payed_amount = new custom_models.form(2);
	
	custom_models.textfield txt_invoice_date     = new custom_models.textfield("", 7);
	custom_models.textfield txt_invoice_time     = new custom_models.textfield("", 6);
	custom_models.textfield txt_invoice_due_date = new custom_models.textfield("", 10);
	custom_models.textfield txt_discount         = new custom_models.textfield(common.monetary(0), 8);
	custom_models.textfield txt_payed_amount     = new custom_models.textfield(common.monetary(0), 8);
	
	JLabel lbl_total_amount_frame     = new JLabel(common.monetary(0));
	JLabel lbl_net_amount_frame       = new JLabel(common.monetary(0));
	
	custom_models.button btn_add_to_list = new custom_models.button("<<", this);
	
	custom_models.button btn_save       = new custom_models.button("حفظ", this);
	custom_models.button btn_print      = new custom_models.button("طباعة", this);
	custom_models.button btn_clear      = new custom_models.button("تفريغ", this);
	custom_models.button btn_add_amount = new custom_models.button("إضافة عنصر", this);
	custom_models.button btn_delete     = new custom_models.button("حذف", this);
	
	custom_models.menu_button btn_menu = new custom_models.menu_button("", this);
	
	JPopupMenu menu = new JPopupMenu();
	
	JMenuItem mi_invoice_list = new JMenuItem("قائمة الفواتير...");
	
	public Invoice invoice;
	
	panel_invoice(Component parent)
	{
		this(parent, -1);
	}
	
	panel_invoice(Component parent, int invoice_id)
	{
		setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		this.parent = parent;
		
		invoice = new Invoice(invoice_id);
		
		// جلب البيانات من قاعدة البيانات
		
		ArrayList<common.item> client_items  = new ArrayList<>();
		ArrayList<common.item> product_items = new ArrayList<>();
		
		try
		{
			ResultSet clients = db.query("SELECT * FROM clients");
			
			while(clients.next())
			{
				client_items.add(new common.item(clients.getInt("id"), clients.getString("name")));
			}
			
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
		
		pcp = new panel_chosen_products(parent, this, db);
		
		lst_pay_status = new JComboBox<common.item>(common.item.create_items_list(new String[] {"تسديد كلي", "تسديد جزئي", "غير مسددة"}, new int[] {Invoice.PAYED, Invoice.PARTIALLY_PAYED, Invoice.UNPAYED}));
		
		txt_research_product = new custom_models.research_box(product_items);
		txt_research_product.setColumns(15);
		
		txt_research_client = new custom_models.research_box(client_items);
		txt_research_client.setColumns(10);
		
		update();
		
		// بيانات الفاتورة
		
		form_payed_amount.add(lbl_payed_amount);
		form_payed_amount.add(txt_payed_amount);
		form_payed_amount.setVisible(false);
		
		JPanel invoice_header = new JPanel();
		
		invoice_header.add(lbl_client);
		invoice_header.add(txt_research_client);
		
		invoice_header.add(new custom_models.separator(5, 10));
		invoice_header.add(lbl_invoice_date);
		invoice_header.add(txt_invoice_date);
		invoice_header.add(txt_invoice_time);
		
		invoice_header.add(new custom_models.separator(5, 10));
		invoice_header.add(lbl_pay_status);
		invoice_header.add(lst_pay_status);
		
		invoice_header.add(lst_pay_status);
		invoice_header.add(form_payed_amount);
		
		lst_pay_status.addItemListener(this);
		
		JPanel pnl_menu = new JPanel();
		
		menu.add(mi_invoice_list);
		
		pnl_menu.add(btn_menu);
		
		mi_invoice_list.addActionListener(this);
		
		JPanel top_panel = new JPanel(new BorderLayout());
		
		top_panel.add(invoice_header, BorderLayout.LINE_START);
		top_panel.add(new JPanel(), BorderLayout.CENTER);
		top_panel.add(pnl_menu, BorderLayout.LINE_END);
		
		// الشريط الجانبي
		
		JPanel sidebar_container = new JPanel(new BorderLayout());
		
		custom_models.form search_form = new custom_models.form(3);
		
		search_form.setBorder(BorderFactory.createTitledBorder("البحث عن منتج: "));
		btn_add_to_list.setMargin(new Insets(0, 3, 0, 3));
		btn_add_to_list.setToolTipText("أضف إلى القائمة");
		search_form.add(txt_research_product);
		search_form.add(btn_add_to_list);
		
		//
		
		chosen_products_panel.setBorder(BorderFactory.createTitledBorder("سلع مختارة: "));
		
		sidebar_container.add(search_form, BorderLayout.PAGE_START);
		
		sidebar_container.add(pcp, BorderLayout.CENTER);
		

		sidebar_container.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
		sidebar_container.setMinimumSize(new Dimension(200, 0));
		
		// قائمة المشتريات
		
		JPanel invoice_container = new JPanel(new BorderLayout());
		
		JScrollPane scroll_items_list = new JScrollPane(tbl_items_list);
		
		JPanel invoice_buttons_panel = new JPanel();
		invoice_buttons_panel.add(btn_save);
		invoice_buttons_panel.add(btn_print);
		invoice_buttons_panel.add(btn_clear);
		invoice_buttons_panel.add(btn_add_amount);
		invoice_buttons_panel.add(btn_delete);
		
		
		custom_models.form invoice_stats = new custom_models.form(2);
		invoice_stats.add(lbl_discount);
		invoice_stats.add(txt_discount);
		invoice_stats.add(lbl_total_amount);
		invoice_stats.add(lbl_total_amount_frame);
		invoice_stats.add(lbl_net_amount);
		invoice_stats.add(lbl_net_amount_frame);
		
		JPanel invoice_footer = new JPanel(new BorderLayout());
		invoice_footer.add(invoice_buttons_panel, BorderLayout.LINE_START);
		invoice_footer.add(lbl_net_amount_screen, BorderLayout.CENTER);
		invoice_footer.add(invoice_stats, BorderLayout.LINE_END);
		
		invoice_container.add(scroll_items_list, BorderLayout.CENTER);
		invoice_container.add(invoice_footer, BorderLayout.PAGE_END);
		
		//
		
		JSplitPane split_container = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, invoice_container, sidebar_container);
		
		split_container.setDividerLocation(0.9);
		split_container.setResizeWeight(0.9);
		
		
		setLayout(new BorderLayout());
		add(top_panel, BorderLayout.PAGE_START);
		add(split_container, BorderLayout.CENTER);
		
		if(parent != null && parent.getClass() == dialog_add_invoice.class)
		{
			txt_research_client.add_text_change_listener((dialog_add_invoice)parent);
		}
		
		applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
	}
	
	void update()
	{
		if(invoice.client.id > 0)
		{
			txt_research_client.selected_item = new common.item(invoice.client.id, invoice.client.name);
			txt_research_client.setText(invoice.client.name);
		}
		
		txt_invoice_date.setText(date.format_date(invoice.date));
		txt_invoice_time.setText(date.format_time(invoice.date));
		txt_invoice_due_date.setText(date.format_date(invoice.due_date));
		
		update_items_list();
	}
	
	void update_items_list()
	{
		ArrayList<Object[]> data = new ArrayList<>();
		
		total_amount = 0;
		net_amount = 0;
		
		int row = 0;
		
		for(Invoice.Item item:invoice.items)
		{
			total_amount += item.total_cost();
			
			JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
			
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_ADD, row, TOOLS_ADD, this));
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_REMOVE, row, TOOLS_REMOVE, this));
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, row, TOOLS_DELETE, this));
			
			data.add(new Object[]
			{
				item.name,
				item.units+"",
				common.monetary(item.unit_price),
				common.monetary(item.total_cost()),
				tools_panel
			});
			
			row++;
		}
		
		String[] columns = new String[] {"التعيين", "الكمية", "سعر الوحدة", "القيمة الإجمالية", "أدوات"};
		
		tbl_items_list.set_model(data, columns);
		tbl_items_list.add_leading_align_column(0);
		tbl_items_list.set_column_width(1, 70);
		tbl_items_list.set_column_width(2, 70);
		tbl_items_list.set_column_width(3, 90);
		tbl_items_list.set_column_width(4, 73);
		
		double discount = common.txt2decimal(txt_discount.getText());
		
		net_amount = total_amount - discount;
		
		lbl_total_amount_frame.setText(common.monetary(total_amount));
		lbl_net_amount_frame.setText(common.monetary(net_amount));
		lbl_net_amount_screen.setText(common.monetary(net_amount));
		
		if(autosave && invoice.items.size() > 0)
		{
			invoice.client.load(get_client_id());
			
			invoice.date = date.parse_date_time(txt_invoice_date.getText(), txt_invoice_time.getText());
			invoice.due_date = date.parse_date_only(txt_invoice_due_date.getText());
			
			invoice.save();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn = (custom_models.tools_button) source;
			
			switch(btn.operation)
			{
			case TOOLS_ADD:
				invoice.items.get(btn.item_index).units++; break;
				
			case TOOLS_REMOVE:
				invoice.items.get(btn.item_index).units--;break;
				
			case TOOLS_DELETE:
				
				int response = msgbox.confirm(this, "تأكيد الحذف");
				
				if(response == msgbox.YES)
				{
					Invoice.Item item = invoice.items.get(btn.item_index);
					
					if(item.id > 0)
					{
						invoice.deleted_items.add(item);
					}
					
					invoice.items.remove(btn.item_index);
				}
				break;
			}
			
			update_items_list();
		}
		
		if(source == btn_menu)
		{
			menu.show(btn_menu, 0, btn_menu.getHeight());
		}
		else if(source == mi_invoice_list)
		{
			dialog_invoice_list dlg_invoice_list = new dialog_invoice_list();
			
			dlg_invoice_list.show(parent);
		}
		
		else if(source == btn_add_to_list)
		{
			if(txt_research_product.selected_item != null)
			{
				add_product(txt_research_product.selected_item.value);
			}
		}
		
		// إضافة أو تعديل فاتورة
		
		else if(source == btn_save)
		{
			if(invoice.items.size() == 0 && invoice.id <= 0)
			{
				msgbox.error(parent, "قائمة المشتريات فارغة!");
				return;
			}
			
			invoice.client.id = get_client_id();
			invoice.date = date.parse_date_time(txt_invoice_date.getText(), txt_invoice_time.getText());
			invoice.due_date = date.parse_date_only(txt_invoice_due_date.getText());
			invoice.pay_status = lst_pay_status.getSelectedIndex();
			invoice.pay_status = lst_pay_status.getSelectedIndex();
			invoice.total_amount = total_amount;
			invoice.net_amount = net_amount;
			invoice.discount = common.txt2decimal(txt_discount.getText());
			invoice.payed_amount = common.txt2decimal(txt_payed_amount.getText());
			
			
			invoice.save();
		}
		
		else if(source == btn_print)
		{
			printer p = new printer();
			
			p.prepare();
		}
		
		else if(source == btn_add_amount)
		{
			dialog_add_invoice_item dlg_add_invoice_item = new dialog_add_invoice_item();
			
			dlg_add_invoice_item.add_update_listener(this);
			
			dlg_add_invoice_item.show(this, invoice, -1);
			
		}
		
		else if(source == btn_delete)
		{
			int response = msgbox.confirm(parent, "تأكيد حذف الفاتورة");
			
			if(response == msgbox.OK)
			{
				invoice.delete();
				
				invoice = new Invoice();
				
				update();
				
				if(parent.getClass() == dialog_add_invoice.class)
				{
					dialog_add_invoice dlg_add_invoice = (dialog_add_invoice) parent;
					
					dlg_add_invoice.remove_current_tab();
				}
			}
		}
	}
	
	// تحميل رقم تعريف الزبون في حال كان مسجلا في قاعدة البيانات
	// إضافة زبون إلى قاعدة البيانات بالاسم الموجود في حقل اسم الزبون في حال لم يكن مسجلا
	
	int get_client_id()
	{
		PreparedStatement pstmt;
		
		String str_client_name = txt_research_client.getText();
		
		try
		{
			if(txt_research_client.selected_item == null || ! str_client_name.equals(invoice.client.name))
			{
				if( ! str_client_name.isBlank())
				{
					int user_record = db.count("clients", "name=\""+str_client_name+"\"");
					
					if(user_record > 0)
					{
						pstmt = db.pstmt("SELECT id FROM clients WHERE name=?");
						
						pstmt.setString(1, str_client_name);
						
						ResultSet res_client = pstmt.executeQuery();
						
						res_client.next();
						
						return res_client.getInt("id");
					}
					else
					{
						pstmt = db.pstmt("INSERT INTO clients(name) VALUES(?)");
						pstmt.setString(1, str_client_name);
						
						pstmt.executeUpdate();
						
						ResultSet generated_keys = pstmt.getGeneratedKeys();
						
						generated_keys.next();
						
						return generated_keys.getInt(1);
					}
				}
			}
			else if(txt_research_client.selected_item != null)
			{
				return txt_research_client.selected_item.value;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return 1;
	}
	
	//
	
	void add_product(int product_id)
	{
		ResultSet product = db.query("SELECT * FROM products WHERE id="+product_id);
		
		try
		{
			if(product.next())
			{
				invoice.add_item(new Invoice.Item(product_id, product.getString("name"), 1, product.getInt("selling_price")));
				
				update_items_list();
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == tbl_items_list)
		{
			if(ev.getClickCount() == 2)
			{
				dialog_add_invoice_item dlg_add_invoice_item = new dialog_add_invoice_item();
				
				dlg_add_invoice_item.add_update_listener(this);
				
				int item_index = tbl_items_list.getSelectedRow();
				
				if(item_index > invoice.items.size())
				{
					dlg_add_invoice_item.show(parent, invoice, item_index);
				}
				else
				{
					dlg_add_invoice_item.show(parent, invoice, item_index);
				}
			}
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
	
	
	@Override
	public void scanned(String barcode)
	{
		//FIXME: الكود التالي ليس موثوقا وقد يخفق في ظروق معينة
		// شرح: الكود التالي يفترض أن الإطارات الموجودة  داخل الإطارات المبوبة هي إطارات غير مرئية ما عدا الإطار الموجود داخل الباب الحالي فهو إطار مرئي 
		//-----------------------------------------------------
		if( ! this.isVisible())
		{
			return;
		}
		//-----------------------------------------------------
		
		invoice.add_item(barcode);
		
		update_items_list();
	}

	@Override
	public void updated()
	{
		update();
	}

	@Override
	public void itemStateChanged(ItemEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == lst_pay_status)
		{
			common.item item = (common.item) lst_pay_status.getSelectedItem();
			
			form_payed_amount.setVisible(item.value == Invoice.PARTIALLY_PAYED);
			
		}
	}
	
}
