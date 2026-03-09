package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import common.date;
import database.database;
import database.Invoice;
import database.Client;

public class dialog_client extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	database db = database.get_instance();
	
	JTabbedPane tabs = new JTabbedPane();
	
	custom_models.list_table tbl_payments = new custom_models.list_table(this);
	
	JLabel lbl_name = new JLabel("الاسم: ");
	JLabel lbl_address = new JLabel("العنوان: ");
	JLabel lbl_phone = new JLabel("الهاتف: ");
	JLabel lbl_email = new JLabel("البريد الإلكتروني: ");
	JLabel lbl_date = new JLabel("البداية: ");
	
	JLabel lbl_total_invoice_count = new JLabel("العدد الإجمالي للفواتير: ");
	JLabel lbl_nonpayed_invoice_count = new JLabel("فواتير غير مسددة: ");
	JLabel lbl_nonpayed_amount = new JLabel("المبلغ الإجمالي للفواتير الغير مسددة: ");
	
	JLabel frame_total_invoice_count = new JLabel();
	JLabel frame_nonpayed_invoice_count = new JLabel();
	JLabel frame_nonpayed_amount = new JLabel();
	
	void show(Component parent, int client_id)
	{
		Client client = new Client(client_id);
		
		custom_models.form form_client_info = new custom_models.form(2);
		
		form_client_info.add(lbl_name);
		form_client_info.add(new JLabel(client.name));
		form_client_info.add(lbl_address);
		form_client_info.add(new JLabel(client.address));
		form_client_info.add(lbl_phone);
		form_client_info.add(new JLabel(client.phone));
		form_client_info.add(lbl_email);
		form_client_info.add(new JLabel(client.email));
		form_client_info.add(lbl_date);
		form_client_info.add(new JLabel(date.format_date_time(client.created_date)));
		
		custom_models.form pnl_invoices_debts = new custom_models.form(2);
		
		pnl_invoices_debts.add(lbl_total_invoice_count);
		pnl_invoices_debts.add(frame_total_invoice_count);
		pnl_invoices_debts.add(lbl_nonpayed_invoice_count);
		pnl_invoices_debts.add(frame_nonpayed_invoice_count);
		pnl_invoices_debts.add(lbl_nonpayed_amount);
		pnl_invoices_debts.add(frame_nonpayed_amount);
		
		JPanel pnl_payments = new JPanel();
		
		tabs.add("بيانات شخصية", form_client_info);
		tabs.add("فواتير وديون", pnl_invoices_debts);
		tabs.add("تسديد الديون", pnl_payments);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(tabs);
		
		update(client_id);
		
		setTitle("بيانات الزبون");
		setSize(400, 300);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void update(int client_id)
	{
		try 
		{
			ResultSet res_client_invoices = db.query("SELECT * FROM invoices WHERE client_id="+client_id);
			
			int invoice_count = 0;
			int nonpayed_invoice_count = 0;
			double nonpayed_amount = 0;
			
			while(res_client_invoices.next())
			{
				invoice_count++;
				
				int pay_status = res_client_invoices.getInt("pay_status");
				
				if(pay_status != Invoice.PAYED)
				{
					nonpayed_invoice_count++;
					
					// نحتاج لمعرفة المبلغ المسدد
				}
			}
			
			frame_total_invoice_count.setText(invoice_count+"");
			frame_nonpayed_invoice_count.setText(nonpayed_invoice_count+"");
			frame_nonpayed_amount.setText(nonpayed_amount+"");
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	void update()
	{
		
	}
}
