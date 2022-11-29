package org.ttn.ecommerce.services;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.AuthResponseDto;
import org.ttn.ecommerce.dto.LoginDto;
import org.ttn.ecommerce.dto.register.CustomerRegisterDto;
import org.ttn.ecommerce.dto.register.SellerRegisterDto;
import org.ttn.ecommerce.entities.register.Customer;
import org.ttn.ecommerce.entities.register.Role;
import org.ttn.ecommerce.entities.register.Seller;
import org.ttn.ecommerce.entities.register.UserEntity;
import org.ttn.ecommerce.entities.token.ActivateUserToken;
import org.ttn.ecommerce.entities.token.BlackListToken;
import org.ttn.ecommerce.entities.token.RefreshToken;
import org.ttn.ecommerce.entities.token.Token;
import org.ttn.ecommerce.exception.UserNotFoundException;
import org.ttn.ecommerce.repository.RegisterRepository.CustomerRepository;
import org.ttn.ecommerce.repository.RegisterRepository.RoleRepository;
import org.ttn.ecommerce.repository.RegisterRepository.SellerRepository;
import org.ttn.ecommerce.repository.TokenRepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.ActivationTokenRepository;
import org.ttn.ecommerce.repository.TokenRepository.JWTBlackListRepository;
import org.ttn.ecommerce.repository.TokenRepository.RefreshTokenRepository;
import org.ttn.ecommerce.repository.RegisterRepository.UserRepository;
import org.ttn.ecommerce.security.JWTGenerator;
import org.ttn.ecommerce.security.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@NoArgsConstructor
@Transactional
@Slf4j
public class UserDaoService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncode;
    private JWTGenerator jwtGenerator;
    private CustomerRepository customerRepository;
    private EmailService emailService;
    private SellerRepository sellerRepository;
    private TokenService tokenService;
    private AccessTokenRepository accessTokenRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JWTBlackListRepository jwtBlackListRepository;

    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    public UserDaoService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncode, JWTGenerator jwtGenerator, CustomerRepository customerRepository, EmailService emailService, SellerRepository sellerRepository, TokenService tokenService, AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository, JWTBlackListRepository jwtBlackListRepository, ActivationTokenRepository activationTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncode = passwordEncode;
        this.jwtGenerator = jwtGenerator;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.sellerRepository = sellerRepository;
        this.tokenService = tokenService;
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtBlackListRepository = jwtBlackListRepository;
        this.activationTokenRepository = activationTokenRepository;
    }

    public ResponseEntity<String> registerCustomer(CustomerRegisterDto registerDto) {
        /*Checking customer exist with this mail or not*/
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            log.info("Email is already registered.");
            return new ResponseEntity<>("Email is already registered.", HttpStatus.BAD_REQUEST);
        }
        /* Checking password and confirm password match or not*/
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            log.info("Password and Confirm password do not match.");
            return new ResponseEntity<>("Password and Confirm password do not match.", HttpStatus.BAD_REQUEST);
        }

        Customer customer = new Customer();
        customer.setFirstName(registerDto.getFirstName());
        customer.setMiddleName(registerDto.getMiddleName());
        customer.setLastName(registerDto.getLastName());

        customer.setActive(false);
        customer.setDeleted(false);
        customer.setExpired(false);
        customer.setLocked(false);
        customer.setInvalidAttemptCount(0);


        customer.setEmail(registerDto.getEmail());
        customer.setPassword(passwordEncode.encode(registerDto.getPassword()));
        customer.setContact(registerDto.getContact());


        Role roles = roleRepository.findByAuthority("ROLE_CUSTOMER").get();
        customer.setRoles(Collections.singletonList(roles));

        customerRepository.save(customer);
        /*send mail*/
        String registerToken = tokenService.generateRegisterToken(customer);

        String subject = customer.getFirstName() + " finish setting up your new Account ";

        String toEmail = customer.getEmail();
        String message = "Activate your account by clicking the link below within 15 minutes.\n"
                + "127.0.0.1:6640/api/auth/activate_account/" + customer.getEmail() + "/" + registerToken;
        emailService.sendEmail(toEmail, subject, message);
        /*success message*/
        log.info("Customer registered successfully");
        return new ResponseEntity<>("Registered successfully!Activate Your Account within 3hrs.", HttpStatus.CREATED);

    }

    public ResponseEntity<String> registerSeller(SellerRegisterDto sellerRegisterDto) {
        /*Checking seller exist with this mail or not*/
        if (userRepository.existsByEmail(sellerRegisterDto.getEmail())) {
            log.info("Email is already registered.");
            return new ResponseEntity<>("Email is already taken", HttpStatus.BAD_REQUEST);
        }
        /* Checking password and confirm password match or not*/
        if (!sellerRegisterDto.getPassword().equals(sellerRegisterDto.getConfirmPassword())) {
            log.info("Password and Confirm password do not match.");
            return new ResponseEntity<>("Password and Confirm password do not match.", HttpStatus.BAD_REQUEST);
        }
        Seller seller = new Seller();
        seller.setFirstName(sellerRegisterDto.getFirstName());
        seller.setMiddleName(sellerRegisterDto.getMiddleName());
        seller.setLastName(sellerRegisterDto.getLastName());

        seller.setActive(false);
        seller.setDeleted(false);
        seller.setExpired(false);
        seller.setLocked(false);
        seller.setInvalidAttemptCount(0);


        seller.setEmail(sellerRegisterDto.getEmail());
        seller.setPassword(passwordEncode.encode(sellerRegisterDto.getPassword()));

        seller.setCompanyContact(sellerRegisterDto.getCompanyContact());
        seller.setCompanyName(sellerRegisterDto.getCompanyName());
        seller.setGst(sellerRegisterDto.getGstNumber());


        Role roles = roleRepository.findByAuthority("ROLE_SELLER").get();
        //System.out.println(roles.getAuthority());
        seller.setRoles(Collections.singletonList(roles));
        sellerRepository.save(seller);
        /*send mail*/
        String subject = "Account Created ";

        String toEmail = seller.getEmail();
        String message = "Congratulation Your account is created.\n Contact admin to activate it.";
        emailService.sendEmail(toEmail, subject, message);

        return new ResponseEntity<>("Seller Registered Successfully!\n Contact admin to activate it.", HttpStatus.CREATED);

    }


    public ResponseEntity<?> login(LoginDto loginDto, UserEntity user) {
        /* Matching login entities with the entities present in security context.*/
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        /* Generate Access Token for user.*/
        Token accessToken = new Token();
        accessToken.setUserEntity(user);
        accessToken.setToken(token);
        accessToken.setCreatedAt(LocalDateTime.now());
        accessToken.setExpiredAt(LocalDateTime.now().plusMinutes(SecurityConstants.ACCESS_PASS_EXPIRE_MINUTES));
        accessTokenRepository.save(accessToken);

        /*Generate Refresh Token to refresh access token.*/
        RefreshToken refreshToken = tokenService.generateRefreshToken(user);
        refreshTokenRepository.save(refreshToken);

        return new ResponseEntity<>(new AuthResponseDto(accessToken.getToken(), refreshToken.getToken()), HttpStatus.OK);
    }

    public ResponseEntity<String> activateAccount(UserEntity userEntity, String token) {
        String out =  tokenService.confirmAccount(userEntity.getId(),token);
        return new ResponseEntity<>(out,HttpStatus.OK);
    }


    public String resendActivationToken(String email) {
        UserEntity customer = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid Email!"));
        if(customer.isActive()){
            return "Account is already activated.";
        }
        Long userId= customer.getId();
        ActivateUserToken activateUserToken = activationTokenRepository.findByUserId(userId);
        if (activateUserToken==null) {
            activationTokenRepository.deleteByUserId(userId);
        }
        String registerToken=tokenService.generateRegisterToken(customer);
        String subject = customer.getFirstName() + " finish setting up your new Account ";

        String toEmail = customer.getEmail();
        String message = "Activate your account by clicking the link below within 15 minutes.\n"
                + "127.0.0.1:6640/api/auth/activate_account/" + customer.getEmail() + "/" + registerToken;
        emailService.sendEmail(toEmail, subject, message);
        return "New Activation Link sent successfully on your registered email";
    }

    @Transactional
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String tokenValue = tokenService.getJWTFromRequest(request);
        if (tokenValue == null) {
            return new ResponseEntity<>("Token not found", HttpStatus.BAD_REQUEST);
        }
        BlackListToken jwtBlacklist = new BlackListToken();
        Optional<Token> token = accessTokenRepository.findByToken(tokenValue);
        if (token.isPresent()) {
            /*need discussion*/
            jwtBlacklist.setToken(token.get().getToken());
            jwtBlacklist.setUserEntity(token.get().getUserEntity());
            jwtBlackListRepository.save(jwtBlacklist);
            if(accessTokenRepository.existsByUserId(token.get().getUserEntity().getId())>0){
                accessTokenRepository.deleteByUserId(token.get().getUserEntity().getId());
            }
            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Access Token Not Found", HttpStatus.BAD_REQUEST);
        }
    }
}