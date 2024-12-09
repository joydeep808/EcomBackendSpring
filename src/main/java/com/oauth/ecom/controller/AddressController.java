package com.oauth.ecom.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.address.CreateAddressDto;
import com.oauth.ecom.entity.Address;
import com.oauth.ecom.services.address.AddressService;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RestController
@RequestMapping("/api/v1/address")
public class AddressController {
  @Autowired 
  private AddressService addressService;

  @PostMapping("/create")
  public ResponseEntity<ReqRes<Object>> createAddress(HttpServletRequest httpServletRequest , @RequestBody @Valid CreateAddressDto createAddressDto) throws Exception{
    return addressService.createAddress(httpServletRequest, createAddressDto);
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes<Address>> updateAddress(HttpServletRequest httpServletRequest , @RequestBody CreateAddressDto createAddressDto){
   return addressService.updateAddress(httpServletRequest, createAddressDto);
  }
  @GetMapping("/get")
  public ResponseEntity<ReqRes<List<Address>>> getAddress(HttpServletRequest request){
   return addressService.getAddress(request);
  }
}
