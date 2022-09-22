package com.kh.demo.web;

import com.kh.demo.domain.dao.Product;
import com.kh.demo.domain.svc.ProductSVC;
import com.kh.demo.web.api.ApiResponse;
import com.kh.demo.web.api.form.AddForm;
import com.kh.demo.web.api.form.EditForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

  @Autowired
  private final ProductSVC productSVC;

  //등록
  @PostMapping("/products")
  public ApiResponse<Object> add (
          @Valid @RequestBody AddForm addForm,
          BindingResult bindingResult
  ) {
      Product product = new Product();
      BeanUtils.copyProperties(addForm, product);

      productSVC.save(product);

      return ApiResponse.createApiResMsg("00", "등록 성공", null);
  }

  //조회
  @GetMapping("/products/{id}")
  public ApiResponse<Product> findById(@PathVariable("id") Long pid) {

    Optional<Product> findedProduct = productSVC.findByProductId(pid);

    ApiResponse<Product> response = null;
    if (findedProduct.isPresent()) {
      response = ApiResponse.createApiResMsg("00", "조회 성공", null);
    } else {
      response = ApiResponse.createApiResMsg("99", "조회하려는 상품이 없습니다.", null);
    }

    return response;
  }

  //수정
  @PatchMapping("/products/{id}")
  public ApiResponse<Object> edit(
          @PathVariable("id") Long pid,
          @Valid @RequestBody EditForm editForm,
          BindingResult bindingResult
  ) {
      Product product = new Product();
      BeanUtils.copyProperties(editForm, product);

    productSVC.update(pid, product);

    return ApiResponse.createApiResMsg("00", "수정 성공", null);
  }

  //삭제
  @DeleteMapping("/products/{id}")
  public ApiResponse<Product> delete(@PathVariable("id") Long pid) {

    Optional<Product> findedProduct = productSVC.findByProductId(pid);

    if (findedProduct.isEmpty()) {
      return ApiResponse.createApiResMsg("99", "삭제하려는 상품이 없습니다.", null);
    }

    productSVC.deleteByProductId(pid);

    return ApiResponse.createApiResMsg("00", "삭제 성공", null);
  }

  //목록
  @GetMapping("/products")
  public ApiResponse<List<Product>> allProducts() {

    productSVC.findAll();

    return ApiResponse.createApiResMsg("00", "전체 상품목록 조회 성공", null);
  }

}
