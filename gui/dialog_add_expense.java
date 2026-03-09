package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import common.common;
import common.date;
import database.database;

public class dialog_add_expense  extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	database db;
	
	dialog_add_expense dlg_add_expense = this;
	
	dialog_expenses dlg_expenses;
	
	custom_models.title_label lbl_title   = new custom_models.title_label("تفاصيل المصروف:");
	JLabel lbl_purpose = new JLabel("السبب أو الوجهة: ");
	JLabel lbl_amount  = new JLabel("المبلغ: ");
	JLabel lbl_date    = new JLabel("التاريخ: ");
	
	JTextField txt_purpose = new JTextField("", 20);
	JTextField txt_amount  = new JTextField(common.monetary(0), 20);
	JTextField txt_date    = new JTextField("", 20);
	
	JButton btn_ok = new JButton("تم");
	JButton btn_cancel = new JButton("إلغاء");
	
	int expense_id = -1;
	
	public void show(Component parent, database db, int expense_id)
	{
		this.db = db;
		
		this.expense_id = expense_id;
		
		String title = "إضافة مصروف";
		
		if(expense_id > 0)
		{
			title = "تعديل المصروف";
			
			ResultSet expense = db.query("SELECT * FROM expenses WHERE id="+expense_id);
			
			try
			{
				if(expense.next())
				{
					txt_purpose.setText(expense.getString("purpose"));
					txt_amount.setText(expense.getString("amount"));
					txt_date.setText(expense.getString("date"));
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		
		txt_date.setText(date.current_date());
		
		JPanel container = new JPanel();
		
		custom_models.form form = new custom_models.form(2);
		
		form.add(lbl_title, 2);
		form.add(lbl_purpose);
		form.add(txt_purpose);
		form.add(lbl_amount);
		form.add(txt_amount);
		form.add(lbl_date);
		form.add(txt_date);
		
		JPanel buttons_container = new JPanel();
		buttons_container.add(btn_ok);
		buttons_container.add(btn_cancel);
		
		container.add(form, BorderLayout.PAGE_START);
		container.add(buttons_container, BorderLayout.PAGE_END);
		
		btn_ok.addActionListener(this);
		btn_cancel.addActionListener(this);
		
		setTitle(title);
		setContentPane(container);
		setSize(350, 200);
		set_visible(true, parent);
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			PreparedStatement pstmt = db.pstmt("INSERT INTO expenses(purpose, amount, date) VALUES(?, ?, ?)");
			
			if(expense_id > 0)
			{
				pstmt = db.pstmt("UPDATE expenses SET purpose=?, amount=?, date=? WHERE id="+expense_id);
			}
			
			try
			{
				pstmt.setString(1, txt_purpose.getText());
				pstmt.setDouble(2, common.txt2decimal(txt_amount.getText()));
				pstmt.setDate(3, java.sql.Date.valueOf(date.parse_date_only(txt_date.getText())));
				
				pstmt.executeUpdate();
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
			
			if(dlg_expenses != null)
			{
				dlg_expenses.update();
			}
			
			dispose();
		}
		if(source == btn_cancel)
		{
			dispose();
		}
	}
	
}
