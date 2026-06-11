package com.example.adoptions.dogs;

import com.example.adoptions.dogs.validation.Validation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
class AdoptionsController {

    private final AdoptionsService adoptionsService;

    AdoptionsController(AdoptionsService adoptionsService) {
        this.adoptionsService = adoptionsService;
    }

    @PostMapping("/dogs/{dogId}/adoptions")
    void adopt(@PathVariable int dogId,
               @RequestParam String owner) {
        this.adoptionsService.adopt(dogId, owner);
    }
}

@Service
@Transactional
class AdoptionsService {

    private final DogRepository dogRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final Validation validation ;

    AdoptionsService(DogRepository dogRepository, ApplicationEventPublisher applicationEventPublisher, Validation validation) {
        this.dogRepository = dogRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.validation = validation;
    }

    void adopt(int dogId, String owner) {
        dogRepository.findById(dogId).ifPresent(dog -> {
            var updated = this.dogRepository.save(
                    new Dog(dog.id(), dog.name(), owner, dog.description()));
            applicationEventPublisher.publishEvent(new DogAdoptedEvent(dogId));
            IO.println("adopted " + updated);
        });
    }
}
