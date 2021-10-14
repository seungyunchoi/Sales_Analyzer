import java.sql.*;


//container for sql key relation
public class SqlRelation {
	public String tableName;
	public String sharedKey;
	
	public SqlRelation(String t, String s) {
		tableName = t;
		sharedKey = s;
	}
}