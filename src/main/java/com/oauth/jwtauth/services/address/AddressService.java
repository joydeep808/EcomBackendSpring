package com.oauth.jwtauth.services.address;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oauth.jwtauth.dto.ReqRes;
import com.oauth.jwtauth.dto.address.CreateAddressDto;
import com.oauth.jwtauth.entity.Address;
import com.oauth.jwtauth.entity.UserEntity;
import com.oauth.jwtauth.repository.AddressRepo;
import com.oauth.jwtauth.repository.UserRepo;
import com.oauth.jwtauth.util.JwtInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AddressService {
  

  @Autowired
  private JwtInterceptor jwtInterceptor;
  @Autowired
  private AddressRepo addressRespo;
  @Autowired
  private UserRepo userRepo;
  public ReqRes createAddress(HttpServletRequest httpServletRequest , CreateAddressDto createAddressDto) {
    ReqRes response = new ReqRes();
    try {
      String email = jwtInterceptor.getEmailFromJwt(httpServletRequest);
      UserEntity user = userRepo.findByEmail(email);
      Address address = new Address();
      address.setFullAddress(createAddressDto.getFullAddress());
      address.setPhone(createAddressDto.getPhone());
      address.setPincode(createAddressDto.getPincode());
      address.setState(createAddressDto.getState());
      address.setUser(user);
      addressRespo.save(address);
      response.setStatusCode(200);
      response.setMessage("Address successfully done!");
      response.setIsSuccess(true);
      return response;
    } catch (Exception e) {
      response.setStatusCode(500);
      response.setMessage("Address not saved");
      response.setError(e.getLocalizedMessage());
      return response;
    }
  }
  public ReqRes updateAddress(HttpServletRequest request , CreateAddressDto updateDto) {
    ReqRes response = new ReqRes();
    try {
        if (updateDto.getId() == null || updateDto.getId().equals(null)) {
          response.setMessage("Address id is required");
          response.setStatusCode(400);
          return response;
        }
        Long id = (long) jwtInterceptor.getIdFromJwt(request);
      Address foundAddress =   addressRespo.findByIdAndUser(updateDto.getId() , id);
      if (foundAddress == null || foundAddress.equals(null)) {
        response.setMessage("Address not found to update");
        response.setStatusCode(404);
        return response;
      }
      Optional.ofNullable(updateDto.getFullAddress()).ifPresent(foundAddress::setFullAddress);
      Optional.ofNullable(updateDto.getPincode()).ifPresent(foundAddress::setPincode);
      Optional.ofNullable(updateDto.getState()).ifPresent(foundAddress::setState);
      Optional.ofNullable(updateDto.getPhone()).ifPresent(foundAddress::setPhone);
     Address updatedAddress =  addressRespo.save(foundAddress);
     response.setMessage("Update successfully done!");
     response.setStatusCode(200);
     response.setIsSuccess(true);
     response.setData(updatedAddress);
     return response;
  
    } catch (Exception e) {
      response.setStatusCode(500);
      response.setMessage("Server not reachable");
      response.setError(e.getLocalizedMessage());
      return response;
    }
  }
  // public ReqRes getAddress(HttpServletRequest request) {
  //   ReqRes response = new ReqRes();
  //   try {
  //     Long id = (long) jwtInterceptor.getIdFromJwt(request);
  //    List<Address> addresses =  addressRespo.findAddressFromUserId(id);
  //    if (addresses == null || addresses.size() == 0) {
  //     response.setStatusCode(404);
  //     response.setMessage("No address found please add one");
  //     return response;
  //    }
  //    response.setStatusCode(200);
  //    response.setMessage("Address found");
  //    response.setData(addresses);
  //    response.setIsSuccess(true);
  //    return response;
     
  //   } catch (Exception e) {
  //     response.setStatusCode(500);
  //     response.setMessage(e.getMessage());
  //     response.setError(e.getLocalizedMessage());
  //     return response;
  //   }
  // }

  
}
