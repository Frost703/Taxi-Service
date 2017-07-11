package taxilogictests.utilitiestests;

import com.projects.taxiservice.taxilogic.utilities.MessageStyler;
import org.junit.Test;

/**
 * Created by O'Neill on 7/11/2017.
 */
public class MessageStylerTest {
    private String invalidMessage = "";
    private String validMessage = "Message!";
    private String nullMessage = null;
    private String invalidSenderName = "";
    private String validSenderName = "Name";
    private String nullSenderName = null;

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnStylingInvalidMessage(){
        MessageStyler.style(invalidMessage, validSenderName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnStylingInvalidSenderName(){
        MessageStyler.style(validMessage, invalidSenderName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnStylingNullMessage(){
        MessageStyler.style(nullMessage, validSenderName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsIAEOnStylingNullSenderName(){
        MessageStyler.style(validMessage ,nullSenderName);
    }
}
