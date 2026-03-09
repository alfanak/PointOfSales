package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import common.common;

public class db_user
{
	public static final int SUCCESS = 0;
	public static final int E_UNKNOWN_ERROR = -1;
	public static final int E_USER_EXISTS = -2;
	
	database db = database.get_instance();
	
	public int id;
	public String name;
	public String pass;
	public int role_id;
	public Date created_at;
	public Date updated_at;
	
	public db_user() {}
	
	public db_user(int user_id)
	{
		load(user_id);
	}
	
	public int find(String user_name, String user_password)
	{
		try
		{
			PreparedStatement pstmt = db.pstmt("SELECT * FROM users WHERE name=? AND pass=?");
			
			pstmt.setString(1, user_name);
			pstmt.setString(2, common.md5(user_password));
			
			ResultSet res_user = pstmt.executeQuery();
			
			if(res_user.next())
			{
				id = res_user.getInt("id");
				name = res_user.getString("name");
				pass = res_user.getString("pass");
				role_id = res_user.getInt("role_id");
				created_at = res_user.getDate("created_at");
				updated_at = res_user.getDate("updated_at");
				
				return SUCCESS;
			}
			
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
	
	public int load(int user_id)
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM users WHERE id=?");
		try
		{
			pstmt.setInt(1, user_id);
			ResultSet res_user = pstmt.executeQuery();
			
			if(res_user.next())
			{
				id = res_user.getInt("id");
				name = res_user.getString("name");
				pass = res_user.getString("pass");
				role_id = res_user.getInt("role_id");
				created_at = res_user.getDate("created_at");
				updated_at = res_user.getDate("updated_at");
			}
			
			return SUCCESS;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
	
	public int add()
	{
		return add(name, pass, role_id);
	}
	
	public int add(String name, String password, int role_id)
	{
		try
		{
			PreparedStatement pstmt = db.pstmt("SELECT COUNT(1) FROM users WHERE name=?");
			pstmt.setString(1, name);
			
			ResultSet user = pstmt.executeQuery();
			
			if(user.next())
			{
				return E_USER_EXISTS;
			}
			
			pstmt = db.pstmt("INSERT INTO users(name, md5(password), role_id) VALUES(?, ?, ?)");
			
			pstmt.setString(1, name);
			pstmt.setString(2, password);
			pstmt.setInt(3, role_id);
			
			pstmt.executeUpdate();
			
			return SUCCESS;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		return E_UNKNOWN_ERROR;
	}
	
	public int edit(int user_id, String name, String password, int role_id)
	{
		PreparedStatement pstmt = db.pstmt("UPDATE users set name=?, password=?, role_id=? WHERE id=?");
		
		try
		{
			pstmt.setString(1, name);
			pstmt.setString(2, password);
			pstmt.setInt(3, role_id);
			pstmt.setInt(4, user_id);
			
			pstmt.executeUpdate();
			
			return SUCCESS;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
	
	public int delete(int user_id)
	{
		PreparedStatement pstmt = db.pstmt("DELETE FROM users WHERE id=?");
		
		try
		{
			pstmt.setInt(1, user_id);
			pstmt.executeUpdate();
			
			return SUCCESS;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
		
}
