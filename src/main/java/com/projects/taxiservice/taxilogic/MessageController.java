package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.taxilogic.interfaces.MessageControllerOperations;
import com.projects.taxiservice.taxilogic.utilities.DirectMessenger;
import com.projects.taxiservice.taxilogic.utilities.MessageStyler;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.users.customer.User;
import com.projects.taxiservice.users.drivers.Driver;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Created by O'Neill on 7/3/2017.
 */
@RestController
@CrossOrigin
@RequestMapping("/messenger")
public class MessageController implements MessageControllerOperations {

    private final String INVALID_TOKEN = "Token not recognized";

    @RequestMapping(path = "/message", method = RequestMethod.POST)
    public Object sendMessage(@RequestParam String token,
                              @RequestParam String message,
                              @RequestParam int receiver){
        if(message == null || message.length() < 1) return "Message can't be empty";
        if(receiver < 1) return "Receiver not specified";

        if(TokenFilter.isUserSession(token)) return sendMessageToDriver(token, message, receiver);
        else if(TokenFilter.isDriverSession(token)) return sendMessageToUser(token, message, receiver);
        else return INVALID_TOKEN;
    }

    @RequestMapping(path = "/message", method = RequestMethod.GET)
    public Object getMessages(@RequestParam String token){
        if(TokenFilter.isUserSession(token)) return getUserMessages(token);
        else if(TokenFilter.isDriverSession(token)) return getDriverMessages(token);
        else return INVALID_TOKEN;
    }

    private Object sendMessageToDriver(String token, String message, int driver){
        String styledMessage = MessageStyler.style(message, TokenFilter.getUser(token).getName());

        DirectMessenger.sendMessageToDriver(new Driver().setId(driver), styledMessage);

        return styledMessage;
    }

    private Object getUserMessages(String token){
        User user = TokenFilter.getUser(token);
        List<String> list = Collections.emptyList();
        if(DirectMessenger.hasUserMessages(user)) list = DirectMessenger.getUserMessages(user);

        return list;
    }

    private Object sendMessageToUser(String token, String message, int user){
        String styledMessage = MessageStyler.style(message, TokenFilter.getDriver(token).getName());

        DirectMessenger.sendMessageToUser(new User().setId(user), styledMessage);

        return styledMessage;
    }

    private Object getDriverMessages(String token){
        Driver driver = TokenFilter.getDriver(token);
        List<String> list = Collections.emptyList();
        if(DirectMessenger.hasDriverMessages(driver)) list = DirectMessenger.getDriverMessages(driver);

        return list;
    }
}
