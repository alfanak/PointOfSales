package database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Client
{
	database db = database.get_instance();
	
	public int id = 1;
	public String name;
	public String address;
	public String email;
	public String phone;
	public LocalDateTime created_date;
	public LocalDateTime updated_date;
	
	public Client() {}
	
	public Client(int id)
	{
		load(id);
	}
	
	public void load()
	{
		load(id);
	}
	
	public void load(int id)
	{
		this.id = id;
		
		ResultSet res_client = db.query("SELECT * FROM clients WHERE id="+id);
		
		try
		{
			if(res_client.next())
			{
				this.name = res_client.getString("name");
				this.address = res_client.getString("address");
				this.email = res_client.getString("email");
				this.phone = res_client.getString("phone");
				this.created_date = res_client.getTimestamp("created_at").toLocalDateTime();
				this.updated_date = res_client.getTimestamp("updated_at").toLocalDateTime();
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void create(String name)
	{
		create(name, "", "", "");
	}
	
	public void create(String name, String address, String email, String phone)
	{
		if(id > 0)
		{
			return;
		}
		
		this.name = name;
		this.address = address;
		this.email = email;
		this.phone = phone;
		
		PreparedStatement pstmt = db.pstmt("INSERT INTO clients(name, address, email, phone) VALUES(?, ?, ?, ?)");
		
		try
		{
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
