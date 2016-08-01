package at.irian.cdiatwork.ideafork.ee.backend.service;

import at.irian.cdiatwork.ideafork.core.api.domain.role.User;
import at.irian.cdiatwork.ideafork.core.api.repository.role.UserRepository;
import at.irian.cdiatwork.ideafork.core.api.security.PasswordManager;
import at.irian.cdiatwork.ideafork.core.impl.event.UserRegisteredEventBroadcaster;
import at.irian.cdiatwork.ideafork.ee.shared.ActiveUserHolder;

import javax.inject.Inject;

@Service
public class UserService {
    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private ActiveUserHolder userHolder;

    @Inject
    private UserRegisteredEventBroadcaster userRegisteredEventBroadcaster;

    public User registerUser(User newUser) {
        if (userRepository.loadByEmail(newUser.getEmail()) == null) {
            newUser.setPassword(passwordManager.createPasswordHash(newUser.getPassword()));
            userRepository.save(newUser);
            User registeredUser = userRepository.findBy(newUser.getId());

            if (registeredUser != null) {
                userRegisteredEventBroadcaster.broadcastUserRegisteredEvent(registeredUser);
                return registeredUser;
            }
        }
        return null;
    }

    public User loadByNickName(String nickName) {
        return userRepository.loadByNickName(nickName);
    }

    public void login(String email, String password) {
        User registeredUser = userRepository.loadByEmail(email);

        if (registeredUser != null) {
            String hashedPassword = passwordManager.createPasswordHash(password);

            if (hashedPassword.equals(registeredUser.getPassword())) {
                userHolder.setAuthenticatedUser(registeredUser);
                return;
            }
        }

        userHolder.setAuthenticatedUser(null);
    }
}
