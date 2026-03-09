package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import database.database;

public class dialog_inventory extends custom_models.dialog
{
	private static final long serialVersionUID = 1L;
	
	static final int TOOLS_EDIT          = 0;
	static final int TOOLS_ADD           = 1;
	static final int TOOLS_REMOVE        = 2;
	static final int TOOLS_DELETE        = 3;
	static final int TOOLS_INVOICES_LIST = 4;
	
	database db;
	
	dialog_inventory dlg_inventory = this;
	
	JLabel lbl_products_count = new JLabel("عدد السلع: ");
	JLabel lbl_products_count_frame = new JLabel();
	
	custom_models.button btn_add = new custom_models.button("إضافة...", this);
	
	custom_models.list_table lst_inventory = new custom_models.list_table(this);
	
	void show(Component parent, database dbase)
	{
		db = dbase;
		
		//
		
		JPanel tools_panel = new JPanel();
		
		tools_panel.add(btn_add);
		
		JPanel top_container = new JPanel(new BorderLayout());
		top_container.add(tools_panel, BorderLayout.LINE_START);
		
		//
		
		JPanel list_footer = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		
		list_footer.add(lbl_products_count);
		list_footer.add(lbl_products_count_frame);
		
		JPanel list_container = new JPanel(new BorderLayout());
		
		list_container.add(new JScrollPane(lst_inventory), BorderLayout.CENTER);
		list_container.add(list_footer, BorderLayout.PAGE_END);
		
		//
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(top_container, BorderLayout.PAGE_START);
		container.add(list_container, BorderLayout.CENTER);
		
		update();
		
		setTitle("المخزن");
		setSize(600, 400);
		setContentPane(container);
		set_visible(true, parent);
	}
	
	void update()
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM inventory");
		
		ArrayList<Object[]> data = new ArrayList<>();
		
		ArrayList<Integer> row_ids = new ArrayList<>();
		
		int products_count = 0;
		
		try
		{	
			ResultSet inventory = pstmt.executeQuery();
			
			while(inventory.next())
			{
				int inventory_id = inventory.getInt("id");
				int product_id = inventory.getInt("product_id");
				int units = inventory.getInt("units");
				
				JPanel tools_panel = new JPanel();
				
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_EDIT, product_id, TOOLS_EDIT, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_LIST, product_id, TOOLS_INVOICES_LIST, this));
				tools_panel.add(new custom_models.tools_button(custom_models.tools_button.BTN_DELETE, product_id, TOOLS_DELETE, this));
				
				ResultSet product = db.query("SELECT * FROM products WHERE id="+product_id);
				
				if(product.next())
				{
					int units_in_pack = product.getInt("units_in_pack");
					
					int packs = 0;
					int extra_units = units;
					
					if(units_in_pack > 0)
					{
					 packs = units / units_in_pack;
					 extra_units = units % units_in_pack;
					}

					String str_packs = "<html><span style='font-size:6px'>"+extra_units+"+</span><span style='font-size:12; font-weight:bold'>"+packs+"</span></html>";
					
					data.add(new Object[] {product.getString("name"), units, str_packs, tools_panel});
				}
				
				row_ids.add(inventory_id);
				
				products_count++;
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		String[] columns = new String[] {"التعيين", "الكمية", "الحزم", "أدوات"};
		
		lst_inventory.set_model(data, columns);
		lst_inventory.set_row_ids(row_ids);
		lst_inventory.add_leading_align_column(0);
		lst_inventory.set_column_width(1, 90);
		lst_inventory.set_column_width(2, 90);
		lst_inventory.set_column_width(columns.length - 1, 73);
		
		lbl_products_count_frame.setText(products_count+"");
	}

	@Override
	public void actionPerformed(ActionEvent ev)
	{
		Object source = ev.getSource();
		
		if(source.getClass() == custom_models.tools_button.class)
		{
			custom_models.tools_button btn = (custom_models.tools_button) source;
			
			if(btn.operation == TOOLS_INVOICES_LIST)
			{
				dialog_buying_invoices dlg_product_buying_invoices = new dialog_buying_invoices();
				
				dlg_product_buying_invoices.show(this, db, btn.item_index);
			}
			
			else if(btn.operation == TOOLS_EDIT)
			{
				dialog_add_inventory dlg_edit_inventory = new dialog_add_inventory();
				
				dlg_edit_inventory.show(this, db, btn.item_index);
			}
			else if(btn.operation == TOOLS_DELETE)
			{
				db.query_nr("UPDATE inventory SET units=0, units_on_shelves=0 WHERE id="+btn.item_index);
				
				update();
			}
		}
		else if(source == btn_add)
		{
			dialog_add_inventory dlg_add_inventory = new dialog_add_inventory();
			
			dlg_add_inventory.show(this, db, -1);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		Object source = ev.getSource();
		
		if(source == lst_inventory && ev.getClickCount() == 2)
		{
			dialog_add_inventory dlg_add_inventory = new dialog_add_inventory();
			
			dlg_add_inventory.show(this, db, lst_inventory.get_row_id(lst_inventory.getSelectedRow()));
		}
	}
	
}
