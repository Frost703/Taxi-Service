package dblogictests;

import com.projects.taxiservice.persistent.DBController;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by O'Neill on 7/11/2017.
 */
public class DBControllerTest {
    @Test
    public void testNotNullConnection(){
        assertNotNull(DBController.getConnection());
    }
}
