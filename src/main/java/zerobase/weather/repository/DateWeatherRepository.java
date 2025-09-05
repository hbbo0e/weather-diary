package zerobase.weather.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.DateWeather;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, LocalDate> {

}
