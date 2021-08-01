package software.plusminus.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.plusminus.security.model.User;

@Component
@Profile("!test")
public class Starter {

    @Autowired
    private UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        addUser("spokusasnu.com.ua", "spokusasnu@ukr.net", "spokusa7");
        addUser("italiyko.com.ua", "mail@italiyko.com.ua", "italiyko7");
        addUser("unimarket.pro/levenya", "mail@levenya.com.ua", "levenya7");
        addUser("unimarket.pro/oleg", "mail@levenya.com.ua", "oleg2");
    }

    private void addUser(String tenant, String email, String password) {

        if (userService.isRegistered(email)) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setTenant(tenant);
        userService.register(user);
    }

}
