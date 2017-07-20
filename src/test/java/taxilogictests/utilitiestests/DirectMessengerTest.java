package taxilogictests.utilitiestests;

import com.projects.taxiservice.taxilogic.utilities.DirectMessenger;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import com.sun.org.apache.bcel.internal.generic.DMUL;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by O'Neill on 7/11/2017.
 */
public class DirectMessengerTest {
    private User validUser = new User();
    private Driver validDriver = new Driver();
    private User invalidUser = new User();
    private Driver invalidDriver = new Driver();

    private String validMessage = "Valid Message";
    private String invalidMessage = "";

    @Before
    private void init() {
        validUser.setId(1);
        validDriver.setId(1);

        invalidDriver.setId(0);
        invalidUser.setId(0);
    }

    @Test
    public void testReturnsAllUserMessages(){
        for(int i=0; i<20; i++) DirectMessenger.sendMessageToUser(validUser, validMessage+i);

        assertEquals(true, DirectMessenger.hasUserMessages(validUser));
        assertEquals(20, DirectMessenger.getUserMessages(validUser).size());
        assertEquals(false, DirectMessenger.hasUserMessages(validUser));
    }

    @Test
    public void testReturnsAllDriverMessages(){
        for(int i=0; i<20; i++) DirectMessenger.sendMessageToDriver(validDriver, validMessage+i);

        assertEquals(true, DirectMessenger.hasDriverMessages(validDriver));
        assertEquals(20, DirectMessenger.getDriverMessages(validDriver).size());
        assertEquals(false, DirectMessenger.hasDriverMessages(validDriver));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnIdSendMessageToDriver(){
        DirectMessenger.sendMessageToDriver(invalidDriver, validMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnIdSendMessageToUser(){
        DirectMessenger.sendMessageToUser(invalidUser, validMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnMessageSendMessageToDriver(){
        DirectMessenger.sendMessageToDriver(validDriver, invalidMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnMessageSendMessageToUser(){
        DirectMessenger.sendMessageToUser(validUser, invalidMessage);
    }

    @Test
    public void testReturnsNullCollection(){
        assertNull(DirectMessenger.getDriverMessages(validDriver));
        assertNull(DirectMessenger.getUserMessages(validUser));
    }
}
