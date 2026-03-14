package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import common.common;
import database.database;
import print.printer;

public class dialog_add_shopping_list extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	private static final int TOOLS_ADD = 0;
	private static final int TOOLS_REMOVE = 1;
	private static final int TOOLS_DELETE = 2;
	
	database db;
	
	dialog_shopping_lists dlg_shopping_lists;

	ImageIcon icon_check = new ImageIcon(getClass().getResource("/icons/check16x16.png"));
	
	custom_models.list_table tbl_items_list = new custom_models.list_table();
	custom_models.list_scroll list_scroll = new custom_models.list_scroll(tbl_items_list);
	
	custom_models.title_label lbl_title = new custom_models.title_label("عنوان القائمة: ");
	JLabel lbl_total_cost = new JLabel("المجموع: ");
	JLabel lbl_designation = new JLabel("التعيين: ");
	JLabel lbl_quantity = new JLabel("الكمية: ");
	JLabel lbl_price = new JLabel("السعر: ");
	
	JLabel lbl_total_cost_frame = new JLabel(common.monetary(0));
	
	JTextField txt_title = new JTextField(20);
	JTextField txt_designation = new JTextField(20);
	JTextField txt_quantity = new JTextField(5);
	JTextField txt_price = new JTextField(5);
	
	custom_models.button btn_add = new custom_models.button(icon_check, this);
	custom_models.button btn_save = new custom_models.button("حفظ", this);
	custom_models.button btn_print = new custom_models.button("طباعة...", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	ArrayList<shopping_item> items = new ArrayList<>();
	
	int list_id = -1;
	
	int item_index = -1;
	
	void show(Component parent, database db)
	{
		show(parent, db, -1);
	}
	
	void show(Component parent, database db, int list_id)
	{
		this.db = db;

		this.list_id = list_id;
		
		if (parent.getClass() == dialog_shopping_lists.class) dlg_shopping_lists = (dialog_shopping_lists) parent;
		
		String title = "إنشاء قائمة مشتريات";
		
		//
		
		if(list_id > -1)
		{
			
			try
			{
				ResultSet shoppinglist = db.query("SELECT * FROM shopping_lists WHERE id="+list_id);
				
				if(shoppinglist.next())
				{
					title = shoppinglist.getString("title");
					
					ResultSet listitems = db.query("SELECT * FROM shopping_list_items WHERE list_id="+list_id);
					
					while(listitems.next())
					{
						items.add(new shopping_item(listitems.getInt("id"), listitems.getString("name"), listitems.getInt("quantity"), listitems.getDouble("price")));
					}
				}
				
				txt_title.setText(title);
				
				update();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		
		//
		
		custom_models.form form_title = new custom_models.form(2);
		form_title.setLayout(new FlowLayout(SwingConstants.LEADING));
		form_title.add(lbl_title);
		form_title.add(txt_title);
		
		custom_models.form form_add_item = new custom_models.form(7);
		
		form_add_item.add(lbl_designation);
		form_add_item.add(txt_designation);
		form_add_item.add(lbl_quantity);
		form_add_item.add(txt_quantity);
		form_add_item.add(lbl_price);
		form_add_item.add(txt_price);
		form_add_item.add(btn_add);
		
		form_add_item.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		form_add_item.setBackground(Color.lightGray);
		
		JPanel stats_panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		stats_panel.add(lbl_total_cost);
		stats_panel.add(lbl_total_cost_frame);
		
		JPanel bottom_panel = new JPanel(new BorderLayout());
		
		bottom_panel.add(stats_panel, BorderLayout.PAGE_START);
		bottom_panel.add(form_add_item, BorderLayout.PAGE_END);
		
		JPanel buttons_panel = new JPanel();
		
		buttons_panel.add(btn_save);
		buttons_panel.add(btn_print);
		buttons_panel.add(btn_cancel);
		
		JPanel sub_container = new JPanel(new BorderLayout());
		
		sub_container.add(form_title, BorderLayout.PAGE_START);
		sub_container.add(list_scroll, BorderLayout.CENTER);
		sub_container.add(bottom_panel, BorderLayout.PAGE_END);
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(sub_container, BorderLayout.CENTER);
		container.add(buttons_panel, BorderLayout.PAGE_END);
		
		tbl_items_list.addMouseListener(this);
		
		setTitle(title);
		setSize(600, 400);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void scroll_down()
	{
		list_scroll.scroll_down();
	}
	
	void update()
	{
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_indexes = new ArrayList<>();
		
		double total_cost = 0;
		
		for(int row = 0; row < items.size(); row++)
		{
			shopping_item item = items.get(row);
			
			JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
			
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_ADD, row, TOOLS_ADD, this));
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_REMOVE, row, TOOLS_REMOVE, this));
			tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, row, TOOLS_DELETE, this));
			
			data.add(new Object[] {item.designation, item.quantity, common.monetary(item.price), common.monetary(item.cost()), tools_panel});
			
			row_indexes.add(row);
			
			total_cost += item.cost();
		}
		
		String[] columns = new String[]{"التعيين", "الكمية", "السعر", "المبلغ", "تحكم"};
		
		tbl_items_list.set_model(data, columns);
		tbl_items_list.set_row_ids(row_indexes);
		tbl_items_list.add_leading_align_column(0);
		tbl_items_list.set_column_width(1, 80);
		tbl_items_list.set_column_width(2, 80);
		tbl_items_list.set_column_width(3, 80);
		tbl_items_list.set_column_width(4, 73);
		
		lbl_total_cost_frame.setText(common.monetary(total_cost));
	}
	
	void clear_fields()
	{
		set_fields("", 0, 0);
	}
	
	void set_fields(String designation, int quantity, double price)
	{
		txt_designation.setText(designation);
		txt_quantity.setText(quantity+"");
		txt_price.setText(common.monetary(price));
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button tools_btn = (custom_models.tools_button) source;
			
			if(tools_btn.operation == TOOLS_ADD)
			{
				items.get(tools_btn.item_index).quantity++;
				
				update();
			}
			
			if(tools_btn.operation == TOOLS_REMOVE)
			{
				if(items.get(tools_btn.item_index).quantity > 0) items.get(tools_btn.item_index).quantity--;
				
				update();
			}
			
			if(tools_btn.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "هل أنت متأكد من أنك تريد حذف العنصر؟");
				
				if(response == msgbox.YES)
				{
					items.remove(tools_btn.item_index);
					
					update();
				}
			}
		}
		
		else if(source == btn_add)
		{
			String designation = txt_designation.getText();
			int quantity = (int) common.txt2num(txt_quantity.getText());
			double price = common.txt2num(txt_price.getText());
			
			if(designation.isBlank())
			{
				msgbox.info(this, "عليك كتابة اسم العنصر أولا.");
				
				return;
			}
			
			if(item_index > -1)
			{
				shopping_item item = items.get(item_index);
				
				item.designation = designation;
				item.quantity = quantity;
				item.price = price;
				
				item_index = -1;
			}
			else
			{
				for(shopping_item item:items)
				{
					if(item.designation.equals(designation))
					{
						int response = msgbox.confirm(this, "يوجد عنصر بنفس الاسم، هل أنت متأكد من أنك تريد تكرار العنصر؟", "تأكيد تكرار العنصر");
						
						if(response != JOptionPane.OK_OPTION)
						{
							return;
						}
						break;
					}
				}
				
				items.add(new shopping_item(designation, quantity, price));
			}
			
			update();
			
			clear_fields();
			
			scroll_down();
		}
		else if(source == btn_save)
		{
			try
			{
				PreparedStatement pstmt = db.pstmt("INSERT INTO shopping_lists(title) VALUES(?)");
				
				if(list_id > -1)
				{
					pstmt = db.pstmt("UPDATE shopping_lists SET title=? WHERE id="+list_id);
				}
				
				String title = txt_title.getText();
				
				if(title.isBlank())
				{
					msgbox.info(this, "عليك كتابة عنوان القائمة أولا.");
					
					return;
				}
				
				pstmt.setString(1, title);
				
				pstmt.executeUpdate();
				
				
				if(list_id == -1)
				{
					ResultSet generated_keys = pstmt.getGeneratedKeys();
					
					generated_keys.next();
					
					list_id = generated_keys.getInt(1);
				}
				
				for(shopping_item item:items)
				{
					// إضافة عناصر القائمة
					
					if(item.id > -1)
					{
						pstmt = db.pstmt("UPDATE shopping_list_items SET list_id=?, name=?, quantity=?, price=? WHERE id="+item.id);
					}
					else
					{
						pstmt = db.pstmt("INSERT INTO shopping_list_items(list_id, name, quantity, price) VALUES(?, ?, ?, ?)");
					}
					
					pstmt.setInt(1, list_id);
					pstmt.setString(2, item.designation);
					pstmt.setInt(3, item.quantity);
					pstmt.setDouble(4, item.price);
					
					pstmt.executeUpdate();
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			if(dlg_shopping_lists != null)
			{
				dlg_shopping_lists.update();
				dlg_shopping_lists.scroll_down();
			}
			
			dispose();
		}
		else if(source == btn_print)
		{
			//printer printer = new printer();
			
			//printer.print_area print_area = new printer.print_area();
			
			printer.preview x = new printer.preview();
			
			printer.page_size ps = new printer.page_size(printer.page_size.NORM_A4);
			
			JPanel header = new JPanel();
			JLabel lbl = new JLabel("أهلا وسهلا");
			header.add(lbl);
			header.setOpaque(true);
			header.setBackground(Color.red);
			
			
			custom_models.list_table new_tbl = new custom_models.list_table(tbl_items_list.getModel());
			
			
			JPanel body = new JPanel();
			body.setBackground(Color.green);
			body.add(new JScrollPane(new_tbl));
			
			JPanel footer = new JPanel();
			footer.setBackground(Color.red);
			footer.add(new JLabel("تذييل"));
			
			
			x.show(this, new printer.page(ps, header, body, footer));
			
			
			
			//printer.prepare();
		}
		if(source == btn_cancel)
		{
			dispose();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			item_index = tbl_items_list.getSelectedRow();
			
			if(item_index > - 1)
			{
				shopping_item item = items.get(item_index);
				set_fields(item.designation, item.quantity, item.price);
			}
		}
	}
	
	class shopping_item
	{
		String designation;
		int id = -1;
		int quantity;
		double price;
		
		shopping_item(String designation, int quantity, double price)
		{
			this.designation = designation;
			this.quantity = quantity;
			this.price = price;
		}
		
		shopping_item(int id, String designation, int quantity, double price)
		{
			this.id = id;
			this.designation = designation;
			this.quantity = quantity;
			this.price = price;
		}
		
		double cost()
		{
			return quantity * price;
		}
	}
}
