package database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class db_user_role
{
	public static final int SUCCESS = 0;
	public static final int E_UNKNOWN_ERROR = -1;
	
	database db = database.get_instance();
	
	public int id;
	public String name;
	public String permissions;
	
	public int load()
	{
		return load(id);
	}
	
	public int save()
	{
		if(id > 0)
		{
			return update();
		}
		else
		{
			return add(name, permissions);
		}
	}
	
	public int update()
	{
		return update(id, name, permissions);
	}
	
	public int delete()
	{
		return delete();
	}
	
	
	public int load(int role_id)
	{
		PreparedStatement pstmt = db.pstmt("SELECT * FROM user_roles WHERE id=?");
		
		try
		{
			pstmt.setInt(1, role_id);
			
			ResultSet res_role = pstmt.executeQuery();
			
			if(res_role.next())
			{
				id = res_role.getInt("id");
				name = res_role.getString("name");
				permissions = res_role.getString("permissions");
				
				return SUCCESS;
			}
			
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
	
	public int add(String name, String permissions)
	{
		PreparedStatement pstmt = db.pstmt("INSERT INTO user_roles(name, permissions) VALUES(?, ?)");
		
		try
		{
			pstmt.setString(1, name);
			pstmt.setString(2, permissions);
			
			pstmt.executeUpdate();
			
			return SUCCESS;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
	
	public int update(int role_id, String nam, String permissions)
	{
		PreparedStatement pstmt = db.pstmt("UPDATE user_roles SET name=?, permissions=? WHERE id=?");
		
		try
		{
			pstmt.setString(1, name);
			pstmt.setString(2, permissions);
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
	
	public int delete(int role_id)
	{
		PreparedStatement pstmt = db.pstmt("DELETE FROM user_roles WHERE id=?");
		
		try
		{
			pstmt.setInt(1, role_id);
			pstmt.executeUpdate();
			
			return SUCCESS;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return E_UNKNOWN_ERROR;
	}
	
	public ArrayList<db_user_role> roles_list()
	{
		ArrayList<db_user_role> roles_list= new ArrayList<>();
		
		ResultSet res_roles = db.query("SELECT * FROM user_roles");
		
		try
		{
			while(res_roles.next())
			{
				db_user_role role = new db_user_role();
					
				role.id = res_roles.getInt("id");
				role.name = res_roles.getString("name");;
				role.permissions = res_roles.getString("permissions");;
					
				roles_list.add(role);
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return roles_list;
	}
	
	// مجموعة الرتب المتاحة للمستخدم لاستخدامها في إنشاء حسابات جديدة
	// المستخدم يستطيع إنشاء حسابات بنفس صلاحياته أو بصلاحيات أقل، ولا يمكنه إنشاء حسابات بصلاحيات أعلى
	// المستخدم لا يستطيع إنشاء حسابات بصلاحيات لا يملكها بغض النظر عن درجتها وأهميتها
	
	public ArrayList<db_user_role> allowable_roles()
	{
		ArrayList<db_user_role> allowable_roles = new ArrayList<>();
		
		user_permissions user_perms = new user_permissions(permissions);
		
		ResultSet res_roles = db.query("SELECT * FROM user_roles");
		
		try
		{
			while(res_roles.next())
			{
				user_permissions role_perms = new user_permissions(res_roles.getString("permissions"));
				
				if(user_perms.has_permissions(role_perms.granted))
				{
					db_user_role role = new db_user_role();
					
					role.id = res_roles.getInt("id");
					role.name = res_roles.getString("name");;
					role.permissions = res_roles.getString("permissions");;
					
					allowable_roles.add(role);
				}
			}
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		
		return allowable_roles;
	}
}
