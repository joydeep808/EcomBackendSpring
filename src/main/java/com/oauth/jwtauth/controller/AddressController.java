package com.oauth.jwtauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.address.CreateAddressDto;
import com.oauth.jwtauth.services.address.AddressService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RestController
@RequestMapping("/api/v1/address")
public class AddressController {
  @Autowired 
  private AddressService addressService;

  @PostMapping("/create")
  public ResponseEntity<ReqRes> createAddress(HttpServletRequest httpServletRequest , @RequestBody @Valid CreateAddressDto createAddressDto){
    return ResponseEntity.ok(addressService.createAddress(httpServletRequest, createAddressDto));
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes> updateAddress(HttpServletRequest httpServletRequest , @RequestBody CreateAddressDto createAddressDto){
    return ResponseEntity.ok(addressService.updateAddress(httpServletRequest, createAddressDto));
  }
  // @GetMapping("/get")
  // public ResponseEntity<ReqRes> getAddress(HttpServletRequest request){
  //   return ResponseEntity.ok(addressService.getAddress(request));
  // }
}
