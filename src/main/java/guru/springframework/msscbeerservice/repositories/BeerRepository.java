package guru.springframework.msscbeerservice.repositories;

import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.domain.Beer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by jt on 2019-05-17.
 */
public interface BeerRepository extends JpaRepository<Beer, UUID> {

    Page<Beer> findAllByBeerName(String beerName, Pageable pageable);

    Page<Beer> findAllByBeerStyle(BeerStyleEnum beerStyle, Pageable pageable);

    Page<Beer> findAllByBeerNameAndBeerStyle(String beerName, BeerStyleEnum beerStyle, Pageable pageable);

     Beer findByUpc(String upc);

    @Cacheable(value="beerCache")
    Optional<Beer> findById (UUID uuid);

    Optional<Beer> findByMinOnHand (int minOnHand);

    @Caching(evict = {@CacheEvict(value="beerCache", allEntries=true)})
    void deleteById(UUID uuid);
}
