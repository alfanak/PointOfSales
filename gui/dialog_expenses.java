package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import common.common;
import common.date;
import database.database;

public class dialog_expenses extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_EDIT = 0;
	static final int TOOLS_DELETE = 1;
	
	database db;
	
	dialog_expenses dlg_expenses = this;
	
	JLabel lbl_total_expenses = new JLabel("المبلغ الإجمالي: ");
	JLabel lbl_period         = new JLabel("الفترة: ");
	JLabel lbl_from           = new JLabel("من: ");
	JLabel lbl_to             = new JLabel("إلى: ");
	
	JLabel lbl_total_expenses_amount = new JLabel("0");
	
	JTextField txt_from = new JTextField(LocalDate.now().withDayOfYear(1).atStartOfDay().toLocalDate().toString(), 10);
	JTextField txt_to   = new JTextField(LocalDate.now().toString(), 10);
	
	JButton btn_refresh = new JButton("تحديث");
	
	JButton btn_add = new JButton("إضافة...");
	
	custom_models.list_table lst_expenses = new custom_models.list_table(this);
	
	void show(Component parent, database db)
	{
		this.db = db;
		/*
		if( ! user.permissions.EXSPENSES)
		{
			return;
		}
		*/
		//
		
		JPanel tools_panel = new JPanel();
		
		tools_panel.add(btn_add);
		
		JPanel period_panel = new JPanel();
		
		period_panel.add(lbl_period);
		period_panel.add(lbl_from);
		period_panel.add(txt_from);
		period_panel.add(lbl_to);
		period_panel.add(txt_to);
		period_panel.add(btn_refresh);
		
		JPanel top_container = new JPanel(new BorderLayout());
		top_container.add(tools_panel, BorderLayout.LINE_START);
		top_container.add(period_panel, BorderLayout.LINE_END);
		
		//
		
		JPanel list_footer = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		list_footer.add(lbl_total_expenses);
		list_footer.add(lbl_total_expenses_amount);
		
		JPanel list_container = new JPanel(new BorderLayout());
		
		list_container.add(new JScrollPane(lst_expenses), BorderLayout.CENTER);
		list_container.add(list_footer, BorderLayout.PAGE_END);
		
		//
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(top_container, BorderLayout.PAGE_START);
		container.add(list_container, BorderLayout.CENTER);
		
		btn_add.addActionListener(this);
		btn_refresh.addActionListener(this);
		
		update();
		
		setTitle("المصاريف");
		setContentPane(container);
		setSize(600, 400);
		set_visible(true, parent);
	}
	
	void update()
	{
		update(txt_from.getText(), txt_to.getText());
	}
	
	void update(String start_date, String end_date)
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM expenses WHERE date BETWEEN ? AND ?");
		
		double total_expenses_amount = 0;
		
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		try
		{
			pstmt.setDate(1, java.sql.Date.valueOf(date.parse_date_only(start_date)));
			pstmt.setDate(2, java.sql.Date.valueOf(date.parse_date_only(end_date)));
			
			ResultSet expenses = pstmt.executeQuery();
			
			while(expenses.next())
			{
				int expense_id = expenses.getInt("id");
				double amount = expenses.getLong("amount");
				
				JPanel tools_panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 2));
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, expense_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, expense_id, TOOLS_DELETE, this));
				
				data.add(new Object[]
						{
							expenses.getString("purpose"),
							common.monetary(amount),
							expenses.getDate("date"),
							tools_panel
						});
				
				total_expenses_amount += amount;
				
				row_ids.add(expense_id);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"السبب/الوجهة", "المبلغ", "التاريخ", "أدوات"};
		
		lst_expenses.set_model(data, columns);
		lst_expenses.set_row_ids(row_ids);
		lst_expenses.add_leading_align_column(0);
		lst_expenses.set_column_width(1, 80);
		lst_expenses.set_column_width(2, 90);
		lst_expenses.set_column_width(3, 50);
		
		lbl_total_expenses_amount.setText(common.monetary(total_expenses_amount));
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
				dialog_add_expense dlg_add_expense = new dialog_add_expense();
				
				dlg_add_expense.dlg_expenses = dlg_expenses;
				
				dlg_add_expense.show(dlg_expenses, db, btn.item_index);
			}
			else if(btn.operation == TOOLS_DELETE)
			{
				int response = msgbox.confirm(this, "تأكيد الحذف");
				
				if(response == msgbox.YES)
				{
					db.query_nr("DELETE FROM expenses WHERE id="+btn.item_index);
				}
				
				update();
			}
		}
		else if(source == btn_add)
		{
			dialog_add_expense dlg_add_expense = new dialog_add_expense();
			
			dlg_add_expense.dlg_expenses = dlg_expenses;
			
			dlg_add_expense.show(dlg_expenses, db, -1);
		}
		else if(source == btn_refresh)
		{
			update();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		if(ev.getClickCount() == 2)
		{
			int expense_id = lst_expenses.get_row_id(lst_expenses.getSelectedRow());
			
			dialog_add_expense dlg_add_expense = new dialog_add_expense();
			
			dlg_add_expense.dlg_expenses = dlg_expenses;
			
			dlg_add_expense.show(dlg_expenses, db, expense_id);
		}
	}

}
