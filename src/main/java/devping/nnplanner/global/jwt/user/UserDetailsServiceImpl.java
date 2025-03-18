package devping.nnplanner.global.jwt.user;


import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.domain.user.entity.User;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return new UserDetailsImpl(user);
    }
}