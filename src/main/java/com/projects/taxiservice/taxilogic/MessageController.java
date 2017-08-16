package com.projects.taxiservice.taxilogic;

import com.projects.taxiservice.taxilogic.interfaces.MessageControllerOperations;
import com.projects.taxiservice.taxilogic.utilities.DirectMessenger;
import com.projects.taxiservice.taxilogic.utilities.MessageStyler;
import com.projects.taxiservice.taxilogic.utilities.TokenFilter;
import com.projects.taxiservice.model.users.User;
import com.projects.taxiservice.model.taxi.Driver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controls all web requests that come through /messenger endpoint
 */
@RestController
@CrossOrigin
@RequestMapping("/messenger")
public class MessageController implements MessageControllerOperations {

    private final String INVALID_TOKEN = "Token not recognized";

    /**
     * Sends a message from user with specified token to user with specified id
     *
     * @param token of sender
     * @param message to be sent
     * @param receiver id of receiver
     * @return 403 status when invalid token. 400 status when token, receiver, message is invalid.
     *         200 status and decorated message when successful @ref MessageStyler
     */
    @RequestMapping(path = "/message", method = RequestMethod.POST)
    public ResponseEntity<?> sendMessage(@RequestParam String token,
                                         @RequestParam String message,
                                         @RequestParam int receiver){
        if(message == null || message.length() < 1) return new ResponseEntity<>("Empty message", HttpStatus.BAD_REQUEST);
        if(receiver < 1) return new ResponseEntity<>("Receiver not specified", HttpStatus.BAD_REQUEST);

        if(TokenFilter.isUserSession(token)) return new ResponseEntity<>(sendMessageToDriver(token, message, receiver), HttpStatus.OK);
        else if(TokenFilter.isDriverSession(token)) return new ResponseEntity<>(sendMessageToUser(token, message, receiver), HttpStatus.OK);
        else {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Gets messages for requester with specified token
     *
     * @param token of requester
     * @return 403 status when invalid token. 400 status when token is invalid.
     *         200 status and list of messages when successful
     */
    @RequestMapping(path = "/message", method = RequestMethod.GET)
    public ResponseEntity<?> getMessages(@RequestParam String token){
        if(TokenFilter.isUserSession(token)) return new ResponseEntity<>(getUserMessages(token), HttpStatus.OK);
        else if(TokenFilter.isDriverSession(token)) return new ResponseEntity<>(getDriverMessages(token), HttpStatus.OK);
        else {
            return new ResponseEntity<Object>(INVALID_TOKEN, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Puts a message to Jedis and gives a styled message to be displayed in sender's ui
     *
     * @param token of sender
     * @param message to be sent
     * @param driver id of recipient
     * @return styled message
     */
    private String sendMessageToDriver(String token, String message, int driver){
        String styledMessage = MessageStyler.style(message, TokenFilter.getUser(token).getName());

        DirectMessenger.sendMessageToDriver(new Driver().setId(driver), styledMessage);

        return styledMessage;
    }

    /**
     * Gets messages from Jedis and gives them to requester
     *
     * @param token of requester
     * @return list of styled messages
     */
    private Object getUserMessages(String token){
        User user = TokenFilter.getUser(token);
        List<String> list = Collections.emptyList();
        if(DirectMessenger.hasUserMessages(user)) list = DirectMessenger.getUserMessages(user);

        return list;
    }

    /**
     * Puts a message to Jedis and gives a styled message to be displayed in sender's ui
     *
     * @param token of sender
     * @param message to be sent
     * @param user id of recipient
     * @return styled message
     */
    private Object sendMessageToUser(String token, String message, int user){
        String styledMessage = MessageStyler.style(message, TokenFilter.getDriver(token).getName());

        DirectMessenger.sendMessageToUser(new User().setId(user), styledMessage);

        return styledMessage;
    }

    /**
     * Gets messages from Jedis and gives them to requester
     *
     * @param token of requester
     * @return list of styled messages
     */
    private Object getDriverMessages(String token){
        Driver driver = TokenFilter.getDriver(token);
        List<String> list = Collections.emptyList();
        if(DirectMessenger.hasDriverMessages(driver)) list = DirectMessenger.getDriverMessages(driver);

        return list;
    }
}
