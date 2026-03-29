package com.beez.beez.users.mapper;

import com.beez.beez.users.dto.UsersDto;
import com.beez.beez.users.repository.Users;

import java.util.Optional;

public interface UsersMapper {

  void insertUser(UsersDto usersDto);

  Optional<Users> findById(String id);
}
