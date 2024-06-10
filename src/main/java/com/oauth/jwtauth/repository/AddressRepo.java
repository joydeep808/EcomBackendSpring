package com.oauth.jwtauth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oauth.jwtauth.entity.Address;

@Repository
public interface AddressRepo  extends JpaRepository<Address , Long>{
  

  @Query(value =  "select * from address where id=:id and user =:user ",nativeQuery=true )
  Address findByIdAndUser(@Param("id") Long id , @Param("user") Long user);
  @Query(value =  "select * from address where user_id=:user" ,nativeQuery = true)
  List<Address> findAddressFromUserId(@Param("user") long Id);
  @Query(value = "select * from address where user_id =:user limit 1" , nativeQuery = true)
  Address findByUser(@Param("user") Long user);
}
