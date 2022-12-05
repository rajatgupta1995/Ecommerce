package org.ttn.ecommerce.services.Impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ttn.ecommerce.dto.LoginDto;
import org.ttn.ecommerce.dto.accountAuthService.AuthResponseDto;
import org.ttn.ecommerce.dto.register.CustomerRegisterDto;
import org.ttn.ecommerce.dto.register.SellerRegisterDto;
import org.ttn.ecommerce.entity.register.Customer;
import org.ttn.ecommerce.entity.register.Role;
import org.ttn.ecommerce.entity.register.Seller;
import org.ttn.ecommerce.entity.register.UserEntity;
import org.ttn.ecommerce.entity.token.ActivateUserToken;
import org.ttn.ecommerce.entity.token.RefreshToken;
import org.ttn.ecommerce.entity.token.Token;
import org.ttn.ecommerce.repository.registerrepository.CustomerRepository;
import org.ttn.ecommerce.repository.registerrepository.RoleRepository;
import org.ttn.ecommerce.repository.registerrepository.SellerRepository;
import org.ttn.ecommerce.repository.registerrepository.UserRepository;
import org.ttn.ecommerce.repository.tokenrepository.AccessTokenRepository;
import org.ttn.ecommerce.repository.tokenrepository.ActivationTokenRepository;
import org.ttn.ecommerce.repository.tokenrepository.RefreshTokenRepository;
import org.ttn.ecommerce.security.JWTGenerator;
import org.ttn.ecommerce.security.SecurityConstants;
import org.ttn.ecommerce.services.TokenService;
import org.ttn.ecommerce.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@NoArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncode;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AccessTokenRepository accessTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private ActivationTokenRepository activationTokenRepository;
    @Autowired
    private MessageSource messageSource;

    /* To register Customer */
    @Override
    public ResponseEntity<String> registerCustomer(CustomerRegisterDto registerDto) {
        Locale locale=LocaleContextHolder.getLocale();
        /*Checking customer exist with this mail or not*/
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            log.info("Email is already registered.");
            return new ResponseEntity<>(messageSource.getMessage("api.error.emailExist",null,locale), HttpStatus.BAD_REQUEST);
        }
        /* Checking password and confirm password match or not*/
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            log.info(messageSource.getMessage("Password and confirmPassword does not match.",null,locale));
            return new ResponseEntity<>(messageSource.getMessage("api.error.passwordNotMatches",null,locale), HttpStatus.BAD_REQUEST);
        }

        /*setting customer details*/
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

        /*send mail to customer*/
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

    /* To register Seller */
    @Override
    public ResponseEntity<String> registerSeller(SellerRegisterDto sellerRegisterDto) {
        Locale locale=LocaleContextHolder.getLocale();
        /*Checking seller exist with this mail or not*/
        if (userRepository.existsByEmail(sellerRegisterDto.getEmail())) {
            log.info("Email is already registered.");
            return new ResponseEntity<>(messageSource.getMessage("api.error.accountExist",null,locale), HttpStatus.BAD_REQUEST);
        }
        /* Checking password and confirm password match or not*/
        if (!sellerRegisterDto.getPassword().equals(sellerRegisterDto.getConfirmPassword())) {
            log.info("Password and confirm password does not match");
            return new ResponseEntity<>(messageSource.getMessage("api.error.passwordNotMatches",null,locale), HttpStatus.BAD_REQUEST);
        }
        /*setting seller details*/
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
        seller.setRoles(Collections.singletonList(roles));
        sellerRepository.save(seller);
        /*send mail to admin*/
        String subject = messageSource.getMessage("api.email.registerSubject",null,locale);

        String toEmail = "rajat.gupta1@tothenew.com";
        String message = "New seller registered with userId:"+seller.getId()+" .Please Activate it.";
        emailService.sendEmail(toEmail, subject, message);

        /*success message*/
        log.info("seller Registered Successfully!");
        return new ResponseEntity<>("Seller Registered Successfully!\n Contact admin to activate it.", HttpStatus.CREATED);

    }

    /* To login as user*/
    @Override
    public ResponseEntity<?> login(LoginDto loginDto, UserEntity user) {
        Locale locale = LocaleContextHolder.getLocale();
        String role="";
        List<Role> roles = (List<Role>) user.getRoles();
        for (Role role1 : roles)
            if (role1.getAuthority().equals("ROLE_ADMIN")) role = "ADMIN";
        /* check invalidAttempts*/
        if(!passwordEncode.matches(loginDto.getPassword(), user.getPassword()) && !role.equals("ADMIN")){
            int count =user.getInvalidAttemptCount()+1;
            user.setInvalidAttemptCount(count);
            userRepository.save(user);
            if(user.getInvalidAttemptCount() >= 3){
                user.setLocked(true);
                userRepository.save(user);
                /*send mail to admin */
                String subject = messageSource.getMessage("api.email.lockedSubject",null,locale);

                String toEmail = "rajat.gupta1@tothenew.com";
                String message = "User account with userId "+ user.getId()+"got locked :";
                emailService.sendEmail(toEmail, subject, message);
                return new ResponseEntity<>("Your account is locked",HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>("Password is wrong",HttpStatus.UNAUTHORIZED);
        }else{
            user.setInvalidAttemptCount(0);
        }
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

    /* To activate customer account*/
    @Override
    public ResponseEntity<String> activateAccount(UserEntity userEntity, String token) {
        return new ResponseEntity<>(tokenService.activateAccount(userEntity.getId(),token),HttpStatus.OK);
    }

    /*To resend activation token to customer */
    @Override
    public String resendActivationToken(String email) {
        UserEntity customer = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("Invalid Email!"));
        /* Account is activated or not */
        if(customer.isActive()){
            log.info("Account is already activated.");
            return "Account is already activated.";
        }
        Long userId= customer.getId();
        ActivateUserToken activateUserToken = activationTokenRepository.findByUserId(userId);
        if (activateUserToken==null) {
            activationTokenRepository.deleteByUserId(userId);
        }


        String registerToken=tokenService.generateRegisterToken(customer);
        /*send mail to customer */
        String subject = customer.getFirstName() + " finish setting up your new Account ";

        String toEmail = customer.getEmail();
        String message = "Activate your account by clicking the link below within 15 minutes.\n"
                + "127.0.0.1:6640/api/auth/activate_account/" + customer.getEmail() + "/" + registerToken;
        emailService.sendEmail(toEmail, subject, message);
        /*success message */
        log.info("New Activation Link sent successfully on your registered email");
        return "New Activation Link sent successfully on your registered email";
    }

    @Override
    @Transactional
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String tokenValue = tokenService.getJWTFromRequest(request);
//        if (tokenValue == null || tokenValue.isEmpty()) {
//            log.info("Token not found");
//            return new ResponseEntity<>("Token not found", HttpStatus.BAD_REQUEST);
//        }
        Optional<Token> token = accessTokenRepository.findByToken(tokenValue);
        if (token.isPresent()) {
            /*delete previous access tokens */
            if(accessTokenRepository.existsByUserId(token.get().getUserEntity().getId())>0){
                accessTokenRepository.deleteByUserId(token.get().getUserEntity().getId());
            }
            /*success message */
            log.info("Logged out successfully");
            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        } else {
            log.info("Access Token Not Found");
            return new ResponseEntity<>("Access Token Not Found", HttpStatus.BAD_REQUEST);
        }
    }
}