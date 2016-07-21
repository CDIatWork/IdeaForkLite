package at.irian.cdiatwork.ideafork.core.api.domain.role;

import at.irian.cdiatwork.ideafork.core.api.repository.role.UserRepository;
import at.irian.cdiatwork.ideafork.core.api.security.PasswordManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserManager {
    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordManager passwordManager;

    public void save(User entity) {
        userRepository.save(entity);
    }

    public User loadById(String id) {
        return userRepository.findBy(id);
    }

    public User loadByEmail(String email) {
        return userRepository.loadByEmail(email);
    }

    public User loadByNickName(String nickName) {
        return userRepository.loadByNickName(nickName);
    }
}
