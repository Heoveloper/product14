package com.kh.demo.domain.svc;

import com.kh.demo.domain.dao.Product;

import java.util.List;
import java.util.Optional;

public interface ProductSVC {
  /**
   * 상품등록
   * @param product
   * @return
   */
  Long save(Product product);

  /**
   * 상품목록
   * @return
   */
  List<Product> findAll();

  /**
   * 상품조회
   * @return
   */
  Optional<Product> findByProductId(Long pid);

  /**
   * 상품변경
   * @param pid 상품아이디
   * @param product 상품
   * @return
   */
  int update(Long pid, Product product);

  /**
   * 상품삭제
   * @param pid
   * @return
   */
  int deleteByProductId(Long pid);
}
