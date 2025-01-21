package com.dailycodework.dreamshops.controller;

import com.dailycodework.dreamshops.request.AddAddressRequest;
import com.dailycodework.dreamshops.request.AddressResponse;
import com.dailycodework.dreamshops.service.Address.IAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/address")
@EnableMethodSecurity(prePostEnabled = true)
public class AddressController {
    private final IAddressService addressService ;


    @PostMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id") // Ensure the user is adding an address to their own account
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddAddressRequest request, @PathVariable Long userId){
        AddressResponse response = addressService.addAddress(request , userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("@addressService.isAddressOwnedByUser(#addressId, authentication.principal.id)")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long addressId,@Valid @RequestBody AddAddressRequest request){
        AddressResponse response = addressService.updateAddress( addressId , request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAddressesByUserId(@PathVariable Long userId){
        List<AddressResponse> responses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("{addressId}")
    @PreAuthorize("@addressService.isAddressOwnedByUser(#addressId, authentication.principal.id)")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted successfully");
    }

}
