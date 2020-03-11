package com.davydov.purchases.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.davydov.purchases.dto.*;
import com.davydov.purchases.model.Purchase;
import com.davydov.purchases.model.PurchaseUI;
import com.davydov.purchases.repository.PurchasesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
public class PurchaseController {

    @Autowired
    PurchasesRepository repository;

    @GetMapping("/bulkcreate")
    public String bulkcreate(){
        // save a single
        repository.save(new Purchase(1, 1, 1));
        // save a list
        repository.saveAll(Arrays.asList(new Purchase(2, 2,2)
                , new Purchase(3, 3,3)
                , new Purchase(4, 4,4)
                , new Purchase(5, 5,5)));

        return "Purchases are created";
    }
    @PostMapping("/create")
    public String create(@RequestBody PurchaseUI purchase){
        // save a single
        repository.save(new Purchase(purchase.getUserId(), purchase.getBookId(), purchase.getPrice()));

        return "Customer is created";
    }
    @GetMapping("/findall")
    public List<PurchaseUI> findAll(){

        List<Purchase> purchases = repository.findAll();
        List<PurchaseUI> purchaseUI = new ArrayList<>();

        for (Purchase purchase : purchases) {
            purchaseUI.add(new PurchaseUI(purchase.getUserId(), purchase.getBookId(), purchase.getPrice()));
        }

        return purchaseUI;
    }

    @RequestMapping("/search/{id}")
    public String search(@PathVariable long id){
        String purchase = "";
        purchase = repository.findById(id).toString();
        return purchase;
    }

    @RequestMapping("/searchbyuserid/{id}")
    public List<PurchaseUI> fetchDataByUserId(@PathVariable long id){

        List<Purchase> purchases = repository.findByUserId(id);
        List<PurchaseUI> purchaseUI = new ArrayList<>();

        for (Purchase purchase : purchases) {
            purchaseUI.add(new PurchaseUI(purchase.getUserId(), purchase.getBookId(), purchase.getPrice()));
        }

        return purchaseUI;
    }


    @PostMapping("/create-purchase")
    public UserResponse createPurchase(@RequestBody CreatePurchase data){
        UserRequisites requisites = data.getRequisites();
        Long bookTypeId = data.getBookTypeId();
        Long amountBooks = data.getAmountBooks();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<bookOperation> bookRequest = new HttpEntity<>(new bookOperation(bookTypeId, -amountBooks));
        ResponseEntity<BookInfo> bookResponse = restTemplate
                .exchange("http://graph.facebook.com/pivotalsoftware", HttpMethod.PUT, bookRequest, BookInfo.class);

        if (bookResponse.getStatusCode() != HttpStatus.OK)
            return UserResponse.error("Товар отсутствует на складе.");

        Double purchasePrice = Objects.requireNonNull(bookResponse.getBody()).getPurchasePrice();
        HttpEntity<UserOperation> userRequest = new HttpEntity<>(new UserOperation(requisites, purchasePrice));
        ResponseEntity<UserInfo> userResponse = restTemplate
                .exchange("http://graph.facebook.com/pivotalsoftware", HttpMethod.PUT, userRequest, UserInfo.class);

        if (userResponse.getStatusCode() != HttpStatus.OK)
        {
            bookRequest = new HttpEntity<>(new bookOperation(bookTypeId, amountBooks));
            restTemplate
                    .exchange("http://graph.facebook.com/pivotalsoftware", HttpMethod.PUT, bookRequest, BookInfo.class);
            return UserResponse.error(Objects.requireNonNull(userResponse.getBody()).getExplanation());
        }

        Long userId = Objects.requireNonNull(userResponse.getBody()).getUserId();
        repository.save(new Purchase(userId, bookTypeId, purchasePrice));

        return UserResponse.success();
    }

    @GetMapping("list-of-orders")
    public List<PurchaseUI> getListOfOrders(@RequestParam String username) throws URISyntaxException {
        List<PurchaseUI> purchaseUI = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<UserOperation> userRequest = new HttpEntity<>(new UserOperation(new UserRequisites(username, null), null));
        ResponseEntity<UserInfo> userResponse = restTemplate
                .exchange("http://graph.facebook.com/pivotalsoftware", HttpMethod.GET, userRequest, UserInfo.class);

        if (userResponse.getStatusCode() != HttpStatus.OK) {
            return purchaseUI;
        }

        List<Purchase> purchases = repository.findByUserId(Objects.requireNonNull(userResponse.getBody()).getUserId());
        for (Purchase purchase : purchases) {
            purchaseUI.add(new PurchaseUI(purchase.getUserId(), purchase.getBookId(), purchase.getPrice()));
        }

        return purchaseUI;
    }
}
