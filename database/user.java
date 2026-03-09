package database;
import java.util.ArrayList;

public class user
{
	private static user current_user;
	
	database db = database.get_instance();
	
	public boolean loggedin = false;
	
	private db_user _user;
	
	private db_user_role role;
	
	private user_permissions permissions;
	
	user()
	{
		_user = new db_user();
		role = new db_user_role();
		permissions = new user_permissions();
	}
	
	public static user current_user()
	{
		if(current_user == null)
		{
			current_user = new user();
		}
		
		return current_user;
	}
	
	public void login(String name, String password)
	{
		int response = _user.find(name, password);
		
		if(response == db_user.SUCCESS)
		{
			loggedin = true;
			
			role.load(_user.role_id);
			
			permissions.load(role.permissions);
		}
		else
		{
			System.err.println("خطأ في تسجيل الدخول");
		}
	}
	
	public void register()
	{
		_user.add();
	}
	
	public void register(String name, String password, int role_id)
	{
		_user.add(name, password, role_id);
	}
	
	public void update()
	{
		_user.edit(_user.id, _user.name, _user.pass, _user.role_id);
	}
	
	public void update(int user_id, String name, String password, int role_id)
	{
		_user.edit(user_id, name, password, role_id);
	}
	
	public void delete()
	{
		_user.delete(_user.id);
	}
	
	public void delete(int user_id)
	{
		_user.delete(user_id);
	}
	
	public boolean has_permission(String str_permission_label)
	{
		return permissions.has_permission(str_permission_label);
	}
	
	public boolean has_permission(user_permissions.labels label)
	{
		return permissions.has_permission(label);
	}
	
	public ArrayList<db_user_role> allowable_roles()
	{
		return role.allowable_roles();
	}
	
	public boolean can_edit_user(int user_id)
	{
		db_user u = new db_user(user_id);
		
		ArrayList<db_user_role> allowable_roles = user.current_user().allowable_roles();
		
		for(db_user_role role:allowable_roles)
		{
			if(role.id == u.role_id)
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	public boolean can_add_user(int user_id)
	{
		// TODO: إضافة صلاحيات خاصة بإضافة مستخدمين جدد
		
		return has_permission("ALL");
	}
	
	public boolean can_delete_user(int user_id)
	{
		// TODO: إضافة صلاحيات خاصة بحذف المستخدمين
		
		return has_permission("ALL");
	}
	
}
