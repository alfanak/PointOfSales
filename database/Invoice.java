package database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;

public class Invoice
{
	public static final int PAYED = 0;
	public static final int PARTIALLY_PAYED = 1;
	public static final int UNPAYED = 2;
	
	database db = database.get_instance();
	
	boolean update_inventory = true; // TODO: هذا الخيار يحدد من قبل المستخدم عند إدخال الفواتير
	
	public int id;
	public LocalDateTime date;
	public LocalDate due_date;
	public int pay_status;
	public double discount;
	public double total_amount;
	public double net_amount;
	public double payed_amount;
	
	public Client client;
	
	public ArrayList<Item> items = new ArrayList<>();
	public ArrayList<Item> initial_items = new ArrayList<>();
	public ArrayList<Item> deleted_items = new ArrayList<>();
	
	public Invoice()
	{
		set_initial_data();
	}

	public Invoice(int id)
	{
		set_initial_data();
		
		load(id);
	}
	
	public void load()
	{
		load(id);
	}
	
	public void set_initial_data()
	{
		client = new Client();
		
		date = LocalDateTime.now();
		due_date = LocalDate.now();
	}
	
	public void load(int invoice_id)
	{
		if(invoice_id < 1)
		{
			return;
		}
		
		id = invoice_id;
		
		ResultSet rs_invoice = db.query("SELECT * FROM invoices WHERE id="+invoice_id);
		
		int client_id = 0;
		
		try
		{
			if(rs_invoice.next())
			{
				client_id = rs_invoice.getInt("client_id");
				
				date = rs_invoice.getTimestamp("date").toLocalDateTime();
				due_date = rs_invoice.getDate("due_date").toLocalDate();
				discount = rs_invoice.getDouble("discount");
			
				ResultSet res_client = db.query("SELECT * FROM clients WHERE id="+client_id);
				
				if(res_client.next())
				{
					client.id = client_id;
					client.name = res_client.getString("name");
				}
				
				ResultSet res_items = db.query("SELECT * FROM invoice_items WHERE invoice_id="+invoice_id);
				
				while(res_items.next())
				{
					Item item = new Item(res_items.getInt("id"), res_items.getInt("product_id"), res_items.getString("name"), res_items.getInt("units"), res_items.getDouble("price"));
					
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
		int total_amount = 0;
		
		for(Item item:items)
		{
			total_amount += item.total_cost();
		}
		
		double net_amount = total_amount - discount;
		
		try
		{
			PreparedStatement pstmt;
			
			//
			
			pstmt = db.pstmt("INSERT INTO invoices(client_id, date, total_amount, discount, net_amount, pay_status) VALUES(?, ?, ?, ?, ?, ?)");
			
			if(id > 0)
			{
				pstmt = db.pstmt("UPDATE invoices SET client_id=?, date=?, total_amount=?, discount=?, net_amount=?, pay_status=? WHERE id="+id);
			}
			
			pstmt.setInt(1, client.id);
			pstmt.setTimestamp(2, Timestamp.valueOf(date));
			pstmt.setDouble(3, total_amount);
			pstmt.setDouble(4, (int) discount);
			pstmt.setDouble(5, (int) net_amount);
			pstmt.setInt(6, pay_status);
			
			pstmt.executeUpdate();
			
			if(id <= 0)
			{
				ResultSet generated_keys = pstmt.getGeneratedKeys();
				
				generated_keys.next();
				
				id = generated_keys.getInt(1);
			}
			
			// معالجة العناصر المحذوفة
			// في حال تم حذف عناصر من الفاتورة عند تعديلها، نقوم بحذفها 
			// وإذا كان خيار تحديث المخزن مفعلا نقوم بتحديث المخزن
			
			for(Item item:deleted_items)
			{
				ResultSet invoice_item = db.query("SELECT * FROM invoice_items WHERE id="+item.id);
				
				if(invoice_item.next() && true) // TODO: تحديد خيار تحديث المخزن من قبل المستخدم
				{
					pstmt = db.pstmt("UPDATE inventory SET units=units-? WHERE product_id=?");
					
					pstmt.setInt(1, invoice_item.getInt("units"));
					pstmt.setInt(2, item.product_id);
					
					pstmt.executeUpdate();
				}
				
				db.query_nr("DELETE FROM invoice_items WHERE id="+item.id);
			}
			
			deleted_items.clear(); // نفرغ هذه القائمة لكي لا يتم استعمال محتوياتها لتحديث المخزن في حال تم حفظ الفاتورة أكثر من مرة
			
			// إدخال العناصر
			
			for(Item item:items)
			{
				pstmt = db.pstmt("INSERT INTO invoice_items(invoice_id, product_id, name, units, price) VALUES(?, ?, ?, ?, ?)");
				
				if(item.id > 0)
				{
					pstmt = db.pstmt("UPDATE invoice_items SET invoice_id=?, product_id=?, name=?, units=?, price=? WHERE id="+item.id);
				}
				
				pstmt.setInt(1, id);
				
				if(item.product_id > 0)
				{
					pstmt.setInt(2, item.product_id);
				}
				else
				{
					pstmt.setNull(2, java.sql.Types.INTEGER);
				}
				
				pstmt.setString(3, item.name);
				pstmt.setInt(4, item.units);
				pstmt.setDouble(5, item.unit_price);
				
				pstmt.executeUpdate();
				
				// تعيين رقم تعريف العنصر لاستعماله في وقت لاحق
				
				if(item.id < 1) 
				{
					ResultSet generated_keys = pstmt.getGeneratedKeys();
					
					generated_keys.next();
					
					item.id = generated_keys.getInt(1);
				}
				
				// تحديث المخزن
				
				if(update_inventory)
				{
					ResultSet product_inventory = db.query("SELECT COUNT(1) FROM inventory WHERE product_id="+item.product_id);
					
					boolean product_exists = product_inventory.next() && product_inventory.getInt(1) > 0;
					
					if(product_exists)
					{
						int units_to_remove = item.units;
						
						if(id > 0)
						{
							for(Item init_item:initial_items)
							{
								if(init_item.id == item.id)
								{
									units_to_remove -= init_item.units;
								}
							}
						}
						
						pstmt = db.pstmt("UPDATE inventory SET units=units-? WHERE product_id=?");
						
						pstmt.setInt(1, units_to_remove);
						pstmt.setInt(2, item.product_id);
						
						pstmt.executeUpdate();
					}
					else if(item.product_id > 0)
					{
						pstmt = db.pstmt("INSERT INTO inventory(product_id, units) VALUES(?, ?)");
	
						pstmt.setInt(1, item.product_id);
						pstmt.setInt(2, item.units);
						
						pstmt.executeUpdate();
					}
				}
			}
			
			// في حال التسديد الجزئي للفاتورة نضيف المبلغ المسدد إلى جدول المدفوعات
			
			if(pay_status == PARTIALLY_PAYED)
			{
				// تحديث المبلغ المسدد في إذا كان المبلغ المسدد مسجلا من قبل
				
				ResultSet res_payment = db.query("SELECT * FROM client_payments WHERE client_id="+client.id+" AND invoice_id="+id);
				
				if(res_payment.next())
				{
					pstmt = db.pstmt("UPDATE client_payments SET amount=? WHERE client_id="+client.id+" AND invoice_id="+id);
					
				}
				else
				{
					pstmt = db.pstmt("INSERT INTO client_payments(amount) VALUES(?)");
				}
				
				pstmt.setDouble(1, payed_amount);
				
				pstmt.executeUpdate();
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void delete()
	{
		try
		{
			for(Item item:items)
			{
				// تحديث المخزن
				
				if(update_inventory)
				{
					ResultSet product_inventory = db.query("SELECT COUNT(1) FROM inventory WHERE product_id="+item.product_id);
					
					boolean product_exists;
					
						product_exists = product_inventory.next() && product_inventory.getInt(1) > 0;
					
					if(product_exists)
					{
						int units_to_return = item.units;
						
						if(id > 0)
						{
							for(Item init_item:initial_items)
							{
								if(init_item.id == item.id)
								{
									units_to_return -= init_item.units;
								}
							}
						}
						
						PreparedStatement pstmt = db.pstmt("UPDATE inventory SET units=units+? WHERE product_id=?");
						
						pstmt.setInt(1, units_to_return);
						pstmt.setInt(2, item.product_id);
						
						pstmt.executeUpdate();
					}
				}
			}
			
			db.query_nr("DELETE FROM invoice_items WHERE invoice_id="+id);
			db.query_nr("DELETE FROM invoice_manual_items WHERE invoice_id="+id);
			db.query_nr("DELETE FROM invoices WHERE id="+id);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		
		
	}
	
	public void add_item(Item item)
	{
		for(Item listed_item:items)
		{
			if(listed_item.product_id == item.product_id)
			{
				listed_item.units++;
				
				return;
			}
		}
		
		items.add(item);
	}
	
	public void add_item(String barcode)
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM products JOIN barcodes ON id = product_id WHERE barcode=?");
		
		try
		{
			pstmt.setString(1, barcode);
			
			ResultSet product = pstmt.executeQuery();
			
			if(product.next())
			{
				add_item(new Item(product.getInt("id"), product.getString("name"), 1, product.getInt("selling_price")));
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	public static class Item
	{
		public int id;
		public int product_id;
		public String name;
		public int units;
		public double unit_price;
		
		public double total_cost()
		{
			return units * unit_price;
		}
		
		public Item() {}
		
		public Item(int product_id, String name, int units, double unit_price)
		{
			this.product_id = product_id;
			this.units = units;
			this.name = name;
			this.unit_price = unit_price;
		}
		
		public Item(int id, int product_id, String name, int units, double unit_price)
		{
			this.id = id;
			this.product_id = product_id;
			this.name = name;
			this.units = units;
			this.unit_price = unit_price;
		}
	}
	
}
