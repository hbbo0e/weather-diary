package zerobase.weather.repository;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

@Repository
public class JdbcMemoRepository {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public JdbcMemoRepository(DataSource daataSource){
    jdbcTemplate = new JdbcTemplate(daataSource);
  }

  /**
   * RowMapper?
   * ResultSet 형태로 반환 { id = 1, text = "text" }
   */
  private RowMapper<Memo> memoRowMapper(){
    return (rs, rowNum) -> new Memo(
        rs.getInt("id"),
        rs.getString("text")
    );
  }

  public void save(Memo memo){
    String sql = "insert into memo values(?, ?)";
    jdbcTemplate.update(sql, memo.getId(), memo.getText());
  }

  public List<Memo> findAll(){
    String sql = "select * from memo";
    return jdbcTemplate.query(sql, memoRowMapper());
  }

  public Optional<Memo> findById(int id){
    String sql = "select * from memo where id = ?";
    return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
  }
}
