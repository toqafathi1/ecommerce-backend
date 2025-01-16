package com.dailycodework.dreamshops.service.Address;

import com.dailycodework.dreamshops.request.AddAddressRequest;
import com.dailycodework.dreamshops.request.AddressResponse;

import java.util.List;

public interface IAddressService {

    AddressResponse addAddress( AddAddressRequest request , Long userId) ;
    AddressResponse updateAddress(Long addressId , AddAddressRequest request);
    List<AddressResponse> getAddressesByUserId(Long userId);
    void deleteAddress(Long addressId);
//    boolean isAddressOwnedByUser(Long addressId , Long userId);
}
