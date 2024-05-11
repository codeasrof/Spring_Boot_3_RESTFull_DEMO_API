package com.spring3.Spring3CRUD.controllers;

import com.spring3.Spring3CRUD.dtos.ProductRecordDTO;
import com.spring3.Spring3CRUD.models.ProductModel;
import com.spring3.Spring3CRUD.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;


    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDTO productRecordDTO) {
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        List<ProductModel> productList = productRepository.findAll();

        if(!productList.isEmpty()){
            for(ProductModel product: productList){
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productList);
    }

    @GetMapping("/products/{idProduct}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "idProduct") UUID idProduct) {
        Optional<ProductModel> product = productRepository.findById(idProduct);
        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Product founded. Click here to see all products."));
        return ResponseEntity.status(HttpStatus.OK).body(product.get());
    }

    @PutMapping("/products/{idProduct}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="idProduct") UUID idProduct, @RequestBody @Valid ProductRecordDTO productRecordDTO) {

        Optional<ProductModel> product = productRepository.findById(idProduct);

       if(product.isEmpty()){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
       }

       var productModel = product.get();
       BeanUtils.copyProperties(productRecordDTO, productModel);
       return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    @DeleteMapping("/products/{idProduct}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "idProduct") UUID idProduct){
        Optional<ProductModel> product = productRepository.findById(idProduct);

        if(product.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
        }
        productRepository.delete(product.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted with success");
    }

}
