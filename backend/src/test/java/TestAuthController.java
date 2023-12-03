import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.inf5190.chat.auth.AuthController;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.auth.repository.FirestoreUserAccount;
import com.inf5190.chat.auth.repository.UserAccountRepository;
import com.inf5190.chat.auth.session.SessionData;
import com.inf5190.chat.auth.session.SessionManager;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestAuthController {

    private final String username = "username";
    private final String password = "pwd";
    private final String hashedPassword = "hash";
    private final FirestoreUserAccount userAccount = new FirestoreUserAccount(this.username, this.hashedPassword);

    private final LoginRequest loginRequest = new LoginRequest(this.username, this.password);

    @Mock
    private SessionManager mockSessionManager;

    @Mock
    private UserAccountRepository mockAccountRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loginExistingUserAccountWithCorrectPassword() throws InterruptedException, ExecutionException {
        final SessionData expectedSessionData = new SessionData(this.username);

        when(this.mockAccountRepository.getUserAccount(loginRequest.username())).thenReturn(userAccount);
        when(this.mockPasswordEncoder.matches(loginRequest.password(), this.hashedPassword)).thenReturn(true);
        when(this.mockSessionManager.addSession(expectedSessionData)).thenReturn(this.username);

        ResponseEntity<LoginResponse> response = this.authController.login(loginRequest);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().username()).isEqualTo(this.username);

        verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
        verify(this.mockPasswordEncoder, times(1)).matches(this.password, this.hashedPassword);
        verify(this.mockSessionManager, times(1)).addSession(expectedSessionData);
    }

    @Test
    public void loginFirstTimeUser() throws InterruptedException, ExecutionException {
        final SessionData expectedSessionData = new SessionData(this.username);

        when(this.mockAccountRepository.getUserAccount(loginRequest.username())).thenReturn(null);
        when(this.mockPasswordEncoder.encode(loginRequest.password())).thenReturn(this.hashedPassword);
        when(this.mockSessionManager.addSession(expectedSessionData)).thenReturn(this.username);

        ResponseEntity<LoginResponse> response = this.authController.login(loginRequest);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().username()).isEqualTo(this.username);

        verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
        verify(this.mockPasswordEncoder, times(1)).encode(this.password);
        verify(this.mockSessionManager, times(1)).addSession(expectedSessionData);
    }

    @Test
    public void loginIncorrectPassword() throws InterruptedException, ExecutionException {
        when(this.mockAccountRepository.getUserAccount(loginRequest.username())).thenReturn(userAccount);
        when(this.mockPasswordEncoder.matches(loginRequest.password(), this.hashedPassword)).thenReturn(false);

        // Le contrôleur doit lancer une ResponseStatusException avec l'erreur 403
        // Forbidden.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> this.authController.login(loginRequest));
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);

        verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
        verify(this.mockPasswordEncoder, times(1)).matches(this.password, this.hashedPassword);
        verify(this.mockSessionManager, times(0)).addSession(Mockito.any());
    }

    @Test
    public void loginUnexpectedException() throws InterruptedException, ExecutionException {
        when(this.mockAccountRepository.getUserAccount(loginRequest.username()))
                .thenThrow(new RuntimeException("Simulated exception"));

        // le contrôleur doit attraper l'exception et renvoyer une erreur 500
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> this.authController.login(loginRequest));
        assertThat(exception.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        verify(this.mockAccountRepository, times(1)).getUserAccount(this.username);
        verify(this.mockPasswordEncoder, times(0)).matches(Mockito.any(), Mockito.any());
        verify(this.mockSessionManager, times(0)).addSession(Mockito.any());
    }
}
