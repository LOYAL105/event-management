package com.matrimony.site.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.matrimony.site.entity.Users;
import com.matrimony.site.repository.UserRepository;

@Service
public class GroupUserDetailService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<Users>user=userRepository.findByUserName(username);
		
		return user.map(GroupUserDetails::new)
				.orElseThrow(()->new UsernameNotFoundException(username+"doesn't exist"));
	}

}
