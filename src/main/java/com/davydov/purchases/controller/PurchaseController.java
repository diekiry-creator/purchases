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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


@RestController
public class PurchaseController {

    @Value("${USERS_SERVICE_URL}")
    public String usersServiceUrl;
    @Value("${BOOKS_SERVICE_URL}")
    public String booksServiceUrl;

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

        return "Purchase is created";
    }
    @GetMapping("/findall")
    public ResponseEntity<List<Purchase>> findAll(){

        List<Purchase> purchases = repository.findAll();
        List<PurchaseUI> purchaseUI = new ArrayList<>();

        for (Purchase purchase : purchases) {
            purchaseUI.add(new PurchaseUI(purchase.getUserId(), purchase.getBookId(), purchase.getPrice()));
        }

        return ResponseEntity.ok().body(purchases);
    }

    @RequestMapping("/search/{id}")
    public String search(@PathVariable long id){
        String purchase = "";
        purchase = repository.findById(id).toString();
        return purchase;
    }

    @RequestMapping("/searchbyuserid/{id}")
    public ResponseEntity<List<Purchase>> fetchDataByUserId(@PathVariable long id){

        List<Purchase> purchases = repository.findByUserId(id);
        List<PurchaseUI> purchaseUI = new ArrayList<>();

        for (Purchase purchase : purchases) {
            purchaseUI.add(new PurchaseUI(purchase.getUserId(), purchase.getBookId(), purchase.getPrice()));
        }

        return ResponseEntity.ok().body(purchases);
    }


    @PostMapping("/create-purchase")
    public ResponseEntity<String> createPurchase(@RequestBody CreatePurchase data){
        UserRequisites requisites = data.getRequisites();
        Long bookTypeId = data.getBookTypeId();
        Long amountBooks = data.getAmountBooks();

        RestTemplate restTemplate = new RestTemplate();

        // Запрос к сервису книг (получение стоимости книги)
        HttpEntity<bookOperation> bookRequest = new HttpEntity<>(new bookOperation(bookTypeId, amountBooks));
        ResponseEntity<BookInfo> bookResponse;
        try {
            bookResponse = restTemplate
                    .exchange("http://192.168.1.159:9090/books/sub", HttpMethod.PUT, bookRequest, BookInfo.class);

            if (bookResponse.getStatusCode() != HttpStatus.OK)
                return ResponseEntity.badRequest().body("Недостаточно товара на складе.");

        } catch (HttpStatusCodeException exception) {
            return ResponseEntity.badRequest().body("Сервер книг недоступен.");
        }

        Double purchasePrice = Objects.requireNonNull(bookResponse.getBody()).getPurchasePrice();

        // Запрос к сервису пользователей (списание стоимости книги со счета, получение id пользователя)
        HttpEntity<UserOperation> userRequest = new HttpEntity<>(new UserOperation(requisites, purchasePrice));
        ResponseEntity<UserInfo> userResponse;
        try {
            userResponse = restTemplate
                    .exchange(usersServiceUrl, HttpMethod.PUT, userRequest, UserInfo.class);

            // Возврат книги, если у пользователя недостаточно средств
            if (userResponse.getStatusCode() != HttpStatus.OK)
            {
                bookRequest = new HttpEntity<>(new bookOperation(bookTypeId, amountBooks));
                restTemplate
                        .exchange("http://192.168.1.159:9090/books/ref", HttpMethod.PUT, bookRequest, BookInfo.class);
                return ResponseEntity.badRequest().body(Objects.requireNonNull(userResponse.getBody()).getExplanation());
            }
        } catch (HttpStatusCodeException exception) {

            // Возврат книги, если в текущий момент сервис пользователей недоступен
            bookRequest = new HttpEntity<>(new bookOperation(bookTypeId, amountBooks));
            bookResponse = restTemplate
                    .exchange("http://192.168.1.159:9090/books/ref", HttpMethod.PUT, bookRequest, BookInfo.class);
            if (bookResponse.getStatusCode() != HttpStatus.OK)
                return ResponseEntity.badRequest().body("Сервер пользователей недоступен. Возврат товара не выполнен.");

            return ResponseEntity.badRequest().body("Сервер пользователей недоступен.  Возврат товара выполнен.");
        }

        // Добавление записи о заказе в таблицу БД
        repository.save(new Purchase(Objects.requireNonNull(userResponse.getBody()).getUserId(), bookTypeId, purchasePrice));

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("list-of-orders")
    public ResponseEntity<?> getListOfOrders(@RequestParam String username) {
        RestTemplate restTemplate = new RestTemplate();

        // Запрос к сервису пользователей (получение id пользователя)
        HttpEntity<UserOperation> userRequest = new HttpEntity<>(new UserOperation(new UserRequisites(username, null), null));
        ResponseEntity<UserInfo> userResponse;
        try {
            userResponse = restTemplate
                    .exchange(usersServiceUrl, HttpMethod.GET, userRequest, UserInfo.class);
        } catch (HttpStatusCodeException exception) {
            return ResponseEntity.badRequest().body("Сервис пользователей недоступен.");
        }
        if (userResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.badRequest().body("Пользователь не найден.");
        }

        List<Purchase> purchases = repository.findByUserId(Objects.requireNonNull(userResponse.getBody()).getUserId());

        return ResponseEntity.ok().body(purchases);
    }
}
