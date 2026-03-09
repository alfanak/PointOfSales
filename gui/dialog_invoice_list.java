package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JPanel;

import common.common;
import common.date;
import database.database;
import database.Invoice;

public class dialog_invoice_list extends custom_models.dialog implements MouseListener, common.update_listener
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_EDIT = 0;
	static final int TOOLS_DELETE = 1;
	
	database db = database.get_instance();
	
	custom_models.list_table tbl_invoices = new custom_models.list_table();
	
	custom_models.period_panel period_panel = new custom_models.period_panel(date.period.NONE, this);
	
	int client_id = -1;
	
	void show(Component parent)
	{
		show(parent, -1);
	}
	
	void show(Component parent, int client_id)
	{
		this.client_id = client_id;
		
		JPanel top_panel = new JPanel(new BorderLayout());
		
		update(client_id);
		
		top_panel.add(period_panel, BorderLayout.LINE_START);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(top_panel, BorderLayout.PAGE_START);
		container.add(tbl_invoices, BorderLayout.CENTER);
		
		tbl_invoices.addMouseListener(this);
		
		setTitle("قائمة الفواتير");
		setSize(800, 600);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void update(int client_id)
	{
		update(period_panel.period.start_date, period_panel.period.end_date, client_id);
	}
	
	void update(LocalDate start_date, LocalDate end_date, int client_id)
	{
		ArrayList<Object[]> data_rows = new ArrayList<>();
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			PreparedStatement pstmt = db.pstmt("SELECT * FROM invoices JOIN clients ON client_id = clients.id WHERE date >= ? AND date <= ?");
			
			if(client_id > 0)
			{
				pstmt = db.pstmt("SELECT * FROM invoices JOIN clients ON client_id = clients.id WHERE date >= ? AND date <= ? AND client_id=?");
				pstmt.setInt(3, client_id);
			}
			
			pstmt.setDate(1, java.sql.Date.valueOf(start_date));
			pstmt.setDate(2, java.sql.Date.valueOf(end_date));
			
			ResultSet res_invoices = pstmt.executeQuery();
			
			while(res_invoices.next())
			{
				int invoice_id = res_invoices.getInt("invoices.id");
				String str_title = res_invoices.getString("clients.name");
				String str_date = res_invoices.getTimestamp("date").toLocalDateTime()+" "+res_invoices.getTimestamp("date").toLocalDateTime().toLocalDate();
				String str_net_amount = common.monetary(res_invoices.getDouble("net_amount"));
				
				JPanel tools_panel = new JPanel();
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, invoice_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, invoice_id, TOOLS_DELETE, this));
				
				data_rows.add(new Object[]{str_title, str_date, str_net_amount, tools_panel});
				
				row_ids.add(invoice_id);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"الزبون", "التاريخ", "المبلغ", "أدوات"};
		
		tbl_invoices.set_model(data_rows, columns);
		tbl_invoices.set_row_ids(row_ids);
		tbl_invoices.add_leading_align_column(0);
		tbl_invoices.set_column_width(1, 130);
		tbl_invoices.set_column_width(2, 90);
		tbl_invoices.set_column_width(3, 50);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn = (custom_models.tools_button) source;
			
			if(btn.operation == TOOLS_EDIT)
			{
				if(parent.getClass() == dialog_add_invoice.class)
				{
					dialog_add_invoice dlg_add_invoice = (dialog_add_invoice) parent;
					
					dlg_add_invoice.open_invoice(btn.item_index);
				}
			}
			else if(btn.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "هل أنت متأكد من أنك تريد حذف الفاتورة؟");
				
				if(response == msgbox.OK)
				{
					Invoice invoice = new Invoice();
					
					invoice.load(btn.item_index);
					
					invoice.delete();
					
					update(client_id);
				}
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == tbl_invoices && ev.getClickCount() == 2)
		{
			int invoice_id = tbl_invoices.get_row_id(tbl_invoices.getSelectedRow());
			
			if(parent.getClass() == dialog_add_invoice.class)
			{
				((dialog_add_invoice) parent).open_invoice(invoice_id);
			}
			else
			{
				(new dialog_add_invoice()).show(this, invoice_id);
			}
			
		}
	}

	@Override
	public void updated()
	{
		date.period period = period_panel.get_date();
		
		update(period.start_date, period.end_date, client_id);
	}
	
}
