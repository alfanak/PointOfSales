package database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import common.common.invoice_item;

public class buying_invoice
{
	public static final int INVOICE_STATUS_PAYED = 0;
	public static final int INVOICE_STATUS_PARTIALY_PAYED = 1;
	public static final int INVOICE_STATUS_NOT_PAYED = 2;
	
	private static database db = database.get_instance();
	
	public int id;
	public int number;
	public int pay_status;
	public LocalDateTime date;
	public LocalDate due_date;
	public double total_amount;
	public double net_amount;
	public double payed_amount;
	public double discount;
	
	public Supplier supplier = new Supplier();
	
	public ArrayList<invoice_item> items = new ArrayList<>();
	public ArrayList<invoice_item> initial_items = new ArrayList<>();
	public ArrayList<invoice_item> deleted_items = new ArrayList<>();
	
	public buying_invoice( int invoice_id)
	{
		date = LocalDateTime.now();
		
		load(invoice_id);
	}
	
	void load(int invoice_id)
	{
		if(invoice_id < 1)
		{
			return;
		}
		
		id = invoice_id;
		
		ResultSet rs_invoice = db.query("SELECT * FROM buying_invoices WHERE id="+invoice_id);
		
		int supplier_id = 0;
		
		try
		{
			if(rs_invoice.next())
			{
				supplier_id = rs_invoice.getInt("supplier_id");
				
				java.sql.Date sql_due_date = rs_invoice.getDate("due_date");
				
				number = rs_invoice.getInt("number");
				date = rs_invoice.getTimestamp("date").toLocalDateTime();
				due_date = sql_due_date != null? sql_due_date.toLocalDate() : null;
				discount = rs_invoice.getDouble("discount");
				pay_status = rs_invoice.getInt("pay_status");
				payed_amount = rs_invoice.getDouble("payed_amount");
			
				ResultSet res_supplier = db.query("SELECT * FROM suppliers WHERE id="+supplier_id);
				
				if(res_supplier.next())
				{
					supplier.id = supplier_id;
					supplier.name = res_supplier.getString("name");
				}
				
				ResultSet res_items = db.query("SELECT * FROM buying_invoice_items LEFT JOIN products ON product_id = products.id WHERE invoice_id="+invoice_id);
				
				items.clear();
				
				while(res_items.next())
				{
					invoice_item item = new invoice_item(res_items.getInt("id"), res_items.getInt("product_id"), res_items.getString("products.name"), res_items.getInt("buying_invoice_items.quantity"), res_items.getDouble("unit_price"), res_items.getDouble("pack_price"));
					
					items.add(item);
					initial_items.add(item);
				}
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void save()
	{
		save(true);
	}
	
	public void save(boolean update_inventory)
	{
		boolean is_update = id > 0;
		
		try
		{
			PreparedStatement pstmt = db.pstmt("INSERT INTO buying_invoices(supplier_id, date, due_date, number, total_amount, discount, net_amount, payed_amount, pay_status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			if(is_update)
			{
				pstmt = db.pstmt("UPDATE buying_invoices SET supplier_id=?, date=?, due_date=?, number=?, total_amount=?, discount=?, net_amount=?, payed_amount=?,pay_status=? WHERE id="+id);
			}
			
			if(supplier.id <= 0)
			{
				pstmt.setNull(1, java.sql.Types.INTEGER);
			}
			else
			{
				pstmt.setInt(1, supplier.id);
			}
			
			pstmt.setTimestamp(2, Timestamp.valueOf(date));
			
			if(due_date == null)
			{
				pstmt.setNull(3, java.sql.Types.DATE);
			}
			else
			{
				pstmt.setDate(3, java.sql.Date.valueOf(due_date));
			}
			
			pstmt.setInt(4, number);
			pstmt.setDouble(5, total_amount);
			pstmt.setDouble(6, discount);
			pstmt.setDouble(7, net_amount);
			pstmt.setDouble(8, payed_amount);
			pstmt.setInt(9, pay_status);
			
			pstmt.executeUpdate();
			
			if( ! is_update)
			{
				ResultSet generated_keys = pstmt.getGeneratedKeys();
				
				generated_keys.next();
				
				id = generated_keys.getInt(1);
			}
			
			// معالجة العناصر المحذوفة
			// في حال تم حذف عناصر من الفاتورة عند تعديلها، نقوم بحذفها 
			// وإذا كان خيار تحديث المخزن مفعلا نقوم بتحديث المخزن
			
			for(invoice_item item : deleted_items)
			{
				if(update_inventory)
				{
					int quantity = get_item_quantity(item);
					
					db.query("UPDATE inventory SET quantity=quantity-"+quantity+" WHERE product_id="+item.product_id);
				}
				
				db.query_nr("DELETE FROM buying_invoice_items WHERE id="+item.id);
			}
			
			
			// حفظ عناصر الفاتورة
			
			for(invoice_item item:items)
			{
				// إضافة السلع المشتراة
				
				pstmt = db.pstmt("INSERT INTO buying_invoice_items(invoice_id, product_id, quantity, unit_price, pack_price) VALUES(?, ?, ?, ?, ?)");
				
				if(is_update && item.id > 0)
				{
					pstmt = db.pstmt("UPDATE buying_invoice_items SET invoice_id=?, product_id=?, quantity=?, unit_price=?, pack_price=? WHERE id="+item.id);
				}
				
				pstmt.setInt(1, id);
				pstmt.setInt(2, item.product_id);
				pstmt.setInt(3, item.quantity);
				pstmt.setDouble(4, item.unit_price);
				pstmt.setDouble(5, item.pack_price);
				
				pstmt.executeUpdate();
				
				// تحديث المخزن
				
				if(update_inventory)
				{
					int quantity = get_item_quantity(item);
					
					ResultSet product_inventory = db.query("SELECT COUNT(1) FROM inventory WHERE product_id="+item.product_id);
					
					boolean product_exists_in_inventory = product_inventory.next() && product_inventory.getInt(1) > 0;
					
					if(product_exists_in_inventory)
					{
						int units_to_add = quantity;
						
						if(is_update)
						{
							for(invoice_item init_item:initial_items)
							{
								if(init_item.id == item.id)
								{
									units_to_add = quantity - get_item_quantity(init_item);
								}
							}
						}
						
						pstmt = db.pstmt("UPDATE inventory SET units=units+? WHERE product_id=?");
						
						pstmt.setInt(1, units_to_add);
						pstmt.setInt(2, item.product_id);
						
						pstmt.executeUpdate();
					}
					else
					{
						pstmt = db.pstmt("INSERT INTO inventory(product_id, units) VALUES(?, ?)");
	
						pstmt.setInt(1, item.product_id);
						pstmt.setInt(2, quantity);
						
						pstmt.executeUpdate();
					}
				}
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	void delete()
	{
		delete(true);
	}
	
	void delete(boolean update_inventory)
	{
		delete(id, update_inventory);
	}
	
	public static void delete(int invoice_id, boolean update_inventory)
	{
		if(update_inventory)
		{
			ResultSet rs_items = db.query("SELECT * FROM buying_invoice_items LEFT JOIN products ON product_id = products.id WHERE invoice_id="+invoice_id);
			
			try
			{
				PreparedStatement pstmt = db.pstmt("UPDATE inventory SET units=units-? WHERE product_id=?");
				
				while(rs_items.next())
				{
					int product_id = rs_items.getInt("product_id");
					
					int item_quantity = get_item_quantity(rs_items);
					
					pstmt.setInt(1, item_quantity);
					pstmt.setInt(2, product_id);
					
					pstmt.addBatch();
				}
				
				pstmt.executeBatch();
				
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
		
		db.query_nr("DELETE FROM buying_invoice_items WHERE invoice_id="+invoice_id);
		db.query_nr("DELETE FROM buying_invoices WHERE id="+invoice_id);
	}
	
	int get_item_quantity(invoice_item item) throws SQLException
	{
		if(item.is_pack())
		{
			ResultSet rs_product = db.query("SELECT units_in_pack FROM products WHERE id="+item.product_id);
			
			if(rs_product.next())
			{
				 return item.quantity * rs_product.getInt("units_in_pack");
			}
		}
		return item.quantity;
	}
	
	private static int get_item_quantity(ResultSet rs_item) throws SQLException
	{
		boolean is_pack = rs_item.getInt("unit_price") <= 0 && rs_item.getInt("pack_price") > 0;
		
		if(is_pack)
		{
			return rs_item.getInt("quantity") * rs_item.getInt("units_in_pack");
		}
		
		return rs_item.getInt("quantity");
	}
	
}
