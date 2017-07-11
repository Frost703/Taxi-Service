package taxilogictests.utilitiestests;

import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by O'Neill on 7/11/2017.
 */
public class TokenFilterTest {
    private String nullKey = null;
    private String invalidKey = "";
    private String validKey = "validKey";

    private User nullUser = null;
    private User validUser = new User();

    private Driver nullDriver = null;
    private Driver validDriver = new Driver();

    @Test
    public void testReturnsFalseOnInvalidOrNullKey(){
        assertEquals(false, TokenFilter.isUserSession(nullKey));
        assertEquals(false, TokenFilter.isDriverSession(nullKey));

        assertEquals(false, TokenFilter.isUserSession(invalidKey));
        assertEquals(false, TokenFilter.isDriverSession(invalidKey));
    }

    @Test
    public void testAddsAndRemovesObjects(){
        assertEquals(1, TokenFilter.addUserSession(validKey, validUser));
        assertEquals(true, TokenFilter.isUserSession(validKey));
        assertEquals(true, TokenFilter.removeUserSession(validKey));

        assertEquals(1, TokenFilter.addDriverSession(validKey, validDriver));
        assertEquals(true, TokenFilter.isDriverSession(validKey));
        assertEquals(true, TokenFilter.removeDriverSession(validKey));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnAddInvalidUser(){
        TokenFilter.addUserSession(invalidKey, validUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnAddNullUser(){
        TokenFilter.addUserSession(validKey, nullUser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnAddInvalidKey(){
        TokenFilter.addDriverSession(invalidKey, validDriver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnAddNullDriver(){
        TokenFilter.addDriverSession(validKey, nullDriver);
    }
}
