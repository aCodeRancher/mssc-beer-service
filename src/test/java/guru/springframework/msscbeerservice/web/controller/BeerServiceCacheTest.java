package guru.springframework.msscbeerservice.web.controller;


import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class BeerServiceCacheTest {
    private final UUID starUUID = UUID.fromString("969b7f32-e7f2-4d59-92bd-a36a99b3cf27");
    private final UUID sunUUID  = UUID.fromString("245119ff-df57-4d7a-b9f7-2228024a1a48");
    private final String starUPC = "0631234200137";
    private final String sunUPC = "0631234211138";
    private final Beer starBeer =
            Beer.builder()
                    .id(starUUID)
                    .beerName("Star Beer")
                    .beerStyle(BeerStyleEnum.PALE_ALE.name())
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .price(new BigDecimal("13.96"))
                    .upc(starUPC)
                    .build();

    private final Beer  sunBeer  =
            Beer.builder()
                    .id(sunUUID)
                    .beerName("Sun Beer")
                    .beerStyle(BeerStyleEnum.PALE_ALE.name())
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .price(new BigDecimal("14.96"))
                    .upc(sunUPC)
                    .build();

    @Autowired
    BeerRepository beerRepository ;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setUp(){
          beerRepository.save(starBeer);
          beerRepository.save(sunBeer);
    }
    @AfterEach
    void clean(){
        beerRepository.deleteAll();
    }

    private Optional<Beer> getCachedBeer(UUID uuid){
        return ofNullable(cacheManager.getCache("beerCache")).map(c->c.get(uuid, Beer.class));
    }

    @Test
    void beerShouldBeCached() {
        Optional<Beer> starBeer = beerRepository.findById(starUUID);
        assertEquals(starBeer, getCachedBeer(starUUID));
    }

    @Test
    void beerShouldNotBeCached(){
        beerRepository.findByUpc(sunUPC);
        assertEquals(empty(), getCachedBeer(sunUUID));
    }
}
