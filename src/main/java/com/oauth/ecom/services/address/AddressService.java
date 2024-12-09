package com.oauth.ecom.services.address;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oauth.ecom.dto.address.CreateAddressDto;
import com.oauth.ecom.entity.Address;
import com.oauth.ecom.entity.UserEntity;
import com.oauth.ecom.repository.AddressRepo;
import com.oauth.ecom.repository.UserRepo;
import com.oauth.ecom.util.JwtInterceptor;
import com.oauth.ecom.util.ReqRes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AddressService {

  @Autowired
  private JwtInterceptor jwtInterceptor;
  @Autowired
  private AddressRepo addressRespo;
  @Autowired
  private UserRepo userRepo;

  public ResponseEntity<ReqRes<Object>> createAddress(HttpServletRequest httpServletRequest,
      CreateAddressDto createAddressDto) throws Exception{
    String email = jwtInterceptor.getEmailFromJwt(httpServletRequest);
    UserEntity user = userRepo.findByEmail(email);
    Address primeryAddress = addressRespo.findByPrimeryAddress(user.getId());
    if (primeryAddress != null) {
      primeryAddress.setPrimery(false);
      addressRespo.save(primeryAddress);
    }
    Address address = new Address();
    address.setFullAddress(createAddressDto.getFullAddress());
    address.setPhone(createAddressDto.getPhone());
    address.setPincode(createAddressDto.getPincode());
    address.setState(createAddressDto.getState());
    address.setPrimery(true);
    address.setUser(user);
    addressRespo.save(address);
    return new ReqRes<>().sendSuccessResponse(200, "Address successfully done!").sendResponseEntity();
  }


  public ResponseEntity<ReqRes<Address>> updateAddress(HttpServletRequest request, CreateAddressDto updateDto) {
    ReqRes<Address> response = new ReqRes<>();
    try {
      if (updateDto.getId() == null || updateDto.getId().equals(null)) {
        response.sendErrorMessage(400, "Address id is required");
        return response.sendResponseEntity();
      }
      Long id = (long) jwtInterceptor.getIdFromJwt(request);
      Address foundAddress = addressRespo.findByIdAndUser(updateDto.getId(), id);
      if (foundAddress == null || foundAddress.equals(null)) {
        response.sendErrorMessage(404, "Address not found to update");
        return response.sendResponseEntity();
      }
      Optional.ofNullable(updateDto.getFullAddress()).ifPresent(foundAddress::setFullAddress);
      Optional.ofNullable(updateDto.getPincode()).ifPresent(foundAddress::setPincode);
      Optional.ofNullable(updateDto.getState()).ifPresent(foundAddress::setState);
      Optional.ofNullable(updateDto.getPhone()).ifPresent(foundAddress::setPhone);
      Address updatedAddress = addressRespo.save(foundAddress);
      response.sendSuccessResponse(200, "Update successfully done!", updatedAddress);
      return response.sendResponseEntity();

    } catch (Exception e) {
      response.sendErrorMessage(500, "server not reachable");
      return response.sendResponseEntity();
    }
  }

  public ResponseEntity<ReqRes<List<Address>>> getAddress(HttpServletRequest request) {
    ReqRes<List<Address>> response = new ReqRes<>();

    try {
      Long id = (long) jwtInterceptor.getIdFromJwt(request);

      List<Address> addresses = addressRespo.findAddressFromUserId(id);
      if (addresses == null || addresses.size() == 0) {
        response.sendErrorMessage(404, "No address found please add one");
        return response.sendResponseEntity();
      }
      response.sendSuccessResponse(200, "Address found", addresses);
      return response.sendResponseEntity();

    } catch (Exception e) {
      response.sendErrorMessage(500, e.getLocalizedMessage());
      return response.sendResponseEntity();
    }
  }

}
