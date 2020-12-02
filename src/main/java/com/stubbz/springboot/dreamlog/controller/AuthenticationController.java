package com.stubbz.springboot.dreamlog.controller;

import com.stubbz.springboot.dreamlog.GenericResponse;
import com.stubbz.springboot.dreamlog.domain.User;
import com.stubbz.springboot.dreamlog.domain.VerificationToken;
import com.stubbz.springboot.dreamlog.dto.PasswordDto;
import com.stubbz.springboot.dreamlog.error.UserNotFoundException;
import com.stubbz.springboot.dreamlog.security.ISecurityUserService;
import com.stubbz.springboot.dreamlog.service.MyUserDetailsService;
import com.stubbz.springboot.dreamlog.service.UserService;
import com.stubbz.springboot.dreamlog.verification.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

@Controller
public class AuthenticationController {
	@Autowired
	UserService userServiceImp;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@Autowired
	ISecurityUserService securityService;

	@Autowired
	private JavaMailSender mailSender;


	private static final String FORGOT_PASSWORD_EMAIL_FOUND = "Information on how to reset your password has been sent to your email address";
	private static final String FORGOT_PASSWORD_EMAIL_NOT_FOUND = "That email is not tied to any current user";
	private static final String PASSWORD_RESET_TOKEN_RESULT_FAIL = "The password reset token has either expired or is incorrect.";
	private static final String PASSWORD_RESET_SUCCESS_MSG = "Your password has been changed successfully";


	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView modelAndView = new ModelAndView();
		 User user = new User();
		 modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return modelAndView;
	}
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		return modelAndView;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView registerUser(@Valid User user, BindingResult bindingResult, ModelMap modelMap, WebRequest request, HttpServletRequest request2)
	{
		ModelAndView modelAndView = new ModelAndView();
		user = userServiceImp.trimInput(user);
		if(bindingResult.hasErrors())
		{
			modelAndView.addObject("message", "Please correct Errors in the form!");
			modelMap.addAttribute("bindingResult", bindingResult);
		}
		else if(userServiceImp.isUsernameTaken(user))
		{
			modelAndView.addObject("message", "Username already taken");
		}
        else if(userServiceImp.isEmailTaken(user))
        {
            modelAndView.addObject("message", "Email already taken");
        }
		else {
			modelAndView.addObject("message", "Registered successfully! Verification email has been sent.");

			try {
				String appUrl = request2.getScheme() + "://" + request2.getServerName() + ":" + request2.getServerPort();
				eventPublisher.publishEvent(new OnRegistrationCompleteEvent
						(user, request.getLocale(), appUrl));
				modelAndView.addObject("emailVerificationMsg", "");
			} catch (Exception me) {
				modelAndView.addObject("emailVerificationMsg", "Something went wrong");
				System.out.println(me);
			}

		}

		modelAndView.addObject("user", new User());
		modelAndView.setViewName("register");
		return modelAndView;
	}


	@RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET)
	public ModelAndView confirmRegistration
			(WebRequest request, Model model, @RequestParam("token") String token) {

		Locale locale = request.getLocale();

		VerificationToken verificationToken = userServiceImp.getVerificationToken(token);

		ModelAndView modelAndView = new ModelAndView();
		if (verificationToken == null) {

			String message = "The token was invalid :/";
			modelAndView.addObject("errorMessage", message);
			modelAndView.setViewName("badUser");
			return modelAndView;
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {

			String messageValue = "The token has expired";
			modelAndView.addObject("errorMessage", messageValue);
			modelAndView.setViewName("badUser");
			return modelAndView;
		}

		user.setEnabled(true);
		userServiceImp.removeByEmail(user.getEmail());
		userServiceImp.deleteVerificationToken(verificationToken.getId());
		userServiceImp.saveUser(user);
		modelAndView.addObject("errorMessage", "Thank you for verifying your account!");
		modelAndView.setViewName("badUser");
		return modelAndView;
	}

	@RequestMapping(value = { "/forgotPassword" }, method = RequestMethod.GET)
	public ModelAndView forgotPassword() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("forgotPassword");
		return modelAndView;
	}

	@RequestMapping(value = "/forgotPassword",
			method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView resetPassword(HttpServletRequest request,
										 @RequestParam("email") String userEmail) {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("forgotPassword");
		User user = userServiceImp.findVerifiedUserByEmail(userEmail);
		if (user == null) {
			modelAndView.addObject("resultMessage",FORGOT_PASSWORD_EMAIL_NOT_FOUND);
			return modelAndView;
		}
//		String appUrl = "http://localhost:8080";
		String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		String token = UUID.randomUUID().toString();
		userServiceImp.createPasswordResetTokenForUser(user, token);
		mailSender.send(constructResetTokenEmail(appUrl,
				request.getLocale(), token, user));
		modelAndView.addObject("resultMessage",FORGOT_PASSWORD_EMAIL_FOUND);
		return modelAndView;
	}


	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public ModelAndView showChangePasswordPage(Locale locale, Model model,
										 @RequestParam("id") long id, @RequestParam("token") String token) {

		ModelAndView modelAndView = new ModelAndView();

		String result = securityService.validatePasswordResetToken(id, token);
		if (result != null) {
			modelAndView.addObject("errorMessage", PASSWORD_RESET_TOKEN_RESULT_FAIL);
			modelAndView.setViewName("prtFailed");
			return modelAndView;
		}

		PasswordDto passwordDto = new PasswordDto();
		modelAndView.addObject("passwordDto", passwordDto );
		modelAndView.setViewName("changePassword");
		return modelAndView;
	}

	@RequestMapping(value = "/savePassword", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView savePassword(Locale locale,
										@Valid PasswordDto passwordDto, BindingResult bindingResult, ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView();
		if(bindingResult.hasErrors())
		{
			modelMap.addAttribute("bindingResult", bindingResult);
			modelAndView.setViewName("changePassword");
			return modelAndView;
		}

		User user =
				(User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();

		userServiceImp.changeUserPassword(user, passwordDto.getNewPassword());
		userServiceImp.deletePasswordResetTokenByUser(user);
		modelAndView.addObject("message", PASSWORD_RESET_SUCCESS_MSG);
		modelAndView.setViewName("login");

		return modelAndView;
	}

	//=====================NON-API=======================

	private SimpleMailMessage constructResetTokenEmail(
			String contextPath, Locale locale, String token, User user) {
		String url = contextPath + "/changePassword?id=" +
				user.getId() + "&token=" + token;

		String message = "In order to reset your account password, click the following link:";
		return constructEmail("Reset Password", message + " \r\n" + url, user);
	}

	private SimpleMailMessage constructEmail(String subject, String body,
											 User user) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(subject);
		email.setText(body);
		email.setTo(user.getEmail());
//		email.setFrom(env.getProperty("support.email"));
		return email;
	}


}

