package com.matrimony.site.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matrimony.site.common.UserConstant;
import com.matrimony.site.entity.Users;
import com.matrimony.site.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {
	
	


	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@PostMapping("/join")
	public String joinGroup(@RequestBody Users user) {
		user.setRoles(UserConstant.DEFAULT_ROLE);
		String encryptedPassword=passwordEncoder.encode(user.getPassword());
		user.setPassword(encryptedPassword);
		userRepository.save(user);
		
		return "Hi "+user.getUserName()+"welcome to group";
	}
	
	@GetMapping("/access/{userId}/{userRole}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String giveAccessToUser(@PathVariable int userId,@PathVariable String userRole, Principal principal) {
		Users user=userRepository.findById(userId).get();
		List<String> activeRoles=getRolesByLoggedInUser(principal);
		
		String newRole="";
		if(activeRoles.contains(userRole)) {
			newRole=user.getRoles()+","+userRole;
			user.setRoles(newRole);
		}
		userRepository.save(user);
		
		return "Hi "+user.getUserName()+" New Role assign to you by "+principal.getName();
	}
	
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public List<Users> loadUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping("/test")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String testUserAccess() {
		return "user can only access this!";
	}
	
	
	private List<String> getRolesByLoggedInUser(Principal principal){
		String roles=getLoggedInUser(principal).getRoles();
		List<String> assignRoles=Arrays.stream(roles.split(",")).collect(Collectors.toList());
	    
		if(assignRoles.contains("ROLE_ADMIN")) {
			return Arrays.stream(UserConstant.ADMIN_ACCESS).collect(Collectors.toList());
		}
		if(assignRoles.contains("ROLE_MODERATOR")) {
			return Arrays.stream(UserConstant.MODERATOR_ACCESS).collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}
	
	private Users getLoggedInUser(Principal principal) {
		return userRepository.findByUserName(principal.getName()).get();
	}
	
	

}
