package guru.springframework.msscbeerservice.web.controller;

import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration
@ExtendWith(SpringExtension.class)
public class BeerRepoCacheTest {

    private final UUID starUUID = UUID.fromString("869b7f32-e7f2-4d59-92bd-a36a99b3cf27");
    private final UUID sunUUID  = UUID.fromString("145119ff-df57-4d7a-b9f7-2228024a1a48");
    private final String starUPC = "0631234200037";
    private final String sunUPC = "0631234200038";
    private final Beer starBeer =  Beer.builder()
            .id(starUUID)
            .beerName("Star Beer")
            .beerStyle(BeerStyleEnum.PALE_ALE.name())
            .minOnHand(22)
            .quantityToBrew(200)
            .price(new BigDecimal("13.96"))
            .upc(starUPC)
            .build();

    private final Beer sunBeer =  Beer.builder()
            .id(sunUUID)
            .beerName("Sun Beer")
            .beerStyle(BeerStyleEnum.PALE_ALE.name())
            .minOnHand(12)
            .quantityToBrew(200)
            .price(new BigDecimal("14.96"))
            .upc(sunUPC)
            .build();

    private BeerRepository mock;

    @Autowired
    private BeerRepository beerRepository;

    @EnableCaching
    @Configuration
    public static class CachingTestConfig{
        @Bean
        public BeerRepository beerRepositoryMockImplementation(){
            return mock(BeerRepository.class);
        }

        @Bean
        public CacheManager cacheManager(){
            return new ConcurrentMapCacheManager("beerCache");
        }
    }

    @BeforeEach
    void setUp(){
         mock = AopTestUtils.getTargetObject(beerRepository);
         Mockito.reset(mock);
         //star beer is not cacheable as it is not annotated @Cacheable in BeerRepository
         when(mock.findByMinOnHand(22))
                  .thenReturn(Optional.of(starBeer));
         //Sun beer is  cached as it is @Cacheable in BeerRepository
        when(mock.findById(sunUUID))
                .thenReturn(Optional.of(sunBeer));

        doNothing().when(mock).deleteById(sunUUID);
    }

    @Test
    void cachedBeer_repoShouldNotBeInvoked(){

        assertEquals(Optional.of(sunBeer), beerRepository.findById(sunUUID));
        verify(mock,times(1)).findById(sunUUID);
        assertEquals(Optional.of(sunBeer),beerRepository.findById(sunUUID));
        //after the verify(mock), the repository findById is not invoked
        verifyNoMoreInteractions(mock);
        verify(beerRepository, times(2)).findById(sunUUID);
    }

    @Test
    void notCachedBeer_repoShouldBeInvoked(){
       assertEquals(Optional.of(starBeer), beerRepository.findByMinOnHand(22));
       assertEquals(Optional.of(starBeer), beerRepository.findByMinOnHand(22));
       assertEquals(Optional.of(starBeer), beerRepository.findByMinOnHand(22));
      verify(mock,times(3)).findByMinOnHand(22);

    }

    @Test
    void delete_Cached_and_nonCached_Beer(){
        beerRepository.deleteById(sunUUID);
        verify(mock,times(1)).deleteById(sunUUID);
        beerRepository.findById(starUUID);
        verify(mock,times(1)).findById(starUUID);
    }
}
