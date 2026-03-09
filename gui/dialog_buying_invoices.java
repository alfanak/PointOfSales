package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import common.common;
import database.buying_invoice;
import database.database;

public class dialog_buying_invoices extends custom_models.dialog implements common.update_listener
{
	// TODO: تمكين عمليات البحث عن فواتير الشراء باستعمال رقم الفاتورة أو المورد أو التاريخ
	
	private static final long serialVersionUID = 1L;
	
	private static final int TOOLS_VIEW = 0;
	private static final int TOOLS_DELETE = 1;
	
	database db;
	
	custom_models.title_label lbl_title = new custom_models.title_label("قائمة الفواتير التي تتضمن شراء: ");
	
	custom_models.list_table tbl_product_invoices_list = new custom_models.list_table(this);

	int product_id = -1;
	
	void show(Component parent, database db)
	{
		show(parent, db, -1);
	}
	
	void show(Component parent, database db, int product_id)
	{
		this.db = db;
		this.product_id = product_id;
		
		int rows_count = update();
		
		JPanel container = new JPanel(new BorderLayout());
		
		if(rows_count == 0)
		{
			container.add(new custom_models.msg_panel("لا توجد فواتير لعرضها.", custom_models.msg_panel.INFO));
		}
		else
		{
			container.add(new JScrollPane(tbl_product_invoices_list), BorderLayout.CENTER);
		}
		
		setTitle("قائمة فواتير الشراء الخاصة بالسلعة");
		setSize(600, 400);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	int update()
	{
		ArrayList<Object[]> data_rows = new ArrayList<>();
		ArrayList<Integer> invoice_ids = new ArrayList<>();
		ArrayList<Integer> ordered_invoice_ids = new ArrayList<>();
		
		try
		{
			
			if(product_id > -1)
			{
				ResultSet product_buying_records = db.query("SELECT * FROM buying_invoice_items WHERE product_id="+product_id+" ORDER BY date DESC");
				
				while(product_buying_records.next())
				{
					invoice_ids.add(product_buying_records.getInt("invoice_id"));
				}
			}
			else
			{
				ResultSet rs_buying_invoices = db.query("SELECT * FROM buying_invoices ORDER BY date DESC");
				
				while(rs_buying_invoices.next())
				{
					invoice_ids.add(rs_buying_invoices.getInt("id"));
				}
			}
			
			for(int invoice_id:invoice_ids)
			{
				ResultSet rs_invoice = db.query("SELECT * FROM buying_invoices LEFT JOIN suppliers ON buying_invoices.supplier_id = suppliers.id WHERE buying_invoices.id="+invoice_id);
				
				if(rs_invoice.next())
				{
					JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
					
					tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_VIEW, invoice_id, TOOLS_VIEW, this));
					tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, invoice_id, TOOLS_DELETE, this));
					
					String supplier_name = rs_invoice.getString("name") != null ? rs_invoice.getString("name") : "غير محدد";
					
					String invoice_title = rs_invoice.getDate("date")+" ("+supplier_name+")";
					
					data_rows.add(new Object[] {invoice_title, rs_invoice.getInt("number"), common.monetary(rs_invoice.getDouble("net_amount")),tools_panel});
					
					ordered_invoice_ids.add(rs_invoice.getInt("buying_invoices.id"));
				}
				
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"التعيين", "رقم الفاتورة", "المبلغ الصافي", "أدوات"};
		
		tbl_product_invoices_list.set_model(data_rows, columns);
		tbl_product_invoices_list.set_row_ids(ordered_invoice_ids);
		tbl_product_invoices_list.set_column_width(1, 70);
		tbl_product_invoices_list.set_column_width(2, 80);
		tbl_product_invoices_list.set_column_width(3, 50);
		tbl_product_invoices_list.add_leading_align_column(0);
		
		return data_rows.size();
	}

	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button tools_btn = (custom_models.tools_button) source;
			
			if(tools_btn.operation == TOOLS_VIEW)
			{
				dialog_add_buying_invoice dlg_buying_inovice = new dialog_add_buying_invoice();
				
				dlg_buying_inovice.show(this, tools_btn.item_index);
			}
			if(tools_btn.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "تأكيد حذف فاتورة المشتريات");
				
				if(response == msgbox.YES)
				{
					buying_invoice.delete(tools_btn.item_index, true);
				}
				
				update();
			}
		}
	}
	
	public void mouseClicked(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == tbl_product_invoices_list && ev.getClickCount() == 2)
		{
			dialog_add_buying_invoice dlg_buying_inovice = new dialog_add_buying_invoice();
			
			dlg_buying_inovice.show(this, tbl_product_invoices_list.get_row_id(tbl_product_invoices_list.getSelectedRow()));
		}
	}

	@Override
	public void updated()
	{
		update();
	}

}
