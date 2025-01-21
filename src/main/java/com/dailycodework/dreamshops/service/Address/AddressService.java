package com.dailycodework.dreamshops.service.Address;

import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Address;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.repository.AddressRepository;
import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.request.AddAddressRequest;
import com.dailycodework.dreamshops.request.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService  implements IAddressService{
    private final UserRepository userRepository;
    private final AddressRepository addressRepository ;
    private final ModelMapper modelMapper ;

    @Override
    public AddressResponse addAddress(AddAddressRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        Address address = Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .user(user)
                .build();
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressResponse.class) ;
    }

    @Override
    public AddressResponse updateAddress(Long addressId, AddAddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found!"));
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setCountry(request.getCountry());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());

        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress , AddressResponse.class);
    }

    @Override
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!"));

        return user.getAddresses().stream()
                .map(address -> modelMapper.map(address , AddressResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found!"));
        addressRepository.delete(address);
    }

    @Override
    public boolean isAddressOwnedByUser(Long addressId, Long userId) {
        return addressRepository.existsByIdAndUserId(addressId , userId);
    }

}
