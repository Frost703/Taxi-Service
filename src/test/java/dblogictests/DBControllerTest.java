package dblogictests;

import com.projects.taxiservice.persistent.DBController;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by O'Neill on 7/11/2017.
 */
public class DBControllerTest {
    @Test
    public void testNotNullConnection() throws SQLException{
        assertNotNull(DBController.getConnection());
    }
}
