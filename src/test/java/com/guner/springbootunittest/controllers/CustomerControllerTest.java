package com.guner.springbootunittest.controllers;

import com.guner.springbootunittest.dto.CustomerDto;
import com.guner.springbootunittest.models.Customer;
import com.guner.springbootunittest.services.CustomerService;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) //Test sınıfları için kullanılan anatasyon
class CustomerControllerTest {

    @Mock //Test edeceğimiz sınıfın bağımlılıklarını mocklamak yani taklit etmek için kullanılan anatasyon
    CustomerService mockCustomerService;

    @InjectMocks //Mock nesnelerini test edeceğimiz sınıfa inject etmek için kullanılıyor.
    CustomerController underTest;

    @Test //Test metodları için kullanılan anatasyon
    void create_shouldCreateSuccessfully(){

        // given

        Customer customer = new Customer();
        customer.setIdentificationNumber("12345678900");
        Optional<Customer> expected = Optional.of(customer);

       when(mockCustomerService.create(any())).thenReturn(expected);
       //mockCustomerService.create() çağrıldığında expected değişkenini dönmesini söylüyoruz.

        //when() --> hangi metodun çağrılacağını burada belirliyoruz
        //thenReturn() --> Çağrılan metodun hangi sonucu dönmesini beklediğimizi burada belirtiyoruz.
        //any() --> Obje tipinde herhangi bir parametre alabileceğini belirttik.

        //when
        CustomerDto request = new CustomerDto();
        ResponseEntity<Customer> response = underTest.create(request);
        Customer actual = response.getBody();

        //Create metodumuzu test ediyoruz ve dönen sonucu actual değişkenine atıyoruz.

        //then
        assertAll(
                ()->assertNotNull(actual),
                ()-> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                ()-> assertEquals(expected.get(),actual),
                ()->assertEquals(customer.getIdentificationNumber(),actual.getIdentificationNumber())
        );

        //assertNotNull(actual) - Assert iddia etmek anlamına geliyor. Burada parametre olarak geçtiğimiz
        //actual değişkeninin null olmayacağını iddia ediyoruz. Actual değişkeni null değilse true döner.

        //assertEquals(param1,param2) - Her iki parametrenin birbirine eşit olmasını karşılaştırıyoruz.

    }

    @Test
    void create_shouldReturnStatusBadRequest(){

        //Create Metodu Worst Case İçin Test

        // given
        when(mockCustomerService.create(any())).thenReturn(Optional.empty());

        //when
        CustomerDto request = new CustomerDto();
        ResponseEntity<Customer> response = underTest.create(request);

        //then
        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    void getAll_shouldReturnCustomerDtoList(){

        //given
        CustomerDto customerDto = new CustomerDto();
        List<CustomerDto> expected = Arrays.asList(customerDto);

        when(mockCustomerService.findAll()).thenReturn(expected);

        //when
        ResponseEntity<List<CustomerDto>> response = underTest.getAll();
        List<CustomerDto> actual = response.getBody();

        //then
        assertAll(
                ()->assertNotNull(actual),
                ()->assertEquals(HttpStatus.OK,response.getStatusCode()),
                ()->assertEquals(expected.size(),actual.size())
        );

    }

    @Test
    void getById_shouldReturnCustomerDto(){

        //given
        CustomerDto expected = new CustomerDto();
        expected.setIdentificationNumber("12345678902");

        when(mockCustomerService.findById(anyLong())).thenReturn(expected);

        //when
        ResponseEntity<CustomerDto> response = underTest.getById(1);
        CustomerDto actual = response.getBody();

        //then
        assertAll(
                ()->assertNotNull(actual),
                ()->assertEquals(HttpStatus.OK,response.getStatusCode()),
                ()->assertEquals(expected.getIdentificationNumber(),actual.getIdentificationNumber())
        );
    }

    @Test
    void getById_shouldReturnStatusNotFound_whenCustomerIdNotExist(){

        //given
        when(mockCustomerService.findById(anyLong())).thenReturn(null);

        //when
        ResponseEntity<CustomerDto> response = underTest.getById(1L);
        CustomerDto actual = response.getBody();

        //then
        assertNull(actual);
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());

    }

    @Test
    void deleteById_shouldDeleteSuccesfully(){

        //given
        //when
        ResponseEntity<Void> response = underTest.deleteById(1L);

        //then
        assertEquals(HttpStatus.NO_CONTENT,response.getStatusCode());

    }

    @Test
    void deleteById_shouldReturnStatusNotFound_whenCustomerIdNotExist(){

        //given
        Mockito.doThrow(EntityNotFoundException.class).
                when(mockCustomerService).deleteById(anyLong());

        //when
        ResponseEntity<Void> response = underTest.deleteById(1L);

        //then
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());

    }
}