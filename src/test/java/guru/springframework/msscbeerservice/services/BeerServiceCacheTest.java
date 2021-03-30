package guru.springframework.msscbeerservice.services;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerServiceCacheTest {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    BeerService beerService ;

    @Autowired
    BeerRepository beerRepository;

    private final UUID starUUID = UUID.fromString("969b7f32-e7f2-4d59-92bd-a36a99b3cf27");

    private final String starUPC = "0631234200137";

    private final BeerDto starBeerDto =
            BeerDto.builder()
                    .id(starUUID)
                    .beerName("Star Beer")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .quantityOnHand(12)
                    .price(new BigDecimal("15.00"))
                    .upc(starUPC)
                    .build();



    private Optional<BeerDto> getCachedBeer(UUID uuid){
        return ofNullable(cacheManager.getCache("beerCache")).map(c->c.get(uuid, BeerDto.class));
    }



    @Test
    void updateBeerCached(){
        Beer beer0 = beerRepository.findAll().get(0);
        BeerDto updatedBeerDto = beerService.updateBeer(beer0.getId(),starBeerDto);
        Optional<BeerDto> cachedBeer = getCachedBeer(beer0.getId());
        assertEquals(updatedBeerDto, cachedBeer.get());
    }
}