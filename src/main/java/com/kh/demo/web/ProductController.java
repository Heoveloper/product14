package com.kh.demo.web;

import com.kh.demo.domain.dao.Product;
import com.kh.demo.domain.svc.ProductSVC;
import com.kh.demo.web.api.ApiResponse;
import com.kh.demo.web.api.form.AddForm;
import com.kh.demo.web.form.DetailForm;
import com.kh.demo.web.form.UpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

  @Autowired
  private final ProductSVC productSVC;

  //등록
  @PostMapping("/add")
  public ApiResponse<Object> add (
          @Valid @RequestBody AddForm addForm,
          BindingResult bindingResult
  ) {
      Product product = new Product();
      BeanUtils.copyProperties(addForm, product);

      productSVC.save(product);

      return ApiResponse.createApiResMsg("00", "성공", null);
  }

  //조회
  @GetMapping("/{id}/detail")
  public String findByProductId(@PathVariable("id") Long productId,
                                Model model) {

    Optional<Product> findedProduct = productSVC.findByProductId(productId);
    DetailForm detailForm = new DetailForm();
    if(!findedProduct.isEmpty()) {
      BeanUtils.copyProperties(findedProduct.get(), detailForm);
    }

    model.addAttribute("form", detailForm);
    return "product/detailForm";
  }

  //수정양식
  @GetMapping("/{id}/edit")
  public String updateForm(@PathVariable("id") Long productId,
                           Model model) {

    Optional<Product> findedProduct = productSVC.findByProductId(productId);
    UpdateForm updateForm = new UpdateForm();
    if(!findedProduct.isEmpty()) {
      BeanUtils.copyProperties(findedProduct.get(), updateForm);
    }
    model.addAttribute("form", updateForm);

    return "product/updateForm";
  }

  //수정
  @PostMapping("/{id}/edit")
  public String update(@PathVariable("id") Long productId,
                       @Valid @ModelAttribute("form") UpdateForm updateForm,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {
      log.info("bindingResult={}", bindingResult);
      return "product/updateForm";
    }

    //필드검증
    //상품수량은 100초과 금지
    if(updateForm.getQuantity() > 100){
      bindingResult.rejectValue("quantity","product.quantity",new Integer[]{100},"상품수량 초과");
      log.info("bindingResult={}", bindingResult);
      return "product/saveForm";
    }

    //오브젝트검증
    //총액(상품수량*단가) 1000만원 초과금지
    if(updateForm.getQuantity() * updateForm.getPrice() > 10_000_000L){
      bindingResult.reject("product.totalPrice",new Integer[]{1000},"총액 초과!");
      log.info("bindingResult={}", bindingResult);
      return "product/saveForm";
    }

    Product product = new Product();
    BeanUtils.copyProperties(updateForm, product);
    productSVC.update(productId, product);

    redirectAttributes.addAttribute("id", productId);
    return "redirect:/products/{id}/detail";
  }

  //삭제
  @GetMapping("/{id}/del")
  public String deleteById(@PathVariable("id") Long productId) {

    productSVC.deleteByProductId(productId);

    return "redirect:/products/all";  //항시 절대경로로
  }

  //목록
  @GetMapping
  public String findAll(Model model) {
    List<Product> products = productSVC.findAll();

    List<Product> list = new ArrayList<>();
    products.stream().forEach(product->{
      BeanUtils.copyProperties(product,new DetailForm());
      list.add(product);
    });
    model.addAttribute("list", list);
    return "product/all";
  }

}
