package guru.springframework.msscbeerservice.repositories;

import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.domain.Beer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BeerRepositoryTest {

    private final UUID starUUID = UUID.fromString("0a818933-087d-47f2-ad83-2f986ed087eb");
    private final String starUPC = "0631234300037";

    @Autowired
    BeerRepository beerRepository;

    private final Beer starBeer =  Beer.builder()
             .beerName("Star Beer")
            .beerStyle(BeerStyleEnum.PALE_ALE.name())
            .minOnHand(22)
            .quantityToBrew(200)
            .price(new BigDecimal("13.96"))
            .upc(starUPC)
            .build();

    @Test
    void save(){

        beerRepository.save(starBeer);
        assertEquals(4,beerRepository.findAll().size());
       // Optional<Beer> beerFound = beerRepository.findById(starUUID);
     //   assertEquals(beerFound.get().getId(), starUUID);
    }

}