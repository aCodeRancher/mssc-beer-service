package guru.springframework.msscbeerservice.services;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BeerServiceTest {

    private final UUID starUUID = UUID.fromString("969b7f32-e7f2-4d59-92bd-a36a99b3cf27");

    private final String starUPC = "0631234200137";

    private final BeerDto starBeerDto =
            BeerDto.builder()
                    .id(starUUID)
                    .beerName("Star Beer")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .quantityOnHand(12)
                    .price(new BigDecimal("13.96"))
                    .upc(starUPC)
                    .build();

    private final Beer starBeer =
                 Beer.builder()
                    .id(starUUID)
                    .beerName("Star Beer")
                    .beerStyle(BeerStyleEnum.PALE_ALE.name())
                    .quantityToBrew(12)
                    .price(new BigDecimal("13.96"))
                    .upc(starUPC)
                    .build();

    @Mock
    BeerRepository beerRepository;

    @Mock
    BeerMapper beerMapper;

    @Autowired
    @InjectMocks
    BeerServiceImpl  beerService;

    @Test
    void updateBeerInCache(){
        Optional<Beer> foundBeer = Optional.of(starBeer);
        when (beerRepository.findById(starUUID)).thenReturn(foundBeer);
        when (beerRepository.save(any(Beer.class))).thenReturn(starBeer);
        beerService.updateBeer(starUUID, starBeerDto);
        verify(beerRepository ,times(1)).findById(starUUID);
     }
}