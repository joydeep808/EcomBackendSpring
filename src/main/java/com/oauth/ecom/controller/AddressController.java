package com.oauth.ecom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.oauth.ecom.dto.address.CreateAddressDto;
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
  public ResponseEntity<ReqRes> createAddress(HttpServletRequest httpServletRequest , @RequestBody @Valid CreateAddressDto createAddressDto){
    ReqRes response = addressService.createAddress(httpServletRequest, createAddressDto);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
  @PutMapping("/update")
  public ResponseEntity<ReqRes> updateAddress(HttpServletRequest httpServletRequest , @RequestBody CreateAddressDto createAddressDto){
    ReqRes response = addressService.updateAddress(httpServletRequest, createAddressDto);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
    // return ResponseEntity.ok(addressService.updateAddress(httpServletRequest, createAddressDto));
  }
  @GetMapping("/get")
  public ResponseEntity<ReqRes> getAddress(HttpServletRequest request){
    ReqRes response = addressService.getAddress(request);
    return   response.getIsSuccess()? ResponseEntity.status(response.getStatusCode()).body(response) : ResponseEntity.status(response.getStatusCode()).body(response);
  }
}
