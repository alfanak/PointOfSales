package database;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class user_log
{
	static database db = database.get_instance();
	
	static void log(int user_id, int subject_id, operation op, String desc)
	{
		PreparedStatement pstmt = db.pstmt("INSERT INTO user_logs(user_id, subject_id, operation, description) VALUES(?, ?, ?, ?)");
		
		try
		{
			pstmt.setInt(1, user_id);
			pstmt.setInt(2, subject_id);
			pstmt.setString(3, op.name());
			pstmt.setString(4, desc);
			
			pstmt.executeUpdate();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	enum operation
	{
		// الحسابات
		
		CREATE_ACCOUNT,
		EDIT_ACCOUNT,
		DELETE_ACCOUNT,
		
		// السلع والمنتجات
		
		ADD_PRODUCT,
		EDIT_PRODUCT,
		DELETE_PRODUCT,
		
		// فواتير البيع
		
		CREATE_INVOICE,
		EDIT_INVOICE,
		DELETE_INVOICE,
		
		// فواتير الشراء
		
		CREATE_BUYING_INVOICE,
		EDIT_BUYING_INVOICE,
		DELETE_BUYING_INVOICE,
		
		// الزبائن
		
		ADD_CLIENT,
		EDIT_CLIENT,
		DELETE_CLIENT,
		
		// الموردون
		
		ADD_SUPPLIER,
		EDIT_SUPPLIER,
		DELETE_SUPPLIER,
		
		// المصاريف
		
		ADD_EXPENSE,
		EDIT_EXPENSE,
		DELETE_EXPENSE,
		
		// قوائم المشتريات
		
		ADD_SHOPPING_LIST,
		EDIT_SHOPING_LIST,
		DELETE_SHOPING_LIST,
		
		// الإعدادات
		
		UPDATE_APP_SETTINGS,
		
		// أمن البيانات
		
		CREATE_BACKUP,
		RESTOR_BACKUP
	}
}
