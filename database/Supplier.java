package database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Supplier
{
database db = database.get_instance();
	
	public int id = 1;
	public String company;
	public String name;
	public String address;
	public String email;
	public String phone;
	public Date created_date;
	public Date updated_date;
	
	public Supplier() {}
	
	public Supplier(int id)
	{
		load(id);
	}
	
	void load()
	{
		load(id);
	}
	
	void load(int id)
	{
		this.id = id;
		
		ResultSet res_supplier = db.query("SELECT * FROM suppliers WHERE id="+id);
		
		try
		{
			if(res_supplier.next())
			{
				this.company = res_supplier.getString("company");
				this.name = res_supplier.getString("name");
				this.address = res_supplier.getString("address");
				this.email = res_supplier.getString("email");
				this.phone = res_supplier.getString("phone");
				this.created_date = res_supplier.getDate("created_at");
				this.updated_date = res_supplier.getDate("updated_at");
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	void create(String name)
	{
		create("", name, "", "", "");
	}
	
	void create(String company, String name, String address, String email, String phone)
	{
		if(id > 0)
		{
			return;
		}
		
		this.company = company;
		this.name = name;
		this.address = address;
		this.email = email;
		this.phone = phone;
		
		PreparedStatement pstmt = db.pstmt("INSERT INTO suppliers(company, name, address, email, phone) VALUES(?, ?, ?, ?, ?)");
		
		try
		{
			pstmt.setString(1, company);
			pstmt.setString(1, name);
			pstmt.setString(2, address);
			pstmt.setString(3, email);
			pstmt.setString(4, phone);
			
			pstmt.executeQuery();
			
			ResultSet generated_keys = pstmt.getGeneratedKeys();
			
			generated_keys.next();
			
			id = generated_keys.getInt(1);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
}
