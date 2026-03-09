package database;
import java.util.ArrayList;

public class user_permissions
{
	static private final String permissions_string_delimiter = ", ";
	
	static final int EQUAL = 0;
	static final int SUPERIOR = 1;
	static final int INFERIOR = 2;
	
	
	database db = database.get_instance();
	
	public ArrayList<labels> granted = new ArrayList<>();
	
	public user_permissions() {}
	
	public user_permissions(String str_permissions)
	{
		load(str_permissions);
	}
	
	public void load(String str_permissions)
	{
		granted = split(str_permissions);
	}
	
	public static ArrayList<labels> split(String str_permissions)
	{
		ArrayList<labels> result = new ArrayList<>();
		
		if(str_permissions != null && ! str_permissions.isBlank())
		{
			String str_labels[] = str_permissions.split(permissions_string_delimiter);
			
			for(String str_label:str_labels)
			{
				result.add(user_permissions.labels.from_string(str_label));
			}
		}
		
		return result;
	}
	
	public boolean has_permission(String str_permission_label)
	{
		user_permissions.labels lbl_permission = user_permissions.labels.from_string(str_permission_label);
		
		return has_permission(lbl_permission);
	}
	
	public boolean has_permission(user_permissions.labels label)
	{
		if(granted.contains(label) || granted.contains(label.parent()) || granted.contains(user_permissions.labels.ALL))
		{
			return true;
		}
		return false;
	}
	
	public boolean has_permissions(ArrayList<labels> perms)
	{
		for(labels l:perms)
		{
			if( ! has_permission(l))
			{
				return false;
			}
		}
		return true;
	}
	
	public static enum labels
	{
		ALL,
		USERS,
		PRODUCTS,
		SALES,
		BUYS,
		INVENTORY,
		EXPENSES,
		SHOPPING_LISTS,
		SETTINGS,
		
		USERS_VIEW,
		USERS_ADD,
		USERS_EDIT,
		USERS_DELETE,
		USERS_VIEW_ROLES,
		USERS_EDIT_ROLES,
		
		PRODUCTS_VIEW,
		PRODUCTS_ADD,
		PRODUCTS_EDIT,
		PRODUCTS_DELETE,
		
		SALES_VIEW_INVOICE,
		SALES_CREATE_INVOICE,
		SALES_EDIT_INVOICE,
		SALES_DELETE_INVOICE,
		
		BUYS_VIEW_INVOICE,
		BUYS_CREATE_INVOICE,
		BUYS_EDIT_INVOICE,
		BUYS_DELETE_INVOICE,
		
		INVENTORY_VIEW,
		INVENTORY_EDIT,
		
		EXPENSES_VIEW,
		EXPENSES_ADD,
		EXPENSES_EDIT,
		EXPENSES_DELETE,
		
		SHOPPING_LISTS_VIEW,
		SHOPPING_LISTS_ADD,
		SHOPPING_LISTS_EDIT,
		SHOPPING_LISTS_DELETE,
		
		SETTINGS_GENERAL,
		SETTINGS_DATABASE,
		SETTINGS_DATA_SECURITY,
		UNDEFINED;
		
		public static labels from_string(String str)
		{
			try
			{
				return valueOf(str);
			}
			catch(IllegalArgumentException ex)
			{
				return  UNDEFINED;
			}
		}
		
		public labels parent()
		{
			return parent(this);
		}
		
		public static labels parent(labels label)
		{
			switch(label)
			{
			case USERS_VIEW:
			case USERS_ADD:
			case USERS_EDIT:
			case USERS_DELETE:
			case USERS_VIEW_ROLES:
			case USERS_EDIT_ROLES:
				return USERS;
			
			case PRODUCTS_VIEW:
			case PRODUCTS_ADD:
			case PRODUCTS_EDIT:
			case PRODUCTS_DELETE:
				return PRODUCTS;
			
			case SALES_VIEW_INVOICE:
			case SALES_CREATE_INVOICE:
			case SALES_EDIT_INVOICE:
			case SALES_DELETE_INVOICE:
				return SALES;
			
			case BUYS_VIEW_INVOICE:
			case BUYS_CREATE_INVOICE:
			case BUYS_EDIT_INVOICE:
			case BUYS_DELETE_INVOICE:
				return BUYS;
				
			case INVENTORY_VIEW:
			case INVENTORY_EDIT:
				return INVENTORY;
				
			case EXPENSES_VIEW:
			case EXPENSES_ADD:
			case EXPENSES_EDIT:
			case EXPENSES_DELETE:
				return EXPENSES;
				
			case SHOPPING_LISTS_VIEW:
			case SHOPPING_LISTS_ADD:
			case SHOPPING_LISTS_EDIT:
			case SHOPPING_LISTS_DELETE:
				return SHOPPING_LISTS;
				
			case SETTINGS_GENERAL:
			case SETTINGS_DATABASE:
				return SETTINGS;
				
			default:
				return null;
			}
		}
	}
}