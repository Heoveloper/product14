package com.kh.demo.domain.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductDAOImpl implements ProductDAO{

  private final JdbcTemplate jt;

  //등록
  @Override
  public Long save(Product product) {
    StringBuffer sql = new StringBuffer();

    sql.append("insert into product(pid, pname, count, price) ");
    sql.append("     values(product_pid_seq.nextval, ?, ?, ?) ");

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jt.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        PreparedStatement pstmt = con.prepareStatement(sql.toString(), new String[]{"pid"});
        pstmt.setString(1, product.getPname());
        pstmt.setLong(2, product.getCount());
        pstmt.setLong(3, product.getPrice());
        return pstmt;
      }
    },keyHolder);

    return Long.valueOf(keyHolder.getKeys().get("pid").toString());
  }

  //목록
  @Override
  public List<Product> findAll() {
    StringBuffer sql = new StringBuffer();
    sql.append("select pid, pname, count, price ");
    sql.append("  from product ");
    sql.append("order by pid desc ");

    List<Product> products = jt.query(sql.toString(), new BeanPropertyRowMapper<>(Product.class));

    return products;
  }

  //조회
  @Override
  public Optional<Product> findByProductId(Long productId) {
    StringBuffer sql = new StringBuffer();

    sql.append("select pid, pname, count, price ");
    sql.append("  from product ");
    sql.append(" where pid = ? ");

    try {

      Product product = jt.queryForObject(
          sql.toString(),
          new BeanPropertyRowMapper<>(Product.class),
          productId);
      return Optional.of(product);

    }catch (EmptyResultDataAccessException e){
      e.printStackTrace();
      return Optional.empty();
    }
  }

  //변경
  @Override
  public int update(Long productId, Product product) {

    StringBuffer sql = new StringBuffer();
    sql.append("update product ");
    sql.append("   set pname = ?, ");
    sql.append("       count = ?, ");
    sql.append("       price = ? ");
    sql.append(" where pid = ? ");

    int affectedRow = jt.update(sql.toString(),
        product.getPname(), product.getCount(), product.getPrice(),productId);
    return affectedRow;
  }

  //삭제
  @Override
  public int deleteByProductId(Long productId) {

    String sql = "delete from product where pid = ? ";

    int affectedRow = jt.update(sql.toString(), productId);

    return affectedRow;
  }
}
