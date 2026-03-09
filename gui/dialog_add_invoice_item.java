package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import common.common;
import database.Invoice;

public class dialog_add_invoice_item extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	common.update_listener update_listener;
	
	JLabel lbl_name = new JLabel("التعيين: ");
	JLabel lbl_quantity = new JLabel("الكمية: ");
	JLabel lbl_unit_price = new JLabel("سعر الوحدة: ");
	
	custom_models.textfield txt_name = new custom_models.textfield("", 20);
	custom_models.textfield txt_quantity = new custom_models.textfield("", 5);
	custom_models.textfield txt_unit_price = new custom_models.textfield("", 5);
	
	custom_models.button btn_ok = new custom_models.button("موافق", this);
	custom_models.button btn_cancel = new custom_models.button("إلغاء", this);
	
	int item_index = -1;
	Invoice invoice;
	
	void show(Component parent, Invoice invoice, int item_index)
	{
		this.parent = parent;
		
		this.invoice = invoice;
		this.item_index = item_index;
		
		if(item_index > -1)
		{
			Invoice.Item item = invoice.items.get(item_index);
			
			txt_name.setText(item.name);
			txt_quantity.setText(item.units+"");
			txt_unit_price.setText(common.monetary(item.unit_price));
		}
		else
		{
			txt_name.setText("بدون اسم");
			txt_quantity.setText("1");
			txt_unit_price.setText(common.monetary(0));
		}
		
		custom_models.form form = new custom_models.form(2);
		
		form.add(lbl_name);
		form.add(txt_name);
		form.add(lbl_quantity);
		form.add(txt_quantity);
		form.add(lbl_unit_price);
		form.add(txt_unit_price);
		
		JPanel buttons_panel = new JPanel();
		buttons_panel.add(btn_ok);
		buttons_panel.add(btn_cancel);
		
		JPanel container = new JPanel(new BorderLayout());
		
		container.add(form, BorderLayout.CENTER);
		container.add(buttons_panel, BorderLayout.PAGE_END);
		
		setTitle("إضافة مشتريات");
		setContentPane(container);
		setSize(300, 180);
		set_visible(true, parent);
	}
	
	void add_update_listener(common.update_listener update_listener)
	{
		this.update_listener = update_listener;
	}
	
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == btn_ok)
		{
			if(item_index > -1)
			{
				Invoice.Item item = invoice.items.get(item_index);
				
				item.name = txt_name.getText();
				item.units = (int) common.txt2num(txt_quantity.getText());
				item.unit_price = common.txt2decimal(txt_unit_price.getText());
			}
			else
			{
				Invoice.Item item = new Invoice.Item();
				
				item.name = txt_name.getText();
				item.units = (int) common.txt2num(txt_quantity.getText());
				item.unit_price = common.txt2decimal(txt_unit_price.getText());
				
				invoice.items.add(item);
			}
			
			update_listener.updated();
			
			dispose();
		}
		else if(source == btn_cancel)
		{
			dispose();
		}
	}
}
