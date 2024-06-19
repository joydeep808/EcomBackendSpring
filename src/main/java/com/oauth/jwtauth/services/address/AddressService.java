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
      response.sendSuccessResponse(200 , "Address successfully done!" );
      return response;
    } catch (Exception e) {
      response.sendErrorMessage(500, "Address not saved" , e.getLocalizedMessage());
      return response;
    }
  }
  public ReqRes updateAddress(HttpServletRequest request , CreateAddressDto updateDto) {
    ReqRes response = new ReqRes();
    try {
        if (updateDto.getId() == null || updateDto.getId().equals(null)) {
          response.sendErrorMessage(400, "Address id is required");
          return response;
        }
        Long id = (long) jwtInterceptor.getIdFromJwt(request);
      Address foundAddress =   addressRespo.findByIdAndUser(updateDto.getId() , id);
      if (foundAddress == null || foundAddress.equals(null)) {
        response.sendErrorMessage(404,"Address not found to update");
        return response;
      }
      Optional.ofNullable(updateDto.getFullAddress()).ifPresent(foundAddress::setFullAddress);
      Optional.ofNullable(updateDto.getPincode()).ifPresent(foundAddress::setPincode);
      Optional.ofNullable(updateDto.getState()).ifPresent(foundAddress::setState);
      Optional.ofNullable(updateDto.getPhone()).ifPresent(foundAddress::setPhone);
     Address updatedAddress =  addressRespo.save(foundAddress);
     response.sendSuccessResponse(200 , "Update successfully done!" , updatedAddress);
     return response;
  
    } catch (Exception e) {
      response.sendErrorMessage(500, "server not reachable" , e.getLocalizedMessage());
      return response;
    }
  }
  public ReqRes getAddress(HttpServletRequest request) {
    ReqRes response = new ReqRes();
   
    try {
      Long id = (long) jwtInterceptor.getIdFromJwt(request);
      
     List<Address> addresses =  addressRespo.findAddressFromUserId(id);
     if (addresses == null || addresses.size() == 0) {
      response.sendErrorMessage(404, "No address found please add one");
      return response;
     }
     response.sendSuccessResponse(200, "Address found" , addresses);
     return response;
     
    } catch (Exception e) {
      response.sendErrorMessage(500 ,e.getLocalizedMessage());
      return response;
    }
  }

  
}
